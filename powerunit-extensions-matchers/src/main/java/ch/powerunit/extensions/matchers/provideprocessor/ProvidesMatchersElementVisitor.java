package ch.powerunit.extensions.matchers.provideprocessor;

import java.util.Optional;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.Diagnostic.Kind;

class ProvidesMatchersElementVisitor extends SimpleElementVisitor8<Optional<TypeElement>, Void> {

	private final ProvidesMatchersAnnotationsProcessor providesMatchersAnnotationsProcessor;
	private final Elements elementsUtils;
	private final Messager messageUtils;
	private final TypeElement provideMatchersTE;

	public ProvidesMatchersElementVisitor(ProvidesMatchersAnnotationsProcessor providesMatchersAnnotationsProcessor,
			Elements elementsUtils, Messager messageUtils, TypeElement provideMatchersTE) {
		this.providesMatchersAnnotationsProcessor = providesMatchersAnnotationsProcessor;
		this.elementsUtils = elementsUtils;
		this.messageUtils = messageUtils;
		this.provideMatchersTE = provideMatchersTE;
	}

	@Override
	public Optional<TypeElement> visitType(TypeElement e, Void p) {
		switch (e.getKind()) {
		case ENUM:
			messageUtils.printMessage(Kind.MANDATORY_WARNING,
					"The annotation `ProvideMatchers` is used on an enum, which is not supported", e,
					this.providesMatchersAnnotationsProcessor.getProvideMatchersAnnotation(provideMatchersTE,
							elementsUtils.getAllAnnotationMirrors(e)));
			return Optional.empty();
		case INTERFACE:
			messageUtils.printMessage(Kind.MANDATORY_WARNING,
					"The annotation `ProvideMatchers` is used on an interface, which is not supported", e,
					this.providesMatchersAnnotationsProcessor.getProvideMatchersAnnotation(provideMatchersTE,
							elementsUtils.getAllAnnotationMirrors(e)));
			return Optional.empty();
		default:
		}
		return Optional.of(e);
	}

	@Override
	protected Optional<TypeElement> defaultAction(Element e, Void p) {
		messageUtils.printMessage(Kind.MANDATORY_WARNING,
				"The annotation `ProvideMatchers` is used on an unsupported element", e,
				this.providesMatchersAnnotationsProcessor.getProvideMatchersAnnotation(provideMatchersTE,
						elementsUtils.getAllAnnotationMirrors(e)));
		return Optional.empty();
	}
}