package com.glaubermd.run;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;

import org.junit.Test;

import com.glaubermd.core.MatchLogFileParser;


public class MatchLogFileParserTest {

	private static final String MATCH_LOG_DIR = "/tmp/teste/matches";
	private MatchLogFileParser parser = new MatchLogFileParser();
	
	@Test
	public void testGetFilesFromDir() {
		
		try {
			assertNotNull(parser.getFilesFromDir(MATCH_LOG_DIR));
			assertFalse(parser.getFilesFromDir(MATCH_LOG_DIR).isEmpty());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
