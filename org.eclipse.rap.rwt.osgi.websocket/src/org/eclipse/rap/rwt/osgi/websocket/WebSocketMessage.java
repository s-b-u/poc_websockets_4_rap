package org.eclipse.rap.rwt.osgi.websocket;

import java.io.Serializable;

import org.eclipse.rap.json.JsonObject;

public class WebSocketMessage implements Serializable{
	
	private static final long serialVersionUID = 6392180592037143577L;
	private final long requestId;

	public WebSocketMessage(long requestId) {
		this.requestId = requestId;
	}
	
	public WebSocketMessage(JsonObject jsonObject) {
		this.requestId = jsonObject.get("requestId").asLong();
	}

	public long getRequestId() {
		return requestId;
	}
	
	public JsonObject toJson() {
		return new JsonObject()
				.add("requestId", requestId)
				.add("type", getClass().getName());
	}
}
