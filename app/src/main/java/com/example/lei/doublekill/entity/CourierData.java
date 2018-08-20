package com.example.lei.doublekill.entity;

public class CourierData {

    //时间
    private String datatime;
    //状态
    private String remark;
    //城市
    private String zone;

    public void setDatatime(String datatime) {
        this.datatime = datatime;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getDatatime() {
        return datatime;
    }

    public String getRemark() {
        return remark;
    }

    public String getZone() {
        return zone;
    }

    @Override
    public String toString() {
        return "CourierData{" +
                "datatime='" + datatime + '\'' +
                ", remark='" + remark + '\'' +
                ", zone='" + zone + '\'' +
                '}';
    }
}
