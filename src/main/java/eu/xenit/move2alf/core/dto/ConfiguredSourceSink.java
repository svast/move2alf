package eu.xenit.move2alf.core.dto;

import java.util.Set;

import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.SourceSink;

public class ConfiguredSourceSink extends IdObject {
	private String sourceSinkClassName;
		
	private Set<ConfiguredSourceSinkParameter> configuredSourceSinkParameterSet;
	
	private Set<ConfiguredAction> configuredActionSet;
	private Set<ConfiguredReport> configuredReportSet;
	
	public ConfiguredSourceSink() {
		
	}


}
