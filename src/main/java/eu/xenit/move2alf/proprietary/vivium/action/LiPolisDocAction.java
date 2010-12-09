package eu.xenit.move2alf.proprietary.vivium.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.action.ParameterRenameAction;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

/*
 * This parser is used for parsing the life polis docs
 * polis docs is one group of documents and it has
 * following properties:
 * 
 * common properties all life docs
 * - JOBNAAM
 * - DOCUMENT_ID
 * - TYPE_DOCUMENT
 * - RUBRIEK
 * - NRVOLGKORG
 * - DATUM
 * 
 * specific properties of polis docs
 * - POLISNUMMER
 * - PRODUCENTNR
 * - NAAM_VN
 * - NAAM_VZ
 * - TAK
 * - JAAR
 * 
 */

public class LiPolisDocAction extends Action {
	/*
	 * the ActionFactory will only register Actions that have implemented the singleton pattern
	 */
	private static final LiPolisDocAction instance = new LiPolisDocAction();
	
    public static LiPolisDocAction getInstance() {
        return instance;
    }

	private LiPolisDocAction(){
		super();		
	}

	@Override
	public void execute(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		//TODO
//		String keyFilePath = inputPropertyMap.get(key)
//		String keyfilePath = resultPath + File.separator + "keyfile.txt";
//		logger.debug("keyfilePath " + keyfilePath);
//		file = new File(keyfilePath);
//
//		logger.debug("Starting to process {}", keyfilePath);
//
//		// path where the .pdf files reside
//		String pdfPath = file.getParent();
//
//		FileReader fileReader = new FileReader(file);
//		BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); 
//		List<LiDocDescription> docDescriptionList = new ArrayList<LiDocDescription>();
//		LiDocDescription docDescription = null;
//
//		// cfr. keyfile.txt for formatting metadata
//
//		String line = null;
//		while ((line = bufferedReader.readLine()) != null) {
//			// ignore first line with field headers and last empty line
//			if (!line.startsWith("File Path") && line.trim().length() > 0) { 
//				// Use String.split instead of StringTokenizer because
//				// StringTokenizer ignores tokens without content.
//				//http://www.velocityreviews.com/forums/t136007-stringtokenizer-
//				// ignores-tokens-without-content.html
//
//				// If the lines ends with an empty field, the last field will
//				// only be picked up if we set the limit to 14
//				String[] lineFields = line.split("\\|", 14); 
//				// File Path
//				// String pdfFilePath = lineFields[0]; // not used, derived from
//				// documentID later on
//				// BEGIN_PDF|
//				// String beginPDF = lineFields[1]; // field not filled in by
//				// Adept
//				// JOBNAAM|
//				String jobName = lineFields[2];
//				// DOCUMENT_ID|
//				String pdfFileName = lineFields[3]+ ".pdf";
//				// TYPE_DOCUMENT|
//				String type = lineFields[4];
//				// POLISNUMMER|
//				String policyNumber = lineFields[5];
//				// PRODUCENTNUMMER|
//				String producerNumber = lineFields[6];
//				// RUBRIEK|
//				String categoryString = lineFields[7];
//				// DATUM|
//				String dateString = lineFields[8];
//				// TAK|
//				String contractTypeString = lineFields[9];
//				// JAAR|
//				String yearString = lineFields[10];
//				// NAAM_VN|
//				String nameInsuranceTaker = lineFields[11].replace("[", " ").replace("\\", "'");
//				// NAAM_VZ|
//				String nameInsured = lineFields[12].replace("[", " ").replace("\\", "'");
//				// NRVOLGKORG
//				String sequenceNumberString = lineFields[13];
//				
//				int sequenceNumber = 0;
//				try {
//					sequenceNumber = Integer.parseInt(sequenceNumberString);
//				} catch (NumberFormatException e) {
//					logger.error("Invalid sequenceNumber {}, skipping ...", sequenceNumberString);
//					continue;// go to next line
//				}
//
//				Date date = null;
//				try {
//					date = dateFormat.parse(dateString);
//				} catch (Exception e) {
//					logger.error("Can not parse date {} skipping ...", dateString);
//					continue;// go to next line
//				}
//
//				try {
//					Integer.parseInt(categoryString);
//				} catch (NumberFormatException e) {
//					logger.error("Invalid category {}, skipping ...", categoryString);
//					continue;// go to next line
//				}
			}
}