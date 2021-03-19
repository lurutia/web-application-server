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
import util.HttpResponse;
import util.IOUtils;
import util.LoginSupport;

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
        	HttpResponse httpResponse = new HttpResponse(out);
        	if(httpRequest == null) return;
        	
        	Map<String, String> headers = httpRequest.getHeader();
        	Map<String, String> params = httpRequest.getParams();
        	Map<String, String> cookies = httpRequest.getCookies();
        	
        	boolean logined = new LoginSupport().isLogin(cookies);

        	if(headers.get("method").equals("POST")) {
        		if("/user/login".equals(httpRequest.getPath())) {
        			
            		String userId = params.get("userId");
            		String password = params.get("password");
            		User findUser = DataBase.findUserById(userId);
            		if(findUser != null && findUser.getPassword().equals(password)) {
            			httpResponse.setHeader("Set-Cookie", "logined=true");
            			httpResponse.redirection("/index.html");
            		} else {
            			httpResponse.setHeader("Set-Cookie", "logined=false");
            			httpResponse.forward("/user/login_failed.html");
            		}
        		} else if("/user/create".equals(headers.get("path"))) {
            		String userId = params.get("userId");
            		String name = params.get("name");
            		String password = params.get("password");
            		String email = params.get("email");
            		User user = new User(userId, password, name, email);
            		
            		DataBase.addUser(user);
            		
            		httpResponse.redirection("/index.html");
        		}
        		
        	} else if(headers.get("method").equals("GET")) {
        		if(headers.get("path").startsWith("/create")) {
        			User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
        			log.debug("User : {}", user);
        			httpResponse.forward("/index.html");
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
        				httpResponse.forward(sb.toString().getBytes());
        			} else {
        				httpResponse.redirection("/user/login.html");
        			}
            	} else if(headers.get("path").endsWith(".css")) {
            		httpResponse.setHeader("Content-Type", "text/css;charset=utf-8\\r\\n");
            		httpResponse.forward(headers.get("path"));
            	} else {
            		httpResponse.forward(headers.get("path"));
            	}
        	}
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    

}
