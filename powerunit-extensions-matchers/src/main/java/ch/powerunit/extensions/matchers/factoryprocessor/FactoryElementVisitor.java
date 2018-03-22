package ch.powerunit.extensions.matchers.factoryprocessor;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.Diagnostic.Kind;

class FactoryElementVisitor extends
		SimpleElementVisitor8<ExecutableElement, Void> {

	private final FactoryAnnotationsProcessor factoryAnnotationsProcessor;
	private final Elements elementsUtils;
	private final Messager messageUtils;
	private final TypeElement factoryTE;

	public FactoryElementVisitor(
			FactoryAnnotationsProcessor factoryAnnotationsProcessor,
			Elements elementsUtils,
			Messager messageUtils, TypeElement factoryTE) {
		this.factoryAnnotationsProcessor = factoryAnnotationsProcessor;
		this.elementsUtils = elementsUtils;
		this.messageUtils = messageUtils;
		this.factoryTE = factoryTE;
	}

	@Override
	public ExecutableElement visitExecutable(ExecutableElement e, Void p) {
		if (e.getModifiers().contains(Modifier.STATIC)
				&& e.getModifiers().contains(Modifier.PUBLIC)) {
			return e;
		}
		messageUtils.printMessage(Kind.MANDATORY_WARNING,
				"The annotation `Factory` is used on an unsupported element",
				e, this.factoryAnnotationsProcessor.getFactoryAnnotation(
						factoryTE, elementsUtils.getAllAnnotationMirrors(e)));
		return null;
	}

	@Override
	protected ExecutableElement defaultAction(Element e, Void p) {
		messageUtils.printMessage(Kind.MANDATORY_WARNING,
				"The annotation `Factory` is used on an unsupported element",
				e, this.factoryAnnotationsProcessor.getFactoryAnnotation(
						factoryTE, elementsUtils.getAllAnnotationMirrors(e)));
		return null;
	}
}