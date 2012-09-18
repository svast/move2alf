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


public class TotalStringSizeTest {

	private static Validator validator;
	private TotalStringSizeTestClass totalStringSizeTestClass;

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
		totalStringSizeTestClass = new TotalStringSizeTestClass();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public final void testTotalStringSize1 () {
		ArrayList<String> stringList = new ArrayList<String>();
		stringList.add("1234567890");
		totalStringSizeTestClass.setStringList(stringList);
		
		Set<ConstraintViolation<TotalStringSizeTestClass>> constraintViolations = validator.validate(totalStringSizeTestClass);
		
		assertEquals(0, constraintViolations.size());
	}
	
	@Test
	public final void testTotalStringSize2 () {
		ArrayList<String> stringList = new ArrayList<String>();
		stringList.add("1234567890a");
		totalStringSizeTestClass.setStringList(stringList);
		
		Set<ConstraintViolation<TotalStringSizeTestClass>> constraintViolations = validator.validate(totalStringSizeTestClass);
		
		assertEquals(1, constraintViolations.size());
		assertEquals("Max length is 10", constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public final void testTotalStringSize3 () {
		ArrayList<String> stringList = new ArrayList<String>();
		stringList.add("12345678");
		stringList.add("a");
		totalStringSizeTestClass.setStringList(stringList);
		
		Set<ConstraintViolation<TotalStringSizeTestClass>> constraintViolations = validator.validate(totalStringSizeTestClass);
		
		assertEquals(0, constraintViolations.size());
	}
	
	@Test
	public final void testTotalStringSize4 () {
		ArrayList<String> stringList = new ArrayList<String>();
		stringList.add("12345678");
		stringList.add("ab");
		totalStringSizeTestClass.setStringList(stringList);
		
		Set<ConstraintViolation<TotalStringSizeTestClass>> constraintViolations = validator.validate(totalStringSizeTestClass);
		
		assertEquals(1, constraintViolations.size());
		assertEquals("Max length is 10", constraintViolations.iterator().next().getMessage());
	}

}
