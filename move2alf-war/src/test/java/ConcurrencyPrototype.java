import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.*;

public class ConcurrencyPrototype {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExecutorService singleThreadExecutor = Executors
				.newSingleThreadExecutor();
		ExecutorService multiThreadedExecutor = Executors.newFixedThreadPool(5);

		System.out.println("******** Execute action with ExecutorService");
		executeAction(singleThreadExecutor);
		executeAction(multiThreadedExecutor);
		System.out.println();

		System.out.println("******** ExecutorCompletionService");
		List<Callable<ActionResult>> actions = new ArrayList<Callable<ActionResult>>();
		for (int i = 0; i < 5; i++) {
			actions.add(new Callable<ActionResult>() {
				@Override
				public ActionResult call() throws Exception {
					return new ActionResult();
				}
			});
		}
		completionService(multiThreadedExecutor, actions);
		System.out.println();

		System.out.println("******** Scheduled actions");
		scheduledTask();
		System.out.println();

		singleThreadExecutor.shutdown();
		multiThreadedExecutor.shutdown();
	}

	private static void completionService(ExecutorService executor,
			List<Callable<ActionResult>> actions) {
		CompletionService<ActionResult> completionService = new ExecutorCompletionService<ActionResult>(
				executor);

		System.out.println("Executing " + actions.size() + " actions");

		for (Callable<ActionResult> action : actions) {
			completionService.submit(action);
		}

		ActionResult result = new ActionResult();
		for (int i = 0; i < actions.size(); i++) {
			try {
				result.addAll(completionService.take().get());
				System.out.println("Got result from action");
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	private static void executeAction(ExecutorService executor) {
		Callable<ActionResult> action = new Callable<ActionResult>() {
			@Override
			public ActionResult call() throws Exception {
				System.out.println("called action in thread "
						+ Thread.currentThread().getName());
				ActionResult result = new ActionResult();
				result.add(new HashMap<String, Object>());
				result.add(new HashMap<String, Object>());
				return result;
			}
		};

		Future<ActionResult> result1 = executor.submit(action);
		Future<ActionResult> result2 = executor.submit(action);

		try {
			ActionResult result = result1.get();
			result.addAll(result2.get());
			System.out.println(result.size());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	private static void scheduledTask() {
		ScheduledExecutorService scheduledExecutor = Executors
				.newScheduledThreadPool(5);

		Callable<String> scheduledTask = new Callable<String>() {
			@Override
			public String call() throws Exception {
				return "scheduled task";
			}
		};

		ScheduledFuture<String> result = scheduledExecutor.schedule(
				scheduledTask, 5, SECONDS);

		try {
			System.out.println(result.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		scheduledExecutor.shutdown();
	}
}
