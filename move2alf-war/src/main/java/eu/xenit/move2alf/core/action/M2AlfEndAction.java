package eu.xenit.move2alf.core.action;


import eu.xenit.move2alf.pipeline.actions.AbstractStateAction;
import eu.xenit.move2alf.pipeline.actions.EOCAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ClassInfo(classId = "M2AlfEndAction",
            description = "Action that ends the cycle and sends mailmessages if configured so")
public class M2AlfEndAction extends AbstractStateAction implements EOCAware {

    private static final Logger logger = LoggerFactory.getLogger(M2AlfEndAction.class);

    @Override
    public void beforeSendEOC() {

    }
}
