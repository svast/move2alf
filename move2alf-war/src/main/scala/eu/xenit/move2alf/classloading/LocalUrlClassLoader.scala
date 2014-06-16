package eu.xenit.move2alf.classloading

import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLClassLoader

class LocalUrlClassLoader(classpath: Array[URL], parent: ClassLoader) extends URLClassLoader(classpath, parent) {
    override def loadClass(name: String): Class[_] = {
        var clazz = findLoadedClass(name)
        if (clazz == null) {
            try {
                clazz = findClass(name)
            }
            catch {
                case e: ClassNotFoundException => {
                    clazz = super.loadClass(name, true)
                }
            }
        }
        resolveClass(clazz)
        clazz
    }

    override def getResource(name: String): URL = {
        var url: URL = null
        if (url == null) {
            url = findResource(name)
            if (url == null) {
                url = super.getResource(name)
            }
        }
        url
    }

    override def getResources(name: String) = findResources(name)

    override def getResourceAsStream(name: String): InputStream = {
        val url = getResource(name)
        try {
            return if (url != null) url.openStream else null
        }
        catch {
            case e: IOException =>
        }
        null
    }
}