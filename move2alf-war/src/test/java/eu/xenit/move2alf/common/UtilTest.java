package eu.xenit.move2alf.common;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


/**
 * @author Stan Mine
 */
public class UtilTest {

    @Test
    public void createRelativePathLinuxTest(){
        try {
            String directory = "/opt/move2alf/DATA/";
            String file = directory + "word001.docx";
            String path = "dd";

            String relativePath = this.getCreateRelativePath(directory, file, path, "/");

            Assert.assertEquals("dd/", relativePath);

        }
        catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void createRelativePathWindowsTest(){
        try {
            String directory = "C:\\Alfresco\\move2alf\\DATA\\";
            String file = directory + "word001.docx";
            String path = "dd";

            String relativePath = this.getCreateRelativePath(directory, file, path, "\\");

            Assert.assertEquals("dd\\", relativePath);

        }
        catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void createRelativePathAppleTest(){
        try {
            String directory = "Macintosh HD:alfresco:move2alf:DATA:";
            String file = directory + "word001.docx";
            String path = "dd";

            String relativePath = this.getCreateRelativePath(directory, file, path, ":");

            Assert.assertEquals("dd:", relativePath);

        }
        catch (Exception e){
            Assert.fail();
        }
    }

    private String getCreateRelativePath(String directory, String file, String path, String separator) throws Exception{
        setFinalStatic(File.class.getDeclaredField("separator"), separator);

        return Util.createRelativePath(path, file, directory);
    }

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        // remove final modifier from field
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }
}
