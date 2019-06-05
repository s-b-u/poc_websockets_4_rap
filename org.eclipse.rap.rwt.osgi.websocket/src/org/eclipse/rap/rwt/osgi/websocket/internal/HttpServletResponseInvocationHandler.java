package org.eclipse.rap.rwt.osgi.websocket.internal;

import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.osgi.websocket.WebsocketResponseMessage;

class HttpServletResponseInvocationHandler implements InvocationHandler {
	private final WebsocketResponseMessage responseMessage;
	private final Writer responseWriter;

	public HttpServletResponseInvocationHandler(WebsocketResponseMessage responseMessage,Writer responseWriter) {
		this.responseMessage = responseMessage;
		this.responseWriter=new PrintWriter(responseWriter);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		switch (method.getName()) {
		case "addCookie":
			responseMessage.getCookies().add((Cookie) args[0]);
			return null;
		case "getWriter":
			return responseWriter;
		case "setContentType":
			return null;
		case "setCharacterEncoding":
			return null;
		case "setHeader":
			responseMessage.getHeaders().put((String)args[0], (String)args[1]);
			return null;
		case "addHeader":
			((HttpServletResponse)proxy).setHeader((String)args[0], (String) args[1]);
			return null;
		case "setDateHeader":
			return null;
		default:
			System.out.println(String.format("HttpServletResponse#%s", method.getName()));
			break;
		}
		
		return null;
	}

}