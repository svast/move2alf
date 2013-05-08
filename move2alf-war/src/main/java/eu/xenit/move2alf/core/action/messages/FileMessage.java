package eu.xenit.move2alf.core.action.messages;

import eu.xenit.move2alf.pipeline.AbstractMessage;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/7/13
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileMessage extends AbstractMessage {

    public final File file;
    public FileMessage(File file){
        this.file = file;
    }
}
