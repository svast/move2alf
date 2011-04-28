package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class TiffTestAction extends Action {

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		
		File datFile = (File) parameterMap.get(Parameters.PARAM_FILE);
		String folder = datFile.getParent();
		String destinationPath = datFile.getAbsolutePath().replace("dat", "pdf");
		File destination = new File(destinationPath);
		
		List<String> tifFiles = new ArrayList<String>();
		tifFiles.add(folder + "/46A1-INSEBC-53000000087730051-01042011_001_000001.tif");
		tifFiles.add(folder + "/46A1-INSEBC-53000000087730051-01042011_002_000002.tif");
		tifFiles.add(folder + "/46A1-INSEBC-53000000087730051-01042011_003_000003.tif");
		tifFiles.add(folder + "/46A1-INSEBC-53000000087730051-01042011_004_000004.tif");
		tifFiles.add(folder + "/46A1-INSEBC-53000000087730051-01042011_005_000005.tif");
		tifFiles.add(folder + "/46A1-INSEBC-53000000087730051-01042011_006_000006.tif");
		
		parameterMap.put(Parameters.PARAM_FILE, destination);
		parameterMap.put(Parameters.PARAM_TRANSFORM_FILE_LIST, tifFiles);
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_METADATA;
	}

	@Override
	public String getDescription() {
		return "Tiff2Pdf test action";
	}

	@Override
	public String getName() {
		return "Tiff2Pdf";
	}

}
