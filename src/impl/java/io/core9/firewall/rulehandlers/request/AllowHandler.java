package io.core9.firewall.rulehandlers.request;

import io.core9.proxy.ProxyRequest;
import io.core9.rules.Rule;
import io.core9.rules.Status;
import io.core9.rules.Status.Type;
import io.netty.handler.codec.http.HttpRequest;

public class AllowHandler extends ClientHandler {
	
	@Override
	public Status handle(Rule rule, ProxyRequest request, Status status) {
		if(request instanceof HttpRequest) {
			Status clientStatus = super.handle(rule, request, status);
			if(clientStatus.getType() == Type.PROCESS) {
				if(PathHandler.matches(rule, request.getRequest().getUri())) {
					return clientStatus.setType(Type.ALLOW);
				}
			}
			return clientStatus.setType(Type.PROCESS);
		}
		return status.setType(Type.PROCESS);
	}

}
