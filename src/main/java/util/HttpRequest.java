package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpRequest {
	private static Logger log = LoggerFactory.getLogger(HttpRequest.class);
	
//	InputStream in;
	Map<String, String> headers = new HashMap<String, String>();
	Map<String, String> params = new HashMap<String, String>();
	Map<String, String> cookies = new HashMap<String, String>();
	RequestLine requestLine;
	
	public HttpRequest(InputStream in) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = br.readLine();
			if(line == null) {
				return;
			}
			
			requestLine = new RequestLine(line);

			while(!"".equals(line)) {
	    		log.info(line);
	    		line = br.readLine();
	    		if(line.contains("Content-Length")) {
	    			headers.put("contentLength", HttpRequestUtils.parseContentLength(line));
	    		}
	    		if(line.contains("Cookie")) {
	    			String[] token = line.split(":");
	    			headers.put("cookies", token[1].trim());
	    			cookies.putAll(HttpRequestUtils.parseCookies(token[1].trim()));
	    		}
	    		if(line.contains("Connection")) {
	    			String[] token = line.split(":");
	    			headers.put("Connection", token[1].trim());
	    		}
	    	}
	    	
			HttpMethod httpMethod = HttpMethod.valueOf(getMethod());
	    	if(httpMethod.isPost()) {
	    		String data = IOUtils.readData(br, getContentLength());
	    		params.putAll(HttpRequestUtils.parseQueryString(data));
	    	} else {
	    		params.putAll(requestLine.getParams());
	    	}
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}
	
	public Map<String, String> getCookies() {
		return cookies;
	}
	
	public Map<String, String> getHeader() {
		return headers;
	}
	
	public Map<String ,String> getParams() {
		return params;
	}
	
	public String getParam(String key) {
		return params.get(key);
	}
	
	public int getContentLength() {
		String contentLength = headers.get("contentLength");
		if(contentLength == null) return 0;
		return Integer.parseInt(contentLength);
	}
	
	public String getHeader(String key) {
		return headers.get(key);
	}
	
	public String getUrl() {
		return requestLine.getUrl();
	}
	
	public String getMethod() {
		return requestLine.getMethod();
	}
	
	public String getPath() {
		return requestLine.getPath();
	}
}
