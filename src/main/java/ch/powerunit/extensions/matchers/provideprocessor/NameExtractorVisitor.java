package ch.powerunit.extensions.matchers.provideprocessor;

import java.util.Optional;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.TypeKindVisitor8;
import javax.tools.Diagnostic.Kind;

final class NameExtractorVisitor extends TypeKindVisitor8<Optional<String>, Boolean> {

	/**
	 * 
	 */
	private final ProcessingEnvironment processingEnv;

	/**
	 * @param providesMatchersSubElementVisitor
	 */
	public NameExtractorVisitor(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
	}

	@Override
	public Optional<String> visitPrimitiveAsBoolean(PrimitiveType t, Boolean asPrimitif) {
		return Optional.of((asPrimitif) ? "boolean" : "Boolean");
	}

	@Override
	public Optional<String> visitPrimitiveAsByte(PrimitiveType t, Boolean asPrimitif) {
		return Optional.of((asPrimitif) ? "byte" : "Byte");
	}

	@Override
	public Optional<String> visitPrimitiveAsShort(PrimitiveType t, Boolean asPrimitif) {
		return Optional.of((asPrimitif) ? "short" : "Short");
	}

	@Override
	public Optional<String> visitPrimitiveAsInt(PrimitiveType t, Boolean asPrimitif) {
		return Optional.of((asPrimitif) ? "int" : "Integer");
	}

	@Override
	public Optional<String> visitPrimitiveAsLong(PrimitiveType t, Boolean asPrimitif) {
		return Optional.of((asPrimitif) ? "long" : "Long");
	}

	@Override
	public Optional<String> visitPrimitiveAsChar(PrimitiveType t, Boolean asPrimitif) {
		return Optional.of((asPrimitif) ? "char" : "Character");
	}

	@Override
	public Optional<String> visitPrimitiveAsFloat(PrimitiveType t, Boolean asPrimitif) {
		return Optional.of((asPrimitif) ? "float" : "Float");
	}

	@Override
	public Optional<String> visitPrimitiveAsDouble(PrimitiveType t, Boolean asPrimitif) {
		return Optional.of((asPrimitif) ? "double" : "Double");
	}

	@Override
	public Optional<String> visitArray(ArrayType t, Boolean asPrimitif) {
		return t.getComponentType().accept(this, true).map(r -> r + "[]");
	}

	@Override
	public Optional<String> visitDeclared(DeclaredType t, Boolean asPrimitif) {
		return Optional.of(t.toString());
	}

	@Override
	public Optional<String> visitTypeVariable(TypeVariable t, Boolean asPrimitif) {
		return Optional.of(t.toString());
	}

	@Override
	public Optional<String> visitUnknown(TypeMirror t, Boolean asPrimitif) {
		processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING, "Unsupported type element",
				processingEnv.getTypeUtils().asElement(t));
		return Optional.empty();
	}
}