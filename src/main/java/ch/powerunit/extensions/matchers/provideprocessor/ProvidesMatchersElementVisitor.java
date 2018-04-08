package ch.powerunit.extensions.matchers.provideprocessor;

import java.util.Optional;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.Diagnostic.Kind;

class ProvidesMatchersElementVisitor extends SimpleElementVisitor8<Optional<TypeElement>, Void> {

	private final RoundMirror roundMirror;

	public ProvidesMatchersElementVisitor(RoundMirror roundMirror) {
		this.roundMirror = roundMirror;
	}

	@Override
	public Optional<TypeElement> visitType(TypeElement e, Void p) {
		switch (e.getKind()) {
		case ENUM:
			warningForType(e, "enum");
			return Optional.empty();
		case INTERFACE:
			warningForType(e, "interface");
			return Optional.empty();
		default:
			return Optional.of(e);
		}
	}

	@Override
	protected Optional<TypeElement> defaultAction(Element e, Void p) {
		warningForType(e, "unexpected element");
		return Optional.empty();
	}

	private void warningForType(Element e, String type) {
		roundMirror.getProcessingEnv().getMessager().printMessage(Kind.MANDATORY_WARNING,
				"The annotation `ProvideMatchers` is used on an " + type + ", which is not supported", e,
				roundMirror.getProvideMatchersAnnotation(e));
	}
}