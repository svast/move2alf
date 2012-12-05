package eu.xenit.move2alf.repository.alfresco.ws;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.CMLUpdate;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.Utils;

import eu.xenit.move2alf.repository.RepositoryAccessException;
import eu.xenit.move2alf.repository.RepositoryException;

public class CMLDocument {

	private Document doc;
	public Document getDocument() {
		return doc;
	}

	private String contentDetails;
	private WebServiceRepositoryAccessSession session;

	public CMLDocument(WebServiceRepositoryAccessSession session, Document doc) {
		this.session = session;
		this.doc = doc;
	}

	public CMLUpdate toCMLUpdate(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	public CMLCreate toCMLCreate() throws RepositoryAccessException, RepositoryException {
		List<NamedValue> contentProps = new ArrayList<NamedValue>();

		// these properties are always present
		contentProps.add(Utils.createNamedValue(Constants.PROP_NAME, doc.file.getName()));
		contentProps.add(Utils.createNamedValue(Constants.PROP_CONTENT,
				getContentDetailsAndCreateIfNotExists()));
		
		if (doc.meta != null) {
			// Enumeration<String> E = meta.;
			// while (E.hasMoreElements()) {
			session.processMetadata(doc.contentModelNamespace, doc.meta, contentProps);
		}
		if (doc.multiValueMeta != null) {
			session.processMultiValuedMetadata(doc.contentModelNamespace, doc.multiValueMeta, contentProps);
		}
		
		contentProps.add(Utils.createNamedValue(Constants.PROP_TITLE, doc.description));
		contentProps.add(Utils.createNamedValue(Constants.PROP_DESCRIPTION, doc.description));
		
		Reference parentSpace = session.createSpaceIfNotExists(doc.spacePath);
		ParentReference parentRef = new ParentReference(WebServiceRepositoryAccessSession.store,
				parentSpace.getUuid(), null, Constants.ASSOC_CONTAINS, null);
		parentRef.setChildName("{http://www.alfresco.org/model/content/1.0}"
				+ doc.file.getName());

		return new CMLCreate(String.valueOf(this.hashCode()), parentRef, parentSpace.getUuid(),
				Constants.ASSOC_CONTAINS, null, /* Constants.TYPE_CONTENT */
				doc.contentModelNamespace + doc.contentModelType, contentProps.toArray(new NamedValue[0]));	
	}
	
	public String getXpath() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getContentDetails(){
		return this.contentDetails;
	}

	public String getSpacePath() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String getContentDetailsAndCreateIfNotExists(){
		if(getContentDetails()==null){
			contentDetails = session.putContent(doc.file, doc.mimeType);
		}
		return getContentDetails();
	}

}
