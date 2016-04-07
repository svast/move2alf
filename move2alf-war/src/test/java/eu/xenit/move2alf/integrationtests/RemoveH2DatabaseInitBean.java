package eu.xenit.move2alf.integrationtests;

import java.io.File;


/**
 * Created by mhgam on 24/03/2016.
 */
public class RemoveH2DatabaseInitBean {
    public void init() {

        File f = new File("target/h2db/move2alf_data.h2.db");
        if (!f.exists()) return;
        System.out.println("Dropping H2 database!");

        /*while (!f.delete())
            try {
                System.out.println("Can't drop, retrying");
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        if (!f.delete())
            throw new UnsupportedOperationException("Can't delete h2 database file");
    }
}
