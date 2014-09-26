package io.core9.proxy;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;

import org.littleshoot.proxy.HostResolver;

public class Core9HostResolver implements HostResolver {
	
	private final Map<String,Proxy> HOSTS = new TreeMap<String,Proxy>();

	@Override
	public InetSocketAddress resolve(String host, int port) throws UnknownHostException {
		if(HOSTS.containsKey(host)) {
			String origin = HOSTS.get(host).getOrigin();
			if(origin.contains(":")) {
				String [] temp = origin.split(":");
				origin = temp[0];
				port = Integer.parseInt(temp[1]);
			}
			return new InetSocketAddress(origin, port);
		} else {
			return null;
		}
	}
	
	public Core9HostResolver addHost(Proxy proxy) {
		HOSTS.put(proxy.getHostname(), proxy);
		return this;
	}
	
	public Core9HostResolver removeHost(String address) {
		HOSTS.remove(address);
		return this;
	}
	
	public Proxy getProxySpecification(String hostname) {
		return HOSTS.get(hostname);
	}
	
	public Core9HostResolver removeAllHosts() {
		HOSTS.clear();
		return this;
	}

}
