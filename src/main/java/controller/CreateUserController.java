package controller;

import java.util.Map;

import db.DataBase;
import model.User;
import util.HttpRequest;
import util.HttpResponse;
import webserver.AbstractController;

public class CreateUserController extends AbstractController {
	@Override
	protected void doPost(HttpRequest request, HttpResponse response) {
		Map<String, String> params = request.getParams();
		String userId = params.get("userId");
		String name = params.get("name");
		String password = params.get("password");
		String email = params.get("email");
		User user = new User(userId, password, name, email);
		
		DataBase.addUser(user);
		
		response.redirection("/index.html");
	}
}
