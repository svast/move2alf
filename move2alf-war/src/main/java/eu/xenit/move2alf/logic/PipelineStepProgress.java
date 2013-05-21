package eu.xenit.move2alf.logic;

public class PipelineStepProgress {
	private volatile long processed;
	private volatile long total;

	public PipelineStepProgress(final int processed, final int total) {
		this.processed = processed;
		this.total = total;
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
