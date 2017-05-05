package com.parking.parkingguide.bean;

import java.util.ArrayList;

/**
 * Created by 37266 on 2017/5/1.
 */

public class WeChatBean {
    public int error_code;
    public String reason;
    public Result result;
    public class  Result{
        public ArrayList<Data> list;
        public String pno;
        public String ps;
        public String totalPage;
    }
    public class Data{
        public String firstImg;
        public String id;
        public String source;
        public String title;
        public String url;
    }
}
