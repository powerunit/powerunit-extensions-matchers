package ch.powerunit.extensions.matchers.common;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;

import ch.powerunit.Test;
import ch.powerunit.extensions.matchers.TestSuiteSupport;

public class ElementHelperTest implements TestSuiteSupport, ElementHelper {

	@Test
	public void testGetSimpleName() {
		assertThatFunction(this::getSimpleName,
				generateMockitoProcessingEnvironment().getElementUtils().getTypeElement("java.lang.Object"))
						.is("Object");
	}

	@Test
	public void testGetQualifiedName() {
		assertThatFunction(this::getQualifiedName,
				generateMockitoProcessingEnvironment().getElementUtils().getTypeElement("java.lang.Object"))
						.is("java.lang.Object");
	}

	@Test(fastFail = false)
	public void testIsSimpleName() {
		assertThatBiFunction(this::isSimpleName,
				generateMockitoProcessingEnvironment().getElementUtils().getTypeElement("java.lang.Object"), "Object")
						.is(true);
		assertThatBiFunction(this::isSimpleName,
				generateMockitoProcessingEnvironment().getElementUtils().getTypeElement("java.lang.Object"), "other")
						.is(false);
	}

	@Test(fastFail = false)
	public void testBoundsAsString() {
		TypeParameterElement tpe = mock(TypeParameterElement.class);
		TypeMirror type1 = mock(TypeMirror.class);
		TypeMirror type2 = mock(TypeMirror.class);
		doReturn(Collections.emptyList()).when(tpe).getBounds();
		assertThatFunction(this::boundsAsString, tpe).is("");
		when(type1.toString()).thenReturn("T1");
		doReturn(Collections.singletonList(type1)).when(tpe).getBounds();
		assertThatFunction(this::boundsAsString, tpe).is("T1");
		when(type1.toString()).thenReturn("T1");
		when(type2.toString()).thenReturn("T2");
		doReturn(Arrays.asList(type1, type2)).when(tpe).getBounds();
		assertThatFunction(this::boundsAsString, tpe).is("T1&T2");
	}

	@Test(fastFail = false)
	public void testGetGeneric() {
		TypeParameterElement tpe1 = mock(TypeParameterElement.class);
		TypeParameterElement tpe2 = mock(TypeParameterElement.class);
		TypeElement te = mock(TypeElement.class);
		doReturn(Collections.emptyList()).when(te).getTypeParameters();
		assertThatFunction(this::getGeneric, te).is("");
		doReturn(Collections.singletonList(tpe1)).when(te).getTypeParameters();
		when(tpe1.toString()).thenReturn("T1");
		assertThatFunction(this::getGeneric, te).is("<T1>");
		doReturn(Arrays.asList(tpe1, tpe2)).when(te).getTypeParameters();
		when(tpe1.toString()).thenReturn("T1");
		when(tpe2.toString()).thenReturn("T2");
		assertThatFunction(this::getGeneric, te).is("<T1,T2>");
	}

	@Test(fastFail = false)
	public void testGetFullGeneric() {
		TypeParameterElement tpe1 = mock(TypeParameterElement.class);
		TypeParameterElement tpe2 = mock(TypeParameterElement.class);
		TypeMirror type1 = mock(TypeMirror.class);
		TypeMirror type2 = mock(TypeMirror.class);
		TypeElement te = mock(TypeElement.class);
		doReturn(Collections.emptyList()).when(te).getTypeParameters();
		assertThatFunction(this::getFullGeneric, te).is("");
		doReturn(Collections.singletonList(tpe1)).when(te).getTypeParameters();
		when(tpe1.toString()).thenReturn("T1");
		assertThatFunction(this::getFullGeneric, te).is("<T1>");
		doReturn(Arrays.asList(tpe1, tpe2)).when(te).getTypeParameters();
		when(tpe1.toString()).thenReturn("T1");
		when(tpe2.toString()).thenReturn("T2");
		assertThatFunction(this::getFullGeneric, te).is("<T1,T2>");
		doReturn(Arrays.asList(tpe1, tpe2)).when(te).getTypeParameters();
		when(tpe1.toString()).thenReturn("T1");
		when(tpe2.toString()).thenReturn("T2");
		doReturn(Arrays.asList(type1, type2)).when(tpe1).getBounds();
		when(type1.toString()).thenReturn("T1");
		when(type2.toString()).thenReturn("T2");
		assertThatFunction(this::getFullGeneric, te).is("<T1 extends T1&T2,T2>");
	}
}
