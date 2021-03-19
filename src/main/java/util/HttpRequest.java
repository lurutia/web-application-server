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
	
	InputStream in;
	Map<String, String> map = new HashMap<String, String>();
	Map<String, String> params = new HashMap<String, String>();
	Map<String, String> cookies = new HashMap<String, String>();
	
	public HttpRequest(InputStream in) throws IOException {
		this.in = in;
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String line = br.readLine();
		if(line == null) return;
    	map.put("method", HttpRequestUtils.parseMethod(line));
    	map.put("url", HttpRequestUtils.getUrl(line));
    	map.put("path", HttpRequestUtils.parseRequestPath(map.get("url")));
    	
    	params.putAll(HttpRequestUtils.parseQueryString(HttpRequestUtils.parseParams(map.get("url"))));
    	
    	while(!"".equals(line)) {
    		if(line.contains("Content-Length")) {
    			map.put("contentLength", HttpRequestUtils.parseContentLength(line));
    		}
    		if(line.contains("Cookie")) {
    			String[] token = line.split(":");
    			map.put("cookies", token[1].trim());
    			cookies.putAll(HttpRequestUtils.parseCookies(token[1].trim()));
    		}
    		if(line.contains("Connection")) {
    			String[] token = line.split(":");
    			map.put("Connection", token[1].trim());
    		}
    		log.info(line);
    		line = br.readLine();
    	}
    	
    	String data = IOUtils.readData(br, getContentLength());
    	params.putAll(HttpRequestUtils.parseQueryString(data));
	}
	
	public Map<String, String> getCookies() {
		return cookies;
	}
	
	public Map<String, String> getHeader() {
		return map;
	}
	
	public Map<String ,String> getParams() {
		return params;
	}
	
	public String getParam(String key) {
		return params.get(key);
	}
	
	public int getContentLength() {
		String contentLength = map.get("contentLength");
		if(contentLength == null) return 0;
		return Integer.parseInt(contentLength);
	}
	
	public String getHeader(String key) {
		return map.get(key);
	}
	
	public String getUrl() {
		return map.get("url");
	}
	
	public String getMethod() {
		return map.get("method");
	}
	
	public String getPath() {
		return map.get("path");
	}
}
