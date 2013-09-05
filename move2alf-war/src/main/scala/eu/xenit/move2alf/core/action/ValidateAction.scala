package eu.xenit.move2alf.core.action

import eu.xenit.move2alf.pipeline.actions.StartAware
import messages.{BatchACLMessage, ValidateMessage, SetAclMessage}
import eu.xenit.move2alf.common.exceptions.Move2AlfException
import eu.xenit.move2alf.logic.PipelineAssemblerImpl
;

/**
 * Created with IntelliJ IDEA.
 * User: rox
 * Date: 9/3/13
 * Time: 3:13 PM
 */
class ValidateAction extends ActionWithDestination[Any, Boolean] with StartAware {
  def onStart() {
    sendTaskToDestination("ValidateMessage", new ValidateMessage(), result => {
      if(result) {
          sendMessage("Destination " + getDestination + " validated");
      } else {
        logger.debug("Destination " + getDestination + " not reachable");
        handleError("StartMessage", "Destination not reachable");
    }})
  }

  protected def executeImpl(message: Any) {}
}
