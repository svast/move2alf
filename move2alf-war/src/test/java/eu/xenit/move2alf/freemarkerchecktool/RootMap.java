package eu.xenit.move2alf.freemarkerchecktool;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;


public class RootMap {

	private static final Logger logger = LoggerFactory.getLogger(RootMap.class);

	private static final String WEB_APP_PATH = "src/main/webapp";
	
	private static ServletContext servletContext = null;
	private static MockHttpServletRequest request = null;
	private static MockHttpServletResponse response = null;
	
	private Map<String, Object> rootMap;
	
	private HashMap<Class<?>, Object> dummyValues;
	

	RootMap () throws Exception {
		if ( servletContext == null ) {
			servletContext = createServletContext();
			request = new MockHttpServletRequest("GET", "");
			response = new MockHttpServletResponse();
		}

		rootMap = new HashMap<String, Object>();
	    RequestContext requestContext = new RequestContext(request, response, servletContext, rootMap);
	    rootMap.put(FreeMarkerView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, requestContext);
	    
	    initDummyValues();
	}
	
	Map<String, Object> toMap() {
		return rootMap;
	}
	
	public void addValue (String key, Object value) {
		rootMap.put(key, value);
	}
	
	public void add (String key, Class<?> clazz) throws Exception {
		Object value = getDummyValue(clazz);
		rootMap.put(key, value);
	}
	
	public void addList (String key, Class<?> clazz) throws Exception {
		Object value = getDummyValue(clazz);
		List<Object> list = new ArrayList<Object>();
		list.add(value);
		rootMap.put(key, list);
	}
	
	private Class<?> getClassOfSetterParam (Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		assert ( 1 == parameterTypes.length ) : "not implemented: setters with none or multiple parameters";
		return parameterTypes[0];
	}
	
	private Class<?> getTypeArgumentOfGenericSetterParam (Method method) {
		Class<?> clazz = null;
		Type[] parameterTypes = method.getGenericParameterTypes();
		assert ( 1 == parameterTypes.length ) : "not implemented: setters with none or multiple parameters";
		Type parameterType = parameterTypes[0];
		if ( parameterType instanceof ParameterizedType ) {
			ParameterizedType parameterizedType = (ParameterizedType) parameterType;
			Type[] fieldArgTypes = parameterizedType.getActualTypeArguments();
			clazz = (Class<?>) fieldArgTypes[0];
        }
		return clazz;
	}
	
	private Method[] getSetters (Class<?> clazz) {
		String setterPrefix = "set";
		Vector<Method> setters = new Vector<Method>();
		for (Method method : clazz.getMethods()) {
			if ( method.getName().startsWith(setterPrefix) ) {
				setters.add(method);
			}
		}
		return setters.toArray(new Method[setters.size()]);
	}
	
	private Object getDummyValue (Class<?> clazz) {
		Object value = dummyValues.get(clazz);
		try {
			if ( value == null ) {
				if ( clazz.isEnum() ) {
					value = clazz.getEnumConstants()[0];
					dummyValues.put(clazz, value);
				} else {
					value = clazz.newInstance();
					dummyValues.put(clazz, value);
					for (Method method : getSetters(value.getClass())) {
						Class<?> fieldType = getClassOfSetterParam(method);
						Object fieldvalue = null;
						if ( Set.class.equals(fieldType) ) {
							Set<Object> set = new HashSet<Object>();
							Class<?> typeArgument = getTypeArgumentOfGenericSetterParam(method);
							Object element = getDummyValue(typeArgument);
							set.add(element);
							fieldvalue = set;
						} else if ( Map.class.equals(fieldType) ) {
							//TODO
						} else if ( List.class.equals(fieldType) ) {
							//TODO
						} else {
							fieldvalue = getDummyValue(fieldType);
							if ( fieldvalue == null ) {
								logger.warn("Type '" + fieldType.getName() + "' not implemented.");
							}
						}
						method.invoke(value, fieldvalue);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}
	
	private void initDummyValues () {
		dummyValues = new HashMap<Class<?>, Object>();
		dummyValues.put(String.class, "x");
		
		dummyValues.put(Integer.class, 1);
		dummyValues.put(int.class, 1);
		
		dummyValues.put(Long.class, 1);
		dummyValues.put(long.class, 1);
		
		dummyValues.put(Date.class, new Date());
	}

	/*
	 * Have a look at http://tedyoung.me/2011/02/14/spring-mvc-integration-testing-controllers for more info
	 */
    private ServletContext createServletContext () throws Exception {
    	// Establish the servlet context and config
        final MockServletContext servletContext = new MockServletContext(WEB_APP_PATH, new FileSystemResourceLoader());
        final MockServletConfig servletConfig = new MockServletConfig(servletContext);
        
        // Create a WebApplicationContext and initialize it with the xml and servlet configuration.
        final XmlWebApplicationContext webApplicationContext = new XmlWebApplicationContext();
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);
        webApplicationContext.setServletConfig(servletConfig);
        
        // Create a DispatcherServlet that uses the previously established WebApplicationContext.
        @SuppressWarnings("serial")
		final DispatcherServlet dispatcherServlet = new DispatcherServlet() {
                @Override
                protected WebApplicationContext createWebApplicationContext(ApplicationContext parent) {
                        return webApplicationContext;
                }
        };
        
        // Prepare the context.
        webApplicationContext.refresh();
        webApplicationContext.registerShutdownHook();
        
        // Initialize the servlet.
        dispatcherServlet.setContextConfigLocation("");
        dispatcherServlet.init(servletConfig);
        
        return dispatcherServlet.getServletContext();
    }
}