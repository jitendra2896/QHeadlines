package com.example.qheadlines;

public class News {
    public String headLine;
    public String additionalInformation;
    public String url;
    public String imageUrl;
    public String source;

    public News(String h,String a,String u,String iu,String source){
        headLine = h;
        additionalInformation = a;
        url = u;
        imageUrl = iu;
        this.source = source;
    }
}
