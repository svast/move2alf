package eu.xenit.move2alf.common;

/**
 * List of parameters passed between actions in the parameterMap.
 */
public class Parameters {
	/**
	 * Counter with number of files to load. Type: CountDownLatch
	 */
	public static final String PARAM_COUNTER = "counter";

	/*
	 * INTERNAL Id of the current cycle Type: Integer
	 */
	public static final String PARAM_CYCLE = "cycle";

	/*
	 * INTERNAL
	 */
	public static final String PARAM_THREADPOOL = "threadpool";

	/*
	 * INTERNAL
	 */
	public static final String PARAM_THREAD = "thread";

	/**
	 * File to upload. Type: File
	 */
	public static final String PARAM_FILE = "file";

	/**
	 * Relative path of file to upload in destination (used to create folder
	 * structure). Type: String
	 */
	public static final String PARAM_RELATIVE_PATH = "relativePath";

	/**
	 * Map with metadata of file. The key is the property name without
	 * namespace, PARAM_NAMESPACE is used as namespace for all properties. Type:
	 * Map<String, String>
	 */
	public static final String PARAM_METADATA = "metadata";

	/**
	 * Map with metadata of file. The key is the property name without
	 * namespace, PARAM_NAMESPACE is used as namespace for all properties.
	 * Values are comma separated. Type: Map<String, String>
	 */
	public static final String PARAM_MULTI_VALUE_METADATA = "multiValueMetadata";

	/**
	 * Description of the file. Type: String
	 */
	public static final String PARAM_DESCRIPTION = "description";

	/**
	 * Content type of the file. Type: String
	 */
	public static final String PARAM_CONTENTTYPE = "contenttype";

	/**
	 * Namespace of the document model. Type: String
	 */
	public static final String PARAM_NAMESPACE = "namespace";

	/**
	 * Mimtype of the file. Type: String
	 */
	public static final String PARAM_MIMETYPE = "mimetype";

	/**
	 * ACL to set. The key contains the folder where the ACL should be applied,
	 * the value is a Map with the authority as key and permission as value.
	 * Type: Map<String, Map<String, String>>
	 */
	public static final String PARAM_ACL = "acl";

	public static final String PARAM_INHERIT_PERMISSIONS = "inheritPermissions";

	public static final String PARAM_STATUS = "status";
	public static final String VALUE_FAILED = "failed";
	public static final String VALUE_OK = "ok";

	public static final String PARAM_ERROR_MESSAGE = "errormessage";

	public static final String PARAM_STAGE = "stage";
	public static final String VALUE_AFTER = "after";
	public static final String VALUE_BEFORE = "before";

	public static final String PARAM_EXTENSION = "extension";

	public static final String PARAM_TRANSFORM_FILE_LIST = "transformFileList";

	public static final String PARAM_INPUT_FILE = "inputFile";

	/**
	 * Fields to add to the report for the current file. Type: Map<String,
	 * String>
	 */
	public static final String PARAM_REPORT_FIELDS = "reportFields";

	public static final String PARAM_COMMAND = "command";

	/*
	 * INTERNAL MoveCycleListener uses this to pass the moved files to the
	 * SourceAction. Type: List<File>
	 */
	public static final String PARAM_FILES_TO_LOAD = "filesToLoad";
}
