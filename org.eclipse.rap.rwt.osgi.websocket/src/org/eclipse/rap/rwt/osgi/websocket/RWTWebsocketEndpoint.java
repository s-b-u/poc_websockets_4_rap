package org.eclipse.rap.rwt.osgi.websocket;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.CancellationException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.engine.RWTServlet;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.osgi.websocket.RWTWebsocketEndpoint.WebsocketClientMessageDecoder;
import org.eclipse.rap.rwt.osgi.websocket.RWTWebsocketEndpoint.WebsocketErrorMessageEncoder;
import org.eclipse.rap.rwt.osgi.websocket.RWTWebsocketEndpoint.WebsocketResponseMessageEncoder;
import org.eclipse.rap.rwt.osgi.websocket.internal.Wrapper;
import org.eclipse.rap.rwt.service.UISession;

@ServerEndpoint(value = "/", decoders = { WebsocketClientMessageDecoder.class }, encoders = {
		WebsocketResponseMessageEncoder.class, WebsocketErrorMessageEncoder.class })
public class RWTWebsocketEndpoint {
	public static final String HTTP_SESSION_ATTR = RWTWebsocketEndpoint.class.getName() + "#httpsession";
	public static final String APP_CONTEXT_ATTR = RWTWebsocketEndpoint.class.getName() + "#appcontext";
	public static final String CONTEXT_PATH_ATTR = RWTWebsocketEndpoint.class.getName() + "#contextpath";
	public static final String SERVLET_PATH_ATTR = RWTWebsocketEndpoint.class.getName() + "#servletpath";
	public static final String REQUEST_ATTR = RWTWebsocketEndpoint.class.getName() + "#handshakerequest";

	private HandshakeRequest handshakeRequest;
	private ApplicationContextImpl applicationContext;

	private String contextPath;
	private String servletPath;

	private UISession uiSession;

	private final Lock lock = new ReentrantLock();

	public void setUiSession(UISession uiSession) {
		this.uiSession = uiSession;
	}

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) throws IOException {
		handshakeRequest = (HandshakeRequest) config.getUserProperties().get(REQUEST_ATTR);
		applicationContext = (ApplicationContextImpl) config.getUserProperties().get(APP_CONTEXT_ATTR);
		contextPath = (String) config.getUserProperties().get(CONTEXT_PATH_ATTR);
		servletPath = (String) config.getUserProperties().get(SERVLET_PATH_ATTR);
	}

	@OnClose
	public void onClose(Session session, CloseReason reason) throws IOException {
		lock.lock();
		try {
			if (uiSession != null) {
				((UISessionImpl) uiSession).shutdown();
			}
		} finally {
			lock.unlock();
		}
	}

	@OnMessage
	public void onMessage(Session session, WebsocketRequestMessage message) throws Exception {

		WebsocketResponseMessage responseMessage = new WebsocketResponseMessage(message.getRequestId());

		HttpSession httpSession = Wrapper.createHttpSession(applicationContext.getServletContext(), session, this);

		HttpServletRequest httpServletRequest = Wrapper.createHttpServletRequest(this, message, httpSession);

		Writer responeWriter = new StringWriter();
		HttpServletResponse httpServletResponse = Wrapper.createHttpServletResponse(responseMessage, responeWriter);

		ServletConfig servletConfig = Wrapper.createServletConfig(applicationContext.getServletContext());
		RWTServlet rwtServlet = new RWTServlet();
		rwtServlet.init(servletConfig);
		try {
			rwtServlet.doGet(httpServletRequest, httpServletResponse);
		} catch (CancellationException e) {
			return;
		}

		String response = responeWriter.toString();
		if (response != null && response.trim().length() > 0) {
			try {
				responseMessage.setMessage(JsonValue.readFrom(response));
			} catch (Exception e) {
				System.out.println(response);
				e.printStackTrace();
			}
		}
		lock.lock();
		try {
			if (session.isOpen()) {
				session.getBasicRemote().sendObject(responseMessage);
			}
		} finally {
			lock.unlock();
		}

	}

	@OnMessage
	public void onPing(PongMessage message) {
	}

	@OnError
	public void onError(Session session, Throwable cause) throws IOException, EncodeException {
		cause.printStackTrace();
		lock.lock();
		try {
			if (session.isOpen()) {
				session.getBasicRemote().sendObject(new WebsocketErrorMessage(-1, cause));
			}
		} finally {
			lock.unlock();
		}
	}

	public HandshakeRequest getHandshakeRequest() {
		return handshakeRequest;
	}

	public ApplicationContextImpl getApplicationContext() {
		return applicationContext;
	}

	public String getContextPath() {
		return contextPath;
	}

	public String getServletPath() {
		return servletPath;
	}

	public static class WebsocketClientMessageDecoder implements Decoder.TextStream<WebsocketRequestMessage> {
		@Override
		public void init(EndpointConfig config) {
		}

		@Override
		public void destroy() {
		}

		@Override
		public WebsocketRequestMessage decode(Reader reader) throws DecodeException, IOException {
			return new WebsocketRequestMessage(JsonObject.readFrom(reader));
		}
	}

	public static class WebsocketResponseMessageEncoder implements Encoder.TextStream<WebsocketResponseMessage> {

		@Override
		public void init(EndpointConfig config) {
		}

		@Override
		public void destroy() {
		}

		@Override
		public void encode(WebsocketResponseMessage message, Writer writer) throws EncodeException, IOException {
			message.toJson().writeTo(writer);
		}

	}

	public static class WebsocketErrorMessageEncoder implements Encoder.TextStream<WebsocketErrorMessage> {

		@Override
		public void init(EndpointConfig config) {
		}

		@Override
		public void destroy() {
		}

		@Override
		public void encode(WebsocketErrorMessage message, Writer writer) throws EncodeException, IOException {
			message.toJson().writeTo(writer);
		}
	}
}
