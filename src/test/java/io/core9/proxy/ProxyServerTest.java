package io.core9.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;

public class ProxyServerTest {

	private ProxyServer server;

	@Before
	public void setup() {
		Proxy proxy = new Proxy();
		proxy.setHostname("www.ocon.nl");
		proxy.setOrigin("87.249.118.71");
		server = new ProxyServerImpl();
		server.addProxy(proxy).start();
		System.out.println("Server started");
	}

	@Test
	public void testRequest() throws ClientProtocolException, IOException {
		final DefaultHttpClient http = new DefaultHttpClient();
		final HttpHost proxy = new HttpHost("127.0.0.1",
				Integer.parseInt(System.getProperty("PORT", "80")), "http");
		http.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		final HttpGet get = new HttpGet("http://www.ocon.nl/");
		get.setHeader("Accept-Encoding", "gzip,deflate,sdch");
		final org.apache.http.HttpResponse hr = http.execute(get);
		HttpEntity entity = hr.getEntity();
		String responseString = EntityUtils.toString(entity, "UTF-8");
		System.out.println(responseString);

	}

	public static String slurp(final InputStream is, final int bufferSize) {
		final char[] buffer = new char[bufferSize];
		final StringBuilder out = new StringBuilder();
		try {
			final Reader in = new InputStreamReader(is, "UTF-8");
			try {
				for (;;) {
					int rsz = in.read(buffer, 0, buffer.length);
					if (rsz < 0)
						break;
					out.append(buffer, 0, rsz);
				}
			} finally {
				in.close();
			}
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return out.toString();
	}

}
