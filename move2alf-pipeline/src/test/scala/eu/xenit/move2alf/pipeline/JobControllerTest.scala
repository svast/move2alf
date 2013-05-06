package eu.xenit.move2alf.pipeline

import eu.xenit.move2alf.pipeline.actions.{ActionConfig, AbstractEndingAction, AbstractBasicAction, AbstractBeginAction}

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/6/13
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */
object JobControllerTest {
  def main(args: Array[String]): Unit = {
    val config = new ActionConfig("First", classOf[StartAction], 1)
    val middleAction = new ActionConfig("Middle", classOf[MiddleAction], 6)
    val endAction = new ActionConfig("End", classOf[EndAction], 3)

    config.addReceiver(middleAction)
    middleAction.addReceiver(endAction)
    val jobName = "TestJob"
    JobController.createJob(jobName, config)
    JobController.startJob(jobName)
  }

}

class StartAction extends AbstractBeginAction{
  def executeImpl() {
    1 to 125 foreach { i => sendMessage("Middle", new StringMessage("Message number "+ i))}
  }
}

class MiddleAction extends AbstractBasicAction[StringMessage] {
  def execute(message: StringMessage) {
    println("MiddleAction: "+message.string)
    sendMessage("End", message)
  }
}

class EndAction extends AbstractEndingAction[StringMessage]{
  def execute(message: StringMessage) {
    println("EndAction: "+ message.string)
  }
}
