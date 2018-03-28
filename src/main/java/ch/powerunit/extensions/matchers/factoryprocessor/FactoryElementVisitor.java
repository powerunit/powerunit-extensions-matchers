package ch.powerunit.extensions.matchers.factoryprocessor;

import java.util.Optional;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.Diagnostic.Kind;

class FactoryElementVisitor extends SimpleElementVisitor8<Optional<ExecutableElement>, Void> {

	private final FactoryAnnotationsProcessor factoryAnnotationsProcessor;
	private final ProcessingEnvironment processingEnv;
	private final TypeElement factoryTE;

	public FactoryElementVisitor(FactoryAnnotationsProcessor factoryAnnotationsProcessor,
			ProcessingEnvironment processingEnv, TypeElement factoryTE) {
		this.factoryAnnotationsProcessor = factoryAnnotationsProcessor;
		this.processingEnv = processingEnv;
		this.factoryTE = factoryTE;
	}

	@Override
	public Optional<ExecutableElement> visitExecutable(ExecutableElement e, Void p) {
		if (e.getModifiers().contains(Modifier.STATIC) && e.getModifiers().contains(Modifier.PUBLIC)) {
			return Optional.of(e);
		}
		return defaultAction(e, p);
	}

	@Override
	protected Optional<ExecutableElement> defaultAction(Element e, Void p) {
		processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING,
				"The annotation `Factory` is used on an unsupported element", e, this.factoryAnnotationsProcessor
						.getFactoryAnnotation(factoryTE, processingEnv.getElementUtils().getAllAnnotationMirrors(e)));
		return Optional.empty();
	}
}