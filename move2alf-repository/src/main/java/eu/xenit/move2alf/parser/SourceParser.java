package eu.xenit.move2alf.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.Configurator;
import eu.xenit.move2alf.EDocDumperMode;

public abstract class SourceParser {
	private static Logger logger = LoggerFactory.getLogger(SourceParser.class);

	protected ThreadPoolExecutor tpe = null;
	protected int nbrOfThreads = 0;

	protected String user;
	protected String password;
	protected URL alfrescoUrl;

	protected String archiveSpaceRoot;

	protected EDocDumperMode docDumperMode = EDocDumperMode.TStore;

	protected StringBuffer report = new StringBuffer();

	// the folderVector is stored here for 2 reasons:
	// FileSystemParser: to determine the relative paths
	// all Parsers that use 'loadedFolder' mechanism => me must be able to find
	// the corresponding loadedFolder
	protected Vector<String> folderVector;

	// map that stores which processedFolder must be used for which basePath
	protected Map<String, File> processedFolderMap = new HashMap<String, File>();

	public SourceParser() {
	}

	public void setThreadPool(int nbrOfThreads) {
		this.nbrOfThreads = nbrOfThreads;
		// use a ThreadPool (else risk of too many threads which slows down)
		// this is an unbounded queue, so adding a task will always succeed
		tpe = new ThreadPoolExecutor(nbrOfThreads, nbrOfThreads,
				Long.MAX_VALUE, TimeUnit.NANOSECONDS,
				new LinkedBlockingQueue<Runnable>());
	}

	public void setConfiguration(Configurator configurator, String pollerId)
			throws MalformedURLException {
		user = configurator.getProperty("admin.alfrescoUser");
		password = configurator.getProperty("admin.alfrescoPassword");
		alfrescoUrl = new URL(configurator.getPollerProperty(pollerId,
				"alfrescoUrl"));

		boolean automove = "true".equals(configurator.getPollerProperty(pollerId,
				"autoMove"));
		if (automove) {
			folderVector = configurator.getAmFolders(pollerId);
		} else {
			folderVector = configurator.getFolders(pollerId);
		}
		
		// create map and check on presence of processedFolder
		Vector<String> processedFolderVector = configurator.getProcessedFolders(pollerId);
		for(int i=0;i<processedFolderVector.size();i++){
			String folder = folderVector.get(i);
			String processedFolder = processedFolderVector.get(i);
			File processedFolderFile = new File(processedFolder);
			if(processedFolderFile.exists() && processedFolderFile.isDirectory()){
				processedFolderMap.put(folder, processedFolderFile);
			}else{
				logger.info("LoadedFolder {} does not exist.", processedFolder);
			}
		}
	}

	public void setArchiveSpace(String archiveSpace) {
		this.archiveSpaceRoot = archiveSpace;
	}

	public int getNbrOfThreads() {
		return nbrOfThreads;
	}

	public EDocDumperMode getDocDumperMode() {
		return docDumperMode;
	}

	public void setDocDumperMode(EDocDumperMode docDumperMode) {
		this.docDumperMode = docDumperMode;
	}

	public abstract Integer[] process(File file) throws FileNotFoundException,
			IOException;

	// needs to be overridden when using DocDumperGroups (to start the dump of
	// the last group)
	public void dump() {
	}

	// only for archive loading
	public ThreadPoolExecutor getTpe() {
		return tpe;
	}

	public String getReport() {
		return report.toString();
	}

	public void clearReport() {
		report = new StringBuffer();
	}

	public static String determineMimeType(String fileName) {
		String mimeType = null;
		if (fileName.toLowerCase().endsWith("pdf")) {
			mimeType = "application/pdf";
		} else if (fileName.toLowerCase().endsWith("fdf")) {
			mimeType = "application/pdf";
		} else if (fileName.toLowerCase().endsWith("tif")) {
			mimeType = "image/tif";
		} else if (fileName.toLowerCase().endsWith("xls")) {
			mimeType = "application/vnd.ms-excel";
		} else if (fileName.toLowerCase().endsWith("doc")) {
			mimeType = "application/msword";
		} else if (fileName.toLowerCase().endsWith("ppt")) {
			mimeType = "application/vnd.ms-powerpoint";
		} else if (fileName.toLowerCase().endsWith("txt")) {
			mimeType = "text/plain";
		} else if (fileName.toLowerCase().endsWith("html")) {
			mimeType = "text/html";
		} else if (fileName.toLowerCase().endsWith("xml")) {
			mimeType = "application/xml";
		} else if (fileName.toLowerCase().endsWith("svg")) {
			mimeType = "image/svg+xml";
		}else if (fileName.toLowerCase().endsWith("msg")) {
			mimeType = "application/vnd.ms-outlook";
		} else {
			logger.warn("Mimetype is unknown");
			mimeType = "unknown";
		}
		return mimeType;
	}
	
	protected String getBestMatchingPath(String path){
		String bestMatchingPath="";
		for (String folder : folderVector) {
			if (path.startsWith(folder)
					&& folder.length() > bestMatchingPath.length()) {
				bestMatchingPath = folder;
			}
		}
        return bestMatchingPath;
	}
}
