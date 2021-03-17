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
		
		assertEquals("/index.html", HttpRequestUtils.parseLocation(request));
		assertEquals("/index.html", HttpRequestUtils.parseLocation(request2));
	}

}
