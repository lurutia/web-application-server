package util;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class RequestLineTest {

	@Test
	public void create_method() {
		RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");
		assertEquals("GET", requestLine.getMethod());
		assertEquals("/index.html", requestLine.getPath());
		assertEquals("GET", requestLine.getMethod());
		
		requestLine = new RequestLine("POST /index.html HTTP/1.1");
		assertEquals("POST", requestLine.getMethod());
		assertEquals("/index.html", requestLine.getPath());
	}
	
	@Test
	public void create_path_and_params() {
		RequestLine requestLine = new RequestLine("GET /user/create?userId=gskill&password=1234 HTTP/1.1");
		assertEquals("GET", requestLine.getMethod());
		assertEquals("/user/create", requestLine.getPath());
		Map<String, String> params = requestLine.getParams();
		assertEquals(2, params.size());
	}

}
