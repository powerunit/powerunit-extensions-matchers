package ch.powerunit.extensions.matchers.factoryprocessor;

import java.util.Optional;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.Diagnostic.Kind;

class FactoryElementVisitor extends SimpleElementVisitor8<Optional<ExecutableElement>, FactoryAnnotationsProcessor> {

	@Override
	public Optional<ExecutableElement> visitExecutable(ExecutableElement e,
			FactoryAnnotationsProcessor factoryAnnotationsProcessor) {
		if (e.getModifiers().contains(Modifier.STATIC) && e.getModifiers().contains(Modifier.PUBLIC)) {
			return Optional.of(e);
		}
		return defaultAction(e, factoryAnnotationsProcessor);
	}

	@Override
	protected Optional<ExecutableElement> defaultAction(Element e,
			FactoryAnnotationsProcessor factoryAnnotationsProcessor) {
		factoryAnnotationsProcessor.getMessager().printMessage(Kind.MANDATORY_WARNING,
				"The annotation `Factory` is used on an unsupported element", e,
				factoryAnnotationsProcessor.getFactoryAnnotation(e));
		return Optional.empty();
	}
}