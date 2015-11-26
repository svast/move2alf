package eu.xenit.move2alf.repository.alfresco.ws;

import eu.xenit.move2alf.repository.RepositoryAccess;
import eu.xenit.move2alf.repository.RepositoryAccessFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by thijs on 11/26/15.
 */
public class WebServiceRepositoryAccessFactory implements RepositoryAccessFactory {

    private final URL url;
    private final String user;
    private final String password;
    private final boolean luceneFallbackEnabled;

    public WebServiceRepositoryAccessFactory(String url, String user, String password, boolean luceneFallbackEnabled) throws MalformedURLException {
        this.url = new URL(url);
        this.user = user;
        this.password = password;
        this.luceneFallbackEnabled = luceneFallbackEnabled;
    }

    @Override
    public RepositoryAccess createRepositoryAccess() {
        return new WebServiceRepositoryAccess(url, user,
                password, luceneFallbackEnabled);
    }
}
