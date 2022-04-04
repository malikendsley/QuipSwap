package com.malikendsley.firebaseutils;

public class ExpandableListItem {

    private Object object;
    private boolean isExpanded;

    public ExpandableListItem(Object object){
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
