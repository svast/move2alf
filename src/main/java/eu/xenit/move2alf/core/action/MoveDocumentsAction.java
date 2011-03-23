package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
				.getParameter("moveBeforeProcessing"));
		boolean moveAfterLoad = "true".equals(configuredAction
				.getParameter("moveAfterLoad"));
		boolean moveNotLoaded = "true".equals(configuredAction
				.getParameter("moveNotLoaded"));
		String stage = configuredAction.getParameter("stage");
		String documentStatus = (String) parameterMap.get("status");

		String moveDirectory = null;
		if (moveBeforeProcessing && "before".equals(stage)) {
			moveDirectory = configuredAction
					.getParameter("moveBeforeProcessingPath");
		}
		if (moveAfterLoad && "after".equals(stage)
				&& "ok".equals(documentStatus)) {
			moveDirectory = configuredAction.getParameter("moveAfterLoadPath");
		}
		if (moveNotLoaded && "after".equals(stage)
				&& "failed".equals(documentStatus)) {
			moveDirectory = configuredAction.getParameter("moveNotLoadedPath");
		}

		if (("before".equals(stage) && moveBeforeProcessing)
				|| ("after".equals(stage) && (moveAfterLoad || moveNotLoaded))) {
			if (moveDirectory != null && !"".equals(moveDirectory)) {
				moveDirectory = moveDirectory.replaceAll("\\\\", "/");
				moveFile(moveDirectory, parameterMap);
			}
		}
	}

	public void moveFile(String moveDirectory,
			Map parameterMap) {
		File file = (File) parameterMap.get("file");
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
		String relativePath = (String) parameterMap.get("relativePath");
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
				parameterMap.put("file", movedFile);
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
