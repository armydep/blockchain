package org.am.com.blockchain.model.block;


import java.util.ArrayList;
import java.util.Collection;

public class InsertionOnlyList<E> extends ArrayList<E> {
    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException("Remove operation is not supported");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Remove operation is not supported");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Remove operation is not supported");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Retain operation is not supported");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Clear operation is not supported");
    }
}
