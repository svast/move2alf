package eu.xenit.move2alf.core.action;

import au.com.bytecode.opencsv.CSVReader;
import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.action.messages.FileInfoMessage;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class CSVMetadataLoader extends Move2AlfAction<FileInfoMessage> {

	private static final Logger logger = LoggerFactory.getLogger(CSVMetadataLoader.class);
	private static final char CSV_DELIMITER = '|';
	private static final char STRING_ESCAPE = '\'';

	public CSVReader createReader(File inputFile) throws FileNotFoundException {
		CSVReader reader = new CSVReader(new FileReader(inputFile), CSV_DELIMITER, STRING_ESCAPE);
		return reader;
	}

	public FileInfo processLine(String[] nextLine, String[] metadataFields) {
		if(nextLine.length != metadataFields.length)
			throw new RuntimeException("Line " + nextLine + " does not have the same number of fields as the header line");

        FileInfo fileInfo = new FileInfo();
        if(nameSpace!=null)
            fileInfo.put(Parameters.PARAM_NAMESPACE, "{"+nameSpace+"}");
        if(contentType!=null)
            fileInfo.put(Parameters.PARAM_CONTENTTYPE, contentType);

		HashMap docMetadata = new HashMap();
		for(int i=0;i<nextLine.length;i++) {
			if(i==0) {
				fileInfo.put(Parameters.PARAM_INPUT_FILE,nextLine[i]);
				fileInfo.put(Parameters.PARAM_FILE,new File(nextLine[i]));
			}
			else
				docMetadata.put(metadataFields[i], nextLine[i]);
		}	
		fileInfo.put(Parameters.PARAM_METADATA,docMetadata);

        return fileInfo;
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

	@Override
	public String getDescription() {
		return "Read metadata from a CSV file";
	}

	public String getCategory() {
		return ConfigurableObject.CAT_METADATA;
	}


    private String nameSpace;
    public void setNameSpace(String nameSpace){
        this.nameSpace = nameSpace;
    }

    private String contentType;
    public void setContentType(String contentType){
        this.contentType = contentType;
    }

    @Override
    public void execute(FileInfoMessage message) {
        File inputFile = (File) message.fileInfo.get(Parameters.PARAM_FILE);

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
                sendMessage(new FileInfoMessage(processLine(nextLine, metadataFields)));
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
}
