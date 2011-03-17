package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.Map;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class MoveDocumentsAction extends Action {
	
	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// TODO move file in parametermap (key "file") to paths in parameters.
		String moveBeforeProcessing = configuredAction.getParameter("moveBeforeProcessing");
		String moveAfterLoad = configuredAction.getParameter("moveAfterLoad");
		String moveNotLoaded = configuredAction.getParameter("moveNotLoaded");
		String inputFolder = configuredAction.getParameter("inputFolder");
		
		if("true".equals(moveBeforeProcessing)){
			String moveDirectory = configuredAction.getParameter("moveBeforeProcessingPath");

				File file = (File) parameterMap.get("file");
				String absolutePath = file.getAbsolutePath();
				
				String relativePath = absolutePath.replace(inputFolder, "");
				
				String fullDestinationPath = moveDirectory+"/"+relativePath;
				
				boolean destinationPathExists = checkParentDirExists(fullDestinationPath);
				
				if(destinationPathExists){
					boolean success = file.renameTo(new File(moveDirectory, file.getName()));
					
					if(!success){
						//not successfully moved
					}
				}else{
					//destination path could not be made
				}

		}
		
		if("true".equals(moveAfterLoad)){
			if("true".equals(moveBeforeProcessing)){
				inputFolder = configuredAction.getParameter("moveBeforeProcessingPath");
			}
			
			String moveDirectory = configuredAction.getParameter("moveAfterLoadPath");
			
			File file = (File) parameterMap.get("file");
			String absolutePath = file.getAbsolutePath();
			
			String relativePath = absolutePath.replace(inputFolder, "");
			
			String fullDestinationPath = moveDirectory+"/"+relativePath;
			
			boolean destinationPathExists = checkParentDirExists(fullDestinationPath);
			
			if(destinationPathExists){
				boolean success = file.renameTo(new File(moveDirectory, file.getName()));
				
				if(!success){
					//not successfully moved
				}
			}else{
				//destination path could not be made
			}
		}
		
		if("true".equals(moveNotLoaded)){
			if("true".equals(moveBeforeProcessing)){
				inputFolder = configuredAction.getParameter("moveBeforeProcessingPath");
			}
			
			String moveDirectory  = configuredAction.getParameter("moveNotLoadedPath");
			
			File file = (File) parameterMap.get("file");
			String absolutePath = file.getAbsolutePath();
			
			String relativePath = absolutePath.replace(inputFolder, "");
			
			String fullDestinationPath = moveDirectory+"/"+relativePath;
			
			boolean destinationPathExists = checkParentDirExists(fullDestinationPath);
			
			if(destinationPathExists){
				boolean success = file.renameTo(new File(moveDirectory, file.getName()));
				
				if(!success){
					//not successfully moved
				}
			}else{
				//destination path could not be made
			}
		}
	}

	public boolean checkParentDirExists(String path){
		
		int index = path.lastIndexOf("/");
		String newPath = path.substring(0, index-1);
		
		File parentDestination = new File(newPath);
		boolean exists = parentDestination.exists();
		
		if(!exists){
			boolean destinationPathMade = checkParentDirExists(newPath);
			if(destinationPathMade){
				boolean success = (new File(newPath)).mkdir();
				return success;
			}else{
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
