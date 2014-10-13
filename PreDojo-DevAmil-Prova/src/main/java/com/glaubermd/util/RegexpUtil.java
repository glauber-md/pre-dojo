package com.glaubermd.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;

public final class RegexpUtil {
	
	private static final String PIPE_STRING = "|";
	private static final String OPEN_PARENTHESIS_STRING = "(";
	private static final String CLOSE_PARENTHESIS_STRING = ")";
	private static final String PLUS_SIGNAL_STRING = "+";

	public static String getGroupSyntax(List<String> strings) {
		return new StringBuilder()
				.append(OPEN_PARENTHESIS_STRING)
				.append(StringUtils.join(strings, PIPE_STRING))
				.append(CLOSE_PARENTHESIS_STRING)
				.append(PLUS_SIGNAL_STRING)
				.toString();
	}
	
}
