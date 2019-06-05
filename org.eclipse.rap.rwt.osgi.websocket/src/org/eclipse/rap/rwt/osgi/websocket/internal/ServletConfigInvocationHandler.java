package org.eclipse.rap.rwt.osgi.websocket.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;

class ServletConfigInvocationHandler implements InvocationHandler {
	private final ServletContext context;

	public ServletConfigInvocationHandler(ServletContext context) {
		this.context = context;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		switch (method.getName()) {
		case "getServletContext":
			return context;
		default:
			System.out.println(String.format("ServletConfig#%s", method.getName()));
			break;
		}
		
		return null;
	}

}