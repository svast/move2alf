package eu.xenit.move2alf.core.simpleaction.data;

import java.util.ArrayList;

public class Batch extends ArrayList<FileInfo> {
	private static final long serialVersionUID = 246107206356440690L;

    public Batch() {
        super();
    }

    public Batch(Batch batch) {
        super(batch);
    }
}
