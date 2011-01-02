/*
 Copyright (c) 2010, The Staccato-Commons Team

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation; version 3 of the License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.
 */
package net.sf.staccato.commons.check.instrument;

import java.lang.annotation.Annotation;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import net.sf.staccato.commons.check.Ensure;
import net.sf.staccato.commons.instrument.context.AnnotationContext;
import net.sf.staccato.commons.instrument.context.ArgumentAnnotationContext;
import net.sf.staccato.commons.instrument.context.MethodAnnotationContext;
import net.sf.staccato.commons.instrument.handler.ArgumentAnnotationHandler;
import net.sf.staccato.commons.instrument.handler.MethodAnnotationHandler;
import net.sf.staccato.commons.instrument.handler.deactivator.Deactivable;
import net.sf.staccato.commons.instrument.handler.deactivator.StackedDeactivableSupport;
import net.sf.staccato.commons.lang.SoftException;

/**
 * @author flbulgarelli
 * 
 */
public abstract class AbstractCheckAnnotationHandler<T extends Annotation> implements
	MethodAnnotationHandler<T>, ArgumentAnnotationHandler<T>, Deactivable {

	protected static final String ENSURE_FULLY_QUALIFIED_NAME = "net.sf.staccato.commons.check.Ensure.";
	protected static final String ASSERT_FULLY_QUALIFIED_NAME = "net.sf.staccato.commons.check.Assert.";
	private final StackedDeactivableSupport deactivableSupport = new StackedDeactivableSupport(
		activeByDefault());
	private final boolean ignoreReturns;

	/**
	 * Creates a new {@link AbstractCheckAnnotationHandler} specifying if check
	 * annotation in methods should be interpreted as checks over its return value
	 * and processed
	 * 
	 * @param ignoreReturns
	 *          if check annotation over returns should be processed
	 */
	public AbstractCheckAnnotationHandler(boolean ignoreReturns) {
		this.ignoreReturns = ignoreReturns;
	}

	@Override
	public void processAnnotatedArgument(T annotation, ArgumentAnnotationContext context)
		throws CannotCompileException {
		if (!deactivableSupport.isActive())
			return;
		try {
			context.getArgumentBehavior().insertBefore(
				ENSURE_FULLY_QUALIFIED_NAME + createArgumentCheck(annotation, context) + ";");
		} catch (CannotCompileException e) {
			logError(context, context.getArgumentBehavior(), e);
			throw e;
		}
	}

	@Override
	public void preProcessAnnotatedMethod(T annotation, MethodAnnotationContext context) {
		if (ignoreReturns || !deactivableSupport.isActive())
			return;
		// TODO handle properly
		Ensure.is(!context.isVoid(), "Context must not be void: %s", context.getMethod().getLongName());
		try {
			context.getMethod().insertAfter(
				ASSERT_FULLY_QUALIFIED_NAME + createMethodCheck(annotation, context) + ";");
		} catch (CannotCompileException e) {
			logError(context, context.getMethod(), e);
			throw SoftException.soften(e);
		}
	}

	private void logError(AnnotationContext context, CtBehavior be, CannotCompileException e) {
		context.logErrorMessage("Could not insert argument check on method {}: {}", //
			be.getLongName(),
			e.getMessage());
	}

	@Override
	public void postProcessAnnotatedMethod(T annotation, MethodAnnotationContext context) {
	}

	protected String createArgumentCheck(T annotation, ArgumentAnnotationContext context) {
		return createCheckCode(
			getArgumentMnemonic(context, getVarMnemonic(annotation)),
			context.getArgumentIdentifier(),
			annotation);
	}

	private String getArgumentMnemonic(ArgumentAnnotationContext context, String annotatedVarName) {
		return annotatedVarName.isEmpty() ? "var" + context.getArgumentNumber() : annotatedVarName;
	}

	private String createMethodCheck(T annotation, MethodAnnotationContext context) {
		return createCheckCode(
			getReturnName(getVarMnemonic(annotation)),
			context.getReturnIdentifier(),
			annotation);
	}

	protected String getReturnName(String returnName) {
		return returnName.isEmpty() ? "returnValue" : returnName;
	}

	public final void deactivate() {
		deactivableSupport.deactivate();
	}

	public final void activate() {
		deactivableSupport.activate();
	}

	protected abstract String createCheckCode(String argumentMnemonic, String argumentIdentifier,
		T annotation);

	protected abstract String getVarMnemonic(T annotation);

	protected boolean activeByDefault() {
		return true;
	}

}
