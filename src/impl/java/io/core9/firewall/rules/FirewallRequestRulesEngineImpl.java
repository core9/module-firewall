package io.core9.firewall.rules;

import io.core9.firewall.rulehandlers.request.AllowHandler;
import io.core9.firewall.rulehandlers.request.DenyHandler;
import io.core9.plugin.database.repository.CrudRepository;
import io.core9.plugin.database.repository.NoCollectionNamePresentException;
import io.core9.plugin.database.repository.RepositoryFactory;
import io.core9.plugin.server.VirtualHost;
import io.core9.proxy.ProxyRequest;
import io.core9.rules.AbstractRulesEngine;
import io.core9.rules.Result;
import io.core9.rules.RuleException;
import io.core9.rules.RuleSet;
import io.core9.rules.Status;
import io.core9.rules.Status.Type;

import java.util.List;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.PluginLoaded;

@PluginImplementation
public class FirewallRequestRulesEngineImpl extends AbstractRulesEngine<ProxyRequest, Status> implements FirewallRequestRulesEngine {
	
	private CrudRepository<RuleSet> ruleSetRepository;
	private VirtualHost vhost;
	
	@PluginLoaded
	public void onRepositoryFactory(RepositoryFactory factory) throws NoCollectionNamePresentException {
		ruleSetRepository = factory.getRepository(RuleSet.class);
	}

	@Override
	public void addVirtualHost(VirtualHost vhost) {
		this.vhost = vhost;
		ruleSetRepository.getAll(vhost).forEach(ruleSet -> {
			this.addRuleSet(vhost, ruleSet);
		});
	}

	@Override
	public void removeVirtualHost(VirtualHost vhost) {
		this.removeVirtualHost(vhost);
	}

	@Override
	public Result handleRuleResult(Status status) {
		switch(status.getType()) {
			case ALLOW:
			case DENY:
			case PROCESSED:
			case INITIALIZED:
				return Result.STOP;
			case PROCESS:
				return Result.CONTINUE;
			case JUMP:
			default:
				throw new UnsupportedOperationException("Not yet implemented");
		}
	}
	
	@Override
	public Status handle(List<String> ruleSets, ProxyRequest request) throws RuleException {
		return this.handle(vhost, ruleSets, request, new Status(Type.INITIALIZED));
	}
	
	public FirewallRequestRulesEngineImpl() {
		this.addRuleHandler("ALLOW", new AllowHandler());
		this.addRuleHandler("DENY", new DenyHandler());
	}
}