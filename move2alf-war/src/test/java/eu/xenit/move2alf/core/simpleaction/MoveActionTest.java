package eu.xenit.move2alf.core.simpleaction;

import eu.xenit.move2alf.common.FileService;
import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.simpleaction.data.FileInfo;
import eu.xenit.move2alf.pipeline.actions.context.SendingContext;
import org.junit.Test;

import java.io.File;

import static org.mockito.Mockito.*;

/**
 * Created by thijs on 11/24/15.
 */
public class MoveActionTest {

    @Test
    public void testMoveKeepStructureWithoutInputPath(){
        FileService mockFileService = mock(FileService.class);
        SendingContext sendingContext = mock(SendingContext.class);
        when(mockFileService.moveFile(any(String.class), any(File.class))).thenReturn(new File("testout"));
        MoveAction moveAction = spy(new MoveAction(mockFileService, true));
        moveAction.setSendingContext(sendingContext);
        moveAction.setPath("test123");
        FileInfo fileInfo = new FileInfo();
        File file = new File("helloWorld");
        fileInfo.put(Parameters.PARAM_FILE, file);
        moveAction.executeImpl(fileInfo);
    }
}
