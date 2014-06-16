package eu.xenit.move2alf.core.sharedresource.castor;

import com.caringo.client.*;
import eu.xenit.move2alf.common.exceptions.Move2AlfException;
import eu.xenit.move2alf.core.sharedresource.SharedResource;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 9/10/13
 * Time: 2:09 PM
 */
public class CastorSharedResource extends SharedResource{

    private static final Logger logger = LoggerFactory.getLogger(CastorSharedResource.class);

    public static final String PARAM_NODES = "nodes";
    private List<String> nodes;
    public void setNodes(String nodes){
        this.nodes = Arrays.asList(nodes.split("\\|"));
    }

    public static final String PARAM_MAXNBROFRETRIES = "maxNbrOfRetries";
    private int maxNbrOfRetries = 3;
    public void setMaxNbrOfRetries(String maxNbrOfRetries){
        this.maxNbrOfRetries = Integer.parseInt(maxNbrOfRetries);
    }

    public static final String PARAM_CLUSTERNAME = "clusterName";
    private String clusterName;
    public void setClusterName(String clusterName){
        this.clusterName = clusterName;
    }

    private int maxTotalNbrOfConnections = 50;
    private int maxNbrOfConnectionsPerHost = 20;
    private int timeout = 5000;

    private ScspClient castorClient;
    private ScspClient getCastorClient() throws IOException {
        if(castorClient == null){
            castorClient = new ScspClient(nodes.toArray(new String[nodes.size()]), 80, maxTotalNbrOfConnections,
                    maxNbrOfConnectionsPerHost, timeout, timeout, timeout);
        }
        castorClient.start();
        return castorClient;
    }

    public static final String PARAM_MINREPS = "minReps";
    private int minReps;
    public void setMinReps(String minReps) {
        this.minReps = Integer.parseInt(minReps);
    }

    public static final String PARAM_DELETABLE = "deletable";
    private ScspDeleteConstraint deleteConstraint;
    public void setDeletable(String deletable){
        if(deletable.equals("true")){
            deleteConstraint = ScspDeleteConstraint.DELETABLE;
        } else {
            deleteConstraint = ScspDeleteConstraint.NOT_DELETABLE;
        }
    }

    public String uploadFile(File file, String mimeType) throws IOException {
        byte[] byteArray = FileUtils.readFileToByteArray(file);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
        int size = byteArray.length;
        int accessAttempt = 0;
        while (accessAttempt < maxNbrOfRetries) {

            int nbrOfRetries = 0;
            int statusCode = HttpStatus.SC_MOVED_PERMANENTLY;
            ScspHeaders headers = new ScspHeaders();
            headers.addLifepoint(null, deleteConstraint, minReps);
            headers.addValue("Content-Type", mimeType);
            while (((statusCode == HttpStatus.SC_TEMPORARY_REDIRECT) || (statusCode == HttpStatus.SC_MOVED_PERMANENTLY))
                    && (nbrOfRetries < maxNbrOfRetries)) {
                logger.debug("Post: StatusCode, nbrOfRetries: "+ statusCode+" "+nbrOfRetries);
                statusCode = HttpStatus.SC_NOT_FOUND;
                try
                {
                    ScspResponse writeResponse = getCastorClient().write("", inputStream, size,
                            new ScspQueryArgs(), headers);
                    statusCode = writeResponse.getHttpStatusCode();
                    logger.debug("Post: New StatusCode "+ statusCode);
                    if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
                            || statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)
                    {
                        logger.debug("Post: StatusCode SC_MOVED_PERMANENTLY or SC_TEMPORARY_REDIRECT");
                        nbrOfRetries++;
                    }
                    else if (statusCode == HttpStatus.SC_CREATED)
                    {
                        logger.debug("Post: StatusCode SC_CREATED");
                        String castorUUID = writeResponse.getResponseHeaders().getHeaderValues("Content-UUID").get(0);

                        String encoding = (new InputStreamReader(new FileInputStream(file))).getEncoding();
                        return "contentUrl=castor://" + clusterName + "/" + castorUUID+"|mimetype="+mimeType+"|size="+size+"|encoding="+encoding;
                    }
                    else
                    {
                        logger.info("Post: Statuscode: "+statusCode);
                    }
                }
                catch (Exception e)
                {
                    logger.error("Post: "+e.getMessage());
                }
            }
            accessAttempt++;
        }
        throw new Move2AlfException("Could not upload to Castor after "+maxNbrOfRetries+" attempts");
    }

}
