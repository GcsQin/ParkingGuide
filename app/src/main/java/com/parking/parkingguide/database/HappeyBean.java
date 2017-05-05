package com.parking.parkingguide.database;

import java.util.AbstractCollection;
import java.util.ArrayList;

/**
 * Created by 37266 on 2017/4/29.
 */

public class HappeyBean {
    public String reason;
    public ArrayList<Result> result;
    public int error_code;
    public class Result{
        public String content;
    }
}
