package com.example.bbcnewsreader;

import java.util.Date;

public class News {
    private long id;
    private final String title;
    private final String description;
    private final String link;
    private final Date pubDate;

    public News(long id, String title, String description, String link, Date pubDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = pubDate;
    }

    public News(String title, String description, String link, Date pubDate) {
        this(0, title, description, link, pubDate);
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setId(long id) {
        this.id = id;
    }
}
