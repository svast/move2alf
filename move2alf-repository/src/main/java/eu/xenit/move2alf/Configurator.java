package eu.xenit.move2alf;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
import java.util.Vector;

public class Configurator {
  private static Logger logger = LoggerFactory.getLogger(Configurator.class);

  private Hashtable<String, String> propertiesMap = new Hashtable<String, String>();

  private Hashtable<String, Hashtable<String, String>> pollerMap = new Hashtable<String, Hashtable<String, String>>();

  private Hashtable<String, Vector<String>> polledFolderMap = new Hashtable<String, Vector<String>>();

  private Hashtable<String, Vector<String>> automoveFolderMap = new Hashtable<String, Vector<String>>();

  private Hashtable<String, Vector<String>> processedFolderMap = new Hashtable<String, Vector<String>>();

  public Configurator(String configPath) {
    try {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      SAXParser sp = spf.newSAXParser();
      ParserAdapter pa = new ParserAdapter(sp.getParser());

      ConfigHandler configHandler = new ConfigHandler();
      pa.setContentHandler(configHandler);
      pa.parse(configPath);
    } catch (Exception e) {
      logger.error("Error reading polling configuration ", e);
    }
  }

  // default constructor used by corrector routines (for hardcoded properties, cfr. setProperty)
  public Configurator() {
  }
  
  private String join(Stack<String> stack, String separator) {
    StringBuffer b = new StringBuffer();

    for (int i = 0; i < stack.size(); i++) {
      b.append(stack.elementAt(i));
      if (i < stack.size() - 1)
        b.append(separator);
    }
    return b.toString();
  }

  public String getProperty(String addr) {
    return (String) propertiesMap.get(addr);
  }

  public Hashtable<String, String> getPollerConfig(String id) {
    return (Hashtable<String, String>) pollerMap.get(id);
  }

  public String getPollerProperty(String pollerId, String property) {
    Hashtable<String, String> props = pollerMap.get(pollerId);
    return props.get(property);
  }

  public Vector<String> getFolders(String pollerId) {
    return polledFolderMap.get(pollerId);
  }

  public Vector<String> getAmFolders(String pollerId) {
	    return automoveFolderMap.get(pollerId);
	  }

  public Vector<String> getProcessedFolders(String pollerId) {
	    return processedFolderMap.get(pollerId);
	  }

  public Enumeration<String> getPollers() {
    return pollerMap.keys();
  }

  // inner class to parse the config file
  public class ConfigHandler extends DefaultHandler {

    private Stack<String> elementStack;

    private Stack<String> pollerStack;

    private StringBuffer btext;

    private Hashtable<String, String> pollerPropertiesMap;

    private String currentPollerId;

    private int multiplicity = 0;

    private Vector<String> polledFolderVector;

    private Vector<String> automoveFolderVector;

    private Vector<String> processedFolderVector;

    public ConfigHandler() {
      super();
      elementStack = new Stack<String>();
      btext = new StringBuffer();
    }

    public void startElement(String namespace, String localName, String qName,
        Attributes atts) {

      if (localName.equals("config")) {
        // config begins
      } else if (localName.equals("poller")) {
        currentPollerId = atts.getValue("id");
        pollerPropertiesMap = new Hashtable<String, String>();
        pollerStack = new Stack<String>();
      } else if (localName.equals("folders")) {
        polledFolderVector = new Vector<String>();
        automoveFolderVector = new Vector<String>();
        processedFolderVector = new Vector<String>();
      } else if (localName.equals("property")) {
        String name = atts.getValue("name");
        String value = atts.getValue("value");
        if (currentPollerId == null) {
          String addr = join(elementStack, ".") + ".prop-" + name;
          propertiesMap.put(addr, value);
        } else {
          pollerPropertiesMap.put(name, value);
        }
      } else { // compound element or ...
        if (currentPollerId == null) {
          // does the element have an id?
          if (atts.getIndex("id") == -1) {

            if (propertiesMap.containsKey(join(elementStack, ".") + "."
                + localName)) {
              multiplicity++;
              elementStack.push(localName + "-" + multiplicity);
            } else {
              multiplicity = 0;
              elementStack.push(localName);
            }

          } else {
            elementStack.push(localName + "-" + atts.getValue("id"));
          }
        } else {
          if (atts.getIndex("id") == -1) {
            if (pollerPropertiesMap.containsKey(join(pollerStack, ".") + "."
                + localName)) {

              multiplicity++;
              pollerStack.push(localName + "-" + multiplicity);
            } else {
              multiplicity = 0;
              pollerStack.push(localName);
            }
          } else {
            pollerStack.push(localName + "-" + atts.getValue("id"));
          }
        }
      } // else (general element)

    } // startElement

    public void characters(char[] ch, int start, int length)
        throws SAXException

    {
      String content = new String(ch);

      String fieldcontent = content.substring(start, start + length);
      btext.append(fieldcontent.trim());

      return;
    }

    public void endElement(String uri, String localName, String qName)
        throws SAXException {

      if (localName.equals("config")) {
        // done
      } else if (localName.equals("poller")) {
        pollerMap.put(currentPollerId, pollerPropertiesMap);
        currentPollerId = null;
      }

      else if (localName.equals("property")) {
        // nothing
      } else if (localName.equals("folders")) {
        polledFolderMap.put(currentPollerId, polledFolderVector);
        automoveFolderMap.put(currentPollerId, automoveFolderVector);
        processedFolderMap.put(currentPollerId, processedFolderVector);
      }

      else { // current element ended
        if (currentPollerId == null) {
          String addr = join(elementStack, ".");
          if (btext.toString().length() > 0)
            propertiesMap.put(addr, btext.toString());
          elementStack.pop();
        } else {
          if (localName.equals("pollDirectory"))
            polledFolderVector.addElement(btext.toString());
          else if (localName.equals("automoveDirectory"))
              automoveFolderVector.addElement(btext.toString());
          else if (localName.equals("processedDirectory"))
              processedFolderVector.addElement(btext.toString());
          else {
            String addr = join(pollerStack, ".");
            if (btext.toString().length() > 0)
              pollerPropertiesMap.put(addr, btext.toString());
            pollerStack.pop();
          }

        }
        btext = new StringBuffer();
      } // else (general element)

    } // endelement

  } // end handler class

  // for correction routines (have hardcoded properties)
  public void setProperty(String key, String value){
    propertiesMap.put(key, value);
  }
  
  public void setPollerProperty(String key, String value){
    // pollerId 0
    Hashtable<String, String> props = pollerMap.get("0");
    if(props == null){
      props = new Hashtable<String, String>();
      pollerMap.put("0", props);
    }
    props.put(key, value);
  }
}
