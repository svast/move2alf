/**
 *
 * Copyright (c) 2005 OneSparrow
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 *
 * Created on Apr 25, 2005
 *
 * Author: Dirk Ooms
 *
 */

package eu.xenit.move2alf.common;


/**
 * <p>Class that can be used as a superclass for a class that has an id attribute.
 * 
 */

public class IdObject {

  protected int id;

  public IdObject() {
    id = 0;
  }

  public IdObject(int anId) {
    id = anId;
  }

  // this method will typically be overruled by child classes
  public String toLabel(){
    return Integer.toString(id);
  }
  
  // to allow a second label (when needed)
  public String toLongLabel(){
    return Integer.toString(id);
  }

  public int getId() {
    return id;
  }

  public String getIdAsString() {
    return Integer.toString(id);
  }

  public void setId(int id) {
    this.id = id;
  }
  
}
