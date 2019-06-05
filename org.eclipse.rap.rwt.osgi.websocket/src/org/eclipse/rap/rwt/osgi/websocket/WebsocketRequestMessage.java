package org.eclipse.rap.rwt.osgi.websocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.RequestMessage;

public class WebsocketRequestMessage extends WebSocketMessage {

	private static final long serialVersionUID = -2529264351073504508L;

	private final RequestMessage requestMessage;
	private final List<Cookie> cookies;
	private final Map<String, String> parameters;

	public class InternalRequestMessage extends RequestMessage {
		private static final long serialVersionUID = 6798278514643718884L;

		protected InternalRequestMessage(JsonObject json) {
			super(json);
		}
	}

	public WebsocketRequestMessage(JsonObject jsonObject) {
		super(jsonObject);
		JsonValue message = jsonObject.get("message");
		requestMessage = message == null || message.isNull() ? null : new InternalRequestMessage(message.asObject());

		ArrayList<Cookie> cookies = new ArrayList<>();
		JsonValue jsoncookies = jsonObject.get("cookies");
		if (jsoncookies != null)
			jsoncookies.asArray().forEach(c -> {
				JsonObject jc = c.asObject();
				cookies.add(new Cookie(jc.get("name").asString(), jc.get("value").asString()));
			});
		this.cookies = Collections.unmodifiableList(cookies);

		JsonValue jparameters = jsonObject.get("parameters");
		this.parameters = new HashMap<>();
		if (jparameters != null) {
			jparameters.asObject()
					.forEach(m -> parameters.put(m.getName(), m.getValue().isNull() ? null : m.getValue().asString()));
		}
		
	}


	public Map<String, String> getParameters() {
		return parameters;
	}

	public RequestMessage getRequestMessage() {
		return requestMessage;
	}

	public List<Cookie> getCookies() {
		return cookies;
	}

}
