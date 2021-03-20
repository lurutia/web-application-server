package webserver;

import util.HttpRequest;
import util.HttpResponse;

public abstract class AbstractController implements Controller {
	@Override
	public void service(HttpRequest request, HttpResponse response) {
		if("POST".equals(request.getMethod())) {
			this.doPost(request, response);
		} else {
			this.doGet(request, response);
		}
		
	}
	
	protected void doGet(HttpRequest request, HttpResponse response) {
		
	}
	
	protected void doPost(HttpRequest request, HttpResponse response) {
		
	}
}
