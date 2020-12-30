package com.javayh.advanced.datastructure;

import com.javayh.advanced.exception.ExceptionNoSuchElement;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-07
 */
public interface Iterator<E> {

    E next() throws ExceptionNoSuchElement;

    boolean hasNext();
}
