package org.eclipse.rap.rwt.osgi.websocket;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;

public class RWTWebsocketEndpointConfigurator extends ServerEndpointConfig.Configurator {
	private final ApplicationContextImpl applicationContextImpl;
	private final String contextName;
	private final String servletName;

	public RWTWebsocketEndpointConfigurator(ApplicationContextImpl applicationContextImpl, String contextName, String servletName) {
		this.applicationContextImpl = applicationContextImpl;
		this.contextName=contextName;
		this.servletName=servletName;
	}

	@Override
	public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
		sec.getUserProperties().put(RWTWebsocketEndpoint.REQUEST_ATTR, request);
		sec.getUserProperties().put(RWTWebsocketEndpoint.HTTP_SESSION_ATTR, request.getHttpSession());
		sec.getUserProperties().put(RWTWebsocketEndpoint.APP_CONTEXT_ATTR, applicationContextImpl);
		sec.getUserProperties().put(RWTWebsocketEndpoint.CONTEXT_PATH_ATTR, contextName);
		sec.getUserProperties().put(RWTWebsocketEndpoint.SERVLET_PATH_ATTR, servletName);
	}

}
