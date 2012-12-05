package eu.xenit.move2alf.repository.alfresco.ws;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.CMLUpdate;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.Utils;

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

	public CMLCreate toCMLCreate(Reference createSpaceIfNotExists) {
		List<NamedValue> contentProps = new ArrayList<NamedValue>();

		// these properties are always present
		contentProps.add(Utils.createNamedValue(Constants.PROP_NAME, doc.file.getName()));
		contentProps.add(Utils.createNamedValue(Constants.PROP_CONTENT,
				getContentDetailsAndCreateIfNotExists()));
		
		return null;		
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
