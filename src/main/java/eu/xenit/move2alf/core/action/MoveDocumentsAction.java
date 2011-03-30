package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;
import eu.xenit.move2alf.core.sourcesink.AlfrescoSourceSink;

public class MoveDocumentsAction extends Action {

	private static final Logger logger = LoggerFactory
			.getLogger(MoveDocumentsAction.class);

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		boolean moveBeforeProcessing = "true".equals(configuredAction
				.getParameter(Parameters.PARAM_MOVE_BEFORE_PROCESSING));
		boolean moveAfterLoad = "true".equals(configuredAction
				.getParameter(Parameters.PARAM_MOVE_AFTER_LOAD));
		boolean moveNotLoaded = "true".equals(configuredAction
				.getParameter(Parameters.PARAM_MOVE_NOT_LOADED));
		String stage = configuredAction.getParameter(Parameters.PARAM_STAGE);
		String documentStatus = (String) parameterMap.get(Parameters.PARAM_STATUS);

		String moveDirectory = null;
		if (moveBeforeProcessing && Parameters.VALUE_BEFORE.equals(stage)) {
			moveDirectory = configuredAction
					.getParameter(Parameters.PARAM_MOVE_BEFORE_PROCESSING_PATH);
		}
		if (moveAfterLoad && Parameters.VALUE_AFTER.equals(stage)
				&& Parameters.VALUE_OK.equals(documentStatus)) {
			moveDirectory = configuredAction.getParameter(Parameters.PARAM_MOVE_AFTER_LOAD_PATH);
		}
		if (moveNotLoaded && Parameters.VALUE_AFTER.equals(stage)
				&& Parameters.VALUE_FAILED.equals(documentStatus)) {
			moveDirectory = configuredAction.getParameter(Parameters.PARAM_MOVE_NOT_LOADED_PATH);
		}

		if ((Parameters.VALUE_BEFORE.equals(stage) && moveBeforeProcessing)
				|| (Parameters.VALUE_AFTER.equals(stage) && (moveAfterLoad || moveNotLoaded))) {
			if (moveDirectory != null && !"".equals(moveDirectory)) {
				moveDirectory = moveDirectory.replaceAll("\\\\", "/");
				moveFile(moveDirectory, parameterMap);
			}
		}
	}

	public void moveFile(String moveDirectory,
			Map parameterMap) {
		File file = (File) parameterMap.get(Parameters.PARAM_FILE);
		String absolutePath = file.getAbsolutePath();

		absolutePath = absolutePath.replaceAll("\\\\", "/");
		// remove file name
		absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));

		// makes sure the input folder does not end on "/"
		int moveDirectoryLength = moveDirectory.length() - 1;
		if (moveDirectory.lastIndexOf("/") == moveDirectoryLength) {
			moveDirectory = moveDirectory.substring(0,
					moveDirectory.length() - 1);
		}

		// assemble full path
		//String relativePath = absolutePath.replace(inputFolder, "");
		String relativePath = (String) parameterMap.get(Parameters.PARAM_RELATIVE_PATH);
		String fullDestinationPath = moveDirectory + "" + relativePath;

		File moveFolder = new File(fullDestinationPath);

		// check if full path exists, otherwise create path
		boolean destinationPathExists = true;
		boolean moveFolderExists = moveFolder.exists();
		if (!moveFolderExists) {
			destinationPathExists = checkParentDirExists(fullDestinationPath);
			if (destinationPathExists) {
				boolean success = (new File(fullDestinationPath)).mkdir();
				if (success) {
				} else {
				}
			}
		}

		// If path exists move document
		if (destinationPathExists) {
			String newFileName = fullDestinationPath + "/" + file.getName();
			boolean success = file.renameTo(new File(newFileName));

			if (success) {
				File movedFile = new File(newFileName);
				parameterMap.put(Parameters.PARAM_FILE, movedFile);
				logger.info("Moved file to " + movedFile.getAbsolutePath());
			}

			if (!success) {
				logger.debug("Could not move document "
						+ file.getAbsolutePath());
			}
		} else {
			logger.debug("Move path could not be made for document "
					+ file.getAbsolutePath());
		}
	}

	public boolean checkParentDirExists(String path) {
		int index = path.lastIndexOf("/");
		String newPath = path.substring(0, index);
		logger.debug("new path is " + newPath);
		File parentDestination = new File(newPath);
		boolean exists = parentDestination.exists();

		if (!exists) {
			boolean destinationPathMade = checkParentDirExists(newPath);
			if (destinationPathMade) {
				boolean success = (new File(newPath)).mkdir();
				logger.debug("Directory " + newPath + " made");
				return success;
			} else {
				logger.debug("Directory " + newPath + " not made");
				return false;
			}
		}
		return true;
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_DEFAULT;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
