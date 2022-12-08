package com.example.privacy.bean;

public class messageBean {
    private String number;
    private String name;
    private String body;
    private String pre;
    private int messId; //分类id
    private int queryId;

    public messageBean(String number, String name, String body, int queryId, int messId) {
        this.number = number;
        this.name = name;
        this.body = body;
        this.messId = messId;
        this.queryId = queryId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getMessId() {
        return messId;
    }

    public void setMessId(int messId) {
        this.messId = messId;
    }

    public int getQueryId() {
        return queryId;
    }

    public void setQueryId(int queryId) {
        this.queryId = queryId;
    }

    public String getPre() {
        return pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }
}
