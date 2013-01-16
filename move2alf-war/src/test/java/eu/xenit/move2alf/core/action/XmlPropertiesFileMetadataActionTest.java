package eu.xenit.move2alf.core.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.action.metadata.XmlPropertiesFileMetadataLoader;

public class XmlPropertiesFileMetadataActionTest {

	private static final String DIR = "src/test/resources/testdocs";
	
	private MetadataAction action;
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
		action = new XmlPropertiesFileMetadataAction();
		contentFile = new File(DIR, "testfile_txt.txt");
		metadataFile = new File(DIR, "testfile_txt.txt.metadata.properties.xml");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testInitMetadataLoader() {
		assertEquals(1, action.metadataLoaders.size());
		assertEquals(XmlPropertiesFileMetadataLoader.class.getName(), action.metadataLoaders.elementAt(0).getClass().getName());
	}

	@Test
	public final void testExecuteImplForContentFile() {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put(Parameters.PARAM_FILE, contentFile);
		
		action.executeImpl(null, parameterMap);
		
		assertEquals(4, parameterMap.size());
		
		assertEquals(contentFile, parameterMap.get(Parameters.PARAM_FILE));
		assertEquals("{http://www.alfresco.org/model/content/1.0}", parameterMap.get(Parameters.PARAM_NAMESPACE));
		assertEquals("content", parameterMap.get(Parameters.PARAM_CONTENTTYPE));
		
		@SuppressWarnings("unchecked")
		Map<String, String> metadata = (Map<String, String>) parameterMap.get(Parameters.PARAM_METADATA);
		assertNotNull(metadata);
	}

	@Test
	public final void testExecuteImplForMetadataFile() {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put(Parameters.PARAM_FILE, metadataFile);
		
		action.executeImpl(null, parameterMap);

		assertEquals(1, parameterMap.size());
		assertEquals(metadataFile, parameterMap.get(Parameters.PARAM_FILE));
	}
}
