package io.core9.proxy;

import io.core9.firewall.rules.FirewallRequestRulesEngine;
import io.core9.firewall.rules.ProxyHasChangedException;
import io.core9.rules.RuleException;
import io.core9.rules.Status;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

import org.apache.log4j.Logger;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

@PluginImplementation
public class ProxyServerImpl implements ProxyServer {
	
	private static final Logger LOG = Logger.getLogger(ProxyServerImpl.class);
	private final Core9HostResolver resolver = new Core9HostResolver();
	private final Map<String, Proxy> hostnameProxies = new HashMap<>();
	
	@InjectPlugin
	private FirewallRequestRulesEngine rules;
	
	@Override
	public ProxyServer addProxy(Proxy proxy) {
		resolver.addHost(proxy);
		hostnameProxies.put(proxy.getHostname(), proxy);
		return this;
	}
	
	@Override
	public Collection<Proxy> getProxies() {
		return hostnameProxies.values();
	}
	
	@Override
	public Proxy getProxy(String hostname) {
		return hostnameProxies.get(hostname);
	}
	
	@Override
	public ProxyServer removeAllProxies() {
		resolver.removeAllHosts();
		return this;
	}

	@Override
	public ProxyServer start() {
		String port = System.getenv("PORT");
		port = System.getProperty("PORT", port);
		if(port == null) {
			port = "8080";
		}
		DefaultHttpProxyServer.bootstrap()
			.withPort(Integer.parseInt(port))
			.withAllowLocalOnly(false)
			.withListenOnAllAddresses(true)
			.withServerResolver(resolver)
			.withFiltersSource(new HttpFiltersSourceAdapter() {
				
				@Override
				public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext context) {
					
					return new HttpFiltersAdapter(originalRequest) {
						
						private ProxyRequest proxyRequest;
						
						@Override
						public HttpResponse requestPre(HttpObject httpObject) {
							try {
								HttpResponse response = null;
								if(httpObject instanceof HttpRequest) {
									proxyRequest = new ProxyRequest((HttpRequest) httpObject, context);
									proxyRequest.setProxy(hostnameProxies.get(((HttpRequest) httpObject).headers().get("Host")));
									response = handleRequestPre(proxyRequest); 
								}
								return response;
							} catch(NullPointerException e) {
								if(proxyRequest.getRequest() != null) {
									LOG.error("Not found: " + proxyRequest.getRequest().headers().get("Host") + proxyRequest.getRequest().getUri());
								} else {
									LOG.error(e);
								}
								return new DefaultFullHttpResponse(
										HttpVersion.HTTP_1_1, 
										HttpResponseStatus.NOT_FOUND);
							}
						}

						@Override
						public HttpResponse requestPost(HttpObject httpObject) {
							proxyRequest.setHttpObject(httpObject);
							setRequestHeaders(proxyRequest);
							return handleRequestPost(proxyRequest);
						}

						@Override
						public HttpObject responsePre(HttpObject httpObject) {
	                        return httpObject;
						}
						
						@Override
						public HttpObject responsePost(HttpObject httpObject) {
							proxyRequest.setHttpObject(httpObject);
							handleResponsePost(proxyRequest);
	                        return proxyRequest.getHttpObject();
						}
						
					};
				}
			}).start();
		return this;
	}
	
	private HttpResponse handleRequestPre(ProxyRequest request) throws NullPointerException {
		List<String> ruleSets = request.getProxy().getRuleSets().getPreRequest();
		if(ruleSets == null) {
			return null;
		} else {
			try {
				handleRuleResponse(request.getProxy(), rules.handle(ruleSets, request), request);
			} catch (ProxyHasChangedException e) {
				return handleRequestPre(request);
			} catch (RuleException e) {
				return new DefaultFullHttpResponse(
						HttpVersion.HTTP_1_1, 
						HttpResponseStatus.INTERNAL_SERVER_ERROR);
			}
		}
		HttpObject result = request.getHttpObject();
		if(result instanceof HttpResponse) {
			return (HttpResponse) result;
		} else {
			return null;
		}
	}
	
	private HttpResponse handleRequestPost(ProxyRequest request) {
		List<String> ruleSets = request.getProxy().getRuleSets().getPostRequest();
		if(ruleSets == null) {
			return null;
		} else {
			try {
				handleRuleResponse(request.getProxy(), rules.handle(ruleSets, request), request);
				return null;
			} catch (RuleException e) {
				e.printStackTrace();
				return new DefaultFullHttpResponse(
						HttpVersion.HTTP_1_1, 
						HttpResponseStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	private void handleResponsePost(ProxyRequest request) {
		List<String> ruleSets = request.getProxy().getRuleSets().getPostResponse();
		if(ruleSets == null) {
			return;
		} else {
			try {
				handleRuleResponse(request.getProxy(), rules.handle(ruleSets, request), request);
			} catch (RuleException e) {
				e.printStackTrace();
				request.setHttpObject(new DefaultFullHttpResponse(
						HttpVersion.HTTP_1_1, 
						HttpResponseStatus.INTERNAL_SERVER_ERROR));
			}
		}
	}
	
	private void setRequestHeaders(ProxyRequest request) {
		request.getRequest().headers().remove("Accept-Encoding");
		request.getRequest().headers().add("Accept-Encoding", "chunked");
		String virtualhost = request.getProxy().getVirtualHostname();
		if(virtualhost != null) {
			request.getRequest().headers().remove("Host");
			request.getRequest().headers().add("Host", virtualhost);
		}
	}
	
	private void handleRuleResponse(Proxy proxy, Status status, ProxyRequest request) throws RuleException {
		if(request.getProxy() != proxy) {
			throw new ProxyHasChangedException();
		}
		switch (status.getType()) {
		case ALLOW:
		case PROCESS:
			return;
		case DENY:
			request.setHttpObject(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
			return;
		case PROCESSED:
		case INITIALIZED:
			return;
		default:
			throw new RuleException();
		}
	}

}
