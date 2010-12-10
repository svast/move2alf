package eu.xenit.move2alf;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.xenit.move2alf.common.ParameterDefinition;

public class ParameterDefinitionTest {
	@Test
	public void testParameterDefinition1() {
		ParameterDefinition<String> paramDef = new ParameterDefinition<String>(
				"foo", String.class, "bar");
		Object o = "sdfsd";
		String s = paramDef.cast(o);
		assertEquals(s, (String) o);
	}
	
	@Test
	public void testParameterDefinition2() {
		ParameterDefinition<Object> paramDef = new ParameterDefinition<Object>(
				"foo", String.class, "bar");
		Object o = "sdfsd";
		String s = (String) paramDef.cast(o);
		assertEquals(s, (String) o);
	}
}
