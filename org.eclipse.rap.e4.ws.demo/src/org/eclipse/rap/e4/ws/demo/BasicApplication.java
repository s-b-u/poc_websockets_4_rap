package org.eclipse.rap.e4.ws.demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.e4.E4ApplicationConfig;
import org.eclipse.rap.e4.E4EntryPointFactory;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.osgi.ApplicationLauncher;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true, property = ApplicationLauncher.PROPERTY_CONTEXT_NAME + "=poc")
public class BasicApplication implements ApplicationConfiguration {

	public void configure(Application application) {
		application.setOperationMode(OperationMode.SWT_COMPATIBILITY);

		ResourceLoader resourceLoader = new ResourceLoader() {
			@Override
			public InputStream getResourceAsStream(String resourceName) throws IOException {
				return this.getClass().getClassLoader().getResourceAsStream("/" + resourceName);
			}
		};

		application.addResource("icons/door_in.png", resourceLoader);

		Map<String, String> properties = new HashMap<String, String>();

		properties.put(WebClient.FAVICON, "icons/door_in.png");
		properties.put(WebClient.PAGE_TITLE, "Hello e4 RAP WS");
		properties.put(WebClient.HEAD_HTML, "<meta name=\"description\" content=\"RAP E4 WebService Sample\">");

		application.addEntryPoint("/hello", new E4EntryPointFactory(new E4ApplicationConfig(
				"platform:/plugin/org.eclipse.rap.e4.ws.demo/Application.e4xmi", null, null, null, false, true, true)

		// E4ApplicationConfig.create("platform:/plugin/org.eclipse.rap.e4.demo/Application.e4xmi")
		), properties);

	}

}
