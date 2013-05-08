package eu.xenit.move2alf.pipeline.actions.context

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/3/13
 * Time: 4:26 PM
 * To change this template use File | Settings | File Templates.
 */
class EOCBlockingContextImpl(private val actionContext: AbstractActionContext) extends EOCBlockingContext{
  def blockEOC() {
    actionContext.blockEOC()
  }

  def unblockEOC() {
    actionContext.unblockEOC()
  }
}
