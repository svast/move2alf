package eu.xenit.move2alf.core.action;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.media.jai.JAI;

import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.common.Tiff2Pdf;

@ActionInfo(classId = "Tiff2PdfAction",
            category = ConfigurableObject.CAT_TRANSFORM,
            description = "Action that transforms multiple tiff files to pdf.")
public class Tiff2PdfAction extends Move2AlfReceivingAction<FileInfo> {

	private static final Logger logger = LoggerFactory
			.getLogger(Tiff2PdfAction.class);

	@Override
	protected void executeImpl(FileInfo message) {

        //TODO: FIX
//		List filesToTransform = null;
//		File destination = (File) parameterMap.get(Parameters.PARAM_FILE);
//		Object fileListParamValue = parameterMap
//				.get(Parameters.PARAM_TRANSFORM_FILE_LIST);
//		if (fileListParamValue != null) {
//			try {
//				filesToTransform = (List) parameterMap
//						.get(Parameters.PARAM_TRANSFORM_FILE_LIST);
//			} catch (ClassCastException e) {
//				logger
//						.warn("Files to transform should be of type List<File> or List<String>");
//				return;
//			}
//		} else {
//			logger.warn("No files to transform");
//			return;
//		}
//		createPdf(destination, filesToTransform);
	}

	public void createPdf(File document, List transformFiles) {
		// assume that docDescription contains a File that describes where the
		// output should go
		// File document = docDescription.getDocument();
		logger
				.info("TIMESTAMP: Single tif creation {}", (new Date())
						.getTime());
		Tiff2Pdf tiff2Pdf = new Tiff2Pdf();
		// assemble tiffs into pdf and write to destination
		tiff2Pdf.init();
		for (Object tifFile : transformFiles) {
			String tifFilePath = null;
			try {
				tifFilePath = (String) tifFile;
			} catch (ClassCastException e) {
				try {
					tifFilePath = ((File) tifFile).getAbsolutePath();
				} catch (ClassCastException e2) {
					logger.error("Tiff2Pdf expects Strings or Files");
					return;
				}
			}
			logger.debug("Adding tif {}", tifFilePath);
			tiff2Pdf.addImage(JAI.create("fileload", tifFilePath));
		}

		String fileName = document.getAbsolutePath();
		String fileNameWithoutExtension = fileName.substring(0, fileName
				.lastIndexOf('.'));
		File singleTifFile = new File(fileNameWithoutExtension + "-single.tif");
		logger.debug("Creating single tif {} ...", singleTifFile
				.getAbsolutePath());
		tiff2Pdf.createSingleTiff(singleTifFile);
		logger.debug("... done!");

		logger.info("TIMESTAMP: Pdf creation {}", (new Date()).getTime());
		logger.debug("Converting to pdf {} ...", document.getAbsolutePath());
		tiff2Pdf.tiff2Pdf(singleTifFile, document);
		logger.debug("... done!");

		// remove temporary singleTifFile
		logger.info("TIMESTAMP: Single tif removal {}", (new Date()).getTime());
		singleTifFile.delete();

	}

}
