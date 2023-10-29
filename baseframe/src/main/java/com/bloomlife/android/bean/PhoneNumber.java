package com.bloomlife.android.bean;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/7.
 */
public class PhoneNumber {

    private String name;
    private String number;
    private String alphabetic;

    public PhoneNumber(){

    }

    public PhoneNumber(String number){
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PhoneNumber)
            return ((PhoneNumber) o).getNumber().equals(number);
        else
            return super.equals(o);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlphabetic() {
        return alphabetic;
    }

    public void setAlphabetic(String alphabetic) {
        this.alphabetic = alphabetic;
    }
}
