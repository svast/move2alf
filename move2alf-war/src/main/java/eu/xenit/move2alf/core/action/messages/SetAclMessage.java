package eu.xenit.move2alf.core.action.messages;

import eu.xenit.move2alf.core.sharedresource.alfresco.ACL;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/12/13
 * Time: 5:09 PM
 */
public class SetAclMessage {

    public final ACL acl;
    public SetAclMessage(ACL acl){
        this.acl = acl;
    }
}
