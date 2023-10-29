package com.bloomlife.videoapp.model;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/6.
 */
public class Contact {

    private String name;
    private String number;
    private String alphabetic;
    private boolean invite;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isInvite() {
        return invite;
    }

    public void setInvite(boolean invite) {
        this.invite = invite;
    }

    public String getAlphabetic() {
        return alphabetic;
    }

    public void setAlphabetic(String alphabetic) {
        this.alphabetic = alphabetic;
    }
}
