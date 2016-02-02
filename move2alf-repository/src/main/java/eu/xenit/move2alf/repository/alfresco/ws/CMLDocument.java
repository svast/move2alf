package eu.xenit.move2alf.repository.alfresco.ws;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.CMLUpdate;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xenit.move2alf.repository.RepositoryAccessException;
import eu.xenit.move2alf.repository.RepositoryException;

public class CMLDocument {

	private static Logger logger = LoggerFactory
			.getLogger(CMLDocument.class);

	private Document doc;

	public Document getDocument() {
		return doc;
	}

	private String id;

	public String getId() {
		return id;
	}

	private String contentDetails;
	private WebServiceRepositoryAccessSession session;

	public CMLDocument(WebServiceRepositoryAccessSession session, Document doc, String id) {
		this.session = session;
		this.doc = doc;
		this.id = id;
        this.contentDetails = doc.contentUrl;
	}

	public CMLUpdate toCMLUpdate(Reference ref) {
		logger.debug("Converting doc to CMLUpdate: {}", doc.name);
		Predicate pred = new Predicate(new Reference[] {ref},
				WebServiceRepositoryAccessSession.store, null);

		return new CMLUpdate(getContentPropsAndCreateIfNotExists(), pred, null);
	}

	public CMLCreate toCMLCreate() throws RepositoryAccessException, RepositoryException {
		logger.debug("Converting doc to CMLCreate: {}", doc.name);
		Reference parentSpace = session.createSpaceIfNotExists(getQnamePath());
		ParentReference parentRef = new ParentReference(WebServiceRepositoryAccessSession.store,
				parentSpace.getUuid(), null, Constants.ASSOC_CONTAINS, null);
		parentRef.setChildName("{http://www.alfresco.org/model/content/1.0}"
				+ doc.name);

		return new CMLCreate(this.getId(), parentRef, parentSpace.getUuid(),
				Constants.ASSOC_CONTAINS, null, /* Constants.TYPE_CONTENT */
				doc.contentModelNamespace + doc.contentModelType, getContentPropsAndCreateIfNotExists());
	}

	private NamedValue[] contentProps;

	private NamedValue[] getContentPropsAndCreateIfNotExists() {
		if (contentProps != null) {
			return contentProps;
		}

		List<NamedValue> contentProps = new ArrayList<NamedValue>();

		// these properties are always present
		contentProps.add(Utils.createNamedValue(Constants.PROP_NAME, doc.name));
		contentProps.add(Utils.createNamedValue(Constants.PROP_CONTENT,getContentDetails()));

		if (doc.meta != null) {
			// Enumeration<String> E = meta.;
			// while (E.hasMoreElements()) {
			session.processMetadata(doc.contentModelNamespace, doc.meta, contentProps);
		}
		if (doc.multiValueMeta != null) {
			session.processMultiValuedMetadata(doc.contentModelNamespace, doc.multiValueMeta, contentProps);
		}

        boolean hasTitle = false;
        boolean hasDescription = false;
        for(NamedValue value:contentProps) {
            if(value.getName().equals(Constants.PROP_TITLE))
                hasTitle = true;
            if(value.getName().equals(Constants.PROP_DESCRIPTION))
                hasDescription = true;
        }
        if(!hasTitle)
            contentProps.add(Utils.createNamedValue(Constants.PROP_TITLE, doc.description));
        if(!hasDescription)
            contentProps.add(Utils.createNamedValue(Constants.PROP_DESCRIPTION, doc.description));
	
	return contentProps.toArray(new NamedValue[0]);
	}

	public String getXpath() {
		return WebServiceRepositoryAccessSession.companyHomePath + session.getXPathEscape(getQnamePath() + "/cm:" + doc.name);
	}

	private String getQnamePath(){
		String[] components = getSpacePath().split("/");
		String qnamePath = "/";
		for(String component: components){
			if ("" == component) {
				qnamePath += "/";
			}
			else if (component.contains(":")) {
				qnamePath += component + "/";
			}
			else {
				qnamePath += "cm:" + component + "/";
			}
		}
		if (qnamePath.length() > 0) {
			qnamePath = qnamePath.substring(0, qnamePath.length() - 1);
		}

		return qnamePath;
	}

	public String getPath() {
		return WebServiceRepositoryAccessSession.companyHomePath +
				getQnamePath() +
				"/cm:" + doc.name;
	}

	public String getContentDetails() {
		return this.contentDetails;
	}

	public String getSpacePath() {
		return doc.spacePath;
	}
}
