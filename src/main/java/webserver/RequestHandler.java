package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequest;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);


    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (
        		InputStream in = connection.getInputStream();
        		OutputStream out = connection.getOutputStream();
        	) {
        	HttpRequest httpRequest = new HttpRequest(in);
        	if(httpRequest == null) return;
        	
        	Map<String, String> headers = httpRequest.getHeader();
        	Map<String, String> params = httpRequest.getParams();
        	Map<String, String> cookies = HttpRequestUtils.parseCookies(headers.get("cookies"));
        	DataOutputStream dos = new DataOutputStream(out);       		
    		Map<String, String> queryStringMap;
        	
        	boolean logined = false;

    		byte[] body = null;
        	if(headers.get("method").equals("POST")) {
        		if("/user/login".equals(httpRequest.getPath())) {
        			
            		String userId = params.get("userId");
            		String password = params.get("password");
            		User findUser = DataBase.findUserById(userId);
            		if(findUser != null && findUser.getPassword().equals(password)) {
            			responseHeader(dos, 302, new String[] {"Location: /index.html\r\n", "Set-Cookie: logined=true"});
            		} else {
            			body = HttpRequestUtils.readFile("/user/login_failed.html");
            			responseHeader(dos, 200, new String[] {"Set-Cookie: logined=false"});
            		}
        		} else if("/user/create".equals(headers.get("path"))) {
        			String data = IOUtils.readData(new BufferedReader(new InputStreamReader(in)), Integer.parseInt(headers.get("contentLength")));
//            		Map<String, String> params = HttpRequestUtils.parseQueryString(data);
            		String userId = params.get("userId");
            		String name = params.get("name");
            		String password = params.get("password");
            		String email = params.get("email");
            		User user = new User(userId, password, name, email);
            		
            		DataBase.addUser(user);
            		
        			responseHeader(dos, 302, new String[] {"Location: /index.html\r\n"});
        		}
        		
        	} else if(headers.get("method").equals("GET")) {
        		if(headers.get("path").startsWith("/create")) {
//        			String params = HttpRequestUtils.parseParams(headers.get("url"));
//        			queryStringMap = HttpRequestUtils.parseQueryString(params);
        			User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
        			log.debug("User : {}", user);
        			headers.put("path", "/index.html");
        			body = HttpRequestUtils.readFile(headers.get("path").contains(".html") ? headers.get("path") : headers.get("path")+".html");
        		} else if("/user/list".equals(headers.get("path"))) {
        			if(logined) {
        				Collection<User> users = DataBase.findAll();
        				StringBuilder sb = new StringBuilder();
        				sb.append("<table border='1'>");
        				for(User user : users) {
        					sb.append("<tr>");
        					sb.append("<td>" + user.getUserId() + "</td>");
        					sb.append("<td>" + user.getName() + "</td>");
        					sb.append("<td>" + user.getEmail() + "</td>");
        					sb.append("</tr>");
        				}
        				sb.append("</table>");
        				body = sb.toString().getBytes();
        			}
            	} else if(headers.get("path").endsWith(".css")) {
            		body = HttpRequestUtils.readFile(headers.get("path"));
            		responseCssHeader(dos, 200, new String[] {"Content-Length: " + body.length + "\r\n"});
            	} else {
            		body = HttpRequestUtils.readFile(headers.get("path"));
            		responseHeader(dos, 200, new String[] {"Content-Length: " + body.length + "\r\n"});
            	}
        	}
            
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private boolean isLogin(Map<String, String> cookieMap) {
    	String value = cookieMap.get("logined");
    	if(value == null) {
    		return false;
    	}
    	return Boolean.parseBoolean(value);
    }

    private void responseHeader(DataOutputStream dos, int status, String[] headerValues) {
    	try {
            dos.writeBytes("HTTP/1.1 " + status + " OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            if(headerValues == null) headerValues = new String[] {};
            for (String string : headerValues) {
				dos.writeBytes(string);
			}
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void responseCssHeader(DataOutputStream dos, int status, String[] headerValues) {
    	try {
            dos.writeBytes("HTTP/1.1 " + status + " OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            if(headerValues == null) headerValues = new String[] {};
            for (String string : headerValues) {
				dos.writeBytes(string);
			}
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
