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

public class FilesystemMetadataLoaderTest {

	private static final String DIR = "src/test/resources/testdocs";

	private FilesystemMetadataLoader metadataLoader;
	private File file;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		metadataLoader = new FilesystemMetadataLoader();
		file = new File(DIR, "testfile_txt.txt");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testHasMetadata() {
		assertEquals(true, metadataLoader.hasMetadata(file));
	}

	//TODO java.io.IOException: Cannot run program "lib/extractWindowsFileSystemProperties": CreateProcess error=2, Het systeem kan het opgegeven bestand niet vinden
	@Ignore
	@Test
	public final void testLoadMetadata() {
		Map<String, String> metadata = metadataLoader.loadMetadata(file);
		
		assertEquals(3, metadata.size());
		assertNotNull(metadata.get(FilesystemMetadataLoader.fsPropCreationDate));
		assertNotNull(metadata.get(FilesystemMetadataLoader.fsPropCreator));
		assertNotNull(metadata.get(FilesystemMetadataLoader.fsPropModifyDate));
	}
}
