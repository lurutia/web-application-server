package webserver;

import java.util.HashMap;
import java.util.Map;

import controller.CreateUserController;
import controller.UserListController;
import controller.UserLoginController;

public class RequestMapping {
	private static Map<String, Controller> controllers = new HashMap<String, Controller>();
	
	static {
    	controllers.put("/user/create", new CreateUserController());
    	controllers.put("/user/list", new UserListController());
    	controllers.put("/user/login", new UserLoginController());
	}
	
	public static Controller getController(String requestUrl) {
		return controllers.get(requestUrl);
	}
}
