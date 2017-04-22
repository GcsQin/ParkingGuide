package com.parking.parkingguide;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
//import com.amap.api.maps.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;

public class LocationActivity extends AppCompatActivity implements LocationSource,AMapLocationListener,
         AMap.OnMapLongClickListener,GeocodeSearch.OnGeocodeSearchListener ,PoiSearch.OnPoiSearchListener{
    //定位服务类
    private AMapLocationClient locationClient = null;
    //定位参数类
    private AMapLocationClientOption locationClientOption = null;
    //控件是指浮在地图图面上的一系列用于操作地图的组件，例如缩放按钮、指南针、定位按钮、比例尺等。
    //UiSettings 类用于操控这些控件，以定制自己想要的视图效果。UiSettings 类对象的实例化需要通过 AMap 类来实现：
    private UiSettings mUiSettings;//定义一个UiSettings对象

    private OnLocationChangedListener locationChangedListener;
    private int cLocation = 0;
    //选中位置的数据
    private double zLongtitude;
    private double zLatitude;
    private String zAddress = "";
    private int cMapLongClick = 0;
    private Marker marker;
    //poi
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
//    private Marker lastCheckMarker;
//    private ArrayList<BitmapDescriptor> lastCheckedBitmapDescriptorList;
    private PoiResult mPoiResult;
//    private Poi
    //------------------------------------
    //获取地图对象
    private MapView mapView;
    private AMap map;
    private GeocodeSearch geocodeSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_location);
        mapView= (MapView) findViewById(R.id.map);
//        在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);//此方法必须重写
        initMap();
        //初始化point
        query = new PoiSearch.Query("停车场", "","");
        query.setPageSize(10);
        poiSearch = new PoiSearch(this, query);
        //--------------------------------------------one-------------------------------------------
        //初始化定位
//        locationClient = new AMapLocationClient(getApplicationContext());
//        locationClient.setLocationListener(this);
//        //定位参数设置
//        locationClientOption = new AMapLocationClientOption();
//        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        locationClient.setLocationOption(locationClientOption);
//        locationClient.startLocation();
        //------------------------------------------------one-----------------------------------
    }
    /*AMapLocationListener
    * aMapLocation对象可以让我们获取到纬度getLatitude()和经度getLongitude()以及目标地址getAddress()
    * */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(locationChangedListener!=null&&aMapLocation!=null){
            if(locationClient!=null&&aMapLocation.getErrorCode()==0){//0表示定位成功
                //显示系统小蓝点
                locationChangedListener.onLocationChanged(aMapLocation);
                //如果是第一次定位成功 就缩放到级别18，级别越高，越精准，但是18差不多已经是最大的了
                if (cLocation == 0) {
                    map.moveCamera(CameraUpdateFactory.zoomTo(20));
                    //-----------------------point搜索模块--------------------------
                    ////keyWord表示搜索字符串，//第二个参数表示POI搜索类型，二者选填其一，
                    /// //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
                    query = new PoiSearch.Query("光明路停车场", "",aMapLocation.getCityCode());
                    // 设置每页最多返回多少条poiitem
                    query.setPageSize(10);
                    //设置查询页码
                    query.setPageNum(0);
                    //构造 PoiSearch 对象，并设置监听。
                    poiSearch = new PoiSearch(this, query);
                    poiSearch.setOnPoiSearchListener(this);
                    //调用 PoiSearch 的 searchPOIAsyn() 方法发送请求。
                    poiSearch.searchPOIAsyn();
                    cLocation++;
                }else {
                    //因为一开始的cLocation为0，之后不为0，如果不注释掉，会产生无法缩放的bug
//                    map.moveCamera(CameraUpdateFactory.zoomTo(18));
                }
            }
        }
        //----------------------------one-----------------------------------------------------------
//        if (aMapLocation != null) {
//            if (aMapLocation.getErrorCode() == 0) {
//                String location="纬度 " + aMapLocation.getLatitude() + "\n" + "经度 " + aMapLocation.getLongitude() + "\n" + aMapLocation.getAddress();
//                textView.setText(location);
//                Log.e("locationActivity",location);
//            } else {
//                String errorMsg="定位失败, ErrCode:"
//                        + aMapLocation.getErrorCode() + ", errInfo:"
//                        + aMapLocation.getErrorInfo();
//                textView.setText(errorMsg);
//                Log.e("locationActivity",errorMsg);
//            }
//        }
        //------------------------------------one---------------------------------------------------
    }
    private void initMap(){
        if(map==null){
            //实例化地图对象
            map=mapView.getMap();
            //不需要标记监听了
//            map.setOnMarkerClickListener(this);
            //设置定位监听
            map.setLocationSource((com.amap.api.maps2d.LocationSource) this);
            // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            map.setMyLocationEnabled(true);
            // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
//            map.setMyLocationStyle();
            //显示室内地图
//            map.showIndoorMap(true);
            mUiSettings=map.getUiSettings();
            //显示默认的定位按钮
            mUiSettings.setMyLocationButtonEnabled(true);
            //指南针用于向 App 端用户展示地图方向，默认不显示
            mUiSettings.setCompassEnabled(true);
            //缩放按钮是提供给 App 端用户控制地图缩放级别的交换按钮，每次点击改变1个级别，此控件默认打开
            //可以通过以下方法控制其隐藏
            mUiSettings.setZoomControlsEnabled(true);
//            map.setOnMapLongClickListener(this);
        }
        //构造 GeocodeSearch 对象，并设置监听。
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
    }
    /*
    * 在activate()中设置定位初始化及启动定位，在deactivate()中写停止定位的相关调用。
    *  此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消
    *  注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
    *  在定位结束后，在合适的生命周期调用onDestroy()方法
    *  在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
    * */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        locationChangedListener=onLocationChangedListener;
        if(locationClient==null){
            //初始化定位
            locationClient = new AMapLocationClient(this);
            //初始化定位参数
            locationClientOption = new AMapLocationClientOption();
            //设置定位回调监听
            locationClient.setLocationListener(this);
            //设置为高精度定位模式
            locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            locationClient.setLocationOption(locationClientOption);

            locationClient.startLocation();
        }
    }
    /*
    * 停止定位
    * */
    @Override
    public void deactivate() {
        locationChangedListener = null;
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
        locationClient = null;
    }
    /*
    * 长按地图的时候调用这个方法
    * */
//    @Override
//    public void onMapLongClick(LatLng latLng) {
//        //获取经纬度
//        zLatitude = latLng.latitude;
//        zLongtitude = latLng.longitude;
//        //用户自定义定位图标
//        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.marker_default);
//        //初始化标注参数对象
//        MarkerOptions markerOptions = new MarkerOptions();
//        //添加标注图片
//        markerOptions.icon(bitmapDescriptor);
//        //添加标注位置
//        markerOptions.position(latLng);
//        //如果是第一次长按点击，map对象就把添加该标注进入地图中，保证只有一个标注对象。
//        if (cMapLongClick == 0) {
//            marker = map.addMarker(markerOptions);
//            cMapLongClick++;
//        } else {
//        }
//        //设置标注位置
//        marker.setPosition(latLng);
//        // 长按时切换中心点
//        map.animateCamera(CameraUpdateFactory.changeLatLng(latLng), 500, null);
//        LatLonPoint latLonPoint = new LatLonPoint(zLatitude, zLongtitude);
//        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
//        geocodeSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
//
//    }
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (i == AMapException.CODE_AMAP_SUCCESS) {
            if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null
                    && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                zAddress = regeocodeResult.getRegeocodeAddress().getFormatAddress()
                        + "附近";
            } else {
                zAddress = "未知位置";
            }
        } else {
            zAddress = "未知位置";
        }
        showLocationInfo();
    }
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
    private void showLocationInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("长按的位置信息");
        String mess = "经度：" + zLongtitude + "\n纬度：" + zLatitude + "\n地址：" + zAddress;
        builder.setMessage(mess);
        builder.setPositiveButton("是", null);
        builder.setNegativeButton("否", null);
        builder.create().show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(locationClient!=null){
//            locationClient.onDestroy();
//            locationClient=null;
//            locationClientOption=null;
//        }
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
        if (null != locationClient) {
            locationClient.onDestroy();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }
    //onPonitSearchListener
    @Override
    public void onPoiSearched(PoiResult poiResult, int code) {
        //v3.2.1及以上版本SDK 返回码i==1000是正常
        if(code==1000&&poiResult!=null&&poiResult.getQuery()!=null){
            mPoiResult=poiResult;
            //poi结果的页数
//            int resultPageCount=mPoiResult.getPageCount();
            ArrayList<PoiItem> pois=poiResult.getPois();
            if(pois!=null&&pois.size()>0){
                PoiOverlay poiOverlay=new PoiOverlay(map,pois);
                poiOverlay.removeFromMap();
                poiOverlay.addToMap();
                poiOverlay.zoomToSpan();
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onMapLongClick(com.amap.api.maps2d.model.LatLng latLng) {
                //获取经纬度
        zLatitude = latLng.latitude;
        zLongtitude = latLng.longitude;
        //用户自定义定位图标
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.marker_default);
        //初始化标注参数对象
        MarkerOptions markerOptions = new MarkerOptions();
        //添加标注图片
        markerOptions.icon(bitmapDescriptor);
        //添加标注位置
        markerOptions.position(latLng);
        //如果是第一次长按点击，map对象就把添加该标注进入地图中，保证只有一个标注对象。
        if (cMapLongClick == 0) {
            marker = map.addMarker(markerOptions);
            cMapLongClick++;
        } else {
        }
        //设置标注位置
        marker.setPosition(latLng);
        // 长按时切换中心点
        map.animateCamera(CameraUpdateFactory.changeLatLng(latLng), 500, null);
        LatLonPoint latLonPoint = new LatLonPoint(zLatitude, zLongtitude);
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求

    }
    //用户点击Marker时，恢复上一个Marker的图标&&设置当前Marker的图标
//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        //恢复上一个Marker的图标
//        lastCheckMarker.setIcons(lastCheckedBitmapDescriptorList);
//        //保存Marker
//        lastCheckMarker=marker;
//        //设置当前Marker选中时的图标
//        ArrayList<BitmapDescriptor> bitmapDescriptorArrayList = new ArrayList<>();
//        bitmapDescriptorArrayList.add(BitmapDescriptorFactory.fromResource(R.mipmap.azure));
//        marker.setIcons(bitmapDescriptorArrayList);
//        return false;
//    }
}
