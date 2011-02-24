package eu.xenit.move2alf.core.sourcesink;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.core.ConfiguredObject;
import eu.xenit.move2alf.core.SourceSink;

public class FileSourceSink extends SourceSink {

	private static final Logger logger = LoggerFactory
			.getLogger(FileSourceSink.class);

	@Override
	public List<File> list(ConfiguredObject sourceConfig, String path,
			boolean recursive) {
		File source = new File(path);
		return listFiles(source, recursive, new ArrayList<File>());
	}

	private List<File> listFiles(File source, boolean recursive,
			ArrayList<File> fileList) {
		File[] files = source.listFiles();
		for (File file : files) {
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
	public void send(ConfiguredObject configuredSourceSink,
			Map<String, Object> parameterMap) {

	}

}
