package ch.powerunit.extensions.matchers.provideprocessor.extension;

import java.util.Collection;
import java.util.function.Supplier;

import ch.powerunit.extensions.matchers.provideprocessor.DSLMethod;

public abstract class AbstractDSLExtensionSupplier {
	protected final String targetName;
	protected final String returnType;
	protected final String methodName;
	protected final String targetMethodName;

	public AbstractDSLExtensionSupplier(String targetName, String returnType, String methodName,
			String targetMethodName) {
		this.targetName = targetName;
		this.returnType = returnType;
		this.methodName = methodName;
		this.targetMethodName = targetMethodName;
	}

	public abstract Collection<Supplier<DSLMethod>> asSuppliers();

	public String[] getOneParameter(String name) {
		return new String[] { targetName, name };
	}

	public String getOneWith(String name) {
		return targetMethodName + "(" + name + ")";
	}
}
