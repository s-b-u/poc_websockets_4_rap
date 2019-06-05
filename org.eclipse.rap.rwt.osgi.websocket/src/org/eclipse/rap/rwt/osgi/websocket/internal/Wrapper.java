package org.eclipse.rap.rwt.osgi.websocket.internal;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.eclipse.rap.rwt.osgi.websocket.RWTWebsocketEndpoint;
import org.eclipse.rap.rwt.osgi.websocket.WebsocketRequestMessage;
import org.eclipse.rap.rwt.osgi.websocket.WebsocketResponseMessage;

public class Wrapper {

	@SuppressWarnings("unchecked")
	private static <T> T getProxy(Class<T> clazz, InvocationHandler invocationHandler)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		return  (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, invocationHandler);
	}

	public static HttpServletRequest createHttpServletRequest(RWTWebsocketEndpoint rwtWebsocketEndpoint,
			WebsocketRequestMessage message, HttpSession httpSession) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		return getProxy(HttpServletRequest.class, new HttpServletRequestInvocationHandler(rwtWebsocketEndpoint, message, httpSession));
	}

	public static HttpServletResponse createHttpServletResponse(WebsocketResponseMessage responseMessage,
			Writer responeWriter) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return getProxy(HttpServletResponse.class, new HttpServletResponseInvocationHandler(responseMessage,responeWriter));
	}

	public static ServletConfig createServletConfig(ServletContext servletContext) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return getProxy(ServletConfig.class, new ServletConfigInvocationHandler(servletContext));

	}

	public static HttpSession createHttpSession(ServletContext context, Session session, RWTWebsocketEndpoint rwtWebsocketEndpoint) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return getProxy(HttpSession.class, new HttpSessionInvocationHandler(context, session, rwtWebsocketEndpoint));
	}
	
	
}
