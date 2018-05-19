package ch.powerunit.extensions.matchers.factoryprocessor;

import java.util.Optional;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.common.AbstractSimpleElementVisitor;

class FactoryElementVisitor extends AbstractSimpleElementVisitor<Optional<ExecutableElement>, Void, RoundMirror> {

	public FactoryElementVisitor(RoundMirror support) {
		super(support);
	}

	@Override
	public Optional<ExecutableElement> visitExecutable(ExecutableElement e, Void ignore) {
		if (e.getModifiers().contains(Modifier.STATIC) && e.getModifiers().contains(Modifier.PUBLIC)) {
			return Optional.of(e);
		}
		return defaultAction(e, ignore);
	}

	@Override
	protected Optional<ExecutableElement> defaultAction(Element e, Void ignore) {
		getProcessingEnv().getMessager().printMessage(Kind.MANDATORY_WARNING,
				"The annotation `Factory` is used on an unsupported element", e, support.getFactoryAnnotation(e));
		return Optional.empty();
	}
}