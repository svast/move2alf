package eu.xenit.move2alf.core.action.messages;

/**
 * Created with IntelliJ IDEA.
 * User: rox
 * Date: 3/13/15
 * Time: 12:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class SetCounterMessage {
    String id;
    Integer counter;

    public SetCounterMessage(String id, Integer counter) {
        this.id = id;
        this.counter = counter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }
}
