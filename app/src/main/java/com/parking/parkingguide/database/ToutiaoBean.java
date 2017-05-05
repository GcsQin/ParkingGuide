package com.parking.parkingguide.database;

import java.util.ArrayList;

/**
 * Created by 37266 on 2017/4/28.
 */

public class ToutiaoBean {
    public String reason;
    public int error_code;
    public Result result;
    public class Result{
        public  int stat;
        public ArrayList<Data> data;
        @Override
        public String toString() {
            return "Result{" +
                    "stat=" + stat +
                    ", data=" + data +
                    '}';
        }
    }
    public class Data{
        public String uniquekey;
        public String title;
        public String date;
        public String author_name;
        public String url;
        public String thumbnail_pic_s;

        @Override
        public String toString() {
            return "Data{" +
                    "uniquekey='" + uniquekey + '\'' +
                    ", title='" + title + '\'' +
                    ", date='" + date + '\'' +
                    ", author_name='" + author_name + '\'' +
                    ", url='" + url + '\'' +
                    ", thumbnail_pic_s='" + thumbnail_pic_s + '\'' +
                    '}';
        }
    }
    @Override
    public String toString() {
        return "ToutiaoBean{" +
                "reason='" + reason + '\'' +
                ", error_code=" + error_code +
                '}';
    }
}
