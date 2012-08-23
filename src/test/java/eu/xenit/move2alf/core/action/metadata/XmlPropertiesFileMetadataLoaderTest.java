package eu.xenit.move2alf.core.action.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.xenit.move2alf.common.exceptions.Move2AlfException;

public class XmlPropertiesFileMetadataLoaderTest {

	private static final String DIR = "src/test/resources/testdocs";
	
	private XmlPropertiesFileMetadataLoader metadataLoader;
	private File contentFile;
	private File metadataFile;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		metadataLoader = new XmlPropertiesFileMetadataLoader();
		contentFile = new File(DIR, "testfile_txt.txt");
		metadataFile = new File(DIR, "testfile_txt.txt.metadata.properties.xml");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testHasMetadata() {
		assertEquals(true, metadataLoader.hasMetadata(contentFile));
		assertEquals(false, metadataLoader.hasMetadata(metadataFile));
	}

	@Test
	public final void testLoadMetadataForContentFile() {
		Map<String, String> metadata = metadataLoader.loadMetadata(contentFile);
		
		assertEquals(1, metadata.size());
		assertNull(metadata.get("type"));
		assertNull(metadata.get("aspects"));
		assertEquals("ikke", metadata.get("contributor"));
	}

	@Test
	public final void testLoadMetadataForMetadataFile() {
		Map<String, String> metadata = metadataLoader.loadMetadata(metadataFile);
		
		assertEquals(0, metadata.size());
	}

	@Test (expected = Move2AlfException.class)
	public final void testLoadMetadataForNonExistingContentFile() {
		File file = new File(DIR, "xyz");
		@SuppressWarnings("unused")
		Map<String, String> metadata = metadataLoader.loadMetadata(file);
	}

	@Test (expected = Move2AlfException.class)
	public final void testLoadMetadataForCorruptPropertiesFile() {
		File file = new File(DIR, "testfile_metadataCorrupt.txt");
		@SuppressWarnings("unused")
		Map<String, String> metadata = metadataLoader.loadMetadata(file);
	}
}
