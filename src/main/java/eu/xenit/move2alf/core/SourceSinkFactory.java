package eu.xenit.move2alf.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

@Service("sourceSinkFactory")
public class SourceSinkFactory extends AbstractFactory<SourceSink> {

	private static final Logger logger = LoggerFactory
			.getLogger(SourceSinkFactory.class);

	@Override
	protected AssignableTypeFilter getTypeFilter() {
		return new AssignableTypeFilter(SourceSink.class);
	}

	@Override
	protected void initializeObject(SourceSink object) {
		// no initialization required for SourceSink objects
	}
}
