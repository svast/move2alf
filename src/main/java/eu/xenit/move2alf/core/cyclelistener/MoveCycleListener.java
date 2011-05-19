package eu.xenit.move2alf.core.cyclelistener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.CycleListener;
import eu.xenit.move2alf.core.action.MoveDocumentsAction;
import eu.xenit.move2alf.core.action.SourceAction;

public class MoveCycleListener extends CycleListener {

	private static final Logger logger = LoggerFactory
			.getLogger(MoveCycleListener.class);

	@Override
	public void cycleEnd(int cycleId) {

	}

	@Override
	public void cycleStart(int cycleId) {
		logger.error("Wrong method called in MoveCycleListener"); // TODO: fix other cyclelisteners so this can be removed.
	}
	
	@Override
	public void cycleStart(int cycleId, Map<String, Object> parameterMap) {
		Map<String, String> moveParameters = getMoveActionParameter(cycleId);
		boolean moveBeforeProcessing = ("true".equals(moveParameters
				.get(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING))) ? true
				: false;
		String moveBeforeProcessingPath = moveParameters
				.get(MoveDocumentsAction.PARAM_MOVE_BEFORE_PROCESSING_PATH);

		if (moveBeforeProcessing) {
			String inputPath = getJobService().getActionParameters(cycleId, SourceAction.class).get(SourceAction.PARAM_PATH);
			inputPath = inputPath.replaceAll("\\\\", "/");
			moveBeforeProcessingPath = moveBeforeProcessingPath.replaceAll(
					"\\\\", "/");
			File src = new File(inputPath);
			File dst = new File(moveBeforeProcessingPath);
			String basePath = src.getAbsolutePath().replaceAll("\\\\", "/");
			if (basePath.endsWith("/")) {
				basePath = basePath.substring(0, basePath.length() - 1);
			}
			List<File> filesToLoad = new ArrayList<File>();
			parameterMap.put(Parameters.PARAM_FILES_TO_LOAD, filesToLoad);
			move(src, dst, basePath, filesToLoad);
		}
	}

	private void move(File src, File dst, String basePath, List<File> filesToLoad) {
		for (File file : src.listFiles()) {
			if (file.isDirectory()) {
				move(file, dst, basePath, filesToLoad);
			} else {
				String absolutePath = file.getAbsolutePath().replaceAll("\\\\",
						"/");
				String folderPath = absolutePath.substring(0, absolutePath
						.lastIndexOf("/"));
				String relativePath = folderPath.substring(basePath.length());
				String moveFolderPath = dst.getAbsolutePath().replaceAll("\\\\",
				"/") + relativePath;
				File moveFolder = new File(moveFolderPath);
				if (!moveFolder.exists()) {
					moveFolder.mkdirs();
				}
				String newFileName = moveFolderPath + "/" + file.getName();
				File movedFile = new File(newFileName);
				boolean success = file.renameTo(movedFile);
				if (success) {
					filesToLoad.add(movedFile);
					logger.info("Moved file to " + movedFile.getAbsolutePath());
				} else {
					logger.debug("Could not move document "
							+ file.getAbsolutePath() + " to " + movedFile.getAbsolutePath());
				}
			}
		}
	}

	private Map<String, String> getMoveActionParameter(int cycleId) {
		return getJobService().getActionParameters(cycleId,
				MoveDocumentsAction.class);
	}

}
