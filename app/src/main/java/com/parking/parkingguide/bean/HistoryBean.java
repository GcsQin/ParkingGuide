package com.parking.parkingguide.bean;

import java.util.ArrayList;

/**
 * Created by 37266 on 2017/5/3.
 */

public class HistoryBean  {
    public String error_code;
    public String reason;
    public ArrayList<Result> result;
    public class Result{
        public String year;
        public String month;
        public String day;
        public String lunar;
        public String title;
        public String des;
    }
}
