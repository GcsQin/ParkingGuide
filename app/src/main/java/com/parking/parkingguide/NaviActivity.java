package com.parking.parkingguide;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;

import java.util.ArrayList;
import java.util.List;

public class NaviActivity extends AppCompatActivity  implements AMapNaviViewListener,AMapNaviListener{
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationClientOption;
    private Toast mToast;
    private AMapNaviView mAMapNaviView;
    private AMapNavi mAMapNavi;
    private NaviLatLng mEndLatlng;
    private NaviLatLng mStartLatlng;
    private int naviType;
    private final List<NaviLatLng> sList = new ArrayList<NaviLatLng>();
    private final List<NaviLatLng> eList = new ArrayList<NaviLatLng>();
    //最多支持设置 4 个途经点坐标
    private List<NaviLatLng> mWayPointList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_navi);
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("bundle");
        double startLat=bundle.getDouble("startlat");
        double startLong=bundle.getDouble("startlong");
        double endLat=bundle.getDouble("endlat");
        double endLong=bundle.getDouble("endlong");
        naviType=bundle.getInt("type");//如果typeNavi的值是0，就是实时导航，否则就是模拟导航。
        Log.e("NaviActivity",startLat+"__"+startLong+"----"+endLat+"__"+endLong);
        mStartLatlng = new NaviLatLng(startLat,startLong);
        mEndLatlng =new NaviLatLng(endLat,endLong);
        initAmapNavi();
        mAMapNaviView = (AMapNaviView) findViewById(R.id.amap_navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        sList.add(mStartLatlng);
        eList.add(mEndLatlng);
    }
    @Override
    public Resources getResources() {
        return getBaseContext().getResources();
    }

    private  void initAmapNavi(){
//        Log.e("initAmapNaviView","不要改需求");
         /*AMapNavi 是导航对外控制类，提供计算规划路线、偏航以及拥堵重新算路等方法
         * 所以我们要通过单例模式获取一个AmaNavi实例
         * */
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        /* AMapNaviListener 导航事件监听，提供导航过程中的事件（如：路径规划成功/失败、拥堵重新算路、到达目的地等）回调接口
        * 当驾车路线规划成功时，若是单一策略，会进 onCalculateRouteSuccess 回调，若是多策略，
        * 会进 nCalculateMultipleRoutesSuccess(int[] routeIds) 回调，在该回调函数中，
        * 可以进行规划路线显示或开始导航
        * */
        mAMapNavi.addAMapNaviListener(this);//添加导航事件回调监听
        mAMapNavi.setEmulatorNaviSpeed(75);
        //---------------------------
    }
    /*
    * AMapNaviViewListener
    * */
    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviCancel() {

    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    @Override
    public void onNaviMapMode(int i) {

    }

    @Override
    public void onNaviTurnClick() {

    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    @Override
    public void onLockMap(boolean b) {

    }

    @Override
    public void onNaviViewLoaded() {

    }
    /*
    * AMapNaviListener
    * */
    /**
     * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
     *
     * @congestion 躲避拥堵
     * @avoidhightspeed 不走高速
     * @cost 避免收费
     * @hightspeed 高速优先
     * @multipleroute 多路径
     *
     *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
     *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
     */
    //导航初始化失败时的回调函数。
    @Override
    public void onInitNaviFailure() {
        Log.e("onInitNaviFaulure","不要改需求");
        Toast.makeText(this, "初始化导航失败", Toast.LENGTH_SHORT).show();
    }
    //导航初始化成功时的回调函数。
    @Override
    public void onInitNaviSuccess() {
        Log.e("onInitNaviSuccess","不要改需求");
        int strategy = 0;
        try {
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //路线规划方法：四个参数from：起点坐标；to：终点坐标；wayPoints：途经点坐标；strategy：路径的计算策略；我在这里省略了途径点。
        mAMapNavi.calculateDriveRoute(sList,eList,mWayPointList,strategy);
    }
    //启动导航后的回调函数
    @Override
    public void onStartNavi(int i) {

    }
    //当前方路况光柱信息有更新时回调函数
    @Override
    public void onTrafficStatusUpdate() {

    }
    //当GPS位置有更新时的回调函数
    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }
    //导航播报信息回调函数
    @Override
    public void onGetNavigationText(int type, String s) {

    }
    //模拟导航停止后回调函数
    @Override
    public void onEndEmulatorNavi() {

    }
    //到达目的地后回调函数
    @Override
    public void onArriveDestination() {

    }
    //当AmapNavi计算路线成功后会调用此方法
    @Override
    public void onCalculateRouteSuccess() {
        if(naviType==0){
            Log.e("onCalculateRouteSuccess","----GPS--------");
            mAMapNavi.startNavi(NaviType.GPS);
        }else {
            mAMapNavi.startNavi(NaviType.EMULATOR);
            Log.e("onCalculateRouteSuccess","----EMULATOR---------");
        }
    }
    //当AmapNavi计算路线失败后会调用此方法
    @Override
    public void onCalculateRouteFailure(int errorInfo) {
        Log.e("onCalculateRouteFailure","errorInfo："+errorInfo);
        switch (errorInfo){
            case -1:
                showToast("路径计算失败，在导航过程中调用calculateDriveRoute方法导致的失败，导航过程中只能用reCalculate方法进行路径计算。");
                break;
            case 1:
                showToast("路径计算成功");
                Log.e("NaviActivity","路径计算成功");
                break;
            case 2:
                showToast("网络超时或网络失败,请检查网络是否通畅，如网络没问题,查看Logcat输出是否出现鉴权错误信息，如有，说明SHA1与KEY不对应导致");
                break;
            case 3:
                showToast("路径规划起点经纬度不合法,请选择国内坐标点，确保经纬度格式正常。");
                break;
            case 4:
                showToast("协议解析错误,请稍后再试");
                break;
            case 5:
                showToast("路径规划终点经纬度不合法,请选择国内坐标点，确保经纬度格式正常");
                break;
            case 6:
                showToast("路径规划起点经纬度不合法,请选择国内坐标点，确保经纬度格式正常");
                break;
            case 7:
                showToast("算路服务端编码失败");
                break;
            case 10:
                showToast("起点附近没有找到可行道路,请对起点进行调整");
                break;
            case 11:
                showToast("终点附近没有找到可行道路,请对终点进行调整");
                break;
            case 12:
                showToast("途经点附近没有找到可行道路,请对途经点进行调整");
                break;
            case 13:
                showToast("key鉴权失败，请仔细检查key绑定的sha1值与apk签名sha1值是否对应，或通过;高频问题查找相关解决办法");
                break;
            case 14:
                showToast("请求的服务不存在,请稍后再试");
                break;
            case 15:
                showToast("请求服务响应错误,请检查网络状况，稍后再试");
                break;
            case 16:
                showToast("无权限访问此服务,请稍后再试");
                break;
            case 17:
                showToast("请求超出配额");
                break;
            case 18:
                showToast("请求参数非法,请检查传入参数是否符合要求。");
                break;
            case 19:
                showToast("未知错误");
                break;
        }
    }

    @Override
    public void onReCalculateRouteForYaw() {

    }
    //当交通拥堵的时候重新计算路线
    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }
    //多路线规划,在应用中我们使用单路线（优先选择躲避拥堵），所以这个方法不会回调
    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo){

    }

    @Override
    public void onPlayRing(int i) {

    }
//--------------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        mAMapNaviView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
        mAMapNavi.stopNavi();
        mAMapNavi.destroy();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();
    }
    private void showToast(String showText){
        if(mToast==null){
            mToast=Toast.makeText(getApplicationContext(),showText,Toast.LENGTH_LONG);
        }else {
            mToast.setText(showText);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();
    }

    @Override
    public void onBackPressed() {
        if(mToast!=null){
            mToast.cancel();
        }
        super.onBackPressed();
    }

}
