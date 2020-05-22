package ch.powerunit.extensions.matchers.provideprocessor;

import java.util.Optional;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.common.AbstractSimpleElementVisitor;

class ProvidesMatchersElementVisitor extends AbstractSimpleElementVisitor<Optional<TypeElement>, Void, RoundMirror>
		implements RoundMirrorSupport {

	public ProvidesMatchersElementVisitor(RoundMirror roundMirror) {
		super(roundMirror);
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
		super.getProcessingEnv().getMessager().printMessage(Kind.ERROR,
				"The annotation `ProvideMatchers` is used on an " + type
						+ ", which is not supported. Since version 0.2.0 of powerunit-extension-matchers this is considered as an error.",
				e, support.getProvideMatchersAnnotation(e));
	}

}