package controller;

import java.util.Collection;

import db.DataBase;
import model.User;
import util.HttpRequest;
import util.HttpResponse;
import util.LoginSupport;
import webserver.AbstractController;

public class UserListController extends AbstractController {
	@Override
	protected void doGet(HttpRequest request, HttpResponse response) {
		boolean logined = new LoginSupport().isLogin(request.getCookies());
		
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
			response.forward(sb.toString().getBytes());
		} else {
			response.redirection("/user/login.html");
		}
	}
}
