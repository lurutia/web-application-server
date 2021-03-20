package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.CreateUserController;
import controller.UserListController;
import controller.UserLoginController;
import util.HttpRequest;
import util.HttpResponse;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);


    private Socket connection;
    private Map<String, Controller> controllers = new HashMap<String, Controller>();

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
        	controllers.put("/user/create", new CreateUserController());
        	controllers.put("/user/list", new UserListController());
        	controllers.put("/user/login", new UserLoginController());
        	
        	HttpRequest httpRequest = new HttpRequest(in);
        	HttpResponse httpResponse = new HttpResponse(out);
//        	if(httpRequest == null) return;
        	
        	Map<String, String> headers = httpRequest.getHeader();

        	Controller controller = controllers.get(httpRequest.getPath());
        	if(controller == null) {
        		if(headers.get("path").endsWith(".css")) {
            		httpResponse.setHeader("Content-Type", "text/css;charset=utf-8\\r\\n");
            	}
        		httpResponse.forward(headers.get("path"));
        	} else {
        		controller.service(httpRequest, httpResponse);
        	}
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    

}
