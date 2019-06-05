package org.eclipse.jetty.osgi.httpservice.webservice;

import java.util.Collections;
import java.util.ServiceLoader;

import javax.servlet.ServletContainerInitializer;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class ServletContainerInitializersStarter extends AbstractLifeCycle
		implements ServletContextHandler.ServletContainerInitializerCaller {

	private final ServletContextHandler.Context ctx;

	public ServletContainerInitializersStarter(ServletContextHandler.Context ctx) {
		super();
		this.ctx = ctx;
	}

	@Override
	protected void doStart() throws Exception {
		ctx.setExtendedListenerTypes(true);
		for (ServletContainerInitializer containerInitializer : ServiceLoader.load(ServletContainerInitializer.class)) {
			try {
				containerInitializer.onStartup(Collections.emptySet(), ctx);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ctx.setExtendedListenerTypes(false);
	}

}
