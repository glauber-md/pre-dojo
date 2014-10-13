package com.glaubermd.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
import com.glaubermd.util.AppEnumUtil;
import com.glaubermd.util.RegexpUtil;

/**
 * Implementa funcoes de interpretacao de arquivos de log.
 */
public final class MatchLogFileParser {

	private static final String SPACES_REGEXP = "\\s+";
	private static final String USING_BY_REGEXP = "using|by";
	private static final String MATCH_STARTED_REGEXP = "New match [\\d]+ has started";
	private static final String MATCH_ID_REGEXP = "[\\d]+";
	private static final String MATCH_ENDED_REGEXP = "Match [\\d]+ has ended";
	private static final String VALID_LINE_REGEXP = "^[\\d]{2}\\/[\\d]{2}\\/[\\d]{4} [\\d]{2}:[\\d]{2}:[\\d]{2} - .*$";
	private static final String LINE_TOKEN_SEPARATOR_REGEXP = " - ";
	private static final String PLAYER_NAME_REGEXP = "[\\w\\d_*/|<>]+";
	private static final String PLAYER_NAME_EXTRACTION_REGEXP;
	private static final String ACTIONS_REGEXP;
	private static final String REPORT_LINE_FORMAT = "%-20s:%20d%20d%20s%20s%38s%n";
	private static final String REPORT_HEADER_FORMAT = "%-21s%20s%20s%20s%20s%38s%n";
	private static final String WORLD_PLAYER_NAME = "<WORLD>";
	private static final String EMPTY_STRING = "";
	private static final String DEFAULT_FILE_ENCODING = "UTF-8";
	private static final String MATCH_LOG_PATH = "/matches";
	private static final String[] VALID_FILE_EXTENSIONS = {"log"};
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	static {
		// Gera pattern para busca por nome de jogadores para cada acao possivel
		ACTIONS_REGEXP = RegexpUtil.getGroupSyntax(AppEnumUtil.getEnumValues(ActionEnum.values()));
		PLAYER_NAME_EXTRACTION_REGEXP = new StringBuilder()
		.append(PLAYER_NAME_REGEXP)
		.append(SPACES_REGEXP)
		.append(ACTIONS_REGEXP)
		.append(SPACES_REGEXP)
		.append(PLAYER_NAME_REGEXP)
		.toString();
	}
	
	
	/**
	 * Obtem arquivos com logs das partidas finalizadas.
	 * @return Collecton contendo Files representando os logs de partidas.
	 * @throws FileNotFoundException 
	 */
	public Collection<File> getFilesFromDir(String dirPath) throws FileNotFoundException {
		File logDir = null;
		if(dirPath != null) {
			// Usa caminho fora da aplicacao
			logDir = new File(dirPath);
		} else {
			// Usa caminho dentro do classpath da aplicacao
			dirPath = MATCH_LOG_PATH;
			URL logDirUrl = this.getClass().getResource(dirPath);
			if(logDirUrl != null) {
				logDir = new File(logDirUrl.getPath());
			}
		}
		if (logDir == null)
			throw new FileNotFoundException("Directory for matches logs not found: " + dirPath);
		
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
			System.err.println("Erro ao ler arquivo: " + e.getMessage());
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
			System.err.println("Formato de data/hora para evento do jogo nao reconhecido: " + e.getMessage());
		}
		
		if(!event.getLog().matches(MATCH_STARTED_REGEXP) 
				&& !event.getLog().matches(MATCH_ENDED_REGEXP)) {
			
			// Acha acoes do jogador
			Pattern actionPattern = Pattern.compile(ACTIONS_REGEXP);
			Matcher actionMatcher = actionPattern.matcher(event.getLog());
			if (actionMatcher.find()) {
				event.setAction(actionMatcher.group(0));
			}
			
			// Acha jogadores (assassino/vitima) no evento
			Pattern playerPattern = Pattern.compile(PLAYER_NAME_EXTRACTION_REGEXP);
			Matcher playerMatcher = playerPattern.matcher(event.getLog());
			if (playerMatcher.find()) {
				// Acao considerada no placar eh apenas de assassinatos
				if(event.getAction() != null && event.getAction().equals(ActionEnum.KILL)) {
					String[] players = playerMatcher.group(0).split(event.getAction().getActionDescription());
					event.setAssassin(new Player(players[0].trim()));
					event.setVictim(new Player(players[1].trim()));						
				}
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
	public void printMatchRanking(List<Match> matches) {
		// Ranking
		for (Match match : matches) {
			System.out.printf("******************** MATCH: %d ********************%n", match.getId());
			System.out.printf(REPORT_HEADER_FORMAT, "-Player-", "-Kills-", "-Deaths-", "-MostUsedWeapon-", "-KillStreak-", "-Awards-");
			List<Player> ranking = new ArrayList<Player>();
			
			for (MatchEvent event : match.getEvents()) {

				if (!isEventMatchNotice(event)) {
					// Evita eventos do usuario <WORLD>
					if(!isEventFromWorld(event)) {
						// assassinatos
						if (ranking.contains(event.getAssassin())) {
							Player assassin = ranking.get(ranking.indexOf(event.getAssassin()));
							assassin.getRanking().addKill(event.getTime());
							assassin.getRanking().recordWeaponUsage(event.getWeapon());
							ranking.set(ranking.indexOf(assassin), assassin);
						} else {
							RankingEntry playerRanking = new RankingEntry(1, 0);
							playerRanking.recordWeaponUsage(event.getWeapon());
							event.getAssassin().setRanking(playerRanking);
							ranking.add(event.getAssassin());
						}
						
						// mortes sofridas
						if (ranking.contains(event.getVictim())) {
							Player victim = ranking.get(ranking.indexOf(event.getVictim()));
							victim.getRanking().addDeath();
							ranking.set(ranking.indexOf(victim), victim);
						} else {
							event.getVictim().setRanking(new RankingEntry(0, 1));
							ranking.add(event.getVictim());
						}
					}
				}
			}
			
			// Ordena o placar
			Collections.sort(ranking);
			Collections.reverse(ranking);
			
			for (Player player : ranking) {
				computeAwards(player);
				System.out.printf(REPORT_LINE_FORMAT, 
						player.getName(), 
						player.getRanking().getKills(), 
						player.getRanking().getDeaths(),
						player.getRanking().getMostUsedWeapon() != null ?
								player.getRanking().getMostUsedWeapon().getName()
								: EMPTY_STRING,
						player.getRanking().getKillStreak(),
						player.getRanking().getAwards()
				);
			}
		}
	}


	/**
	 * Calcula os premios recebidos pelo jogador na partida.
	 * @param player - o jogador a ter o premio calculado.
	 */
	private void computeAwards(Player player) {
		// Premio por terminar partida sem morrer
		if (player.getRanking().getDeaths() == 0) {
			player.getRanking().addAward(AwardTypeEnum.IMMORTAL);
		}
		// Premio por matar 5 ou mais vezes em um minuto 
		if (player.getRanking().getKillsInOneMinute() >= 5) {
			player.getRanking().addAward(AwardTypeEnum.MOST_KILLS_IN_ONE_MINUTE);
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
