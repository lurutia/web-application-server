package util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webserver.WebServer;

public class HttpRequestParseTest {
	private static final Logger log = LoggerFactory.getLogger(WebServer.class);
	
	@Test
	public void requestParse() {
		String request = "GET /index.html HTTP/1.1";
		String request2 = "GET / HTTP/1.1";
		
		assertEquals("/index.html", HttpRequestUtils.getUrl(request));
		assertEquals("/index.html", HttpRequestUtils.getUrl(request2));
	}
	
	@Test
	public void requestParsePathAndQueryString() {
		String request = "GET /user/create?userId=gosuljo&password=password&name=Gosuljo HTTP1.1";
		String url = HttpRequestUtils.getUrl(request);
		int index = url.indexOf('?');
		String requestPath = url.substring(0, index);
		String params = url.substring(index+1);
		
		assertEquals("/user/create", requestPath);
		assertEquals("userId=gosuljo&password=password&name=Gosuljo", params);
	}
	
	@Test
	public void parseMethod() {
		String request = "GET /index.html HTTP/1.1";
		String request2 = "POST / HTTP/1.1";
		
		assertEquals("GET", HttpRequestUtils.parseMethod(request));
		assertEquals("POST", HttpRequestUtils.parseMethod(request2));
	}

	@Test
	public void parsePath() {
		String request = "/user/create?userId=gosuljo&password=password&name=Gosuljo";
		String path = HttpRequestUtils.parseRequestPath(request);
		assertEquals("/user/create", path);
	}
	
	@Test
	public void parsePathNone물음표() {
		String request = "/user/create";
		String path = HttpRequestUtils.parseRequestPath(request);
		assertEquals("/user/create", path);
	}
	
	@Test
	public void parseQueryString() {
		String request = "/user/create?userId=gosuljo&password=password&name=Gosuljo";
		String params = HttpRequestUtils.parseParams(request);
		assertEquals("userId=gosuljo&password=password&name=Gosuljo", params);
	}
	
	@Test
	public void parseQueryStringNone물음표() {
		String request = "/user/create";
		String params = HttpRequestUtils.parseParams(request);
		assertEquals("", params);
	}
	
}
