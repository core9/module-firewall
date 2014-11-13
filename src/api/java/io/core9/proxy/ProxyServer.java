package io.core9.proxy;

import java.util.Collection;

import io.core9.core.plugin.Core9Plugin;

public interface ProxyServer extends Core9Plugin {

	ProxyServer addProxy(Proxy proxy);
	
	ProxyServer start();

	ProxyServer removeAllProxies();

	Collection<Proxy> getProxies();

	Proxy getProxy(String hostname);
	
}
