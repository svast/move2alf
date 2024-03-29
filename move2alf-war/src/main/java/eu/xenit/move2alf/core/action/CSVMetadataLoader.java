package eu.xenit.move2alf.core.action;

import au.com.bytecode.opencsv.CSVReader;
import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.logic.PipelineAssemblerImpl;
import eu.xenit.move2alf.pipeline.actions.EOCAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@ClassInfo(classId = "CSVMetadataLoader",
        category = ConfigurableObject.CAT_METADATA,
        description = "Loads metadata and filenames from pipe separated CSV file")
public class CSVMetadataLoader extends FileWithMetadataAction implements EOCAware {

	private static final Logger logger = LoggerFactory.getLogger(CSVMetadataLoader.class);
	private char CSV_DELIMITER = '\t';
	private static final char STRING_QUOTE = '"';
    private static final char STRING_ESCAPE = '\0';  // no escaping
    Map localCounters = new HashMap();

    private String inputPath = "";

    @Override
    public String getInputPath() {
        return inputPath;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    private File inputFile = null;

	public CSVReader createReader() throws FileNotFoundException {
		CSVReader reader = new CSVReader(new FileReader(inputFile), CSV_DELIMITER, STRING_QUOTE, STRING_ESCAPE);
		return reader;
	}

	public FileInfo processLine(String[] nextLine, String[] metadataFields) {
        localCounters.clear();
		if(nextLine.length != metadataFields.length)
			throw new RuntimeException("Line " + Arrays.asList(nextLine) + " does not have the same number of fields as the header line");

        FileInfo fileInfo = new FileInfo();
        fileInfo.put(Parameters.PARAM_INPUT_FILE,inputFile);

        if(nameSpace!=null)
            fileInfo.put(Parameters.PARAM_NAMESPACE, "{"+nameSpace+"}");
        if(contentType!=null)
            fileInfo.put(Parameters.PARAM_CONTENTTYPE, contentType);

		HashMap docMetadata = new HashMap();
		for(int i=0;i<nextLine.length;i++) {
            localCounters.put(inputFile.getAbsolutePath(),Integer.valueOf(1));
			if(Parameters.PARAM_FILE.equals(metadataFields[i])) {
                String newPath = processPath(nextLine[i]);
                File file = new File(newPath);
//                inputPath = file.getParentFile().getAbsolutePath();
                fileInfo.put(Parameters.PARAM_INPUT_PATH,inputPath);
                fileInfo.put(Parameters.PARAM_FILE,file);
                fileInfo.put(Parameters.PARAM_NAME, newPath.substring(newPath.lastIndexOf("/")+1));
                localCounters.put(file.getAbsolutePath(),Integer.valueOf(1));
                setCounter(file.getAbsolutePath(), Integer.valueOf(1));
			}
			else
				docMetadata.put(metadataFields[i], nextLine[i]);
		}	
		fileInfo.put(Parameters.PARAM_METADATA,docMetadata);
        logger.info("fileInfo=" + fileInfo);

        return fileInfo;
	}

    private String processPath(String path) {
        String newPath = path;
        String pathMappingRemote = getParameter(Parameters.PARAM_PATH_MAPPING_REMOTE);
        String pathMappingLocal = getParameter(Parameters.PARAM_PATH_MAPPING_LOCAL);
        if(pathMappingRemote != null && pathMappingLocal != null) {
            newPath = newPath.replace(pathMappingRemote,pathMappingLocal);
        }

        char oldSeparator = '/';
        if(newPath.indexOf('\\')!=-1)
            oldSeparator = '\\';

        newPath = newPath.replace(oldSeparator,File.separatorChar);

        return newPath;
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

        // if there is a mapping for parameters, use it
        boolean hasFile = false;
        for(int i=0; i < metadataFields.length; i++) {
            String value = getParameter(metadataFields[i]);
            if(value != null)
                metadataFields[i]=value;
            logger.debug("Metadata field " + i + ": " + metadataFields[i]);
            if(Parameters.PARAM_FILE.equals(metadataFields[i]))
                hasFile=true;
        }

        if(!hasFile) {
            throw new RuntimeException("The header should contain the 'file' parameter or there should be a mapping for one of headers parameters to 'file'");
        }

		return metadataFields;
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
    protected void executeImpl(FileInfo message) {

        String delimiter = getParameter(Parameters.PARAM_CSV_DELIMITER);
        if(delimiter != null) {
            if(delimiter.length()>1)
                logger.error("CsvDelimiter should have length=1, using first character");
            CSV_DELIMITER = delimiter.charAt(0);
        }

        inputFile = (File) message.get(Parameters.PARAM_FILE);
        inputPath = (String) message.get(Parameters.PARAM_INPUT_PATH);

        CSVReader reader = null;
        String[] metadataFields = null;

        try {
            reader = createReader();
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
        int filesToProcess=0;
        try {
            while ((nextLine = reader.readNext()) != null) {
                FileInfo fileInfo = processLine(nextLine, metadataFields);
                HashMap c = new HashMap(localCounters);
                sendFileInfoWithCounters(fileInfo, c);
                filesToProcess++;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            reader.close();
            setCounter(inputFile.getAbsolutePath(), Integer.valueOf(filesToProcess));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void beforeSendEOC() {
    }
}
