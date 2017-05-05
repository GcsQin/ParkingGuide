package com.parking.parkingguide.bean;

import java.util.ArrayList;

/**
 * Created by 37266 on 2017/4/30.
 */

public class BusBean {
    public int error_code;
    public String reason;
    public ArrayList<Result> result;
    public class Result{
        public String company;
        public String end_time;
        public String front_name;
        public String key_name;
        public String length;
        public String name;
        public String start_time;
        public ArrayList<Stationdes> stationdes;
        public String terminal_name;
    }
    public class Stationdes{
        public String name;
        public String stationNum;
    }
}
