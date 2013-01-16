package eu.xenit.move2alf.repository;

import java.io.File;
import java.util.List;
import java.util.Map;

import eu.xenit.move2alf.repository.alfresco.ws.Document;

/**
 * 
 * <p>To be usable by move2alf the methods in this interface must be implemented by
 * a specific repository interface (e.g. webservice interface).</p>
 * 
 * <p>For an alfresco repository spacePaths are relative to 'Company Home' in
 * the SpacesStore store and are formatted as /cm:Space1/cm:Space2/cm:Space3.</p>
 * 
 * <p>Exception handling</p>
 * <ul>
 * <li>If a method encounters a connection problem with the repository, a
 * {@link RepositoryAccessException} will be thrown.</li>
 * <li>If a method request can not be handled by the repository, a
 * {@link RepositoryException} will be thrown.</li>
 * <li>If a method encounters an 'impossible/abnormal' state it will 
 * throw a {@link RepositoryFatalException}.</li>
 * </ul>
 */

public interface RepositoryAccessSession {

	/**
	 * Close this session.
	 */
	public abstract void closeSession();

	/**
	 * Store a document in the repository in space <code>spacePath</code>. 
	 * Missing parent spaces are created automatically.
	 * 
	 * @param document : document to store
	 * @param mimeType : mime-type of the document to store
	 * @param spacePath : 
	 * @param description : this value will be written in the title and description
	 * properties
	 * @param contentModelNamespace : namespace of the content model, e.g. "{li.model}"
	 * @param contentModelType : content model, e.g. "polisDoc"
	 * @param meta : map of name-value pairs to store as properties in alfresco
	 * @param multiValueMeta : map of properties that can be multi-valued. The values are separated by a ','
	 * @throws RepositoryAccessException : exception thrown when there is
	 * a connectivity problem to the repository.
	 * @throws RepositoryException : exception thrown when the repository can not execute the request.
	 * @throws IllegalDocumentException 
	 */
	public abstract void storeDocAndCreateParentSpaces(File document,
			String mimeType, String spacePath, String description,
			String contentModelNamespace, String contentModelType,
			Map<String, String> meta, Map<String, String> multiValueMeta)
			throws RepositoryAccessException, RepositoryException, IllegalDocumentException;

	/**
	 * Check whether the document with name <code>docName</code> exists in the
	 * space <code>spacePath</code>
	 * @param docName
	 * @param spacePath
	 * @return true if the document is present in the repository.
	 * @throws RepositoryAccessException : exception thrown when there is
	 * a connectivity problem to the repository.
	 */
	public abstract boolean doesDocExist(String docName, String spacePath)
			throws RepositoryAccessException;

	/**
	 * Update a document in the repository in space <code>spacePath</code>.
	 * 
	 * @param spacePath
	 * @param docName : name of document in alfresco of which content will be updated
	 * @param docNewContent : file on the file system that contains the new content
	 * @param mimeType
	 * @param checkSize if <code>true</code> a check is first done on the size. If the
	 * size is equal no update is done.
	 * @throws RepositoryAccessException : exception thrown when there is
	 * a connectivity problem to the repository.
	 * @throws RepositoryException : exception thrown when the repository can not execute the request.
	 */
	public abstract void updateContentByDocNameAndPath(String spacePath,
			String docName, File docNewContent, String mimeType, boolean checkSize)
			throws RepositoryAccessException, RepositoryException;

	/**
	 * Update the meta data of the document that is stored with the name
	 * <code>docName</code> in space <code>spacePath</code>.
	 * @param spacePath
	 * @param docName
	 * @param meta : map of name-value pairs to store as properties in alfresco
	 * @throws RepositoryAccessException : exception thrown when there is
	 * a connectivity problem to the repository.
	 * @throws RepositoryException : exception thrown when the repository can not execute the request.
	 */
	public void updateMetaDataByDocNameAndPath(String spacePath,
			String docName, String nameSpace, Map<String, String> meta)
			throws RepositoryAccessException, RepositoryException;

	/**
	 * Delete the document that is stored with the name
	 * <code>docName</code> in space <code>spacePath</code>.
	 * 
	 * @param spacePath
	 * @param docName
	 * @throws RepositoryAccessException : exception thrown when there is
	 * a connectivity problem to the repository.
	 * @throws RepositoryException : exception thrown when the repository can not execute the request.
	 */
	public void deleteByDocNameAndSpace(String spacePath, String docName)
			throws RepositoryAccessException, RepositoryException;

	/**
	 * Delete the space named <code>spacePath</code>.
	 * 
	 * @param spacePath
	 * @param onlyIfEmpty if true space is only deleted when empty, else space
	 * is deleted with its children
	 * @throws RepositoryAccessException : exception thrown when there is
	 * a connectivity problem to the repository.
	 * @throws RepositoryException : exception thrown when the repository can not execute the request.
	 */
	public void deleteSpace(String spacePath, boolean onlyIfEmpty)
			throws RepositoryAccessException, RepositoryException;

	/**
	 * Sets groups and their roles to a 
	 * @param path : path on which to set access control list
	 * @param inheritPermissions : if <code>true</code>, permissions of parent space are inherited
	 * @param accessControl : a map with the group name as key and the role as value, e.g. for
	 * alfresco
	 * <code>
	 *  HashMap<String,String> acMap = new HashMap<String,String>();
     *  acMap.put("GROUP_GSLRBE-SCANNINGNLMED", "Consumer");
     *  acMap.put("GROUP_GSLRBE-SCANNINGNLMGR", "Coordinator");
	 * </code>
	 * @throws RepositoryAccessException : exception thrown when there is
	 * a connectivity problem to the repository.
	 * @throws RepositoryException : exception thrown when the repository can not execute the request.
	 */
	public void setAccessControlList(String path,
			boolean inheritPermissions, Map<String, String> accessControl)
			throws RepositoryAccessException, RepositoryException;

	/**
	 * Remove all documents with size 0 that are descendants of <code>spacePath</code>.
	 * @param spacePath
	 * @return the number of documents removed
	 * @throws RepositoryAccessException : exception thrown when there is
	 * a connectivity problem to the repository.
	 * @throws RepositoryException : exception thrown when the repository can not execute the request.
	 */
	public long removeZeroSizedFromTree(String spacePath)
			throws RepositoryAccessException, RepositoryException;

	public void storeDocAndCreateParentSpaces(Document document)
			throws RepositoryAccessException, RepositoryException, IllegalDocumentException;

	/**
	 * Store all documents and create parent spaces if necessary
	 * @param documents	The list of documents
	 * @param allowOverwrite	Should an overwrite be done when the document exists in the destination?
	 * @throws RepositoryAccessException
	 * @throws RepositoryException
	 * @throws PartialUploadFailureException 
	 */
	public void storeDocsAndCreateParentSpaces(List<Document> documents, boolean allowOverwrite)
			throws RepositoryAccessException, RepositoryException, PartialUploadFailureException;

	/**
	 * Clear all caches
	 */
	public void clearCaches();

	/**
	 * Store documents and create parent spaces if necessary.
	 * @param documents The list of documents
	 * @param allowOverwrite	What to do for documents that already exist?
	 * @param optimistic	Should we try to upload without checking if the document exists? If true, uploads will go faster in case of success.
	 * @throws RepositoryException 
	 * @throws RepositoryAccessException 
	 * @throws PartialUploadFailureException 
	 */
	public abstract void storeDocsAndCreateParentSpaces(List<Document> documents,
			boolean allowOverwrite, boolean optimistic) throws RepositoryAccessException,
			RepositoryException, PartialUploadFailureException;

}