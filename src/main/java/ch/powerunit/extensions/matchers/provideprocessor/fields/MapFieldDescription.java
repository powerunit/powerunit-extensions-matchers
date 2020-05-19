/**
 * 
 */
package ch.powerunit.extensions.matchers.provideprocessor.fields;

import java.util.ArrayList;
import java.util.Collection;

import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;

public class MapFieldDescription extends DefaultFieldDescription {

	public MapFieldDescription(ProvidesMatchersAnnotatedElementData containingElementMirror,
			FieldDescriptionMirror mirror) {
		super(containingElementMirror, mirror);
	}

	@Override
	protected Collection<FieldDSLMethod> getSpecificFieldDslMethodFor() {
		String fieldType = getFieldType();
		final String emptyMatcher = "new org.hamcrest.CustomTypeSafeMatcher<" + fieldType
				+ ">(\"map is empty\"){ public boolean matchesSafely(" + fieldType + " o) {return o.isEmpty();}}";
		final String sizeMatcher = "new org.hamcrest.CustomTypeSafeMatcher<" + fieldType
				+ ">(\"map size is \"+other.size()){ public boolean matchesSafely(" + fieldType
				+ " o) {return o.size()==other.size();} protected void describeMismatchSafely("+fieldType+" item, org.hamcrest.Description mismatchDescription) {mismatchDescription.appendText(\" was size=\").appendValue(item.size());}}";
		Collection<FieldDSLMethod> tmp = new ArrayList<>();
		tmp.add(getDslMethodBuilder().withSuffixDeclarationJavadocAndDefault("IsEmpty", "the map is empty",
				emptyMatcher));
		if (!"".equals(generic)) {
			tmp.add(getDslMethodBuilder().withDeclaration("HasSameValues", fieldType + " other")
					.withJavaDoc("verify that the value from the other map are exactly the once inside this map",
							"other the other map")
					.havingDefault(MATCHERS + ".both(" + sizeMatcher + ").and(" + MATCHERS
							+ ".allOf(other.entrySet().stream().map(kv->" + MATCHERS + ".hasEntry(" + MATCHERS
							+ ".is(kv.getKey())," + MATCHERS
							+ ".is(kv.getValue()))).collect(java.util.stream.Collectors.toList())))"));
		}
		return tmp;
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
