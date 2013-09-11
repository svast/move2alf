package eu.xenit.move2alf.web.controller.destination.model;

import eu.xenit.move2alf.web.dto.DestinationConfig;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 9/11/13
 * Time: 11:36 AM
 */
public class CastorDestinationModel extends DestinationConfig {

    @NotEmpty
    private String node1;

    public String getNode2() {
        return node2;
    }

    public void setNode2(String node2) {
        this.node2 = node2;
    }

    public String getNode1() {
        return node1;
    }

    public void setNode1(String node1) {
        this.node1 = node1;
    }

    public String getNode3() {
        return node3;
    }

    public void setNode3(String node3) {
        this.node3 = node3;
    }

    @NotEmpty
    private String node2;

    @NotEmpty
    private String node3;

    @NotEmpty
    private String clusterName;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getEncodedNodes() {
        return getNode1()+"|"+getNode2()+"|"+getNode3();
    }
}
