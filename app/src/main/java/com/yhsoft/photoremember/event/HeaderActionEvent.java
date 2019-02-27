package com.yhsoft.photoremember.event;

public class HeaderActionEvent {
    public enum SelectedAction {
        ACTION_OPEN_MENU,
        ACTION_OPEN_MAP,
        ACTION_BACK,
        ACTION_SEARCH,
    }

    public enum MapAction {
        OPEN_SINGLE,
        OPEN_MULTIPLE,
    }

    private SelectedAction action;
    private MapAction mapAction;
    private int photoID;

    public HeaderActionEvent(SelectedAction act) {
        action = act;
    }

    public HeaderActionEvent(SelectedAction act, MapAction mapAction) {
        action = act;
        this.mapAction = mapAction;
    }

    public HeaderActionEvent(SelectedAction act, MapAction mapAction, int id) {
        action = act;
        this.mapAction = mapAction;
        photoID = id;
    }

    public SelectedAction getAction() {
        return action;
    }

    public MapAction getMapAction() {
        return mapAction;
    }

    public int getPhotoID() {
        return photoID;
    }
}