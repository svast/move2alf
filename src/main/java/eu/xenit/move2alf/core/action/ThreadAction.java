package eu.xenit.move2alf.core.action;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import eu.xenit.move2alf.common.Parameters;
import eu.xenit.move2alf.core.Action;
import eu.xenit.move2alf.core.ConfigurableObject;
import eu.xenit.move2alf.core.dto.ConfiguredAction;

public class ThreadAction extends Action {

	public static Map<Integer, Integer> runningThreadsForCycle = new HashMap<Integer, Integer>();

	private static final Logger logger = LoggerFactory
			.getLogger(ThreadAction.class);

	@Override
	public void execute(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		ExecutorService threadPool = (ExecutorService) parameterMap
				.get(Parameters.PARAM_THREADPOOL);
		ConfiguredAction nextAction = configuredAction
				.getAppliedConfiguredActionOnSuccess();
		int cycleId = (Integer) parameterMap.get(Parameters.PARAM_CYCLE);
		synchronized (runningThreadsForCycle) {
			Integer threads = runningThreadsForCycle.get(cycleId);
			if (threads == null) {
				threads = 1;
			} else {
				threads = threads + 1;
			}
			runningThreadsForCycle.put(cycleId, threads);
		}
		threadPool.execute(new ActionRunner(nextAction, parameterMap));
	}

	@Override
	protected void executeImpl(ConfiguredAction configuredAction,
			Map<String, Object> parameterMap) {
		// empty
	}

	public class ActionRunner extends Thread {
		private ConfiguredAction configuredAction;
		private Map<String, Object> parameterMap;

		public ActionRunner(ConfiguredAction configuredAction,
				Map<String, Object> parameterMap) {
			this.configuredAction = configuredAction;
			this.parameterMap = parameterMap;
		}

		public void run() {
			// openSession(getSessionFactory());

			int cycleId = (Integer) parameterMap.get(Parameters.PARAM_CYCLE);

			parameterMap.put(Parameters.PARAM_THREAD, Thread.currentThread()
					.toString());
			getJobService().executeAction(cycleId, configuredAction,
					parameterMap);

			synchronized (runningThreadsForCycle) {
				Integer threads = runningThreadsForCycle.get(cycleId);
				threads = threads - 1;
				runningThreadsForCycle.put(cycleId, threads);
				if (threads == 0) {
					logger.debug("Last thread finished");
					getJobService().completeCycleStage(cycleId, 2);
				}
			}

			((CountDownLatch) parameterMap.get(Parameters.PARAM_COUNTER))
					.countDown();

			// closeSession(getSessionFactory());
		}

		private void openSession(SessionFactory sessionFactory) {
			Session session = null;
			try {
				session = SessionFactoryUtils.getSession(sessionFactory, false);
			}
			// If not already bound the Create and Bind it!
			catch (java.lang.IllegalStateException ex) {
				session = SessionFactoryUtils.getSession(sessionFactory, true);
				TransactionSynchronizationManager.bindResource(sessionFactory,
						new SessionHolder(session));
			}
			session.setFlushMode(FlushMode.AUTO);
		}

		private void closeSession(SessionFactory sessionFactory) {
			try {
				SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager
						.unbindResource(sessionFactory);
				if (!FlushMode.MANUAL.equals(sessionHolder.getSession()
						.getFlushMode())) {
					sessionHolder.getSession().flush();
				}
				SessionFactoryUtils.closeSession(sessionHolder.getSession());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public String getCategory() {
		return ConfigurableObject.CAT_DEFAULT;
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Thread";
	}

}
