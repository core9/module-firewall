package io.core9.proxy;

import io.core9.plugin.database.repository.AbstractCrudEntity;
import io.core9.plugin.database.repository.Collection;
import io.core9.plugin.database.repository.CrudEntity;

@Collection("core.proxies")
public class Proxy extends AbstractCrudEntity implements CrudEntity {

	private String hostname;
	private String virtualHostname;
	private String origin;
	private RuleSets ruleSets;

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getVirtualHostname() {
		return virtualHostname;
	}

	public void setVirtualHostname(String virtualHostname) {
		this.virtualHostname = virtualHostname;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	public RuleSets getRuleSets() {
		return this.ruleSets;
	}

	public void setRuleSets(RuleSets ruleSets) {
		this.ruleSets = ruleSets;
	}
}
