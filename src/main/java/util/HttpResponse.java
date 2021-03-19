package util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
	static Logger log = LoggerFactory.getLogger(HttpResponse.class);
	Map<String, String> map = new HashMap<String, String>();
	DataOutputStream dos;
	
	public HttpResponse(OutputStream out) {
		this.dos = new DataOutputStream(out);
		map.put("Content-Type", "text/html;charset=utf-8\\r\\n");
	}
	
	public void setHeader(String key, String value) {
		map.put(key, value);
	}
	
	public void forward(String file) throws IOException {
		try {
			byte[] body = HttpRequestUtils.readFile(file);
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
	        writeHeader();
	        dos.writeBytes("Content-Length: "+ body.length +"\r\n");
	        dos.writeBytes("\r\n");
	        responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
	}
	
	public void forward(byte[] bytes) throws IOException {
		try {
//			byte[] body = HttpRequestUtils.readFile(file);
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
	        writeHeader();
	        dos.writeBytes("Content-Length: "+ bytes.length +"\r\n");
	        dos.writeBytes("\r\n");
	        responseBody(dos, bytes);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
	}
	
	public void redirection(String path) {
    	try {
            dos.writeBytes("HTTP/1.1 302 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Location: " + path +"\r\n");
            writeHeader();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
	}
	
	private void writeHeader() throws IOException {
		for(String key : map.keySet()) {
        	dos.writeBytes(key+": "+ map.get(key) +"\r\n");
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
