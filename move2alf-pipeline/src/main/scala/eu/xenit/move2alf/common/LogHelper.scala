package eu.xenit.move2alf.common;
import org.apache.log4j.Logger;

/**
 * LogHelper is a trait you can mix in to provide easy log4j logging
 * for your scala classes.
 **/
trait LogHelper {
  val loggerName = this.getClass.getName
  lazy val logger = Logger.getLogger(loggerName)
}