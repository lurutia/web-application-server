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

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            
        	BufferedReader br = new BufferedReader(new InputStreamReader(in));
        	String line = br.readLine();
        	String method = HttpRequestUtils.parseMethod(line);
        	String location = HttpRequestUtils.parseLocation(line);
    		Map<String, String> map;
        	int contentLength = 0;
        	
        	while(!"".equals(line)) {
        		log.info(line);
        		line = br.readLine();
        		contentLength = line.contains("Content-Length") ? HttpRequestUtils.parseContentLength(line) : contentLength;
        	}

        	String requestPath = HttpRequestUtils.parseRequestPath(location);
    		byte[] body = null;
        	if(method.equals("POST")) {
        		String data = IOUtils.readData(br, contentLength);
        		map = HttpRequestUtils.parseQueryString(data);
        		
        		String userId = map.get("userId");
        		String name = map.get("name");
        		String password = map.get("password");
        		String email = map.get("email");
        		User user = new User(userId, password, name, email);
        		
        		body = user.toString().getBytes();
        	} else if(method.equals("GET")) {
            	String params = HttpRequestUtils.parseParams(location);
        		map = HttpRequestUtils.parseQueryString(params);
        		body = HttpRequestUtils.readFile(requestPath);
        	}
    		
            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
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
