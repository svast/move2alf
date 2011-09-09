package eu.xenit.move2alf.core.simpleaction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.sourcesink.FileSourceSink;

public class SASource extends SimpleAction {

	@Override
	public List<Map<String, Object>> execute(
			final Map<String, Object> parameterMap,
			final Map<String, String> config) {
		FileSourceSink source = new FileSourceSink();
		File inputFile = (File) parameterMap.get(Parameters.PARAM_FILE);
		String inputPath = Util.normalizePath(inputFile.getAbsolutePath());

		List<File> files = source.list(null, inputFile.getAbsolutePath(), true);
		List<Map<String, Object>> output = new ArrayList<Map<String, Object>>();
		for (File file : files) {
			Map<String, Object> fileMap = new HashMap<String, Object>();
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
