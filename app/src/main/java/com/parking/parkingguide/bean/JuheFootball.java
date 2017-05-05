package com.parking.parkingguide.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by 37266 on 2017/4/30.
 */

public class JuheFootball implements Serializable{
    public int error_code;
    public String reason;
    public Result result;
    public class Result implements Serializable{
        public String key;
        public Tabs tabs;
        public Views views;
    }
    public class Tabs implements Serializable{
        public String jifenbang;
        public String saicheng1;
        public String saicheng2;
        public String sheshoubang;
    }
    public class Views implements Serializable{
        public ArrayList<Jifengbang> jifenbang;
        public ArrayList<Saicheng1> saicheng1;
        public ArrayList<Saicheng2> saicheng2;
        public ArrayList<Sheshoubang> sheshoubang;
    }
    public class Jifengbang implements Serializable{
        //赛事情况
        public String c1;
        //球队名称
        public String c2;
        //链接
        public String c2L;
        //总比赛场数
        public String c3;
        //胜场
        public String c41;
        //平局
        public String c42;
        //败北
        public String c43;
        //净胜球
        public String c5;
        //总积分
        public String c6;
    }
    public class Saicheng1 implements Serializable{
        //比赛状态
        public String c1;
        //比赛时间
        public String c2;
        public String c3;
        //球队1
        public String c4T1;
        //比赛结果
        public String c4R;
        //球队2
        public String c4T2;
    }
    public class Saicheng2 implements Serializable{
        //比赛状态
        public String c1;
        //比赛时间
        public String c2;
        public String c3;
        //球队1
        public String c4T1;
        //比赛结果
        public String c4R;
        //球队2
        public String c4T2;
    }
    public class Sheshoubang implements Serializable{
        //个人排名
        public String c1;
        //名字
        public String c2;
        //所属球队
        public String c3;
        //进球数
        public String c4;
        //点球数
        public String c5;
    }
}
