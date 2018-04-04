package ch.powerunit.extensions.matchers.provideprocessor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import ch.powerunit.extensions.matchers.ProvideMatchers;

public class ProvideMatchersMirror {
	private final String comments;
	private final String simpleNameOfGeneratedClass;
	private final String fullyQualifiedNameOfGeneratedClass;
	private final String packageNameOfGeneratedClass;

	public ProvideMatchersMirror(ProcessingEnvironment processingEnv, TypeElement annotatedElement) {
		String fullyQualifiedNameOfClassAnnotatedWithProvideMatcher = annotatedElement.getQualifiedName().toString();
		String tpackageName = processingEnv.getElementUtils().getPackageOf(annotatedElement).getQualifiedName()
				.toString();
		String toutputClassName = fullyQualifiedNameOfClassAnnotatedWithProvideMatcher + "Matchers";
		String tsimpleNameOfGeneratedClass = annotatedElement.getSimpleName().toString() + "Matchers";
		ProvideMatchers pm = annotatedElement.getAnnotation(ProvideMatchers.class);
		this.comments = pm.comments();
		if (!"".equals(pm.matchersClassName())) {
			toutputClassName = toutputClassName.replaceAll(tsimpleNameOfGeneratedClass + "$", pm.matchersClassName());
			tsimpleNameOfGeneratedClass = pm.matchersClassName();
		}
		this.simpleNameOfGeneratedClass = tsimpleNameOfGeneratedClass;
		if (!"".equals(pm.matchersPackageName())) {
			toutputClassName = toutputClassName.replaceAll("^" + tpackageName, pm.matchersPackageName());
			tpackageName = pm.matchersPackageName();
		}
		this.fullyQualifiedNameOfGeneratedClass = toutputClassName;
		this.packageNameOfGeneratedClass = tpackageName;
	}

	public String getComments() {
		return comments;
	}

	public String getSimpleNameOfGeneratedClass() {
		return simpleNameOfGeneratedClass;
	}

	public String getFullyQualifiedNameOfGeneratedClass() {
		return fullyQualifiedNameOfGeneratedClass;
	}

	public String getPackageNameOfGeneratedClass() {
		return packageNameOfGeneratedClass;
	}

}
