package eu.xenit.move2alf.core.simpleaction;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;

public class SAUploadTest {
	@Test
	public void testSingleFileUpload() {
		final SAUpload actionUnderTest = actionUnderTest();
		final ActionConfig config = actionConfigWithBatchSize(1);
		final FileInfo dummyFileInfo = dummyFileInfo();
		final List<FileInfo> result1 = actionUnderTest.execute(dummyFileInfo,
				config);
		assertEquals(1, result1.size());
		final List<FileInfo> result2 = actionUnderTest.execute(dummyFileInfo,
				config);
		assertEquals(1, result2.size());
	}

	@Test
	public void testBatchUpload() {
		final SAUpload actionUnderTest = actionUnderTest();
		final ActionConfig config = actionConfigWithBatchSize(3);
		final FileInfo dummyFileInfo = dummyFileInfo();
		final List<FileInfo> result1 = actionUnderTest.execute(dummyFileInfo,
				config);
		assertEquals(0, result1.size());
		final List<FileInfo> result2 = actionUnderTest.execute(dummyFileInfo,
				config);
		assertEquals(0, result2.size());
		final List<FileInfo> result3 = actionUnderTest.execute(dummyFileInfo,
				config);
		assertEquals(3, result3.size());
		final List<FileInfo> result4 = actionUnderTest.execute(dummyFileInfo,
				config);
		assertEquals(0, result4.size());
		final List<FileInfo> result5 = actionUnderTest.execute(dummyFileInfo,
				config);
		assertEquals(0, result5.size());
		final List<FileInfo> result6 = actionUnderTest.execute(dummyFileInfo,
				config);
		assertEquals(3, result6.size());
	}

	private FileInfo dummyFileInfo() {
		final FileInfo dummyFileInfo = new FileInfo();
		dummyFileInfo.put(Parameters.PARAM_CYCLE, 123);
		dummyFileInfo.put(Parameters.PARAM_RELATIVE_PATH, "/foo");
		return dummyFileInfo;
	}

	private SAUpload actionUnderTest() {
		final SourceSink mockSink = mockSink();
		final ConfiguredSourceSink mockSinkConfig = null;
		return new SAUpload(mockSink, mockSinkConfig);
	}

	private ActionConfig actionConfigWithBatchSize(final int batchSize) {
		final ActionConfig config = new ActionConfig();
		config.put(SAUpload.PARAM_BATCH_SIZE, Integer.toString(batchSize));
		config.put(SAUpload.PARAM_PATH, "/");
		return config;
	}

	private SourceSink mockSink() {
		return new SourceSink() {

			@Override
			public void send(final ConfiguredSourceSink configuredSourceSink,
					final String docExistsMode, final String basePath,
					final String remotePath, final String mimeType,
					final String namespace, final String contentType,
					final String description,
					final Map<String, String> metadata,
					final Map<String, String> multiValueMetadata,
					final Map<String, Map<String, String>> acl,
					final boolean inheritPermissions, final File document) {
				// TODO Auto-generated method stub

			}

			@Override
			public List<File> list(final ConfiguredSourceSink sourceConfig,
					final String path, final boolean recursive) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean exists(final ConfiguredSourceSink sinkConfig,
					final String remotePath, final String name) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void delete(final ConfiguredSourceSink sinkConfig,
					final String remotePath, final String name) {
				// TODO Auto-generated method stub

			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getCategory() {
				// TODO Auto-generated method stub
				return null;
			}

		};
	}
}
