package org.eclipse.rap.rwt.osgi.websocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;

public class WebsocketResponseMessage extends WebSocketMessage{

	private static final long serialVersionUID = -2529264351073504508L;

	private JsonValue message;

	private final List<Cookie> cookies = new ArrayList<>();
	private final Map<String,String> headers = new HashMap<>();

	public WebsocketResponseMessage(long requestId) {
		super(requestId);
	}

	public List<Cookie> getCookies() {
		return cookies;
	}

	public JsonValue getMessage() {
		return message;
	}

	public void setMessage(JsonValue message) {
		this.message = message;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public JsonObject toJson() {
		
		JsonArray cookies = new JsonArray();
		this.cookies.forEach(c -> cookies.add(new JsonObject().add("name", c.getName()).add("value", c.getValue())));
		JsonObject headers=new JsonObject();
		this.headers.forEach((k,v)->headers.add(k, v));
		JsonObject ret= super.toJson();
		if(message!=null)
			ret.add("message", message);
		return ret
				.add("cookies", cookies)
				.add("headers", headers);
				
	}

}
