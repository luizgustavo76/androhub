package com.androhub;

import java.util.List;

public class FeedCache {
    public static List<FeedItem> cachedRepos = null;
    public static String cachedUsername = "";
    public static long lastFetchTime = 0;
    private static final long EXPIRE_TIME = 5 * 60 * 1000;

    public static boolean isValid(String username) {
        if (cachedRepos == null || !cachedUsername.equals(username)) {
            return false;
        }
        return (System.currentTimeMillis() - lastFetchTime) < EXPIRE_TIME;
    }

    public static void save(String username, List<FeedItem> repos) {
        cachedUsername = username;
        cachedRepos = repos;
        lastFetchTime = System.currentTimeMillis();
    }
}