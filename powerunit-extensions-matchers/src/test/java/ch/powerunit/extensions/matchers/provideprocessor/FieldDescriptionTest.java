package ch.powerunit.extensions.matchers.provideprocessor;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.mockito.Mock;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;

public class FieldDescriptionTest implements TestSuite {
	@Rule
	public final TestRule rules = mockitoRule();

	@Mock
	private TypeMirror fieldTypeMirrorMainInterface;

	@Mock
	private TypeMirror fieldTypeMirror1;

	@Mock
	private TypeMirror fieldTypeMirror2;

	@Mock
	private DeclaredType fieldTypeMirrorAsDeclaredType;

	@Test
	public void testComputeGenericInformationIsNotDeclaredTypeThenEmptyString() {
		assertThatFunction(FieldDescription::computeGenericInformation, fieldTypeMirrorMainInterface).is("");
	}

	@Test
	public void testComputeGenericInformationIsDeclaredTypeThenJoinArgumentType() {
		when(fieldTypeMirror1.toString()).thenReturn("X");
		when(fieldTypeMirror2.toString()).thenReturn("Y");
		when(fieldTypeMirrorAsDeclaredType.getTypeArguments())
				.thenReturn((List) Arrays.asList(fieldTypeMirror1, fieldTypeMirror2));
		assertThatFunction(FieldDescription::computeGenericInformation, fieldTypeMirrorAsDeclaredType).is("X,Y");
	}
}
