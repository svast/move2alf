package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.core.simpleaction.SimpleAction;

public class PipelineStepProgress {
	private SimpleAction action;
	private volatile long processed;
	private volatile long total;

	public PipelineStepProgress(final SimpleAction action, final int processed, final int total) {
		this.action = action;
		this.processed = processed;
		this.total = total;
	}

	public SimpleAction getAction() {
		return action;
	}

	public long getProcessed() {
		return processed;
	}

	public void setProcessed(final long processed) {
		this.processed = processed;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(final long total) {
		this.total = total;
	}
}
