package eu.xenit.move2alf.common;

/**
 * List of Global Job State parameters, accessible via the StateContext (getstatevalue)
 */
public class GlobalJobState {
	/**
	 * Returns the JobModel instance for the current job
	 */
	public static final String JOB_MODEL = "GlobalJobState_JobModel";
	/**
	 * Returns cycleId of the current job, integer
	 */
	public static final String CYCLE_ID = "GlobalJobState_CycleId";

}
