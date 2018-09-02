package com.example.lei.doublekill.entity;

import cn.bmob.v3.BmobUser;
//Bmob用户管理
//JavaBean（对应为Bmob后台的数据表)
public class MyUser extends BmobUser{
    private int age;
    private boolean sex;
    private String desc;
    private String imageString;

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
