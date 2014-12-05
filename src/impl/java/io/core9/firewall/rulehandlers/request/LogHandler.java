package io.core9.firewall.rulehandlers.request;

import org.apache.log4j.Logger;

import io.core9.proxy.ProxyRequest;
import io.core9.rules.Rule;
import io.core9.rules.Status;
import io.core9.rules.handlers.RuleHandler;

public class LogHandler implements RuleHandler<ProxyRequest, Status> {
	
	private static final Logger LOG = Logger.getLogger(LogHandler.class);

	@Override
	public Status handle(Rule rule, ProxyRequest context, Status status) {
		LOG.info("Received request: " + context.getProxy().getHostname() + context.getRequest().getUri() + " from " + context.getCtx().channel().remoteAddress());
		return status;
	}
	
}
