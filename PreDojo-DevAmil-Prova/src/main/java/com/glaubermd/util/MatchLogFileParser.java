package com.glaubermd.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.glaubermd.entity.ActionEnum;
import com.glaubermd.entity.AwardTypeEnum;
import com.glaubermd.entity.Match;
import com.glaubermd.entity.MatchEvent;
import com.glaubermd.entity.Player;
import com.glaubermd.entity.RankingEntry;
import com.glaubermd.entity.Weapon;

/**
 * Implementa funcoes de interpretacao de arquivos de log.
 */
public class MatchLogFileParser {

	private static final String WORLD_PLAYER_NAME = "<WORLD>";
	private static final String USING_BY_REGEXP = "using|by";
	private static final String DEFAULT_FILE_ENCODING = "UTF-8";
	private static final String MATCH_STARTED_REGEXP = "New match [0-9]+ has started";
	private static final String MATCH_ID_REGEXP = "[0-9]+";
	private static final String MATCH_ENDED_REGEXP = "Match [0-9]+ has ended";
	private static final String MATCH_LOG_PATH = "/matches";
	private static final String VALID_LINE_REGEXP = "^[0-9]{2}\\/[0-9]{2}\\/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2} - .*$";
	private static final String LINE_TOKEN_SEPARATOR_REGEXP = " - ";
	private static final String PLAYER_NAME_REGEXP = "[a-zA-Z0-9_*/<>]+";
	private static final String PLAYER_NAMES_EXTRACTION_REGEXP = PLAYER_NAME_REGEXP + " " + ActionEnum.KILL.getAction() + " " + PLAYER_NAME_REGEXP;
	private static final String[] VALID_FILE_EXTENSIONS = {"log"};
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	
	/**
	 * Obtem arquivos com logs das partidas finalizadas.
	 * @return Collecton contendo Files representando os logs de partidas.
	 * @throws FileNotFoundException 
	 */
	public Collection<File> getFilesFromDir() throws FileNotFoundException {
		File logDir = null;
		URL logDirUrl = this.getClass().getResource(MATCH_LOG_PATH);
		if(logDirUrl != null) {
			logDir = new File(logDirUrl.getPath());
		}
		if (logDir == null)
			throw new FileNotFoundException("Directory for matches logs not found: " + MATCH_LOG_PATH);
		
		return FileUtils.listFiles(logDir, VALID_FILE_EXTENSIONS, false);
	}
	
	
	/**
	 * Interpreta o arquivo de log de partida finalizada.
	 * @param file - arquivo representando o log da partida.
	 * @return List contendo MatchEvents obtidos da extracao do log.
	 */
	public List<MatchEvent> parseFile(File file) {
		List<MatchEvent> events = new ArrayList<MatchEvent>();
		try {
			List<String> lines = FileUtils.readLines(file, DEFAULT_FILE_ENCODING);
			for (String l : lines) {
				if(l.matches(VALID_LINE_REGEXP)) {
					events.add(extractMatchEvent(l));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return events;
	}
	
	
	/**
	 * Extrai o MatchEvent de uma linha do log da partida.
	 * @param line - linha do arquivo de log da partida.
	 * @return MatchEvent representado pela linha do arquivo de log.
	 */
	private MatchEvent extractMatchEvent(String line) {
		MatchEvent event = new MatchEvent();
		String[] splitLogLine = line.split(LINE_TOKEN_SEPARATOR_REGEXP);
		
		// associa info basica do evento
		try {
			event.setTime(sdf.parse(splitLogLine[0]));
			event.setLog(splitLogLine[1]);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!event.getLog().matches(MATCH_STARTED_REGEXP) 
				&& !event.getLog().matches(MATCH_ENDED_REGEXP)) {
			
			// acha jogadores
			Pattern playerPattern = Pattern.compile(PLAYER_NAMES_EXTRACTION_REGEXP);
			Matcher playerMatcher = playerPattern.matcher(event.getLog());
			if (playerMatcher.find()) {
				String[] players = playerMatcher.group(0).split(ActionEnum.KILL.getAction());
				event.setAssassin(new Player(players[0].trim()));
				event.setVictim(new Player(players[1].trim()));
			}
			
			// acha armas / eventos fatais do mundo
			Pattern weaponPattern = Pattern.compile(USING_BY_REGEXP);
			Matcher weaponMatcher = weaponPattern.matcher(event.getLog());
			if (weaponMatcher.find()) {
				String weaponName = event.getLog().substring(weaponMatcher.end());
				event.setWeapon(new Weapon(weaponName.trim()));
			}
		}
		return event;
	}
	
	
	/**
	 * Formata e exibe o relatorio de final da partida (Ranking).
	 * @param matches - List contendo Match, representando as partidas intepretadas a partir dos arquivos de log.
	 */
	public void reportMatchRanking(List<Match> matches) {
		// Ranking
		for (Match match : matches) {
			System.out.printf("******************** GAME: %d%n", match.getId());
			System.out.printf("%-21s%20s%20s%20s%38s%n", "_Player_", "_Kills_", "_Deaths_", "_MostUsedWeapon_", "_Awards_");
			Map<Player, RankingEntry> ranking = new HashMap<Player, RankingEntry>();
			
			for (MatchEvent event : match.getEvents()) {

				if (!isEventMatchNotice(event)) {
					// Evita eventos do usuario <WORLD>
					if(!isEventFromWorld(event)) {
						// assassinatos
						if (ranking.containsKey(event.getAssassin())) {
							RankingEntry playerRanking = ranking.get(event.getAssassin());
							playerRanking.addKill(event.getTime());
							playerRanking.recordWeaponUsage(event.getWeapon());
							ranking.put(event.getAssassin(), playerRanking);
						} else {
							RankingEntry playerRanking = new RankingEntry(1, 0);
							playerRanking.recordWeaponUsage(event.getWeapon());
							ranking.put(event.getAssassin(), playerRanking);
						}
						
						// mortes sofridas
						if (ranking.containsKey(event.getVictim())) {
							RankingEntry playerRanking = ranking.get(event.getVictim());
							playerRanking.addDeath();
							ranking.put(event.getVictim(), playerRanking);
						} else {
							ranking.put(event.getVictim(), new RankingEntry(0, 1));
						}
					}
				}
			}
			
			for (Player player : ranking.keySet()) {
				
				// Premio por terminar partida sem morrer
				if (ranking.get(player).getDeaths() == 0) {
					ranking.get(player).addAward(AwardTypeEnum.IMMORTAL);
				}
				// Premio por matar 5 ou mais vezes em um minuto 
				if (ranking.get(player).getKillsInOneMinute() >= 5) {
					ranking.get(player).addAward(AwardTypeEnum.MOST_KILLS_IN_ONE_MINUTE);
				}
				
				System.out.printf("%-20s:%20d%20d%20s%38s%n", 
						player.getName(), 
						ranking.get(player).getKills(), 
						ranking.get(player).getDeaths(),
						ranking.get(player).getMostUsedWeapon() != null ?
								ranking.get(player).getMostUsedWeapon().getName()
								: "",
						ranking.get(player).getAwards()
				);
			}
		}
	}


	/**
	 * Identifica se evento representa uma mensagem da partida (ou seja, nao gerado pela acao 
	 * de um usuario).
	 * @param event
	 * @return
	 */
	private static boolean isEventMatchNotice(MatchEvent event) {
		return event.getAssassin() == null && event.getVictim() == null;
	}


	/**
	 * Identifica se evento foi realizado por um usuario comum ou pelo mundo/ambiente do jogo (WORLD).
	 * @param event - MatchEvent extraido do arquivo de log da partida.
	 * @return true se evento foi realizado pelo usuario WORLD; false caso contrario.
	 */
	private static boolean isEventFromWorld(MatchEvent event) {
		return event.getAssassin() != null && event.getAssassin().getName().equals(WORLD_PLAYER_NAME);
	}

	
	/**
	 * Cria um Match a partir de uma lista de MatchEvents.
	 * @param events List contendo MatchEvents extraidos do arquivo de log da partida.
	 * @return o Match criado.
	 */
	public Match create(List<MatchEvent> events) {
		Match match = new Match();
		match.setEvents(events);
		for (MatchEvent event : match.getEvents()) {
			Pattern matchIdPattern = Pattern.compile(MATCH_ID_REGEXP);
			// Inicio partida
			if(event.getLog().matches(MATCH_STARTED_REGEXP)) {
				Matcher matchIdMatcher = matchIdPattern.matcher(event.getLog());
				if(matchIdMatcher.find()) {
					match.setId(Integer.valueOf(matchIdMatcher.group()));	
				}
				match.setStart(event.getTime());
			}
			// Fim partida
			if(event.getLog().matches(MATCH_ENDED_REGEXP)) {
				match.setEnd(event.getTime());
			}
		}
		return match;
	}

}
