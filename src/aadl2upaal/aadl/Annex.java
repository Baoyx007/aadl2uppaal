package aadl2upaal.aadl;

import aadl2upaal.visitor.NodeVisitor;

public  class Annex {
    private String name;

    public Annex(String name) {
        super();
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
