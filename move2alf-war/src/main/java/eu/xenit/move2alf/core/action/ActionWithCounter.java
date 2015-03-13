package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.simpleaction.data.FileInfo;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: rox
 * Date: 3/13/15
 * Time: 10:41 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ActionWithCounter {
    public void setCounter(String counterId, int counter);
    public void setCounters(Map<String,Integer> counters);
    public void sendFileInfoWithCounters(FileInfo fileInfo, String... counterIds);
    public void sendFileInfoWithCounters(FileInfo fileInfo, Map<String, Integer> counters);
}
