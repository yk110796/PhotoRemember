/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.event;

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