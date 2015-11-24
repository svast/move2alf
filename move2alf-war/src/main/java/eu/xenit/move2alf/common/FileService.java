package eu.xenit.move2alf.common;

import java.io.File;

/**
 * Created by thijs on 11/24/15.
 */
public interface FileService {
    File moveFile(String newPath, File file);

    String createRelativePath(String path, String file, String directory);
}
