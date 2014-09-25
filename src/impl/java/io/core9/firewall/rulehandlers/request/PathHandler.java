package io.core9.firewall.rulehandlers.request;

import io.core9.rules.Rule;

public class PathHandler {

	public static boolean matches(Rule rule, String path) {
		if (rule.getStartsWith() != null) {
			for (String prefix : rule.getStartsWith()) {
				if (path.startsWith(prefix)) {
					return true;
				}
			}
		}
		if (rule.getExact() != null) {
			for (String exactPath : rule.getExact()) {
				if (path.equals(exactPath)) {
					return true;
				}
			}
		}
		if (rule.getEndsWith() != null) {
			for (String suffix : rule.getEndsWith()) {
				if (path.endsWith(suffix)) {
					return true;
				}
			}
		}
		return false;
	}

}
