package com.glaubermd.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.glaubermd.entity.Match;
import com.glaubermd.entity.MatchEvent;
import com.glaubermd.util.MatchLogFileParser;

public class MatchLogReaderRunner {

	public static void main(String[] args) {
		
		// Le arquivos de log de partidas
		Map<String,List<MatchEvent>> matchLogFiles = new HashMap<String,List<MatchEvent>>(); 
		MatchLogFileParser fp = new MatchLogFileParser();
		try {
			for (File logFile : fp.getFilesFromDir()) {
				matchLogFiles.put(logFile.getName(), fp.parseFile(logFile));
			}

			// Cria partidas a partir dos logs
			List<Match> matches = new ArrayList<Match>();
			for (String logFileId : matchLogFiles.keySet()) {
				matches.add(fp.create(matchLogFiles.get(logFileId)));
			}

			// Exibe ranking das partidas
			fp.printMatchRanking(matches);
			
		} catch (FileNotFoundException e) {
			System.err.println("Arquivo nao encontrado: " + e.getMessage());
		}
	}

}
