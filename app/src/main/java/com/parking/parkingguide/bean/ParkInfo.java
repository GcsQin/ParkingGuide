package com.parking.parkingguide.bean;

/**
 * Created by 37266 on 2017/4/20.
 */

public class ParkInfo {
    public String area;
    public String recordId;
    public String id;
    public String parkName;
    public String parkType;
    public String parkCompany;
    public String parkNum;
    public String parkLevel;

    public ParkInfo(String area, String recordId, String id, String parkName, String parkType, String parkCompany, String parkNum, String parkLevel) {
        this.area = area;
        this.recordId = recordId;
        this.id = id;
        this.parkName = parkName;
        this.parkType = parkType;
        this.parkCompany = parkCompany;
        this.parkNum = parkNum;
        this.parkLevel = parkLevel;
    }

    @Override
    public String toString() {
        return "ParkInfo{" +
                "area='" + area + '\'' +
                ", recordId='" + recordId + '\'' +
                ", id='" + id + '\'' +
                ", parkName='" + parkName + '\'' +
                ", parkType='" + parkType + '\'' +
                ", parkCompany='" + parkCompany + '\'' +
                ", parkNum='" + parkNum + '\'' +
                ", parkLevel='" + parkLevel + '\'' +
                '}';
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }
    public String getParkType() {
        return parkType;
    }

    public void setParkType(String parkType) {
        this.parkType = parkType;
    }

    public String getParkCompany() {
        return parkCompany;
    }

    public void setParkCompany(String parkCompany) {
        this.parkCompany = parkCompany;
    }

    public String getParkNum() {
        return parkNum;
    }

    public void setParkNum(String parkNum) {
        this.parkNum = parkNum;
    }

    public String getParkLevel() {
        return parkLevel;
    }

    public void setParkLevel(String parkLevel) {
        this.parkLevel = parkLevel;
    }
}
