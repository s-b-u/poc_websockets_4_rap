package org.eclipse.rap.rwt.osgi.websocket;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.rap.json.JsonObject;

public class WebsocketErrorMessage extends WebSocketMessage {
	
	private static final long serialVersionUID = 5624072790406956857L;
	private final String error;
	private final String message;
	private final String stackTrace;

	public WebsocketErrorMessage(long requetsId, Throwable cause) {
		super(requetsId);
		error = cause.getClass().getName();
		message = cause.getLocalizedMessage();
		StringWriter writer = new StringWriter();
		cause.printStackTrace(new PrintWriter(writer));
		stackTrace = writer.toString();
	}

	public String getType() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public JsonObject toJson() {
		return super.toJson()
				.add("error", error)
				.add("message", getMessage())
				.add("stackTrace", getStackTrace());
	}

	
}
