package eu.xenit.move2alf.logic;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface Scheduler {

	public void reloadSchedules();

}