package eu.xenit.move2alf.proprietary.vivium.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.core.action.ActionPropertyDefinition;
import eu.xenit.move2alf.core.dto.Action;
import eu.xenit.poller.vivium.LiDocDescription;
import eu.xenit.poller.vivium.LiGroupType;

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
   
	public LiPolisDocAction(){
		super();
		this.configPropertyDefinitionMap.put("test",new ActionPropertyDefinition("test",java.lang.String.class,null));
		
	}

	@Override
	public boolean execute(Map<String, Object> configPropertyMap, Map<String, Object> inputPropertyMap) {
		String keyFilePath = inputPropertyMap.get(key)
		String keyfilePath = resultPath + File.separator + "keyfile.txt";
		logger.debug("keyfilePath " + keyfilePath);
		file = new File(keyfilePath);

		logger.debug("Starting to process {}", keyfilePath);

		// path where the .pdf files reside
		String pdfPath = file.getParent();

		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); 
		List<LiDocDescription> docDescriptionList = new ArrayList<LiDocDescription>();
		LiDocDescription docDescription = null;

		// cfr. keyfile.txt for formatting metadata

		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			// ignore first line with field headers and last empty line
			if (!line.startsWith("File Path") && line.trim().length() > 0) { 
				// Use String.split instead of StringTokenizer because
				// StringTokenizer ignores tokens without content.
				//http://www.velocityreviews.com/forums/t136007-stringtokenizer-
				// ignores-tokens-without-content.html

				// If the lines ends with an empty field, the last field will
				// only be picked up if we set the limit to 14
				String[] lineFields = line.split("\\|", 14); 
				// File Path
				// String pdfFilePath = lineFields[0]; // not used, derived from
				// documentID later on
				// BEGIN_PDF|
				// String beginPDF = lineFields[1]; // field not filled in by
				// Adept
				// JOBNAAM|
				String jobName = lineFields[2];
				// DOCUMENT_ID|
				String pdfFileName = lineFields[3]+ ".pdf";
				// TYPE_DOCUMENT|
				String type = lineFields[4];
				// POLISNUMMER|
				String policyNumber = lineFields[5];
				// PRODUCENTNUMMER|
				String producerNumber = lineFields[6];
				// RUBRIEK|
				String categoryString = lineFields[7];
				// DATUM|
				String dateString = lineFields[8];
				// TAK|
				String contractTypeString = lineFields[9];
				// JAAR|
				String yearString = lineFields[10];
				// NAAM_VN|
				String nameInsuranceTaker = lineFields[11].replace("[", " ").replace("\\", "'");
				// NAAM_VZ|
				String nameInsured = lineFields[12].replace("[", " ").replace("\\", "'");
				// NRVOLGKORG
				String sequenceNumberString = lineFields[13];
				
				int sequenceNumber = 0;
				try {
					sequenceNumber = Integer.parseInt(sequenceNumberString);
				} catch (NumberFormatException e) {
					logger.error("Invalid sequenceNumber {}, skipping ...", sequenceNumberString);
					continue;// go to next line
				}

				Date date = null;
				try {
					date = dateFormat.parse(dateString);
				} catch (Exception e) {
					logger.error("Can not parse date {} skipping ...", dateString);
					continue;// go to next line
				}

				try {
					Integer.parseInt(categoryString);
				} catch (NumberFormatException e) {
					logger.error("Invalid category {}, skipping ...", categoryString);
					continue;// go to next line
				}

				// contractType can also contain char, e.g. 50R, so remove check
//				try {
//					Integer.parseInt(contractTypeString);
//				} catch (NumberFormatException e) {
//					logger.error("Invalid contractType {}, skipping ...", contractTypeString);
//					continue;// go to next line
//				}

				Integer year = null;
				try {
					year = Integer.parseInt(yearString);
				} catch (NumberFormatException e) {
					logger.info("Invalid year {}", yearString);
					// not a mandatory field, keep null
				}

				logger.debug("FileName {}", pdfFileName);
				File document = new File(pdfPath+File.separator+pdfFileName);
				docDescription = new LiDocDescription(document, LiGroupType.LiDocPolis,
						jobName, type, sequenceNumber,date);
				//store all 3 digits
				docDescription.setCategory(categoryString);
				docDescription.setContractType(contractTypeString);
				docDescription.setNameInsuranceTaker(nameInsuranceTaker);
				docDescription.setNameInsured(nameInsured);
				docDescription.setPolicyNumber(policyNumber);
				docDescription.setProducerNumber(producerNumber);
				docDescription.setYear(year);

				docDescriptionList.add(docDescription);
			}
		}

		bufferedReader.close();
		fileReader.close();

		int nbrOfPages = 0;// notknown at the moment
		for (LiDocDescription docDescription1 : docDescriptionList) {
			logger.info("docGroupIdentifier {}",
					docGroupIdentifier);
			if (docGroupIdentifier == null) {
				docGroupIdentifier = docDescription1.getPolicyNumber();
			} else if (docGroupIdentifier.equals(docDescription1
					.getPolicyNumber())
					&& liDocGroupList.size() < 100) {
				// building up the list of related documents
			} else {
				// there was already a doc group, it can be scheduled now
				dump();

				// start of new docGroup
				docGroupIdentifier = docDescription1.getPolicyNumber();
			}
			logger.info("Current docGroupSize {}, adding 1 doc", liDocGroupList
					.size());
			liDocGroupList.add(docDescription1);

			report.append(docDescription1.getDocument().getPath()).append("\n");
		}

		logger
				.info(
						"Finished processing. keyfile.txt contained {} document(s) and {} pages",
						docDescriptionList.size(), nbrOfPages);
		logger.info("******************************************");
		return new Integer[] { docDescriptionList.size(), nbrOfPages };
	}
	
	
}
