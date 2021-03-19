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
        		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        	) {
        	String line = br.readLine();
        	if(line == null) {
        		return;
        	}
        	
        	DataOutputStream dos = new DataOutputStream(out);       		
            
        	
        	String method = HttpRequestUtils.parseMethod(line);
        	String url = HttpRequestUtils.getUrl(line);
    		Map<String, String> queryStringMap;
    		Map<String, String> cookieMap = null;
        	int contentLength = 0;
        	boolean logined = false;
        	
        	while(!"".equals(line)) {
        		log.info(line);
        		line = br.readLine();
        		if(line.contains("Content-Length")) {
        			contentLength = HttpRequestUtils.parseContentLength(line);
        		}
        		if(line.contains("Cookie")) {
        			String[] token = line.split(":");
        			cookieMap = HttpRequestUtils.parseCookies(token[1].trim());
        			logined = isLogin(cookieMap);
        			log.debug("로그인여부" + logined + ", " + cookieMap.get("logined"));
        		}
        	}

        	String requestPath = HttpRequestUtils.parseRequestPath(url);
    		byte[] body = null;
        	if(method.equals("POST")) {
        		if("/user/login".equals(requestPath)) {
        			String data = IOUtils.readData(br, contentLength);
            		Map<String, String> params = HttpRequestUtils.parseQueryString(data);
            		String userId = params.get("userId");
            		String password = params.get("password");
            		User findUser = DataBase.findUserById(userId);
            		if(findUser != null && findUser.getPassword().equals(password)) {
            			responseHeader(dos, 302, new String[] {"Location: /index.html\r\n", "Set-Cookie: logined=true"});
            		} else {
            			body = HttpRequestUtils.readFile("/user/login_failed.html");
            			responseHeader(dos, 200, new String[] {"Set-Cookie: logined=false"});
            		}
        		} else if("/user/create".equals(requestPath)) {
        			String data = IOUtils.readData(br, contentLength);
            		Map<String, String> params = HttpRequestUtils.parseQueryString(data);
            		String userId = params.get("userId");
            		String name = params.get("name");
            		String password = params.get("password");
            		String email = params.get("email");
            		User user = new User(userId, password, name, email);
            		
            		DataBase.addUser(user);
            		
        			responseHeader(dos, 302, new String[] {"Location: /index.html\r\n"});
        		}
        		
        	} else if(method.equals("GET")) {
        		if(url.startsWith("/create")) {
        			String params = HttpRequestUtils.parseParams(url);
        			queryStringMap = HttpRequestUtils.parseQueryString(params);
        			User user = new User(queryStringMap.get("userId"), queryStringMap.get("password"), queryStringMap.get("name"), queryStringMap.get("email"));
        			log.debug("User : {}", user);
        			requestPath = "/index.html";
        			body = HttpRequestUtils.readFile(requestPath.contains(".html") ? requestPath : requestPath+".html");
        		} else if("/user/list".equals(requestPath)) {
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
            	} else {
            		body = HttpRequestUtils.readFile(requestPath);
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
