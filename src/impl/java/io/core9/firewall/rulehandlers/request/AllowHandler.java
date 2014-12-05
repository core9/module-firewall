package io.core9.firewall.rulehandlers.request;

import io.core9.proxy.ProxyRequest;
import io.core9.rules.Rule;
import io.core9.rules.Status;
import io.core9.rules.Status.Type;
import io.netty.handler.codec.http.HttpRequest;

import org.apache.log4j.Logger;

public class AllowHandler extends ClientHandler {
	
	private static final Logger LOG = Logger.getLogger(AllowHandler.class);
	
	@Override
	public Status handle(Rule rule, ProxyRequest proxyRequest, Status status) {
		HttpRequest request = proxyRequest.getRequest(); 
		Status clientStatus = super.handle(rule, proxyRequest, status);
		if(clientStatus.getType() == Type.PROCESS) {
			if(PathHandler.matches(rule, request.getUri())) {
				LOG.info("Allowed request: " + proxyRequest.getProxy().getHostname() + request.getUri() + " from " + proxyRequest.getCtx().channel().remoteAddress());
				return clientStatus.setType(Type.ALLOW);
			}
		}
		return clientStatus.setType(Type.PROCESS);
	}

}
