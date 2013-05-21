package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.SendMailMessage;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/15/13
 * Time: 11:54 AM
 * To change this template use File | Settings | File Templates.
 */
@ActionInfo(classId = "EmailAction",
            description = "Sends an email")
public class EmailAction extends Move2AlfReceivingAction<SendMailMessage> {
    @Override
    protected void executeImpl(SendMailMessage message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
