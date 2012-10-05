package eu.xenit.move2alf.freemarkerchecktool;

import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreemarkerCheckTool {

	private static final String DEFAULT_TEMPLATE_LOADER_PATH = "file:src/main/webapp/WEB-INF/freemarker";
	private static final String SPRING_FTL_LOADER_PATH = "classpath:org/springframework/web/servlet/view/freemarker";

	private final Configuration freemarkerConfiguration;

	public FreemarkerCheckTool(final String templateLoaderPath)
			throws Exception {
		final FreeMarkerConfigurationFactoryBean fmCfgFactory = new FreeMarkerConfigurationFactoryBean();
		fmCfgFactory.setTemplateLoaderPaths(new String[] { templateLoaderPath,
				SPRING_FTL_LOADER_PATH });
		fmCfgFactory.afterPropertiesSet();
		freemarkerConfiguration = fmCfgFactory.getObject();
	}

	public FreemarkerCheckTool() throws Exception {
		this(DEFAULT_TEMPLATE_LOADER_PATH);
	}

	public String processTemplateIntoString(final String templateName,
			final RootMap rootMap) throws Exception {
		final Template template = freemarkerConfiguration
				.getTemplate(templateName);
		final String result = FreeMarkerTemplateUtils
				.processTemplateIntoString(template, rootMap.toMap());
		return result;
	}

	public RootMap createRootMap() throws Exception {
		return new RootMap();
	}
}