package eu.xenit.move2alf.freemarker;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.*;
import org.springframework.util.Assert;

import eu.xenit.move2alf.core.action.FilesystemMetadataAction;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.ProcessedDocument;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.enums.EProcessedDocumentStatus;
import eu.xenit.move2alf.freemarkerchecktool.FreemarkerCheckTool;
import eu.xenit.move2alf.freemarkerchecktool.RootMap;
import eu.xenit.move2alf.web.dto.JobConfig;

@Ignore
public class FreemarkerTemplatesTest {
	public static FreemarkerCheckTool freemarkerCheckTool;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		freemarkerCheckTool = new FreemarkerCheckTool();
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

	@Test
	public void testCreateJob() throws Exception {
		RootMap rootMap = createJobRootMap();
		String result = freemarkerCheckTool.processTemplateIntoString("create-job.ftl", rootMap);
        Assert.notNull(result);
        System.out.println(result);
	}

	@Test
	public void testEditJob() throws Exception {
		RootMap rootMap = createJobRootMap();
		String result = freemarkerCheckTool.processTemplateIntoString("edit-job.ftl", rootMap);
        Assert.notNull(result);
        System.out.println(result);
	}

	@Test
	public void testExportReportCsv() throws Exception {
		RootMap rootMap = createReportRootMap();
		String result = freemarkerCheckTool.processTemplateIntoString("export-report-csv.ftl", rootMap);
        Assert.notNull(result);
        System.out.println(result);
	}

	@Test
	public void testExportReportCsv_old() throws Exception {
		//TODO remove this test; this test is a duplicate of testExportReportCsv
		RootMap rootMap = createRootMap2();
		String result = freemarkerCheckTool.processTemplateIntoString("export-report-csv.ftl", rootMap);
        Assert.notNull(result);
        System.out.println(result);
	}

	private RootMap createReportRootMap () throws Exception {
		RootMap rootMap = freemarkerCheckTool.createRootMap();
		rootMap.add("job", Job.class);
		rootMap.add("cycle", Cycle.class);
		rootMap.add("duration", String.class);
		rootMap.add("documentListSize", Long.class);
		rootMap.add("docsPerSecond", String.class);
		rootMap.addList("processedDocuments", ProcessedDocument.class);
		
		return rootMap;
	}

	private RootMap createRootMap2 () throws Exception {
		//TODO test rootMap.addValue in unit test RootMapTest
		RootMap rootMap = freemarkerCheckTool.createRootMap();

		rootMap.addValue("duration", "x");
		rootMap.addValue("documentListSize", "x");
		rootMap.addValue("docsPerSecond", "x");
		
		Job job = new Job();
		job.setName("x");
		job.setDescription("x");
		rootMap.addValue("job", job);
		
		Cycle cycle = new Cycle();
		cycle.setId(1);
		cycle.setStartDateTime(new Date());
		cycle.setEndDateTime(new Date());
		rootMap.addValue("cycle", cycle);
		
		List<ProcessedDocument> processedDocuments = new ArrayList<ProcessedDocument>();
		ProcessedDocument processedDocument = new ProcessedDocument();
		processedDocument.setName("x");
		processedDocument.setProcessedDateTime(new Date());
		processedDocument.setStatus(EProcessedDocumentStatus.OK);
		Set<ProcessedDocumentParameter> processedDocumentParameterSet = new HashSet<ProcessedDocumentParameter>();
		ProcessedDocumentParameter processedDocumentParameter = new ProcessedDocumentParameter();
		processedDocumentParameter.setName("x");
		processedDocumentParameter.setValue("x");
		processedDocumentParameterSet.add(processedDocumentParameter);
		processedDocument.setProcessedDocumentParameterSet(processedDocumentParameterSet);
		processedDocuments.add(processedDocument);
		rootMap.addValue("processedDocuments", processedDocuments);
		
		return rootMap;
	}

	private RootMap createJobRootMap () throws Exception {
		//TODO remove the usage of rootMap.addValue
		RootMap rootMap = freemarkerCheckTool.createRootMap();

		ArrayList<Object> destinations = new ArrayList<Object>();

		ArrayList<Object> transformOptions = new ArrayList<Object>();

		ArrayList<Object> destinationOptions = new ArrayList<Object>();
		
		rootMap.add("job", JobConfig.class);
		rootMap.addValue("destinations", destinations); //TODO rootMap.addList("destinations", ... .class)
		rootMap.addList("metadataOptions", FilesystemMetadataAction.class);
		rootMap.addValue("transformOptions", transformOptions); //TODO rootMap.addList("transformOptions", ... .class)
		rootMap.addValue("destinationOptions", destinationOptions); //TODO rootMap.addList("destinationOptions", ... .class)
		rootMap.addValue("role", "x");
		
		rootMap.add("docsPerSecond", JobConfig.class);
		rootMap.addList("processedDocuments", ProcessedDocument.class);
		
		return rootMap;
	}
}