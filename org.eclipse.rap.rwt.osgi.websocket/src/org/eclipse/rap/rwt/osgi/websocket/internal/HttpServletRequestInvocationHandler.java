package org.eclipse.rap.rwt.osgi.websocket.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.osgi.websocket.RWTWebsocketEndpoint;
import org.eclipse.rap.rwt.osgi.websocket.WebsocketRequestMessage;

class HttpServletRequestInvocationHandler implements InvocationHandler {

	private final RWTWebsocketEndpoint rwtWebsocketEndpoint;
	private final WebsocketRequestMessage requestMessage;
	private final HttpSession httpSession;
	private final ServletInputStream inputStream;
	private final Map<Object, Object> attributes=new HashMap<>();

	public HttpServletRequestInvocationHandler(RWTWebsocketEndpoint rwtWebsocketEndpoint,
			WebsocketRequestMessage requestMessage, HttpSession httpSession) throws IOException {
		this.rwtWebsocketEndpoint = rwtWebsocketEndpoint;
		this.requestMessage = requestMessage;
		this.httpSession = httpSession;
		StringWriter writer = new StringWriter();
		if (requestMessage.getRequestMessage() != null)
			requestMessage.getRequestMessage().toJson().writeTo(writer);
		InputStream inputStream = new ByteArrayInputStream(writer.toString().getBytes());
		this.inputStream = new ServletInputStream() {

			@Override
			public boolean isFinished() {
				return true;
			}

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
				try {
					readListener.onDataAvailable();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					readListener.onAllDataRead();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			@Override
			public int read() throws IOException {
				return inputStream.read();
			}

		};

	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		switch (method.getName()) {
		case "getSession":
			return httpSession;
		case "getHeader":
			return String.join(",", this.rwtWebsocketEndpoint.getHandshakeRequest().getHeaders().getOrDefault(args[0],
					Collections.emptyList()));
		case "getParameter":
			return requestMessage.getParameters().get(args[0]);
		case "getServletPath":
			return this.rwtWebsocketEndpoint.getContextPath();
		case "getLocales":
			String al = this.rwtWebsocketEndpoint.getHandshakeRequest().getHeaders()
					.getOrDefault("Accept-Language", Collections.emptyList())
					.stream().findFirst().orElse(null);
			if (al == null)
				return Collections.enumeration(Arrays.asList(Locale.getDefault()));
			al = al.split(";")[0];
			return Collections.enumeration(Arrays.asList(al.split(",")).stream().map((l) -> {
				String[] lc = l.split("-");
				return new Locale(lc[0], lc.length > 1 ? lc[1] : "");
			}).collect(Collectors.toSet()));
		case "getCookies":
			return requestMessage.getCookies().toArray(new Cookie[requestMessage.getCookies().size()]);
		case "isSecure":
			return "wss".equalsIgnoreCase(this.rwtWebsocketEndpoint.getHandshakeRequest().getRequestURI().getScheme());
		case "getMethod":
			return "POST";
		case "getContentType":
			return "application/json";
		case "getInputStream":
			return inputStream;
		case "getPathInfo":
			return null; // @ RWTServlet#handleRequest
		case "getCharacterEncoding":
			return StandardCharsets.UTF_8.displayName();
		case "setAttribute":
			attributes.put(args[0], args[1]);
			break;
		case "getAttribute":	
			return attributes.get(args[0]);
		case "toString":
			return this.toString();
		default:
			System.out.println(String.format("HttpServletRequest#%s", method.getName()));
			break;
		}

		return null;
	}

}