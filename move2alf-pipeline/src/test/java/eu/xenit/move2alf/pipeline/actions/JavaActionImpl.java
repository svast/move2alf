package eu.xenit.move2alf.pipeline.actions;

import eu.xenit.move2alf.pipeline.AbstractMessage;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 4/9/13
 * Time: 10:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class JavaActionImpl<T extends AbstractMessage, U extends AbstractMessage> extends BasicAction<T, U> {


    @Override
    public void executeImpl(T message) {

    }

    private String param1;
    public void setParam1(String param1){
        this.param1 = param1;
    }
}
