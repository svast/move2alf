package eu.xenit.move2alf.core.action

import eu.xenit.move2alf.pipeline.actions.StartAware
import messages.ValidateMessage
/**
 * Created with IntelliJ IDEA.
 * User: rox
 * Date: 9/3/13
 * Time: 3:13 PM
 */
class ValidateAction extends ActionWithDestination[Any, Boolean] with StartAware {
  def onStart() {
      logger.debug("destination=" + getDestination)
      
      sendTaskToDestination("ValidateMessage", new ValidateMessage(), result => {
        if(result) {
          sendMessage("Destination " + getDestination + " validated");
        } else {
          logger.debug("Destination " + getDestination + " not reachable");
          handleError("ValidateMessage", "Destination not reachable");
        }})
    }

  protected def executeImpl(message: Any) {}
}