package eu.xenit.move2alf.pipeline;

import eu.xenit.move2alf.pipeline.StringMessage;
import eu.xenit.move2alf.pipeline.actions.SimpleAction;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 3/5/13
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class JavaAction extends SimpleAction<StringMessage, StringMessage> {



    @Override
    public void executeImpl(StringMessage message) {
        System.out.println("This is a java action");
        sendMessage(new StringMessage("test123"));

    }
}
