package util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequestLine {
	private static Logger log = LoggerFactory.getLogger(RequestLine.class);
	private String method;
	private String path;
	private String url;
	private Map<String, String> params = new HashMap<String, String>();
	
	public RequestLine(String line) {
		log.debug("request line : {}", line);
		String[] tokens = line.split(" ");
		validate(tokens);
		
		this.method = HttpRequestUtils.parseMethod(line);
		this.url = HttpRequestUtils.getUrl(line);
		this.path = HttpRequestUtils.parseRequestPath(this.url);
		this.params = HttpRequestUtils.parseQueryString(HttpRequestUtils.parseParams(getUrl()));
	}
	
	public String getMethod() {
		return this.method;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public Map<String, String> getParams() {
		return this.params;
	}
	
	private void validate(String[] tokens) {
		if(tokens.length != 3) {
			throw new IllegalArgumentException("요청이 형식에 맞지 않습니다.");
		}
	}
}
