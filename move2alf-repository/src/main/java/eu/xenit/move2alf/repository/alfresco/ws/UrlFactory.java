package eu.xenit.move2alf.repository.alfresco.ws;

import java.net.MalformedURLException;
import java.net.URL;

import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.AuthenticationUtils;

public class UrlFactory {
	private String alfrescoUrl = null;
	
	
	public UrlFactory(String alfrescoUrl) {
		this.alfrescoUrl = alfrescoUrl;
	}

	public URL getDownloadLink(Reference reference, String fileName,
			boolean useTicket) throws MalformedURLException {
		URL url = new URL(alfrescoUrl);
		StringBuilder sb = new StringBuilder().append("http://").append(
				url.getHost()).append(getPortClean(url)).append(
				"/alfresco/d/d/").append(reference.getStore().getScheme())
				.append("/").append(reference.getStore().getAddress()).append(
						"/").append(reference.getUuid()).append("/").append(
						fileName);

		if (useTicket) {
			sb.append("?ticket=").append(AuthenticationUtils.getTicket());
		}
		return new URL(sb.toString());
	}

	private String getPortClean(URL url) {
		return (url.getPort() == -1) ? "" : ":"
				+ Integer.toString(url.getPort());
	}

	// url with uuid
	public URL getDownloadLink(Reference reference, boolean useTicket)
			throws MalformedURLException {
		URL url = new URL(alfrescoUrl);
		StringBuilder sb = new StringBuilder().append("http://").append(
				url.getHost()).append(getPortClean(url)).append(
				"/alfresco/service/api/node/content/").append(
				reference.getStore().getScheme()).append("/").append(
				reference.getStore().getAddress()).append("/").append(
				reference.getUuid());
		if (useTicket) {
			// for this type of url one needs to use "alf_ticket", not "ticket"
			sb.append("?alf_ticket=").append(AuthenticationUtils.getTicket());
		}
		return new URL(sb.toString());
	}

	public URL getDownloadLinkAsInlineDocument(Reference reference,
			String fileName, boolean useTicket) throws MalformedURLException {
		URL url = new URL(alfrescoUrl);
		StringBuilder sb = new StringBuilder().append("http://").append(
				url.getHost()).append(

		getPortClean(url)).append("/alfresco/d/i/").append(
				reference.getStore().getScheme()).append("/")

		.append(reference.getStore().getAddress()).append("/").append(
				reference.getUuid()).append("/").append(fileName);

		if (useTicket)
			sb.append("?ticket=").append(AuthenticationUtils.getTicket());
		return new URL(sb.toString());
	}

	public URL getDownloadLinkAsSpace(Reference reference, boolean useTicket)
			throws MalformedURLException {
		URL url = new URL(alfrescoUrl);
		StringBuilder sb = new StringBuilder().append("http://").append(
				url.getHost()).append(

		getPortClean(url)).append("/alfresco/n/browse/").append(
				reference.getStore().getScheme()).append("/")

		.append(reference.getStore().getAddress()).append("/").append(
				reference.getUuid());

		if (useTicket)
			sb.append("?ticket=").append(AuthenticationUtils.getTicket());
		return new URL(sb.toString());
	}

	public URL getDetailsPageLink(Reference reference, boolean useTicket)
	throws MalformedURLException {
URL url = new URL(alfrescoUrl);
// from the details page one can copy the signature of the direct url to
// this page (Details Page URL)
// http://localhost:8080/alfresco/n/dialog:showDocDetails/workspace/
// SpacesStore/561d5804-e8a6-4ee2-87ae-a668f0db1f2c
StringBuilder sb = new StringBuilder().append("http://").append(
		url.getHost()).append(getPortClean(url)).append(
		"/alfresco/n/showDocDetails/").append(
		reference.getStore().getScheme()).append("/").append(
		reference.getStore().getAddress()).append("/").append(
		reference.getUuid());

if (useTicket) {
	sb.append("?ticket=").append(AuthenticationUtils.getTicket());
}
return new URL(sb.toString());
}

}
