package eu.xenit.move2alf.core.action.messages;

import java.io.InputStream;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: rox
 * Date: 2/10/14
 * Time: 12:23 PM
 */
public class CmisDocumentMessage {
    private Map<String, Object> properties;
    private InputStream inputStream;

    public CmisDocumentMessage(Map<String, Object> properties, InputStream inputStream) {
        this.properties = properties;
        this.inputStream = inputStream;
    }
}
