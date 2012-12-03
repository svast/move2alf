package eu.xenit.move2alf.core.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.action.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;


public class CSVMetadataLoader extends Action {

	private static final Logger logger = LoggerFactory.getLogger(CSVMetadataLoader.class);
	private static final char CSV_DELIMITER = '|';
	private static final char STRING_ESCAPE = '\'';

	public CSVReader createReader(File inputFile) throws FileNotFoundException {
		CSVReader reader = new CSVReader(new FileReader(inputFile), CSV_DELIMITER, STRING_ESCAPE);
		return reader;
	}

	@Override
	public void execute(ConfiguredAction configuredAction, Map<String, Object> parameterMap) {
		File inputFile = (File)parameterMap.get(Parameters.PARAM_FILE);
		String namespace = configuredAction.getParameter("NAMESPACE");
		if(namespace!=null)
			parameterMap.put(Parameters.PARAM_NAMESPACE, "{"+namespace+"}");
		String type = configuredAction.getParameter("CONTENTTYPE");
		if(type!=null)
			parameterMap.put(Parameters.PARAM_CONTENTTYPE, type);
		/*String mime = configuredAction.getParameter("MIMETYPE");
		if(mime!=null)
			parameterMap.put(Parameters.PARAM_MIMETYPE, mime);*/
		
		CSVReader reader = null;
		String[] metadataFields = null;

		try {
			reader = createReader(inputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			metadataFields = readMetadataFields(reader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		String[] nextLine;
		try {
			while ((nextLine = reader.readNext()) != null) {
				processLine(nextLine,metadataFields,parameterMap);
				//printMetadata(parameterMap);
				dump(configuredAction,parameterMap);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void processLine(String[] nextLine, String[] metadataFields, Map<String, Object> parameterMap) {
		if(nextLine.length != metadataFields.length)
			throw new RuntimeException("Line " + nextLine + " does not have the same number of fields as the header line");


		HashMap docMetadata = new HashMap();
		for(int i=0;i<nextLine.length;i++) {
			if(i==0) {
				parameterMap.put(Parameters.PARAM_INPUT_FILE,nextLine[i]);
				parameterMap.put(Parameters.PARAM_FILE,new File(nextLine[i]));
			}
			else
				docMetadata.put(metadataFields[i], nextLine[i]);
		}	
		parameterMap.put(Parameters.PARAM_METADATA,docMetadata);
	}

	private void printMetadata(Map<String,Object> parameterMap) {
		Iterator iterator = parameterMap.keySet().iterator();  
		while (iterator.hasNext()) {  
			String key = iterator.next().toString();  
			String value = parameterMap.get(key).toString();  

			logger.debug(key + " " + value);  
		} 

	}


	public String[]  readMetadataFields(CSVReader reader) throws IOException {
		// read first line from the file, with metadata description
		String[] metadataFields = reader.readNext();

		if(metadataFields==null)
			throw new RuntimeException("Empty input file");

		return metadataFields;
	}


	public void dump(ConfiguredAction configuredAction, Map<String, Object> parameterMap)  {
		ConfiguredAction nextAction = configuredAction.getAppliedConfiguredActionOnSuccess();
		if (nextAction != null) {
			getJobService().executeAction((Integer) parameterMap.get("cycle"), nextAction, parameterMap);
		}
	}

	@Override
	public String getName() {
		return "CSVMetadata Parser";
	}

	@Override
	public String getDescription() {
		return "CSVMetadata Parser";
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_METADATA;
	}

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// TODO Auto-generated method stub

	}

}
