package io.core9.proxy;

import io.core9.core.plugin.Core9Plugin;

public interface ProxyServer extends Core9Plugin {

	ProxyServer addProxy(Proxy proxy);
	
	ProxyServer start();

	ProxyServer removeAllProxies();
	
}
