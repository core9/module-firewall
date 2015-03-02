package io.core9.proxy;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.LastHttpContent;

import org.littleshoot.proxy.HostResolver;

public class ContextualObjectAggregator extends HttpObjectAggregator {
	
	public final String CONTENT_LENGTH_HEADER = "Content-Length";
	
	private final Core9HostResolver resolver;
	private Proxy proxy;
	private HttpMessage headers;
	private int maxContentLength;

	public ContextualObjectAggregator(int maxContentLength, HostResolver hostResolver) {
		super(maxContentLength);
		this.maxContentLength = maxContentLength;
		this.resolver = (Core9HostResolver) hostResolver;
	}
	
	@Override
	public boolean acceptInboundMessage(Object msg) throws Exception {
		if (msg instanceof HttpMessage) {
			this.headers = (HttpMessage) msg;
			assert proxy == null;
			String length = this.headers.headers().get(CONTENT_LENGTH_HEADER);
			if(length != null) {
				if(Long.parseLong(length) > this.maxContentLength) {
					this.headers = null;
					return false;
				}
			}
			this.proxy = resolver.getProxySpecification(((HttpMessage) msg).headers().get("Host"));
			return this.proxy == null ? false : this.proxy.needsFullHttpObject();
		} else if(msg instanceof HttpContent) {
			assert proxy != null;
			boolean headerAvailable = this.headers != null;
			if(msg instanceof LastHttpContent) {
				this.headers = null;
			}
			return this.proxy == null || !headerAvailable ? false : this.proxy.needsFullHttpObject();
		} else {
			throw new Error();
		}
	}
}
