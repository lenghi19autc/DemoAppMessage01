package com.tapbi.demomessage.DTO;

public class ItemMessage {
    private String name;
    private String content;
    private String number;

    public ItemMessage() {
    }

    public ItemMessage(String name, String content, String number) {
        this.name = name;
        this.content = content;
        this.number = number;
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
}
