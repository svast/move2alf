package eu.xenit.move2alf.freemarkerchecktool;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import freemarker.template.Configuration;
import freemarker.template.Template;


public class FreemarkerCheckTool {

	private static final String DEFAULT_TEMPLATE_LOADER_PATH = "file:src/main/webapp/WEB-INF/freemarker";
	private static final String SPRING_FTL_LOADER_PATH = "classpath:org/springframework/web/servlet/view/freemarker";
	private static final String WEB_APP_PATH = "src/main/webapp";

	
	private final Configuration freemarkerConfiguration;
	private final ServletContext servletContext;
	private final MockHttpServletRequest request;
	private final MockHttpServletResponse response;
	
	
	public FreemarkerCheckTool (String templateLoaderPath) throws Exception {
		FreeMarkerConfigurationFactoryBean fmCfgFactory = new FreeMarkerConfigurationFactoryBean();
		fmCfgFactory.setTemplateLoaderPaths(new String[]{templateLoaderPath, SPRING_FTL_LOADER_PATH});
		fmCfgFactory.afterPropertiesSet();
		freemarkerConfiguration = (Configuration) fmCfgFactory.getObject();
		
		servletContext = createServletContext();
	    request = new MockHttpServletRequest("GET", "");
	    response = new MockHttpServletResponse();
	}
	
	public FreemarkerCheckTool () throws Exception {
		this(DEFAULT_TEMPLATE_LOADER_PATH);
	}

	public String processTemplateIntoString(String templateName, Map<String, Object> rootMap) throws Exception {
		Template template = freemarkerConfiguration.getTemplate(templateName);
        String result = FreeMarkerTemplateUtils.processTemplateIntoString(template, rootMap);
        return result;
	}

	public Map<String, Object> createRootMap () {
		Map<String, Object> rootMap = new HashMap<String, Object>();
	    RequestContext requestContext = new RequestContext(request, response, servletContext, rootMap);
	    rootMap.put(FreeMarkerView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, requestContext);
		return rootMap;
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