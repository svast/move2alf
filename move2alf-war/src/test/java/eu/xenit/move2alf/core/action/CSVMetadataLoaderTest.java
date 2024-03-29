package eu.xenit.move2alf.core.action;

import au.com.bytecode.opencsv.CSVReader;
import eu.xenit.move2alf.common.Parameters;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@Ignore
public class CSVMetadataLoaderTest {

	private static final String DIR = "src/test/resources/testdocs";

	private CSVMetadataLoader metadataLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public void setUp() throws Exception {
		metadataLoader = new CSVMetadataLoader();
        metadataLoader.setInputFile(new File(DIR, "testfile_csv.csv"));
	}

	public void tearDown() throws Exception {
	}

	@Test
	public final void testReadMetadataFields() throws Exception {
		setUp();
		CSVReader reader = metadataLoader.createReader();
		String[] metadataFields = metadataLoader.readMetadataFields(reader);

		assertEquals(4, metadataFields.length);
		assertEquals("path",metadataFields[0]);
		assertEquals("InputMetadata1",metadataFields[1]);
		assertEquals("InputMetadata2",metadataFields[2]);
		assertEquals("InputMetadata3",metadataFields[3]);
		reader.close();
	}

	@Test
	public final void testProcessLineWrongNumberOfFields() throws Exception {
		setUp();
		CSVReader reader = metadataLoader.createReader();
		String[] metadataFields = metadataLoader.readMetadataFields(reader);

		String[] line = {"a"};
		try {
			metadataLoader.processLine(line, metadataFields);
		} catch (Throwable e) {
			assert(true);
			return;
		}
		assert(false);
		reader.close();
	}

	@Test
	public final void testProcessLine() throws Exception {
		setUp();
		CSVReader reader = metadataLoader.createReader();
		String[] metadataFields = metadataLoader.readMetadataFields(reader);
		HashMap parameterMap = new HashMap();

		String[] line = {"/home/rox/m2a-dev-tutorial/input-csv/file1.txt","metadata11","metadata21","metadata31"};
		metadataLoader.processLine(line, metadataFields);

		assertEquals("/home/rox/m2a-dev-tutorial/input-csv/file1.txt",((File)(parameterMap.get(Parameters.PARAM_FILE))).getAbsolutePath());
		HashMap<String,String> metadata = (HashMap)parameterMap.get(Parameters.PARAM_METADATA);
		assertEquals("metadata11",metadata.get(metadataFields[1]));
		assertEquals("metadata21",metadata.get(metadataFields[2]));
		assertEquals("metadata31",metadata.get(metadataFields[3]));
		reader.close();
	}
}
