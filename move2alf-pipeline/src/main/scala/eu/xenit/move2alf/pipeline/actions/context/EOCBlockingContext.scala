package eu.xenit.move2alf.pipeline.actions.context

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/3/13
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
trait EOCBlockingContext {

  def blockEOC()

  def unblockEOC()

}
