package eu.xenit.move2alf.pipeline

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/5/13
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
case object Start
case object EOC

case class M2AMessage(message: AnyRef)
class StringMessage(val string: String)
class StringMessage2(val string: String)
