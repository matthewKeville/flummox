package com.keville.flummox.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Conversions {

  public static <T> List<T> iterableToList(Iterable<T> iterable) {

    Iterator<T> iterator = iterable.iterator();
    List<T> list = new LinkedList<T>();
    while ( iterator.hasNext() ) {
      list.add(iterator.next());
    }
    return list;

  }

}
