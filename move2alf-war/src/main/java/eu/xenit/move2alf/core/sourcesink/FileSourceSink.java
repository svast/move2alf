package eu.xenit.move2alf.core.sourcesink;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.repository.UploadResult;
import eu.xenit.move2alf.repository.alfresco.ws.Document;

public class FileSourceSink extends SourceSink {

	private static final Logger logger = LoggerFactory
			.getLogger(FileSourceSink.class);

	@Override
	public List<File> list(final ConfiguredSourceSink sourceConfig,
			final String path, final boolean recursive) {
		logger.info("Reading files from " + path);
		final File source = new File(path);
		if (source.exists() == false) {
			source.mkdir();
		}
		return listFiles(source, recursive, new ArrayList<File>());
	}

	private List<File> listFiles(final File source, final boolean recursive,
			final ArrayList<File> fileList) {
		final File[] files = source.listFiles();
		for (final File file : files) {
			if (recursive && file.isDirectory()) {
				listFiles(file, recursive, fileList);
			}
			if (file.isFile()) {
				fileList.add(file);
			}
		}
		return fileList;
	}

	@Override
	public void send(final ConfiguredSourceSink configuredSourceSink,
			final WriteOption docExistsMode,
			final String remotePath, final String mimeType,
			final String namespace, final String contentType,
			final String description, final Map<String, String> metadata,
			final Map<String, String> multiValueMetadata,
			final File document) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean exists(final ConfiguredSourceSink sinkConfig,
			final String remotePath, final String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_DEFAULT;
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Filesystem";
	}

	@Override
	public void delete(final ConfiguredSourceSink sinkConfig,
			final String remotePath, final String name, DeleteOption option) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearCaches(final ConfiguredSourceSink sinkConfig) {
		// nothing to do here
	}

	@Override
	public HashMap<String, UploadResult> sendBatch(final ConfiguredSourceSink configuredSourceSink,
			final WriteOption docExistsMode, final List<Document> documents) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setACL(final ConfiguredSourceSink configuredSourceSink,
			final ACL acls) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean fileNameExists(ConfiguredSourceSink sinkConfig, String name) {
		// TODO Auto-generated method stub
		return false;
	}
}
