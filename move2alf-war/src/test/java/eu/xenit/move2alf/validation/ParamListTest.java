package eu.xenit.move2alf.validation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class ParamListTest {

	private static Validator validator;
	private ParamListTestClass paramListTestClass;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		paramListTestClass = new ParamListTestClass();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public final void testParamList_NullParam () {
		paramListTestClass.setStringList(null);
		
		Set<ConstraintViolation<ParamListTestClass>> constraintViolations = validator.validate(paramListTestClass);
		
		assertEquals(0, constraintViolations.size());
	}
	
	@Test
	public final void testParamList_NoParams () {
		ArrayList<String> stringList = new ArrayList<String>();
		paramListTestClass.setStringList(stringList);
		
		Set<ConstraintViolation<ParamListTestClass>> constraintViolations = validator.validate(paramListTestClass);
		
		assertEquals(0, constraintViolations.size());
	}
	
	@Test
	public final void testParamList_OneValidParam () {
		ArrayList<String> stringList = new ArrayList<String>();
		stringList.add("12345@@@1234567890");
		paramListTestClass.setStringList(stringList);
		
		Set<ConstraintViolation<ParamListTestClass>> constraintViolations = validator.validate(paramListTestClass);
		
		assertEquals(0, constraintViolations.size());
	}
	
	@Test
	public final void testParamList_TwoValidParams () {
		ArrayList<String> stringList = new ArrayList<String>();
		stringList.add("12345@@@1234567890");
		stringList.add("12345@@@1234567890");
		paramListTestClass.setStringList(stringList);
		
		Set<ConstraintViolation<ParamListTestClass>> constraintViolations = validator.validate(paramListTestClass);
		
		assertEquals(0, constraintViolations.size());
	}
	
	@Test
	public final void testParamList_InvalidKey () {
		ArrayList<String> stringList = new ArrayList<String>();
		stringList.add("123456|1234567890");
		paramListTestClass.setStringList(stringList);
		
		Set<ConstraintViolation<ParamListTestClass>> constraintViolations = validator.validate(paramListTestClass);
		
		assertEquals(1, constraintViolations.size());
		assertEquals("Max length is 5-10", constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public final void testParamList_InvalidValue () {
		ArrayList<String> stringList = new ArrayList<String>();
		stringList.add("12345|12345678901");
		paramListTestClass.setStringList(stringList);
		
		Set<ConstraintViolation<ParamListTestClass>> constraintViolations = validator.validate(paramListTestClass);
		
		assertEquals(1, constraintViolations.size());
		assertEquals("Max length is 5-10", constraintViolations.iterator().next().getMessage());
	}

}
