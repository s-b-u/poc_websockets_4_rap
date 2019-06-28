package org.eclipse.emf.ecp.makeithappen.application.sample.rap.e4;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.makeithappen.model.task.TaskPackage;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTView;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.emf.ecp.view.spi.model.ModelChangeListener;
import org.eclipse.emf.ecp.view.spi.model.ModelChangeNotification;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class View {

	private ECPSWTView render;
	
	@Inject
	MPart part;

	private EObject getDummyEObject() {
		// Replace this with your own model EClass to test the application with a custom
		// model
		final EClass eClass = TaskPackage.eINSTANCE.getUser();
		return EcoreUtil.create(eClass);
	}

	@PostConstruct
	public void createPartControl(Composite parent) throws ECPRendererException {

		final Composite content = new Composite(parent, SWT.NONE);
		content.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		content.setLayout(GridLayoutFactory.fillDefaults().margins(10, 10).create());

		render = ECPSWTViewRenderer.INSTANCE.render(content, getDummyEObject());
		render.getViewModelContext().registerDomainChangeListener(new ModelChangeListener() {
			@Override
			public void notifyChange(ModelChangeNotification notification) {
				System.out.println(notification.getStructuralFeature().getName());
				part.setDirty(true);
			}
		});
		content.layout();

	}

	@Focus
	public void setFocus() {
		render.getSWTControl().setFocus();
	}

	@PreDestroy
	public void dispose() {
		render.dispose();
	}
	
	@Persist
	public void save() {
		part.setDirty(false);
	}
	

}
