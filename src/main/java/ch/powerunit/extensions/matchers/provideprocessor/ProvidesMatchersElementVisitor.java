package ch.powerunit.extensions.matchers.provideprocessor;

import java.util.Optional;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.Diagnostic.Kind;

class ProvidesMatchersElementVisitor extends SimpleElementVisitor8<Optional<TypeElement>, Void> {

	private final ProvidesMatchersAnnotationsProcessor providesMatchersAnnotationsProcessor;
	private final ProcessingEnvironment processingEnv;
	private final TypeElement provideMatchersTE;

	public ProvidesMatchersElementVisitor(ProvidesMatchersAnnotationsProcessor providesMatchersAnnotationsProcessor,
			ProcessingEnvironment processingEnv, TypeElement provideMatchersTE) {
		this.providesMatchersAnnotationsProcessor = providesMatchersAnnotationsProcessor;
		this.processingEnv = processingEnv;
		this.provideMatchersTE = provideMatchersTE;
	}

	@Override
	public Optional<TypeElement> visitType(TypeElement e, Void p) {
		switch (e.getKind()) {
		case ENUM:
			processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING,
					"The annotation `ProvideMatchers` is used on an enum, which is not supported", e,
					this.providesMatchersAnnotationsProcessor.getProvideMatchersAnnotation(provideMatchersTE,
							processingEnv.getElementUtils().getAllAnnotationMirrors(e)));
			return Optional.empty();
		case INTERFACE:
			processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING,
					"The annotation `ProvideMatchers` is used on an interface, which is not supported", e,
					this.providesMatchersAnnotationsProcessor.getProvideMatchersAnnotation(provideMatchersTE,
							processingEnv.getElementUtils().getAllAnnotationMirrors(e)));
			return Optional.empty();
		default:
		}
		return Optional.of(e);
	}

	@Override
	protected Optional<TypeElement> defaultAction(Element e, Void p) {
		processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING,
				"The annotation `ProvideMatchers` is used on an unsupported element", e,
				this.providesMatchersAnnotationsProcessor.getProvideMatchersAnnotation(provideMatchersTE,
						processingEnv.getElementUtils().getAllAnnotationMirrors(e)));
		return Optional.empty();
	}
}