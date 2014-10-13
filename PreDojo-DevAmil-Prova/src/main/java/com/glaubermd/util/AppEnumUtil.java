package com.glaubermd.util;

import java.util.ArrayList;
import java.util.List;

import com.glaubermd.entity.ActionEnum;

public final class AppEnumUtil {

	public static List<String> getEnumValues(ActionEnum ... e ) {
		List<String> actions = new ArrayList<String>();
		if(e != null) {
			for (ActionEnum action : ActionEnum.values()) {
				actions.add(action.getActionDescription());
			}
		}
		return actions;
	}
	
}
