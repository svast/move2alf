package eu.xenit.move2alf.proprietary.vivium.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.ParameterDefinition;
import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

/*
 * This parser is used for parsing the life polis docs
 * polis docs is one group of documents and it has
 * following properties:
 * 
 * common properties all life docs
 * - JOBNAAM
 * - DOCUMENT_ID => base for pdfFileName
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
	private static Logger logger = LoggerFactory
			.getLogger(LiPolisDocAction.class);

	// config parameters

	// output parameters
	private static final String LI_NAMESPACE = "{li.model}";
	
	private static final String OP_LiJobName = "jobName";
	private static final String OP_LiTypeDocument = "type";
	private static final String OP_LiCategory = "category";
	private static final String OP_LiSequenceNumber = "sequenceNumber";
	private static final String OP_LiDate = "date";
	private static final String OP_LiPolicyNumber = "policyNumber";
	private static final String OP_LiProducerNumber = "producerNumber";
	private static final String OP_LiNameInsuranceTaker = "nameInsuranceTaker";
	private static final String OP_LiNameInsured = "nameInsured";
	private static final String OP_LiContractType = "contractType";
	private static final String OP_LiYear = "year";

	public LiPolisDocAction() {
	}

	@Override
	public void execute(ConfiguredAction configuredAction,
			Map<String, Object> oldParameterMap) {
		File inputFile = (File) oldParameterMap.get(Parameters.PARAM_FILE);
		String filePath = inputFile.getAbsolutePath();
		logger.debug("file path: "+ filePath);
		if (filePath.endsWith("keyfile.txt")) {
			
			CountDownLatch countDownLatch = (CountDownLatch) oldParameterMap.get(Parameters.PARAM_COUNTER);
			countDownLatch.countDown();
			
			logger.debug("keyfilePath {}", filePath);
			File file = new File(filePath);

			// path where the .pdf files reside
			String pdfFolderPath = file.getParent();

			try {
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);

				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

				// cfr. keyfile.txt for formatting metadata

				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					Map<String, Object> parameterMap = new HashMap<String, Object>(oldParameterMap);
					// ignore first line with field headers and last empty line
					if (!line.startsWith("File Path")
							&& line.trim().length() > 0) {
						// Use String.split instead of StringTokenizer because
						// StringTokenizer ignores tokens without content.
						// http://www.velocityreviews.com/forums/t136007-stringtokenizer-
						// ignores-tokens-without-content.html

						// If the lines ends with an empty field, the last field
						// will
						// only be picked up if we set the limit to 14
						String[] lineFields = line.split("\\|", 14);
						// File Path
						String pdfFilePath = lineFields[0]; // not used,
						
						File pdfFile = new File(pdfFilePath);
						
						parameterMap.put(Parameters.PARAM_FILE, pdfFile);
						
						// JOBNAAM|
						String jobName = lineFields[2];
						// DOCUMENT_ID|
						//String pdfFilePath = pdfFolderPath + File.separator
						//		+ lineFields[3] + ".pdf";
						// TYPE_DOCUMENT|
						String type = lineFields[4];
						// POLISNUMMER|
						String policyNumber = lineFields[5];
						// PRODUCENTNUMMER|
						String producerNumber = lineFields[6];
						// RUBRIEK|
						String categoryString = lineFields[7];
						// DATUM|
						String dateString = lineFields[8].trim();
						// TAK|
						String contractTypeString = lineFields[9];
						// JAAR|
						String yearString = lineFields[10];
						// NAAM_VN|
						String nameInsuranceTaker = lineFields[11].replace("[",
								" ").replace("]", "'");
						// NAAM_VZ|
						String nameInsured = lineFields[12].replace("[", " ")
								.replace("]", "'");
						// NRVOLGKORG
						String sequenceNumberString = lineFields[13];

						try {
							Integer sequenceNumber = Integer
									.parseInt(sequenceNumberString);
							
							logger.debug("date string: "+dateString);
							Date date = dateFormat.parse(dateString);
							logger.debug("string to date");
							SimpleDateFormat dateFormatTwo = new SimpleDateFormat("yyyy-MM-dd");
						    StringBuffer res = new StringBuffer();
						    logger.debug("now date to string");
						    res.append(dateFormatTwo.format(date));
						    res.append("T00:00:00.000Z");
							
						    logger.debug("new date: "+res.toString());
						    
							Integer.parseInt(categoryString);
							Integer year = Integer.parseInt(yearString);

							// determine spacepath
							StringBuffer sb = new StringBuffer();
							sb.append("/cm:");
							// group per 10**5
							if (!"008".equals(categoryString)) {
								String base = "p"
										+ policyNumber.substring(0, 5);
								sb.append(base);
								sb.append("00000-");
								sb.append(base);
								sb.append("99999");
								sb.append("/cm:p");
								sb.append(policyNumber);
								sb.append("/cm:");
								sb.append(categoryString);
							} else {
								String base = "pr";
								sb.append(base);
								sb.append("00000-");
								sb.append(base);
								sb.append("99999");
								sb.append("/cm:pr");
								sb.append(producerNumber);
								sb.append("/cm:");
								sb.append(categoryString);
							}
							
							Map<String, String> metadataMap = new HashMap();
							// everything ok, store in parameterMap
							metadataMap.put(OP_LiJobName, jobName);
							metadataMap.put(OP_LiTypeDocument, type);
							metadataMap.put(OP_LiCategory, categoryString);
							metadataMap.put(OP_LiSequenceNumber,
									sequenceNumberString);
							metadataMap.put(OP_LiDate, res.toString());
							metadataMap.put(OP_LiYear, yearString);

							parameterMap.put(Parameters.PARAM_METADATA, metadataMap);
							
							parameterMap.put(Parameters.PARAM_NAMESPACE,
									LI_NAMESPACE);

							if ("001".equals(categoryString)
									|| "002".equals(categoryString)
									|| "003".equals(categoryString)
									|| "004".equals(categoryString)
									|| "005".equals(categoryString)
									|| "006".equals(categoryString)
									|| "007".equals(categoryString)
									|| "010".equals(categoryString)
									|| "011".equals(categoryString)
									|| "012".equals(categoryString)
									|| "013".equals(categoryString)) {
								parameterMap.put(Parameters.PARAM_CONTENTTYPE,
										"polisDoc");
								
								metadataMap.put(OP_LiPolicyNumber, policyNumber);
								metadataMap.put(OP_LiProducerNumber,
										producerNumber);
								metadataMap.put(OP_LiNameInsuranceTaker,
										nameInsuranceTaker);
								metadataMap.put(OP_LiNameInsured, nameInsured);
								metadataMap.put(OP_LiContractType,
										contractTypeString);
								
							}

							if ("009".equals(categoryString)) {
								parameterMap.put(Parameters.PARAM_CONTENTTYPE,
										"list201");
								metadataMap.put(OP_LiPolicyNumber, policyNumber);
								metadataMap.put(OP_LiNameInsuranceTaker,
										nameInsuranceTaker);
								metadataMap.put(OP_LiNameInsured, nameInsured);
								metadataMap.put(OP_LiContractType,
										contractTypeString);
							}

							if ("008".equals(categoryString)) {
								parameterMap.put(Parameters.PARAM_CONTENTTYPE,
										"prodInfo");
								metadataMap.put(OP_LiProducerNumber,
										producerNumber);
							}

							ConfiguredAction nextAction = configuredAction
									.getAppliedConfiguredActionOnSuccess();
							if (nextAction != null) {
								getJobService().executeAction(
										(Integer) parameterMap.get("cycle"),
										nextAction, parameterMap);
							}
						} catch (Exception e) {
							logger.error("Invalid metadata {}, skipping ...",
									line);
							// TODO
							// configuredAction.getAppliedConfiguredActionOnFailure()
							// .execute(parameterMap);
							continue;// go to next line
						}
					}
				}
				bufferedReader.close();
				fileReader.close();
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage());
				// TODO
				// configuredAction.getAppliedConfiguredActionOnFailure().execute(
				// parameterMap);
			} catch (IOException e) {
				logger.error(e.getMessage());
				// TODO
				// configuredAction.getAppliedConfiguredActionOnFailure().execute(
				// parameterMap);
			}
		} else {
			logger.debug("not the keyfile");
			CountDownLatch countDownLatch = (CountDownLatch) oldParameterMap.get(Parameters.PARAM_COUNTER);
			countDownLatch.countDown();
		}
	}

	@Override
	public String getCategory() {
		return CAT_METADATA;
	}

	@Override
	public String getDescription() {
		return "Action that parses the adept keyfile";
	}

	@Override
	public String getName() {
		return "Vivium Life PolisDoc";
	}

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// TODO Auto-generated method stub

	}
}