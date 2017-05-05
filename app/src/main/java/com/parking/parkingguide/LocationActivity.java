package com.parking.parkingguide;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.AMap;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.Photo;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationActivity extends AppCompatActivity implements View.OnClickListener,LocationSource,AMapLocationListener,
         AMap.OnMapLongClickListener,GeocodeSearch.OnGeocodeSearchListener ,PoiSearch.OnPoiSearchListener,AMap.OnMarkerClickListener {
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
    private ArrayList<Marker> markers;
    private PoiResult mPoiResult;
    private List<Photo> poiPhoto;
    //获取地图对象
    private MapView mapView;
    private AMap map;
    private GeocodeSearch geocodeSearch;
    //
    private PopupWindow popupWindow;
    private View popupWindowView;
    private TextView tvPopTitle, tvPopTips;
    private Button btnPopNavi;
    private String searchInfo;
    private ImageView imgPopNavi;
    //跳转导航页面传递当前经纬度
    private double naviStartLatitude;
    private double naviStartLongtitude;
    private double naviEndLatitude;
    private double naviEndLongtittude;
    //-------------------------------------------------------
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    private static final int PERMISSON_REQUESTCODE = 0;
    private boolean isNeedCheck = true;
    //-------------------------------------------------

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_location);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if (bundle != null) {
            searchInfo = bundle.getString("parkName");
        }
        mapView = (MapView) findViewById(R.id.map);
//        在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);//此方法必须重写
        initMap();
        //初始化point
        markers = new ArrayList<Marker>();
        if (searchInfo.isEmpty()) {
            searchInfo = "停车场";
        }
    }

    /*AMapLocationListener
    * aMapLocation对象可以让我们获取到纬度getLatitude()和经度getLongitude()以及目标地址getAddress()
    * */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.e("onLocationChanged", "" + System.currentTimeMillis() + "=====aMapLocation0" + aMapLocation.getErrorCode());
        if (locationChangedListener != null && aMapLocation != null) {
            if (locationClient != null && aMapLocation.getErrorCode() == 0) {//0表示定位成功
                //显示系统小蓝点
//                locationChangedListener.onLocationChanged(aMapLocation);
                //如果是第一次定位成功 就缩放到级别12，级别越高，越精准，但是18差不多已经是最大的了
                if (cLocation == 0) {
                    naviStartLatitude = aMapLocation.getLatitude();
                    naviStartLongtitude = aMapLocation.getLongitude();
                    Log.e(aMapLocation.getLatitude() + "", "" + aMapLocation.getLongitude());
                    locationChangedListener.onLocationChanged(aMapLocation);
                    map.moveCamera(CameraUpdateFactory.zoomTo(12));
                    //-----------------------point搜索模块--------------------------
                    ////keyWord表示搜索字符串，//第二个参数表示POI搜索类型，二者选填其一，
                    /// //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
                    query = new PoiSearch.Query(searchInfo, "", aMapLocation.getCityCode());
                    // 设置每页最多返回多少条poiitem
                    query.setPageSize(10);
                    //设置查询页码
                    query.setPageNum(0);
                    //构造 PoiSearch 对象，并设置监听。
                    poiSearch = new PoiSearch(this, query);
                    if (searchInfo.equals("停车场")) {//设置poi搜索范围以当前经纬度为圆心,1000为半径的区域
                        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(naviStartLatitude
                                , naviStartLongtitude), 1000));
                        Log.e("LocationTest", "1111");
                    }
                    poiSearch.setOnPoiSearchListener(this);
                    //调用 PoiSearch 的 searchPOIAsyn() 方法发送请求。
                    poiSearch.searchPOIAsyn();
                    cLocation++;
                } else {
                    //因为一开始的cLocation为0，之后不为0，如果不注释掉，会产生无法缩放的bug
//                    map.moveCamera(CameraUpdateFactory.zoomTo(18));
                    Log.e("LocationActivity", "定位失败" + aMapLocation.getErrorCode());
                }
            }
        }
    }

    private void initMap() {
        Log.e("initMap", "1111111111111");
        if (map == null) {
            //实例化地图对象
            map = mapView.getMap();
            //不需要标记监听了
            map.setOnMarkerClickListener(this);
            //设置定位监听
            map.setLocationSource(this);
            // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            map.setMyLocationEnabled(true);
            // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
//            map.setMyLocationStyle();
            //显示室内地图
//            map.showIndoorMap(true);
            mUiSettings = map.getUiSettings();
            //显示默认的定位按钮
            mUiSettings.setMyLocationButtonEnabled(true);
            //指南针用于向 App 端用户展示地图方向，默认不显示
            mUiSettings.setCompassEnabled(true);
            //缩放按钮是提供给 App 端用户控制地图缩放级别的交换按钮，每次点击改变1个级别，此控件默认打开
            //可以通过以下方法控制其隐藏
            mUiSettings.setZoomControlsEnabled(true);
            map.setOnMapLongClickListener(this);
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
        Log.e("LocationActivity", "activate+========================");
        locationChangedListener = onLocationChangedListener;
        if (locationClient == null) {
            //初始化定位
            locationClient = new AMapLocationClient(this);
            //初始化定位参数
            locationClientOption = new AMapLocationClientOption();
            //设置定位回调监听
            locationClient.setLocationListener(this);
            //设置为高精度定位模式
            locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置发起定位请求时间间隔
            locationClientOption.setInterval(5000);
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
    @Override
    public void onMapLongClick(LatLng latLng) {
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
        //----------------------------------------------------------
        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }
    }
    //------------------------------------

    //------------------------------------
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
        Log.e("onPoiSearched", "1111111111111");
        //v3.2.1及以上版本SDK 返回码i==1000是正常
        if (code == 1000 && poiResult != null && poiResult.getQuery() != null) {
            double markerLatitude;
            double markerLongtitude;
            LatLng markerLatLng;
            Marker poiMarker;
            //定义poi的marker图标。
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.mipmap.red);
            //初始化poi的marker参数对象
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(descriptor);
            mPoiResult = poiResult;
            //poi结果的页数
//            int resultPageCount=mPoiResult.getPageCount();
            ArrayList<PoiItem> pois = poiResult.getPois();
            if (pois != null && pois.size() > 0) {
                //保证marker不重复添加，每次获得的poi后都重新设置markers容器
                if (!markers.isEmpty()) {
                    markers.clear();
                }
                for (int i = 0; i < pois.size(); i++) {
                    PoiItem poiItem = pois.get(i);
                    poiPhoto = poiItem.getPhotos();
                    LatLonPoint latLonPoint = poiItem.getLatLonPoint();
                    markerLatitude = latLonPoint.getLatitude();
                    markerLongtitude = latLonPoint.getLongitude();
                    markerLatLng = new LatLng(markerLatitude, markerLongtitude);
                    markerOptions.position(markerLatLng);
                    poiMarker = map.addMarker(markerOptions);
                    poiMarker.setPosition(markerLatLng);
                    poiMarker.setTitle(poiItem.getTitle());
                    poiMarker.setSnippet(poiItem.getCityName() + poiItem.getAdName() + poiItem.getSnippet() + "\n" + "停车场类型:" + poiItem.getParkingType());
                    //第一次获取到查询结果的时候，移动中心点到该地区
                    if (i == 0) {
                        map.animateCamera(CameraUpdateFactory.changeLatLng(markerLatLng), 500, null);
                    }

//                    RegeocodeQuery regeocodeQuery=new RegeocodeQuery(latLonPoint,200,GeocodeSearch.AMAP);
//                    geocodeSearch.getFromLocationAsyn(regeocodeQuery);// 设置异步逆地理编码请求
                    markers.add(poiMarker);
                }

            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String title = marker.getTitle();
        String msg = marker.getSnippet();
        //获取导航终点经纬度
        naviEndLatitude = marker.getOptions().getPosition().latitude;
        naviEndLongtittude = marker.getOptions().getPosition().longitude;
        Log.e("onMarkerClick", title + msg);
//        if(!title.isEmpty()&&!msg.isEmpty()){
        showPopupWindows(title, msg);
//        }
        return false;
    }

    private void showPopupWindows(String title, String msg) {
        if (popupWindow == null) {
            LayoutInflater layoutInflater = getLayoutInflater();
            popupWindowView = layoutInflater.inflate(R.layout.layout_popupwindow, null);
            tvPopTitle = (TextView) popupWindowView.findViewById(R.id.tv_title);
            tvPopTips = (TextView) popupWindowView.findViewById(R.id.tv_tips);
            btnPopNavi = (Button) popupWindowView.findViewById(R.id.btn_navi);
            imgPopNavi = (ImageView) popupWindowView.findViewById(R.id.img_navi);
            tvPopTitle.setText(title);
            tvPopTips.setText(msg);
            if (poiPhoto.size() == 0) {
                imgPopNavi.setImageResource(R.drawable.nopicture);
            } else {
                Photo photo = poiPhoto.get(0);
                Glide.with(this).load(photo.getUrl()).placeholder(R.drawable.pictureloading).error(
                        R.drawable.nopicture).into(imgPopNavi);
            }
            btnPopNavi.setOnClickListener(this);
            popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        //使其聚焦
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        //设置动画
        popupWindow.setAnimationStyle(R.style.PopupWindowsAnimation);
        //设置允许在外点击小时
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(popupWindowView, Gravity.BOTTOM, 0, 0);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_navi:
                Toast.makeText(getApplicationContext(), "导航导航页面", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LocationActivity.this, NaviActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("startlat", naviStartLatitude);
                bundle.putDouble("startlong", naviStartLongtitude);
                bundle.putDouble("endlat", naviEndLatitude);
                bundle.putDouble("endlong", naviEndLongtittude);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                break;
        }
    }

    //--------------------------
    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList && needRequestPermissonList.size() > 0) {
            //list.toarray将集合转化为数组  
            ActivityCompat.requestPermissions(this, needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
    }

    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(grantResults)) {//没有授权  
                showMissingPermissionDialog();//显示提示信息  
                isNeedCheck = false;
            }
        }
    }

    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("wo xuyao shouquan");
        builder.setMessage("msg");
        // 拒绝, 退出应用
        builder.setNegativeButton("juejue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton("tongyi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            startAppSettings();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
//
    private boolean verifyPermissions(int[] grantResults) {
        for(int result:grantResults){
            if(result!= PackageManager.PERMISSION_GRANTED){
                return false;
                }
            }
        return true;
    }
    private void startAppSettings()  {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:"+getPackageName()));
        startActivity(intent);
    }
    @Override
    public  boolean onKeyDown(int  keyCode,KeyEvent  event) {
    if(keyCode==KeyEvent.KEYCODE_BACK){
        this.finish();
        return  true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

