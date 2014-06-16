package eu.xenit.move2alf.web.controller.destination.model;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 9/11/13
 * Time: 1:38 PM
 */
public class ContentStoreModel {

    private String name;
    private int id;

    public ContentStoreModel(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
