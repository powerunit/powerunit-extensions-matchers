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
			roundMirror.getProcessingEnv().getMessager().printMessage(Kind.MANDATORY_WARNING,
					"The annotation `ProvideMatchers` is used on an enum, which is not supported", e,
					roundMirror.getProvideMatchersAnnotation(
							roundMirror.getProcessingEnv().getElementUtils().getAllAnnotationMirrors(e)));
			return Optional.empty();
		case INTERFACE:
			roundMirror.getProcessingEnv().getMessager().printMessage(Kind.MANDATORY_WARNING,
					"The annotation `ProvideMatchers` is used on an interface, which is not supported", e,
					roundMirror.getProvideMatchersAnnotation(
							roundMirror.getProcessingEnv().getElementUtils().getAllAnnotationMirrors(e)));
			return Optional.empty();
		default:
		}
		return Optional.of(e);
	}

	@Override
	protected Optional<TypeElement> defaultAction(Element e, Void p) {
		roundMirror.getProcessingEnv().getMessager().printMessage(Kind.MANDATORY_WARNING,
				"The annotation `ProvideMatchers` is used on an unsupported element", e,
				roundMirror.getProvideMatchersAnnotation(
						roundMirror.getProcessingEnv().getElementUtils().getAllAnnotationMirrors(e)));
		return Optional.empty();
	}
}