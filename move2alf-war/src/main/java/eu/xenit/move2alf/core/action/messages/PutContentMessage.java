package eu.xenit.move2alf.core.action.messages;

import java.io.File;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 7/1/13
 * Time: 3:38 PM
 */
public class PutContentMessage extends AlfrescoMessage{

    public final File file;
    public final String mimeType;

    public PutContentMessage(File file, String mimeType) {
        this.file = file;
        this.mimeType = mimeType;
    }
}
