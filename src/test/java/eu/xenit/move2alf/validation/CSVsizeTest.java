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


public class CSVsizeTest {

	private static Validator validator;
	private CSVsizeTestClass csvSizeTestClass;

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
		csvSizeTestClass = new CSVsizeTestClass();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public final void testCSVsize_ValidEmptyStringList () {
		ArrayList<String> stringList = new ArrayList<String>();
		csvSizeTestClass.setStringList(stringList);
		
		Set<ConstraintViolation<CSVsizeTestClass>> constraintViolations = validator.validate(csvSizeTestClass);
		
		assertEquals(0, constraintViolations.size());
	}
	
	@Test
	public final void testCSVsize_ValidOneStringList () {
		ArrayList<String> stringList = new ArrayList<String>();
		stringList.add("1234567890");
		csvSizeTestClass.setStringList(stringList);
		
		Set<ConstraintViolation<CSVsizeTestClass>> constraintViolations = validator.validate(csvSizeTestClass);
		
		assertEquals(0, constraintViolations.size());
	}
	
	@Test
	public final void testCSVsize_InvalidOneStringList () {
		ArrayList<String> stringList = new ArrayList<String>();
		stringList.add("1234567890a");
		csvSizeTestClass.setStringList(stringList);
		
		Set<ConstraintViolation<CSVsizeTestClass>> constraintViolations = validator.validate(csvSizeTestClass);
		
		assertEquals(1, constraintViolations.size());
		assertEquals("Max length is 10", constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public final void testCSVsize_ValidTwoStringList () {
		ArrayList<String> stringList = new ArrayList<String>();
		stringList.add("12345678");
		stringList.add("a");
		csvSizeTestClass.setStringList(stringList);
		
		Set<ConstraintViolation<CSVsizeTestClass>> constraintViolations = validator.validate(csvSizeTestClass);
		
		assertEquals(0, constraintViolations.size());
	}
	
	@Test
	public final void testCSVsize_InvalidTwoStringList () {
		ArrayList<String> stringList = new ArrayList<String>();
		stringList.add("12345678");
		stringList.add("ab");
		csvSizeTestClass.setStringList(stringList);
		
		Set<ConstraintViolation<CSVsizeTestClass>> constraintViolations = validator.validate(csvSizeTestClass);
		
		assertEquals(1, constraintViolations.size());
		assertEquals("Max length is 10", constraintViolations.iterator().next().getMessage());
	}

}
