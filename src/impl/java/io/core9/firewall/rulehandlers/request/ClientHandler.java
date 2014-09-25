package io.core9.firewall.rulehandlers.request;

import io.core9.proxy.ProxyRequest;
import io.core9.rules.Client;
import io.core9.rules.Modifier;
import io.core9.rules.Rule;
import io.core9.rules.Status;
import io.core9.rules.Status.Type;
import io.core9.rules.handlers.RuleHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ClientHandler implements RuleHandler<ProxyRequest, Status> {

	@Override
	public Status handle(Rule rule, ProxyRequest request, Status status) {
		Status clientStatus = new Status(Type.PROCESS);
		if(rule.getClients() == null) {
			return clientStatus;
		} else {
			InetSocketAddress socketAddress = (InetSocketAddress) request.getCtx().channel().remoteAddress();
		    InetAddress inetaddress = socketAddress.getAddress();
			String requester = inetaddress.getHostAddress();
			for(Client client : rule.getClients()) {
				if(requester.equals(client.getIp())) {
					if(client.getModifier() == Modifier.NOT) {
						return clientStatus.setType(Type.DENY);
					} else {
						return clientStatus.setType(Type.ALLOW);
					}
				}
			}
			return clientStatus.setType(Type.PROCESS);
		}
	}
}
