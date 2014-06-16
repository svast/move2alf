package eu.xenit.move2alf.common;

import java.util.Comparator;

// sort based on label
public class IdObjectComparator implements Comparator<IdObject> {
  
  private boolean ascending;
  
  public IdObjectComparator(boolean ascending){
    this.ascending = ascending;
  }
  
  public int compare(IdObject o1, IdObject o2) {
    int result = o1.toLabel().compareTo(o2.toLabel());
    return (ascending)?result:-result;
  }

}
