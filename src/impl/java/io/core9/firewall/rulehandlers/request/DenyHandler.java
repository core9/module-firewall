package io.core9.firewall.rulehandlers.request;

import org.apache.log4j.Logger;

import io.core9.proxy.ProxyRequest;
import io.core9.rules.Rule;
import io.core9.rules.Status;
import io.core9.rules.Status.Type;

public class DenyHandler extends ClientHandler {
	
	private static final Logger LOG = Logger.getLogger(DenyHandler.class);

	
	@Override
	public Status handle(Rule rule, ProxyRequest request, Status status) {
		Status clientStatus = super.handle(rule, request, status);
		if(clientStatus.getType() == Type.DENY) {
			return status.setType(Type.PROCESS);
		} else {
			if(PathHandler.matches(rule, request.getRequest().getUri())) {
				LOG.warn("Denied request: " + request.getProxy().getHostname() + request.getRequest().getUri() + " from " + request.getCtx().channel().remoteAddress());
				return status.setType(Type.DENY);
			}
		}
		return status.setType(Type.PROCESS);
	}

}
