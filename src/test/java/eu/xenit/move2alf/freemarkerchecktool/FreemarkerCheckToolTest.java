package eu.xenit.move2alf.freemarkerchecktool;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.Assert;

import eu.xenit.move2alf.web.dto.JobConfig;
import freemarker.core.InvalidReferenceException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

public class FreemarkerCheckToolTest {
	public static FreemarkerCheckTool freemarkerCheckTool;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		freemarkerCheckTool = new FreemarkerCheckTool("classpath:eu/xenit/move2alf/freemarkerchecktool");
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

	private Map<String, Object> createRootMap () {
		Map<String, Object> rootMap = freemarkerCheckTool.createRootMap();
		
		JobConfig jobConfig = new JobConfig();
		jobConfig.setName("x");
		jobConfig.setDescription("x");
		
		rootMap.put("job", jobConfig);
		
		return rootMap;
	}

	@Test
	public void testTemplate() throws Exception {
		Map<String, Object> rootMap = createRootMap();
		String result = freemarkerCheckTool.processTemplateIntoString("testTemplate.ftl", rootMap);
        Assert.notNull(result);
        assertEquals("    <textarea id=\"description\" name=\"description\" >x</textarea>\n" 
	        + "param=\"x\"; Name: x; Description: x", result);
	}

	@Test (expected = InvalidReferenceException.class)
	public void testTemplate_invalidFmReference() throws Exception {
		Map<String, Object> rootMap = createRootMap();
		freemarkerCheckTool.processTemplateIntoString("testTemplate_invalidFmReference.ftl", rootMap);
	}

	@Test (expected = TemplateModelException.class)
	public void testTemplate_invalidSpringBinding() throws Exception {
		Map<String, Object> rootMap = createRootMap();
		freemarkerCheckTool.processTemplateIntoString("testTemplate_invalidSpringBinding.ftl", rootMap);
	}

	@Test (expected = TemplateException.class)
	public void testTemplate_invalidMacroParam() throws Exception {
		Map<String, Object> rootMap = createRootMap();
		freemarkerCheckTool.processTemplateIntoString("testTemplate_invalidMacroParam.ftl", rootMap);
	}
}