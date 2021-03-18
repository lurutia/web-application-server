package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
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
        	
        	while(!"".equals(line)) {
        		log.info(line);
        		line = br.readLine();
        		contentLength = line.contains("Content-Length") ? HttpRequestUtils.parseContentLength(line) : contentLength;
        		cookieMap = line.contains("Cookie") ? HttpRequestUtils.parseCookies(line) : cookieMap;
        	}

        	String requestPath = HttpRequestUtils.parseRequestPath(url);
    		byte[] body = null;
        	if(method.equals("POST")) {
        		String data = IOUtils.readData(br, contentLength);
        		queryStringMap = HttpRequestUtils.parseQueryString(data);
        		
        		if("/user/login".equals(requestPath)) {
            		String userId = queryStringMap.get("userId");
            		String password = queryStringMap.get("password");
            		User findUser = DataBase.findUserById(userId);
            		if(findUser.getPassword().equals(password)) {
            			body = HttpRequestUtils.readFile("/index.html");
            			responseHeader(dos, body.length, 302, new String[] {"Location: /index.html\r\n", "Set-Cookie: logined=true"});
            		} else {
            			body = HttpRequestUtils.readFile("/user/login_failed.html");
            			responseHeader(dos, body.length, 302, new String[] {"Location: /user/login_failed.html\r\n", "Set-Cookie: logined=false"});
            		}
        		} else if("/user/create".equals(requestPath)) {
            		String userId = queryStringMap.get("userId");
            		String name = queryStringMap.get("name");
            		String password = queryStringMap.get("password");
            		String email = queryStringMap.get("email");
            		User user = new User(userId, password, name, email);
            		
            		DataBase.addUser(user);
            		
            		body = user.toString().getBytes();
        			responseHeader(dos, body.length, 302, new String[] {"Location: /index.html\r\n"});
        		}
        		
        	} else if(method.equals("GET")) {
        		if(url.startsWith("/create")) {
        			String params = HttpRequestUtils.parseParams(url);
        			queryStringMap = HttpRequestUtils.parseQueryString(params);
        			User user = new User(queryStringMap.get("userId"), queryStringMap.get("password"), queryStringMap.get("name"), queryStringMap.get("email"));
        			log.debug("User : {}", user);
        			requestPath = "/index.html";
        		}
        		
            	if("/user/list".equals(requestPath)) {
            		System.out.println(cookieMap.keySet());
            	}
        		body = HttpRequestUtils.readFile(requestPath.contains(".html") ? requestPath : requestPath+".html");
        		
        		responseHeader(dos, body.length, 200, null);
        	}
    		
            
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseHeader(DataOutputStream dos, int lengthOfBodyContent, int status, String[] headerValues) {
    	try {
            dos.writeBytes("HTTP/1.1 " + status + " OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            if(headerValues == null) headerValues = new String[] {};
            for (String string : headerValues) {
				dos.writeBytes(string);
			}
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
//    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
//        try {
//            dos.writeBytes("HTTP/1.1 200 OK \r\n");
//            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
//            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
//            dos.writeBytes("\r\n");
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }
//    }
//    
//    private void response302Header(DataOutputStream dos, int lengthOfBodyContent) {
//        try {
//            dos.writeBytes("HTTP/1.1 302 OK \r\n");
//            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
//            
//            
//            dos.writeBytes("\r\n");
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }
//    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
