package com.malikendsley.firebaseutils.secureschema;

public class ExpandableListItem {

    private final Object object;
    private boolean isExpanded;

    public ExpandableListItem(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
