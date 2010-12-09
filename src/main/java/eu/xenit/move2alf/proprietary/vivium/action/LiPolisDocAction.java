package eu.xenit.move2alf.proprietary.vivium.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.common.ParameterDefinition;
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

	// input parameters
	private static final String IP_AdeptKeyFilePath = "adeptKeyFilePath";

	// output parameters
	private static final String OP_LiJobName = "liJobName";
	private static final String OP_LiSourcePdfFilePath = "liSourcePdfFilePath";
	private static final String OP_LiTypeDocument = "liTypeDocument";
	private static final String OP_LiCategory = "liCategory";
	private static final String OP_LiSequenceNumber = "liSequenceNumber";
	private static final String OP_LiDate = "liDate";
	private static final String OP_LiPolicyNumber = "liPolicyNumber";
	private static final String OP_LiProducerNumber = "liProducerNumber";
	private static final String OP_LiNameInsuranceTaker = "liNameInsuranceTaker";
	private static final String OP_LiNameInsured = "liNameInsured";
	private static final String OP_LiContractType = "liContractType";
	private static final String OP_LiYear = "liYear";

	private static final String OP_LiSinkFolderRelativePath = "liSinkFolderRelativePath";
	private static final String OP_LiDocumentGroup = "liDocumentGroup";

	/*
	 * the ActionFactory will only register Actions that have implemented the
	 * singleton pattern
	 */
	private static final LiPolisDocAction instance = new LiPolisDocAction();

	public static LiPolisDocAction getInstance() {
		return instance;
	}

	private LiPolisDocAction() {
		name = "Vivium Life PolisDoc Action";
		description = "Action that parses the adept keyfile";
		addAprioriInputParameter(new ParameterDefinition(IP_AdeptKeyFilePath,
				String.class, ""));

		addAprioriOutputParameter(new ParameterDefinition(OP_LiJobName,
				String.class, null));
		addAprioriOutputParameter(new ParameterDefinition(
				OP_LiSourcePdfFilePath, String.class, null));
		addAprioriOutputParameter(new ParameterDefinition(OP_LiTypeDocument,
				String.class, null));
		addAprioriOutputParameter(new ParameterDefinition(OP_LiCategory,
				String.class, null));
		addAprioriOutputParameter(new ParameterDefinition(OP_LiSequenceNumber,
				Integer.class, null));
		addAprioriOutputParameter(new ParameterDefinition(OP_LiDate,
				Date.class, null));
		addAprioriOutputParameter(new ParameterDefinition(OP_LiPolicyNumber,
				String.class, null));
		addAprioriOutputParameter(new ParameterDefinition(OP_LiProducerNumber,
				String.class, null));
		addAprioriOutputParameter(new ParameterDefinition(
				OP_LiNameInsuranceTaker, String.class, null));
		addAprioriOutputParameter(new ParameterDefinition(OP_LiNameInsured,
				String.class, null));
		addAprioriOutputParameter(new ParameterDefinition(OP_LiContractType,
				String.class, null));
		addAprioriOutputParameter(new ParameterDefinition(OP_LiYear,
				Integer.class, null));

		addAprioriOutputParameter(new ParameterDefinition(
				OP_LiSinkFolderRelativePath, String.class, null));
		addAprioriOutputParameter(new ParameterDefinition(OP_LiDocumentGroup,
				String.class, null));
	}

	@Override
	public void execute(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		String keyFilePath = (String) parameterMap.get(IP_AdeptKeyFilePath);
		// String keyFilePath =
		// aprioriOutputParameterMap.get(IP_AdeptKeyFilePath).getClazz().cast((parameterMap.get(IP_AdeptKeyFilePath)));
		// String keyFilePath =
		// String.class.cast((parameterMap.get(IP_AdeptKeyFilePath)));
		logger.debug("keyfilePath {}", keyFilePath);
		File file = new File(keyFilePath);

		// path where the .pdf files reside
		String pdfFolderPath = file.getParent();

		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

			// cfr. keyfile.txt for formatting metadata

			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				// ignore first line with field headers and last empty line
				if (!line.startsWith("File Path") && line.trim().length() > 0) {
					// Use String.split instead of StringTokenizer because
					// StringTokenizer ignores tokens without content.
					// http://www.velocityreviews.com/forums/t136007-stringtokenizer-
					// ignores-tokens-without-content.html

					// If the lines ends with an empty field, the last field
					// will
					// only be picked up if we set the limit to 14
					String[] lineFields = line.split("\\|", 14);
					// File Path
					// String pdfFilePath = lineFields[0]; // not used, derived
					// from
					// documentID later on
					// BEGIN_PDF|
					// String beginPDF = lineFields[1]; // field not filled in
					// by
					// Adept
					// JOBNAAM|
					String jobName = lineFields[2];
					// DOCUMENT_ID|
					String pdfFilePath = pdfFolderPath + File.separator
							+ lineFields[3] + ".pdf";
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
					String nameInsuranceTaker = lineFields[11]
							.replace("[", " ").replace("\\", "'");
					// NAAM_VZ|
					String nameInsured = lineFields[12].replace("[", " ")
							.replace("\\", "'");
					// NRVOLGKORG
					String sequenceNumberString = lineFields[13];

					try {
						Integer sequenceNumber = Integer
								.parseInt(sequenceNumberString);
						Date date = dateFormat.parse(dateString);
						Integer.parseInt(categoryString);
						Integer year = Integer.parseInt(yearString);

						// determine spacepath
						StringBuffer sb = new StringBuffer();
						sb.append("/cm:");
						// group per 10**5
						String base = "p" + policyNumber.substring(0, 5);
						sb.append(base);
						sb.append("00000-");
						sb.append(base);
						sb.append("99999");
						sb.append("/cm:p");
						sb.append(policyNumber);
						sb.append("/cm:");
						sb.append(categoryString);

						// everything ok, store in parameterMap
						parameterMap.put(OP_LiJobName, jobName);
						parameterMap.put(OP_LiSourcePdfFilePath, pdfFilePath);
						parameterMap.put(OP_LiTypeDocument, type);
						parameterMap.put(OP_LiCategory, categoryString);
						parameterMap.put(OP_LiSequenceNumber, sequenceNumber);
						parameterMap.put(OP_LiDate, date);
						parameterMap.put(OP_LiPolicyNumber, policyNumber);
						parameterMap.put(OP_LiProducerNumber, producerNumber);
						parameterMap.put(OP_LiNameInsuranceTaker,
								nameInsuranceTaker);
						parameterMap.put(OP_LiNameInsured, nameInsured);
						parameterMap.put(OP_LiContractType, contractTypeString);
						parameterMap.put(OP_LiYear, year);
						parameterMap.put(OP_LiSinkFolderRelativePath,
								sb.toString());
						parameterMap.put(OP_LiDocumentGroup, policyNumber);

						configuredAction.getAppliedConfiguredActionOnSuccess()
								.execute(parameterMap);
					} catch (Exception e) {
						logger.error("Invalid metadata {}, skipping ...", line);
						configuredAction.getAppliedConfiguredActionOnFailure()
								.execute(parameterMap);
						continue;// go to next line
					}
				}
			}
			bufferedReader.close();
			fileReader.close();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			configuredAction.getAppliedConfiguredActionOnFailure().execute(
					parameterMap);
		} catch (IOException e) {
			logger.error(e.getMessage());
			configuredAction.getAppliedConfiguredActionOnFailure().execute(
					parameterMap);
		}
	}
}