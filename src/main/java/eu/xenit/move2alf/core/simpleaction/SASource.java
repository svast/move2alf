package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.simpleaction.data.ActionConfig;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.core.sourcesink.FileSourceSink;

public class SASource extends SimpleAction {

	@Override
	public List<FileInfo> execute(
			final FileInfo parameterMap,
			final ActionConfig config, final Map<String, Serializable> state) {
		FileSourceSink source = new FileSourceSink();
		File inputFile = (File) parameterMap.get(Parameters.PARAM_FILE);
		String inputPath = Util.normalizePath(inputFile.getAbsolutePath());

		List<File> files = source.list(null, inputFile.getAbsolutePath(), true);
		List<FileInfo> output = new ArrayList<FileInfo>();
		for (File file : files) {
			FileInfo fileMap = new FileInfo();
			fileMap.put(Parameters.PARAM_FILE, file);
			fileMap.put(Parameters.PARAM_INPUT_FILE, file);
			List<File> transformFiles = new ArrayList<File>();
			transformFiles.add(file);
			fileMap.put(Parameters.PARAM_TRANSFORM_FILE_LIST, transformFiles);
			fileMap.put(Parameters.PARAM_RELATIVE_PATH, Util.relativePath(inputPath,
					file));
			fileMap.put(Parameters.PARAM_INPUT_PATH, inputPath);
			output.add(fileMap);
		}
		return output;
	}
}
