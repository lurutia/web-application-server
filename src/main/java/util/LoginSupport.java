package util;

import java.util.Map;

public class LoginSupport {
    public boolean isLogin(Map<String, String> cookies) {
    	String value = cookies.get("logined");
    	if(value == null) {
    		return false;
    	}
    	return Boolean.parseBoolean(value);
    }
}
