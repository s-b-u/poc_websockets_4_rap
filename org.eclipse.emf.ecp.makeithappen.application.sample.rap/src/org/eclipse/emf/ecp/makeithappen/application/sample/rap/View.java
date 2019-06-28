/**
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * EclipseSource Munich - initial API and implementation
 */
package org.eclipse.emf.ecp.makeithappen.application.sample.rap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.makeithappen.model.task.TaskPackage;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTView;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * Example View for displaying a Forms Editor for an EObject.
 */
public class View extends ViewPart {
	/**
	 * View ID.
	 */
	public static final String ID = "TestApp.view"; //$NON-NLS-1$

	private ECPSWTView render;

	private EObject getDummyEObject() {
		// Replace this with your own model EClass to test the application with a custom model
		final EClass eClass = TaskPackage.eINSTANCE.getUser();
		return EcoreUtil.create(eClass);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 *
	 * @param parent the {@link Composite} to render to
	 */
	@Override
	public void createPartControl(Composite parent) {

		final EObject dummyObject = getDummyEObject();

		try {
			parent.getShell().setMaximized(true);
			parent.setLayout(GridLayoutFactory.fillDefaults().equalWidth(true).numColumns(1).create());
			parent.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, true)
				.create());
			parent.getParent().setLayout(GridLayoutFactory.fillDefaults().equalWidth(true).numColumns(1).create());
			parent.getParent().setLayoutData(
				GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, true).create());

			final Composite content = new Composite(parent, SWT.NONE);
			content.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			content.setLayout(GridLayoutFactory.fillDefaults().margins(10, 10).create());
			content.setLayoutData(GridDataFactory.fillDefaults().create());

			render = ECPSWTViewRenderer.INSTANCE.render(content, dummyObject);

			content.layout();

		} catch (final ECPRendererException e) {
			e.printStackTrace();
		}
		parent.layout();
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void dispose() {
		if (render != null) {
			render.dispose();
		}
	}
}