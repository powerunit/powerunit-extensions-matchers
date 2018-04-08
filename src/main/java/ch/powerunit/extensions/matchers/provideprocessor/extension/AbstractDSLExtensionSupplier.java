package ch.powerunit.extensions.matchers.provideprocessor.extension;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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

	public String[][] getSeveralParameter(boolean lastIsVarargs, String... name) {
		List<String[]> tmp = Arrays.stream(name).map(this::getOneParameter).collect(toList());
		if (lastIsVarargs) {
			tmp.get(name.length - 1)[0] += "...";
		}
		return tmp.toArray(new String[0][0]);
	}

	public String[] getOneParameter(String name) {
		return new String[] { targetName, name };
	}

	public String getSeveralWith(String... name) {
		return Arrays.stream(name).map(this::getOneWith).collect(joining(","));
	}

	public String getOneWith(String name) {
		return targetMethodName + "(" + name + ")";
	}
}
