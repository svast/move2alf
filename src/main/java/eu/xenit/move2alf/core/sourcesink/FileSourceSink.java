package eu.xenit.move2alf.core.sourcesink;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.SourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;

public class FileSourceSink extends SourceSink {

	private static final Logger logger = LoggerFactory
			.getLogger(FileSourceSink.class);

	@Override
	public List<File> list(ConfiguredSourceSink sourceConfig, String path,
			boolean recursive) {
		logger.info("Reading files from " + path);
		File source = new File(path);
		if(source.exists() == false){
			source.mkdir();
		}
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
	public void send(ConfiguredSourceSink configuredSourceSink,
			Map<String, Object> parameterMap, String path, String docExistsMode) {

	}
	
	@Override
	public boolean exists(ConfiguredSourceSink sinkConfig, String remotePath,
			String name) {
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
	public void delete(ConfiguredSourceSink sinkConfig, String remotePath,
			String name) {
		// TODO Auto-generated method stub
		
	}

}
