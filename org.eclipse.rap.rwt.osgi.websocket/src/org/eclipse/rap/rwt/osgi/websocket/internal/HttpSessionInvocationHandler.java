package org.eclipse.rap.rwt.osgi.websocket.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.websocket.Session;

import org.eclipse.rap.rwt.osgi.websocket.RWTWebsocketEndpoint;
import org.eclipse.rap.rwt.service.UISession;

class HttpSessionInvocationHandler implements InvocationHandler {
	private final Session session;
	private final ServletContext context;
	private final RWTWebsocketEndpoint rwtWebsocketEndpoint;

	public HttpSessionInvocationHandler(ServletContext context, Session session,
			RWTWebsocketEndpoint rwtWebsocketEndpoint) {
		this.session = session;
		this.context = context;
		this.rwtWebsocketEndpoint = rwtWebsocketEndpoint;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		switch (method.getName()) {
		case "getAttribute":
			return session.getUserProperties().get(args[0]);
		case "setAttribute":
			if (UISession.class.isInstance(args[1]))
				rwtWebsocketEndpoint.setUiSession((UISession) args[1]);
			return session.getUserProperties().put((String) args[0], args[1]);
		case "removeAttribute":
			Object removed = session.getUserProperties().remove(args[0]);
			if (removed instanceof HttpSessionBindingListener) {
				HttpSessionBindingListener listener = (HttpSessionBindingListener) removed;
				HttpSessionBindingEvent evt = new HttpSessionBindingEvent((HttpSession) proxy, (String) args[0],
						removed);
				listener.valueUnbound(evt);
			}
			return removed;
		case "getServletContext":
			return context;
		case "getMaxInactiveInterval":
			return (int) session.getMaxIdleTimeout() / 1000;
		default:
			System.out.println(String.format(" HttpSession#%s", method.getName()));
			break;
		}

		return null;
	}

}