package io.core9.proxy;

import io.core9.rules.RulesEngine;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import java.nio.charset.Charset;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

@PluginImplementation
public class ProxyServerImpl implements ProxyServer {
	
	private final Core9HostResolver resolver = new Core9HostResolver();
	
	@InjectPlugin
	private RulesEngine rules;
	
	@Override
	public ProxyServer addProxy(Proxy proxy) {
		resolver.addHost(proxy);
		return this;
	}

	@Override
	public ProxyServer start() {
		DefaultHttpProxyServer.bootstrap()
			.withPort(Integer.parseInt(System.getProperty("PORT", "80")))
			.withServerResolver(resolver)
			.withFiltersSource(new HttpFiltersSourceAdapter() {
				
				@Override
				public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
					
					return new HttpFiltersAdapter(originalRequest) {
						
						@Override
						public HttpResponse requestPre(HttpObject httpObject) {
							rules.
							System.out.println("REQ-PRE : " + httpObject.getClass().getName());
							if(httpObject instanceof HttpRequest) {
								HttpRequest request = (HttpRequest) httpObject;
								request.headers().remove("Accept-Encoding");
								request.headers().add("Accept-Encoding", "chunked");
							}
							return null;
						}

						@Override
						public HttpResponse requestPost(HttpObject httpObject) {
							return null;
						}

						@Override
						public HttpObject responsePre(HttpObject httpObject) {
							return httpObject;
						}

						@Override
						public HttpObject responsePost(HttpObject httpObject) {
							if (httpObject instanceof HttpContent) {
								System.out.println(((HttpContent) httpObject).content().toString(Charset.forName("UTF-8")));
							}
	                        return httpObject;
						}
						
					};
				}
			}).start();
		return this;
	}

}
