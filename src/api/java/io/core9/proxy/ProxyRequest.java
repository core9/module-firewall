package io.core9.proxy;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

public class ProxyRequest {

	private HttpRequest request;
	private ChannelHandlerContext ctx;
	private HttpObject object;
	private Proxy proxy;
	private Map<String, Object> context;

	public HttpRequest getRequest() {
		return request;
	}

	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	public HttpObject getHttpObject() {
		return object;
	}

	public void setHttpObject(HttpObject object) {
		this.object = object;
	}
	
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}
	
	public Proxy getProxy() {
		return this.proxy;
	}

	public void putContext(String name, Object value) {
		if(context == null) {
			context = new HashMap<String, Object>();
		}
		context.put(name, value);
	}
	
	public void removeContext(String name) {
		if(context == null) {
			return;
		}
		context.remove(name);
	}
	
	public Object getContext(String name) {
		if(context == null) {
			return null;
		}
		return context.get(name);
	}
	
	public ProxyRequest(HttpRequest req, ChannelHandlerContext ctx) {
		this.request = req;
		this.ctx = ctx;
	}
}
