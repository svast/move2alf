import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PipelinePrototype {

	interface Action {
		public void execute(Map<String, String> params);
	}

	abstract class AbstractAction implements Action {
		private Action nextAction;

		public AbstractAction(Action nextAction) {
			this.nextAction = nextAction;
		}

		public void execute(Map<String, String> params) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			executeImpl(params);
			if (this.nextAction != null) {
				this.nextAction.execute(params);
			}
		}

		abstract void executeImpl(Map<String, String> params);
	}

	class Action1 extends AbstractAction {
		public Action1(Action nextAction) {
			super(nextAction);
		}

		public void executeImpl(Map<String, String> params) {
			params.put("action1", Thread.currentThread().toString());
		}
	}

	class Action2 extends AbstractAction {
		public Action2(Action nextAction) {
			super(nextAction);
		}

		public void executeImpl(Map<String, String> params) {
			params.put("action2", Thread.currentThread().toString());
		}
	}

	class ActionReport extends AbstractAction {
		public ActionReport(Action nextAction) {
			super(nextAction);
		}

		public void executeImpl(Map<String, String> params) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("-----------------------");
			for(String key : params.keySet()) {
				System.out.println(key + "\t-\t" + params.get(key));
			}
			System.out.println("-----------------------");
		}
	}

	class ActionRunner extends Thread {
		private Action action;
		private Map<String, String> params;

		public ActionRunner(Action action, Map<String, String> params) {
			this.action = action;
			this.params = params;
		}

		public void run() {
			action.execute(this.params);
		}
	}

	class ActionSource implements Action {
		private Action nextAction;

		public ActionSource(Action nextAction) {
			this.nextAction = nextAction;
		}

		public void execute(Map<String, String> params) {
			for (int i = 0; i < 5; i++) {
				Map<String, String> paramsNew = new HashMap<String, String>();
				paramsNew.putAll(params);
				paramsNew.put("file", "file" + i + ".txt");
				nextAction.execute(paramsNew);
			}
		}
	}

	class ActionThread implements Action {
		private Action nextAction;

		private ExecutorService threadPool;

		public ActionThread(Action nextAction, ExecutorService threadPool) {
			this.threadPool = threadPool;
			this.nextAction = nextAction;
		}

		public void execute(Map<String, String> params) {
			this.threadPool.execute(new ActionRunner(nextAction, params));
		}
	}
	
	private final static int NUM_THREADS = 3;

	public PipelinePrototype() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("path", "A:\\Floppy");
		ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
		new ActionSource(new ActionThread(new Action1(new Action2(new ActionReport(
				null))), threadPool)).execute(params);
	}

	public static void main(String[] args) {
		new PipelinePrototype();
	}
}
