/**
 * 
 */
package ch.powerunit.extensions.matchers.provideprocessor.fields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementMirror;

public class MapFieldDescription extends DefaultFieldDescription {

	private static final String EMPTY_MATCHER = "new org.hamcrest.CustomTypeSafeMatcher<%1$s>(\"map is empty\"){ public boolean matchesSafely(%1$s o) {return o.isEmpty();}}";

	private static final String SIZE_MATCHER = "new org.hamcrest.CustomTypeSafeMatcher<%1$s>(\"map size is \"+other.size()){ public boolean matchesSafely(%1$s o) {return o.size()==other.size();} protected void describeMismatchSafely(%1$s item, org.hamcrest.Description mismatchDescription) {mismatchDescription.appendText(\" was size=\").appendValue(item.size());}}";

	public MapFieldDescription(ProvidesMatchersAnnotatedElementData containingElementMirror,
			FieldDescriptionMirror mirror) {
		super(containingElementMirror, mirror);
	}

	@Override
	protected Collection<FieldDSLMethod> getSpecificFieldDslMethodFor() {
		String fieldType = getFieldType();
		final String emptyMatcher = String.format(EMPTY_MATCHER, fieldType);

		Collection<FieldDSLMethod> tmp = new ArrayList<>();
		tmp.add(getDslMethodBuilder().withSuffixDeclarationJavadocAndDefault("IsEmpty", "the map is empty",
				emptyMatcher));
		if (!"".equals(generic)) {
			Object matchers[] = Optional.of(mirror.getFieldTypeMirror()).filter(m -> m instanceof DeclaredType)
					.map(DeclaredType.class::cast).filter(m -> m.getTypeArguments().size() == 2)
					.map(m -> m.getTypeArguments().stream().map(Object::toString)
							.map(o -> Optional.ofNullable(getByName(o))
									.filter(ProvidesMatchersAnnotatedElementMirror::hasWithSameValue)
									.map(t -> t.getFullyQualifiedNameOfGeneratedClass() + "."
											+ t.getMethodShortClassName() + "WithSameValue")
									.orElse(MATCHERS + ".is"))
							.toArray())
					.orElse(new String[] { MATCHERS + ".is", MATCHERS + ".is" });

			tmp.add(generateHasSameValue(fieldType, matchers[0].toString(), matchers[1].toString()));
		}
		return tmp;
	}

	private FieldDSLMethod generateHasSameValue(String fieldType, String keyMatcher, String keyValue) {
		final String sizeMatcher = String.format(SIZE_MATCHER, fieldType);
		return getDslMethodBuilder().withDeclaration("HasSameValues", fieldType + " other")
				.withJavaDoc("verify that the value from the other map are exactly the once inside this map",
						"other the other map")
				.havingDefault(MATCHERS + ".both(" + sizeMatcher + ").and(" + MATCHERS
						+ ".allOf(other.entrySet().stream().map(kv->" + MATCHERS + ".hasEntry(" + keyMatcher
						+ "(kv.getKey())," + keyValue
						+ "(kv.getValue()))).collect(java.util.stream.Collectors.toList())))");
	}

	@Override
	public String getMatcherForField() {
		String matcher = super.getMatcherForField();
		if (!"".equals(generic)) {
			String localGeneric = generic.contains("?") ? "" : "<" + generic + ">";
			matcher += "\n" + String.format(
					"private static class %1$sMatcherSameValue%2$s extends org.hamcrest.FeatureMatcher<%3$s,%4$s> {\n  public %1$sMatcherSameValue(org.hamcrest.Matcher<? super %4$s> matcher) {\n    super(matcher,\"%5$s\",\"%5$s\");\n  }\n  protected %4$s featureValueOf(%3$s actual) {\n    return (java.util.Set)actual.entrySet();\n  }\n}\n",
					mirror.getMethodFieldName(), containingElementMirror.getFullGeneric(), getFieldType(),
					"java.util.Set<java.util.Map.Entry" + localGeneric + ">", " [entries of] ");
		}
		return matcher;
	}

	@Override
	public String getFieldCopy(String lhs, String rhs) {
		if (!"".equals(generic)) {
			String fieldAccessor = getFieldAccessor();
			String fieldName = getFieldName();
			return "if(" + rhs + "." + fieldAccessor + "==null) {" + lhs + "." + fieldName + "(" + MATCHERS
					+ ".nullValue()); } else {" + lhs + "." + fieldName + "HasSameValues(" + rhs + "." + fieldAccessor
					+ ");}";
		}
		return super.getFieldCopy(lhs, rhs);

	}

}
