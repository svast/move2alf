package eu.xenit.move2alf.core.sourcesink;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.core.AbstractFactory;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;

@Service("sourceSinkFactory")
public class SourceSinkFactory extends AbstractFactory<SourceSink> {

	private static final int DEFAULT_THREADS = 5;

	private static final Logger logger = LoggerFactory
			.getLogger(SourceSinkFactory.class);

	private final Map<Integer, ExecutorService> threadPools = new HashMap<Integer, ExecutorService>();

	@Override
	protected AssignableTypeFilter getTypeFilter() {
		return new AssignableTypeFilter(SourceSink.class);
	}

	@Override
	protected void initializeObject(final SourceSink object) {
		// no initialization required for SourceSink objects
	}

	public ExecutorService getThreadPool(final ConfiguredSourceSink sourceSink) {
		synchronized (threadPools) {
			final ExecutorService threadPool = threadPools.get(sourceSink
					.getId());
			if (threadPool != null) {
				return threadPool;
			} else {
				// create new threadpool
				final String threadsValue = sourceSink.getParameter("threads");
				int threads = DEFAULT_THREADS;
				if (threadsValue != null) {
					threads = Integer.parseInt(threadsValue);
				}
				final ExecutorService newThreadPool = Executors
						.newFixedThreadPool(threads);
				threadPools.put(sourceSink.getId(), newThreadPool);
				return newThreadPool;
			}
		}
	}
}
