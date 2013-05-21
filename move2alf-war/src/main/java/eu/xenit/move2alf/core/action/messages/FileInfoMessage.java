package eu.xenit.move2alf.core.action.messages;

import eu.xenit.move2alf.core.simpleaction.data.FileInfo;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/7/13
 * Time: 12:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileInfoMessage{

    public final FileInfo fileInfo;
    public FileInfoMessage(FileInfo fileInfo){
        this.fileInfo = fileInfo;
    }
}
