package eu.xenit.move2alf.pipeline.actions.context

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/2/13
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
trait EOCBlockingActionContext extends AbstractActionContext{

  var blocked = false
  var nmbOfEOC = 0

  override protected def eocMessage(){
    if(!blocked){
      super.eocMessage()
    } else {
      nmbOfEOC += 1
    }
  }

  def blockEOC(){
    blocked = true
  }

  def unblockEOC(){
    blocked = false
    1 to nmbOfEOC foreach { _ => eocMessage()}
  }

}
