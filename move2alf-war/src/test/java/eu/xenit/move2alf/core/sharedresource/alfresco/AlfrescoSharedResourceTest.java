package eu.xenit.move2alf.core.sharedresource.alfresco;

import eu.xenit.move2alf.repository.RepositoryAccess;
import eu.xenit.move2alf.repository.RepositoryAccessFactory;
import eu.xenit.move2alf.repository.RepositoryAccessSession;
import org.junit.Test;
import java.io.File;

import static org.mockito.Mockito.*;


/**
 * Created by thijs on 11/26/15.
 */
public class AlfrescoSharedResourceTest {

    @Test
    public void testPutContent(){
        String contentUrl = "testContentURL";
        RepositoryAccessSession repositoryAccessSession = mock(RepositoryAccessSession.class);
        when(repositoryAccessSession.putContent(any(File.class), any(String.class)))
                .thenThrow(new RuntimeException(AlfrescoSharedResource.PUT_CONTENT_401_MESSAGE))
                .thenReturn(contentUrl);

        RepositoryAccess repositoryAccess = mock(RepositoryAccess.class);
        when(repositoryAccess.createSessionAndRetry()).thenReturn(repositoryAccessSession);

        RepositoryAccessFactory repositoryAccessFactory = mock(RepositoryAccessFactory.class);
        when(repositoryAccessFactory.createRepositoryAccess()).thenReturn(repositoryAccess);

        AlfrescoSharedResource alfrescoSharedResource = new AlfrescoSharedResource(repositoryAccessFactory);
        String resultContentUrl = alfrescoSharedResource.putContent(mock(File.class), "testMimetype");
        assert resultContentUrl == contentUrl;
    }

}
