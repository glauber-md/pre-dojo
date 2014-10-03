package com.glaubermd.util;

import java.io.File;
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
import com.glaubermd.entity.Match;
import com.glaubermd.entity.MatchEvent;
import com.glaubermd.entity.Player;
import com.glaubermd.entity.Weapon;

public class MatchLogFileParser {

	private static final String USING_BY = "using|by";
	private static final String DEFAULT_FILE_ENCODING = "UTF-8";
	private static final String MATCH_STARTED_REGEXP = "New match [0-9]+ has started";
	private static final String MATCH_ID_REGEXP = "[0-9]+";
	private static final String MATCH_ENDED_REGEXP = "Match [0-9]+ has ended";
	private static final String MATCH_LOG_PATH = "/matches";
	private static final String VALID_LINE_REGEXP = "^[0-9]{2}\\/[0-9]{2}\\/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2} - .*$";
	private static final String LINE_TOKEN_SEPARATOR_REGEXP = " - ";
	private static final String[] VALID_FILE_EXTENSIONS = {"log"};
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static final String PLAYER_NAMES_REGEXP = "[a-zA-Z0-9_*/<>]+ killed [a-zA-Z0-9_*/<>]+ ";
	
	public Collection<File> getFilesFromDir() {
		File logDir = null;
		URL logDirUrl = this.getClass().getResource(MATCH_LOG_PATH);
		if(logDirUrl != null) {
			logDir = new File(logDirUrl.getPath());
		}
		// FIXME NPE below
		return FileUtils.listFiles(logDir, VALID_FILE_EXTENSIONS, false);
	}
		
	public List<MatchEvent> parseFile(File file) {
		List<MatchEvent> events = new ArrayList<MatchEvent>();
		try {
			List<String> lines = FileUtils.readLines(file, DEFAULT_FILE_ENCODING);
			for (String l : lines) {
				if(l.matches(VALID_LINE_REGEXP)) {
					events.add(getMatchEvent(l));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return events;
	}
	
	private MatchEvent getMatchEvent(String line) {
		MatchEvent event = null;
		// TODO use Matcher/Pattern
		String[] splitLogLine = line.split(LINE_TOKEN_SEPARATOR_REGEXP);
		event = new MatchEvent();
		
		try {
			event.setTime(sdf.parse(splitLogLine[0]));
			event.setLog(splitLogLine[1]);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!event.getLog().matches(MATCH_STARTED_REGEXP) 
				&& !event.getLog().matches(MATCH_ENDED_REGEXP)) {
			System.out.println(event.getLog());
			
			// acha jogadores
			Pattern playerPattern = Pattern.compile(PLAYER_NAMES_REGEXP);
			Matcher playerMatcher = playerPattern.matcher(event.getLog());
			if (playerMatcher.find()) {
				//TODO melhorar/+generico
				String[] players = playerMatcher.group(0).split(ActionEnum.KILL.getAction());
				event.setAssassin(new Player(players[0].trim()));
				event.setVictim(new Player(players[1].trim()));
			}
			
			// acha armas / eventos do mundo
			Pattern weaponPattern = Pattern.compile(USING_BY);
			Matcher weaponMatcher = weaponPattern.matcher(event.getLog());
			if (weaponMatcher.find()) {
				String weaponName = event.getLog().substring(weaponMatcher.end());
				event.setWeapon(new Weapon(weaponName.trim()));
			}
		}
		
		
		
		return event;
	}
	
	public static void main(String[] args) {
		Map<String,List<MatchEvent>> games = new HashMap<String,List<MatchEvent>>(); 
		MatchLogFileParser fp = new MatchLogFileParser();
		for (File logFile : fp.getFilesFromDir()) {
			games.put(logFile.getName(), fp.parseFile(logFile));
		}
		
		List<Match> matches = new ArrayList<Match>();
		for (String logfile : games.keySet()) {
			matches.add(fp.create(games.get(logfile)));
		}
		
		// Ranking
		for (Match match : matches) {
			System.out.println("GAME: " + match.getId());
			Map<Player, Integer> scoreKill = new HashMap<Player, Integer>();
			Map<Player, Integer> scoreKilled = new HashMap<Player, Integer>();
			for (MatchEvent event : match.getEvents()) {
				// TODO terminar
				if(event.getAssassin() != null && !event.getAssassin().getName().equals("<WORLD>")) {
					System.out.println("Nao sou WORLD");
				}
			}
		}
	}

	/**
	 * @param match
	 */
	private Match create(List<MatchEvent> events) {
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
