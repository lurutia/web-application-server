package controller;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequest;
import util.HttpResponse;
import webserver.AbstractController;

public class UserLoginController extends AbstractController {
	static Logger log = LoggerFactory.getLogger(UserLoginController.class);
	
	@Override
	protected void doPost(HttpRequest request, HttpResponse response) {
		Map<String, String> params = request.getParams();
		String userId = params.get("userId");
		String password = params.get("password");
		User findUser = DataBase.findUserById(userId);
		if(findUser != null && findUser.getPassword().equals(password)) {
			response.setHeader("Set-Cookie", "logined=true");
			response.redirection("/index.html");
		} else {
			response.setHeader("Set-Cookie", "logined=false");
			response.forward("/user/login_failed.html");
		}
	}
}
