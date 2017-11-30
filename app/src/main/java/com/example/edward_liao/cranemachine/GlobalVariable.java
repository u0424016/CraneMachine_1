package com.example.edward_liao.cranemachine;

import android.app.Application;




public class GlobalVariable extends Application {

    private int money_total;

    public void setMoney_total(int money_total) {
        this.money_total = money_total;
    }

    public int getMoney_total() {
        return money_total;
    }



    private String password = "abc1234";

    public void setPassword(String password){this.password=password;}

    public String getPassword(){return password;}



    private String id = "nkfust";

    public void setId(String id){this.id=id;}

    public String getId(){return id;}


    private String CM_Token = "";

    public void setCM_Token(String CM_Token){this.CM_Token=CM_Token;}

    public String getCM_Token(){return CM_Token;}


    private String CM_ID = "";

    public void setCM_ID(String CM_ID){this.CM_ID=CM_ID;}

    public String getCM_ID(){return CM_ID;}

}