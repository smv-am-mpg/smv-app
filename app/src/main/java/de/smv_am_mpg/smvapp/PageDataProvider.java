package de.smv_am_mpg.smvapp;

public class PageDataProvider {
    private static final String apiUrl = "http://www.smv-am-mpg.de/api/";
    private static final String pageDataUrl = "contentproviderhiddenapi/";

    public static String getContentUrlFromId(int id) {
        return apiUrl + pageDataUrl + "?id=" + id;
    }

    public static String getContentUrlFromPath(String path) {
        return apiUrl + pageDataUrl + "?path=" + path;
    }
}

