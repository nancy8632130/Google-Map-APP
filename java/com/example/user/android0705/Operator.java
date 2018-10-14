package com.example.user.android0705;
//紀錄客運營運商資訊

public class Operator {
    private String OperatorID;
    private String OperatorName;

    public void Store(String OpID ,String OpName)
    {
        this.OperatorID=OpID;
        this.OperatorName=OpName;
    }

}
