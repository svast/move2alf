package eu.xenit.move2alf.core.simpleaction;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.xenit.move2alf.common.exceptions.Move2AlfException;

public class SAMimeTypeTest {

	private static final String DIR = "src/test/resources/testdocs";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test (expected=Move2AlfException.class)
	public final void testDetermineMimeTypeOfNonExistingFile () {
		File file = new File(DIR, "xyz");
		SAMimeType saMimeType = new SAMimeType();
		saMimeType.determineMimeType(file);
	}

	@Test
	public final void testDetermineMimeType () {		
		testDetermineMimeType("testfile_gif.gif", "image/gif");
		testDetermineMimeType("testfile_gif", "image/gif");
		
		//incorrect extension
		testDetermineMimeType("testfile_gif.jpg", "image/gif");
		
		//file corrupt
		testDetermineMimeType("testfile_corrupt_gif.gif", "image/gif");
		testDetermineMimeType("testfile_corrupt_gif", "application/octet-stream");

		//no magic detection for powerpoint file without extension ...
		testDetermineMimeType("testfile_ppt.ppt", "application/vnd.ms-powerpoint");
		testDetermineMimeType("testfile_ppt", "application/x-tika-msoffice");
	}
	
	private final void testDetermineMimeType (String filename, String expectedResult) {
		File file = new File(DIR, filename);
		SAMimeType saMimeType = new SAMimeType();
		String mimeType = saMimeType.determineMimeType(file);
		
		assertEquals(expectedResult, mimeType);
	}
}
