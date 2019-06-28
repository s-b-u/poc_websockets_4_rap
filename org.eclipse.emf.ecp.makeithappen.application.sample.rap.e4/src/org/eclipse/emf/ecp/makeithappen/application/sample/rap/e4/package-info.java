@Headers({ 
	@Header(name = Constants.BUNDLE_ACTIVATIONPOLICY, value = Constants.ACTIVATION_LAZY),
	@Header(name = Constants.BUNDLE_SYMBOLICNAME, value = "${@package};singleton:=true") 
})
package org.eclipse.emf.ecp.makeithappen.application.sample.rap.e4;

import org.osgi.annotation.bundle.Header;
import org.osgi.annotation.bundle.Headers;
import org.osgi.framework.Constants;