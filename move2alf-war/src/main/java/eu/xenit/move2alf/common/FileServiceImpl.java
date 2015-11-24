package eu.xenit.move2alf.common;

import java.io.File;

/**
 * Created by thijs on 11/24/15.
 */
public class FileServiceImpl implements FileService {
    @Override
    public File moveFile(String newPath, File file) {
        return Util.moveFile(newPath, file);
    }

    @Override
    public String createRelativePath(String path, String file, String directory) {
        return Util.createRelativePath(path, file, directory);
    }
}
