package eu.xenit.move2alf.repository.cmis;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

import eu.xenit.move2alf.repository.RepositoryAccessException;
import eu.xenit.move2alf.repository.RepositoryAccessSession;
import eu.xenit.move2alf.repository.RepositoryException;
import eu.xenit.move2alf.repository.UploadResult;
import eu.xenit.move2alf.repository.alfresco.ws.Document;

public class CmisRepositoryAccessSession implements RepositoryAccessSession {

	private Session session;

	public CmisRepositoryAccessSession(URL url, String user, String pass) {
		SessionFactory f = SessionFactoryImpl.newInstance();
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put(SessionParameter.USER, user);
		parameter.put(SessionParameter.PASSWORD, pass);
		parameter.put(SessionParameter.ATOMPUB_URL, url.toString());
		parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
		// TODO: repo-id automatisch ophalen...
		parameter.put(SessionParameter.REPOSITORY_ID, "3fae28d2-0653-42a3-b2cd-f6b8ad36341b");
		this.session = f.createSession(parameter);
	}
	
	public void closeSession() {
	}

	public void deleteByDocNameAndSpace(String spacePath, String docName)
			throws RepositoryAccessException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void deleteSpace(String spacePath, boolean onlyIfEmpty)
			throws RepositoryAccessException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public boolean doesDocExist(String docName, String spacePath)
			throws RepositoryAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	
	public long removeZeroSizedFromTree(String spacePath)
			throws RepositoryAccessException, RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setAccessControlList(String path, boolean inheritPermissions,
			Map<String, String> accessControl)
			throws RepositoryAccessException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void storeDocAndCreateParentSpaces(File document, String mimeType,
			String spacePath, String description, String contentModelNamespace,
			String contentModelType, Map<String, String> meta,
			Map<String, String> multiValueMeta)
			throws RepositoryAccessException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void updateContentByDocNameAndPath(String spacePath, String docName,
			File docNewContent, String mimeType, boolean checkSize)
			throws RepositoryAccessException, RepositoryException {
		// TODO Auto-generated method stub

	}

	public void updateMetaDataByDocNameAndPath(String spacePath,
			String docName, String nameSpace, Map<String, String> meta)
			throws RepositoryAccessException, RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public HashMap<String,UploadResult> storeDocAndCreateParentSpaces(Document document)
			throws RepositoryAccessException, RepositoryException {
		// TODO
		throw new RuntimeException("not implemented");
	}

	@Override
	public HashMap<String, UploadResult> storeDocsAndCreateParentSpaces(List<Document> documents, boolean allowOverwrite)
			throws RepositoryAccessException, RepositoryException {
		// TODO
		throw new RuntimeException("not implemented");
	}

	@Override
	public void clearCaches() {
		// TODO
		throw new RuntimeException("not implemented");
	}

	public HashMap<String,UploadResult> storeDocsAndCreateParentSpaces(List<Document> documents, boolean allowOverwrite,
			boolean optimistic) throws RepositoryAccessException, RepositoryException {
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean doesFileNameExists(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doesFileNameExists(String name) {
		// TODO Auto-generated method stub
		return false;
	}

}
