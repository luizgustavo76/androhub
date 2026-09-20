package com.androhub;

public class FeedItem {
    private String user;
    private String action;
    private String time;
    private String repoName;
    private String description;
    private String lang;
    private String stars;
    private String avatarUrl;

    public FeedItem(String user, String action, String time, String repoName, String description, String lang, String stars, String avatarUrl) {
        this.user = user;
        this.action = action;
        this.time = time;
        this.repoName = repoName;
        this.description = description; 
        this.lang = lang;
        this.stars = stars;
        this.avatarUrl = avatarUrl;
    }

    public String getUser() { return user; }
    public String getAction() { return action; }
    public String getTime() { return time; }
    public String getRepoName() { return repoName; }
    public String getDescription() { return description; }
    public String getLang() { return lang; }
    public String getStars() { return stars; }
    public String getAvatarUrl() { return avatarUrl; }
}