package io.core9.firewall.rules;

import io.core9.core.plugin.Core9Plugin;
import io.core9.plugin.server.VirtualHost;
import io.core9.proxy.ProxyRequest;
import io.core9.rules.RuleException;
import io.core9.rules.RuleSet;
import io.core9.rules.RulesEngine;
import io.core9.rules.Status;

import java.util.List;
import java.util.Map;

public interface FirewallRequestRulesEngine extends RulesEngine<ProxyRequest, Status>, Core9Plugin {

	Status handle(List<String> ruleSets, ProxyRequest context) throws RuleException;
	
	Map<String, RuleSet> getRuleSets();

	void setVirtualHost(VirtualHost vhost);

}
