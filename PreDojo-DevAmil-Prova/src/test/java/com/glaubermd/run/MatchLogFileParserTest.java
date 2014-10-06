package com.glaubermd.run;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

import com.glaubermd.util.MatchLogFileParser;


public class MatchLogFileParserTest {

	private MatchLogFileParser parser = new MatchLogFileParser();
	
	@Test
	public void testGetFilesFromDir() {
		
		try {
			assertNotNull(parser.getFilesFromDir());
			assertFalse(parser.getFilesFromDir().isEmpty());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
