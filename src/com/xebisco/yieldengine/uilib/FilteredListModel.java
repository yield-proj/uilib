package com.xebisco.yieldengine.uilib;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class FilteredListModel<T> extends AbstractListModel<T> {
    public interface Filter<T> {
        boolean accept(T element);
    }

    private final ListModel<T> source;
    private Filter<T> filter;
    private final ArrayList<Integer> indices = new ArrayList<>();

    public FilteredListModel(ListModel source) {
        if (source == null)
            throw new IllegalArgumentException("Source is null");
        this.source = source;
        this.source.addListDataListener(new ListDataListener() {
            public void intervalRemoved(ListDataEvent e) {
                doFilter();
            }

            public void intervalAdded(ListDataEvent e) {
                doFilter();
            }

            public void contentsChanged(ListDataEvent e) {
                doFilter();
            }
        });
    }

    public void setFilter(Filter<T> f) {
        filter = f;
        doFilter();
    }

    public void doFilter() {
        indices.clear();

        Filter<T> f = filter;
        if (f != null) {
            int count = source.getSize();
            for (int i = 0; i < count; i++) {
                T element = source.getElementAt(i);
                if (f.accept(element)) {
                    indices.add(i);
                }
            }
            fireContentsChanged(this, 0, getSize() - 1);
        }
    }

    public int getSize() {
        return (filter != null) ? indices.size() : source.getSize();
    }

    public T getElementAt(int index) {
        return (filter != null) ? source.getElementAt(indices.get(index)) : source.getElementAt(index);
    }

    public ListModel<T> getSource() {
        return source;
    }

    public Filter<T> getFilter() {
        return filter;
    }

    public ArrayList<Integer> getIndices() {
        return indices;
    }
}