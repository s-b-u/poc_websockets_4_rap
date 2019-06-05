@Header(name = "Jetty-ContextFilePath", value = "contexts/websocket.xml")
@Requirements({
		@Requirement(
				namespace = ExtenderNamespace.EXTENDER_NAMESPACE,
				name = "osgi.serviceloader.processor"),
		@Requirement(
				namespace = "osgi.serviceloader",
				effective = "resolve",
				cardinality = Cardinality.MULTIPLE,
				filter = "(osgi.serviceloader=javax.servlet.ServletContainerInitializer)")
})
package org.eclipse.jetty.osgi.httpservice.webservice;

import org.osgi.annotation.bundle.Header;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.annotation.bundle.Requirement.Cardinality;
import org.osgi.annotation.bundle.Requirements;
import org.osgi.namespace.extender.ExtenderNamespace;