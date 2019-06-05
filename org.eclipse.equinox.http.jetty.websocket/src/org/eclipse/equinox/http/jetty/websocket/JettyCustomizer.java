package org.eclipse.equinox.http.jetty.websocket;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

public class JettyCustomizer extends org.eclipse.equinox.http.jetty.JettyCustomizer {

	@Override
	public Object customizeContext(Object context, Dictionary<String, ?> settings) {
		
		try {
			System.out.println("--------configure context--------");
			ServerContainer container = WebSocketServerContainerInitializer
					.configureContext((ServletContextHandler) context);
			Bundle bundle = FrameworkUtil.getBundle(container.getClass());
			
			// https://github.com/eclipse/jetty.project/issues/3543
			if (bundle.getState() != Bundle.ACTIVE) {
				System.out.println(String.format("--------start bundle %s--------", bundle.getSymbolicName()));
				bundle.start();
			}
			bundle.getBundleContext().registerService(ServerContainer.class, container, new Hashtable<>());

		} catch (ServletException | BundleException e) {			
			e.printStackTrace(System.err);
		}
		return super.customizeContext(context, settings);
	}

	@Override
	public Object customizeHttpConnector(Object connector, Dictionary<String, ?> settings) {
		return super.customizeHttpConnector(connector, settings);
	}

	@Override
	public Object customizeHttpsConnector(Object connector, Dictionary<String, ?> settings) {
		return super.customizeHttpsConnector(connector, settings);
	}
}
