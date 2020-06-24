package com.tapbi.demomessage.dto;

public class ItemMessage {
    private int id;
    private String name;
    private String content;
    private String number;
    private String creator;

    public ItemMessage() {
    }

    public ItemMessage(int id, String name, String content, String number, String creator) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.number = number;
        this.creator = creator;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
