package eu.xenit.move2alf.core.simpleaction;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.sourcesink.ACL;
import eu.xenit.move2alf.core.sourcesink.SourceSink;
import eu.xenit.move2alf.repository.alfresco.ws.Document;

public class SAUploadTest {
	@Captor
	ArgumentCaptor<List<Document>> documentsCaptor;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testSingleFileUpload() {
		final SourceSink mockSink = mock(SourceSink.class);
		final SAUpload actionUnderTest = actionUnderTest(mockSink);
		final ActionConfig config = actionConfigWithBatchSize(1);
		final FileInfo dummyFileInfo = dummyFileInfo();

		// file 1
		final List<FileInfo> result1 = actionUnderTest.execute(dummyFileInfo,
				config);
		assertEquals(1, result1.size());
		verify(mockSink).sendBatch(any(ConfiguredSourceSink.class),
				anyString(), documentsCaptor.capture());
		assertEquals(1, documentsCaptor.getValue().size());

		// file 2
		reset(mockSink);
		final List<FileInfo> result2 = actionUnderTest.execute(dummyFileInfo,
				config);
		assertEquals(1, result2.size());
		verify(mockSink).sendBatch(any(ConfiguredSourceSink.class),
				anyString(), documentsCaptor.capture());
		assertEquals(1, documentsCaptor.getValue().size());

		verifyNoMoreInteractions(mockSink);
	}

	@Test
	public void testBatchUpload() {
		final SourceSink mockSink = mock(SourceSink.class);
		final SAUpload actionUnderTest = actionUnderTest(mockSink);
		final ActionConfig config = actionConfigWithBatchSize(3);
		final FileInfo dummyFileInfo = dummyFileInfo();

		// batch 1, three files
		final List<FileInfo> result1 = actionUnderTest.execute(dummyFileInfo,
				config);
		assertEquals(0, result1.size());
		verifyZeroInteractions(mockSink);
		final List<FileInfo> result2 = actionUnderTest.execute(dummyFileInfo,
				config);
		assertEquals(0, result2.size());
		verifyZeroInteractions(mockSink);
		final List<FileInfo> result3 = actionUnderTest.execute(dummyFileInfo,
				config);
		assertEquals(3, result3.size());
		verify(mockSink).sendBatch(any(ConfiguredSourceSink.class),
				anyString(), documentsCaptor.capture());
		assertEquals(3, documentsCaptor.getValue().size());

		// batch 2, three files
		reset(mockSink);
		final List<FileInfo> result4 = actionUnderTest.execute(dummyFileInfo,
				config);
		assertEquals(0, result4.size());
		verifyZeroInteractions(mockSink);
		final List<FileInfo> result5 = actionUnderTest.execute(dummyFileInfo,
				config);
		assertEquals(0, result5.size());
		verifyZeroInteractions(mockSink);
		final List<FileInfo> result6 = actionUnderTest.execute(dummyFileInfo,
				config);
		assertEquals(3, result6.size());
		verify(mockSink).sendBatch(any(ConfiguredSourceSink.class),
				anyString(), documentsCaptor.capture());
		assertEquals(3, documentsCaptor.getValue().size());

		verifyNoMoreInteractions(mockSink);
	}

	private FileInfo dummyFileInfo() {
		final FileInfo dummyFileInfo = new FileInfo();
		dummyFileInfo.put(Parameters.PARAM_CYCLE, 123);
		dummyFileInfo.put(Parameters.PARAM_RELATIVE_PATH, "/foo");
		return dummyFileInfo;
	}

	@Test
	public void testUploadWithACL() {
		final SourceSink mockSink = mock(SourceSink.class);
		final SAUpload actionUnderTest = actionUnderTest(mockSink);
		final ActionConfig config = actionConfigWithBatchSize(2);
		final FileInfo dummyFileInfoWithoutACL = dummyFileInfo();
		final FileInfo dummyFileInfoWithACL = dummyFileInfoWithACL();

		// batch 1, 2 acls
		final List<FileInfo> result1 = actionUnderTest.execute(
				dummyFileInfoWithACL, config);
		assertEquals(0, result1.size());
		verifyZeroInteractions(mockSink);
		final List<FileInfo> result2 = actionUnderTest.execute(
				dummyFileInfoWithACL, config);
		assertEquals(2, result2.size());
		final InOrder inOrder = Mockito.inOrder(mockSink);
		inOrder.verify(mockSink).sendBatch(any(ConfiguredSourceSink.class),
				anyString(), documentsCaptor.capture());
		assertEquals(2, documentsCaptor.getValue().size());
		inOrder.verify(mockSink, times(2)).setACL(
				any(ConfiguredSourceSink.class), any(ACL.class));

		// batch 2, 1 acl
		reset(mockSink);
		final List<FileInfo> result3 = actionUnderTest.execute(
				dummyFileInfoWithoutACL, config);
		assertEquals(0, result3.size());
		verifyZeroInteractions(mockSink);
		final List<FileInfo> result4 = actionUnderTest.execute(
				dummyFileInfoWithACL, config);
		assertEquals(2, result4.size());
		verify(mockSink).sendBatch(any(ConfiguredSourceSink.class),
				anyString(), documentsCaptor.capture());
		assertEquals(2, documentsCaptor.getValue().size());
		verify(mockSink)
				.setACL(any(ConfiguredSourceSink.class), any(ACL.class));

		verifyNoMoreInteractions(mockSink);
	}

	private FileInfo dummyFileInfoWithACL() {
		final FileInfo dummyFileInfo = new FileInfo();
		dummyFileInfo.put(Parameters.PARAM_CYCLE, 123);
		dummyFileInfo.put(Parameters.PARAM_RELATIVE_PATH, "/foo");
		final Map<String, Map<String, String>> acls = new HashMap<String, Map<String, String>>();
		dummyFileInfo.put(Parameters.PARAM_ACL, acls);
		dummyFileInfo.put(Parameters.PARAM_INHERIT_PERMISSIONS, true);
		return dummyFileInfo;
	}

	private SAUpload actionUnderTest(final SourceSink mockSink) {
		final ConfiguredSourceSink mockSinkConfig = null;
		return new SAUpload(mockSink, mockSinkConfig);
	}

	private ActionConfig actionConfigWithBatchSize(final int batchSize) {
		final ActionConfig config = new ActionConfig();
		config.put(SAUpload.PARAM_BATCH_SIZE, Integer.toString(batchSize));
		config.put(SAUpload.PARAM_PATH, "/");
		return config;
	}
}
