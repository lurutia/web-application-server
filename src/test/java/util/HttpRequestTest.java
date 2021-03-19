package util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;

public class HttpRequestTest {
	private String testDirectory = "./src/test/resources/";

	@Test
	public void request_GET() throws Exception {
		InputStream in = getInputStream("Http_GET.txt");
		HttpRequest request = new HttpRequest(in);
		
		assertEquals("GET", request.getMethod());
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("gskill", request.getParam("userId"));
	}

	@Test
	public void request_POST() throws Exception {
		InputStream in = getInputStream("Http_POST.txt");
		HttpRequest request = new HttpRequest(in);
		
		assertEquals("POST", request.getMethod());
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("gskill", request.getParam("userId"));
	}
	
	private InputStream getInputStream(String file) throws FileNotFoundException {
		InputStream in = new FileInputStream(new File(testDirectory + file));
		return in;
	}
}
