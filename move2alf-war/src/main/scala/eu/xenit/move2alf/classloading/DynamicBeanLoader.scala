package eu.xenit.move2alf.classloading

import java.net.URL
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.`type`.filter.AssignableTypeFilter
import org.springframework.core.io.DefaultResourceLoader
import eu.xenit.move2alf.pipeline.actions.Action

import scala.collection.JavaConversions._
import java.util.{Set => JSet}
import org.springframework.beans.factory.FactoryBean
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.{Autowired, Value}
import java.util
import org.springframework.util.StringUtils

/**
 * @author Laurent Van der Linden
 */
trait ClasspathScanner {
  val SERVER_PRODUCTION: String = "production"

  def findMatchingClasses(basePackage: String, resourceType: Class[_],
                          parentClassLoader: ClassLoader = classOf[Action].getClassLoader): JSet[Class[_]]

  def isStatic: Boolean
}


class SystemClasspathScanner extends ClasspathScanner {

  def findMatchingClasses(basePackage: String, resourceType: Class[_], parentClassLoader: ClassLoader): JSet[Class[_]] = {
    val provider = new ClassPathScanningCandidateComponentProvider(false)
    provider.addIncludeFilter(new AssignableTypeFilter(resourceType))
    provider.findCandidateComponents(basePackage).map(bd => parentClassLoader.loadClass(bd.getBeanClassName))
  }

  def isStatic: Boolean = true
}

class DynamicClasspathScanner(var classPaths: Array[String], var typeServer: String) extends ClasspathScanner {
  val systemClasspathScanner = new SystemClasspathScanner()

  def findMatchingClasses(basePackage: String, resourceType: Class[_], parentClassLoader: ClassLoader): JSet[Class[_]] = {
    val cl = new LocalUrlClassLoader(classPaths.map(new URL(_)), parentClassLoader)

    val provider = new ClassPathScanningCandidateComponentProvider(false)
    provider.setResourceLoader(new DefaultResourceLoader(cl))
    provider.addIncludeFilter(new AssignableTypeFilter(resourceType))
    provider.findCandidateComponents("").map(bd => cl.loadClass(bd.getBeanClassName))
  }

  def isStatic: Boolean = {
    if (StringUtils.hasText(typeServer) && typeServer.equals(SERVER_PRODUCTION)) true
    else false
  }
}

/**
 * It's based to loaded core classes from the system classpath to avoid class mismatches.
 * We only load user classes from the hot deploy path
 * @param classPaths
 */
class MixedClasspathScanner(var classPaths: Array[String], var typeServer: String) extends ClasspathScanner {
  val systemClasspathScanner = new SystemClasspathScanner()
  val dynamicClasspathScanner = new DynamicClasspathScanner(classPaths, typeServer)

  def findMatchingClasses(basePackage: String, resourceType: Class[_], parentClassLoader: ClassLoader): JSet[Class[_]] = {
    val result = new util.HashSet[Class[_]]()
    val canonicalNames = new util.HashSet[String]()

    // we want to give preference to the dynamic classloader
    // also compare by name as A.class does not equal B.class if loaded by different classloader
    val prober: (Class[_]) => AnyVal = {
      clazz =>
        val cName: String = clazz.getCanonicalName
        if (!canonicalNames.contains(cName)) {
          canonicalNames.add(cName)
          result.add(clazz)
        }
    }

    dynamicClasspathScanner.findMatchingClasses(basePackage, resourceType).foreach (prober)

    systemClasspathScanner.findMatchingClasses(basePackage, resourceType).foreach(prober)

    result
  }

  def isStatic: Boolean = {
    if (StringUtils.hasText(typeServer) && typeServer.equals(SERVER_PRODUCTION)) true
    else false
  }
}

@Service
class ClasspathScannerFactory extends FactoryBean[ClasspathScanner] {
  @Value(value = "#{'${hotdeploy.paths:}'}") private var hotdeployPaths: String = null
  @Value(value = "#{'${type.server:}'}") private var typeServer: String = null

  def getObject: ClasspathScanner = {
    if (StringUtils.hasText(hotdeployPaths)) {
      new MixedClasspathScanner(hotdeployPaths.split(","),typeServer)
    } else {
      new SystemClasspathScanner()
    }
  }

  def getObjectType = classOf[ClasspathScanner]

  def isSingleton = true
}