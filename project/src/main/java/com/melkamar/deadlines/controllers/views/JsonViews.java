package com.melkamar.deadlines.controllers.views;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 10.04.2016 10:56
 */
public interface JsonViews {
    public static interface Base  {}

    public static interface GroupShowAdminInfo {}
    public static interface GroupBasic extends GroupShowAdminInfo, Base {}
}
