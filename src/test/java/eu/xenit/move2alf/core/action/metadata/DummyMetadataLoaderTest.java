package eu.xenit.move2alf.core.action.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class DummyMetadataLoaderTest {

	private static final String DIR = "src/test/resources/testdocs";

	private static DummyMetadataLoader metadataLoader;
	private File file;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		metadataLoader = new DummyMetadataLoader();
		file = new File(DIR, "testfile_txt.txt");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testHasMetadata() {
		assertEquals(true, metadataLoader.hasMetadata(file));
	}

	@Test
	public final void testLoadMetadata() {
		Map<String, String> metadata = metadataLoader.loadMetadata(file);
		
		assertEquals(0, metadata.size());
	}
}
