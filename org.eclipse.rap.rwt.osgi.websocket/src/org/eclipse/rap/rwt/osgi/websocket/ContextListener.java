package org.eclipse.rap.rwt.osgi.websocket;

import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.internal.RWTProperties;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.osgi.ApplicationLauncher;
import org.eclipse.rap.rwt.osgi.ApplicationReference;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardListener;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

@Component(reference = @Reference(name = "app.references", service = ApplicationReference.class, cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC))
@HttpWhiteboardContextSelect(value = "(|(" + HTTP_WHITEBOARD_CONTEXT_NAME
		+ "=org_eclipse_rap_rwt_osgi_internal_HttpContextWrapper*)(" + HTTP_WHITEBOARD_CONTEXT_NAME
		+ "=org_eclipse_rap_ui_internal_RAPHttpContext*))")
@HttpWhiteboardListener
public class ContextListener implements ServletContextListener {

	@Reference(service = LoggerFactory.class)
	private Logger logger;

	@Activate
	private BundleContext bundleContext;

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		if (!RWTProperties.getBooleanProperty("org.eclipse.rap.rwt.osgi.websocket.active", true)) {
			logger.info("RWT Websocket support is disabled");
			return;
		}

		ServletContext servletContext = sce.getServletContext();
		ApplicationContextImpl applicationContext = ApplicationContextImpl.getFrom(servletContext);

		if (applicationContext == null) {
			logger.warn("RWT ApplicationContext not populated so far - giving up");
			return;
		}

		ServletContext root = servletContext.getContext("/");
		ServerContainer serverContainer = (ServerContainer) root.getAttribute(ServerContainer.class.getName());

		if (serverContainer == null) {
			logger.warn("no ServerContainer found - Websockets are not enabled for the ServletContext");
			return;
		}

		try {
			String resource = "rwt.remote.Request.js";
			ResourceManager resourceManager = applicationContext.getResourceManager();
			resourceManager.register(resource, this.getClass().getClassLoader().getResourceAsStream("/js/" + resource));
			applicationContext.getStartupPage().addJsLibrary(resourceManager.getLocation(resource));

			// FIXME maybe ApplicationContextImpl#getContextName or
			// ApplicationContextImpl#getApplicationConfiguration
			Field field = ApplicationContextImpl.class.getDeclaredField("applicationConfiguration");
			field.setAccessible(true);
			ApplicationConfiguration configuration = (ApplicationConfiguration) field.get(applicationContext);
			Class<? extends ApplicationConfiguration> clazz = configuration.getClass();

			Collection<ServiceReference<ApplicationConfiguration>> serviceReferences = bundleContext
					.getServiceReferences(ApplicationConfiguration.class,
							"(" + "component.name" + "=" + clazz.getName() + ")");

			// FIXME @see org.eclipse.rap.examples.internal.Activator#start
			// contextPath=rapdemo vs PROPERTY_CONTEXT_NAME=contextName (!!!)
			Optional<String> contextName = serviceReferences.stream()
					.map(r -> (String) r.getProperty(ApplicationLauncher.PROPERTY_CONTEXT_NAME))
					.filter(Objects::nonNull).findFirst();

			EntryPointManager entryPointManager = applicationContext.getEntryPointManager();
			for (String servletName : entryPointManager.getServletPaths()) {
				String path = contextName.map(n -> String.format("/%s%s", n, servletName)).orElse(servletName);
				serverContainer.addEndpoint(ServerEndpointConfig.Builder.create(RWTWebsocketEndpoint.class, path)
						.configurator(new RWTWebsocketEndpointConfigurator(applicationContext, contextName.orElse(""),
								servletName))
						.build());
			}
		} catch (Exception e) {
			logger.error("%s", e.getLocalizedMessage(), e);
			e.printStackTrace();
		}
		;

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}

}
