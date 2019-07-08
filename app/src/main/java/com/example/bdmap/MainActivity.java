package com.example.bdmap;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.example.bdmap.base.AppManager;
import com.example.bdmap.contract.MainContract;
import com.example.bdmap.presenter.Presenter;
import com.example.bdmap.utils.PoiOverlay;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements View.OnClickListener
        , CompoundButton.OnCheckedChangeListener, MainContract.MainView, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "MainActivity";
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Toolbar mToolbar;
    private SearchView search_menu;
    private Switch switch1, rl, jt;
    private FloatingActionButton fw, gs, dh, mr;
    private FloatingActionsMenu fab_menu_button_down;
    private RadioGroup radioGroup;
    BitmapDescriptor icon,icon1;
    private DrawerLayout drawerLayout;
    private Presenter presenter;
    MapStatusUpdate update;
    PoiSearch mPoiSearch;
    SuggestionSearch suggestionSearch;
    private static boolean isExit = false;
    private float mCurrentX;
    private MyOrientationListener myOrientationListener;
    public LocationClient mLocationClient;
    public BDAbstractLocationListener myListener = new MyLocationListener();

    private double latitude,longitude;//经纬度
    private String city;//当前城市
    private boolean isFirstLoc = true;//是否是第一次定位
    private LatLng latLng;//定位数据

    private ArrayAdapter<String> sugAdapter = null;
    /**
     * 个性化地图皮肤
     *
     * @param context
     * @param fileName
     */
    private void setMapCustomFile(Context context, String fileName) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        String moduleName = null;
        try {
            inputStream = context.getAssets().open("customConfigDir/" + fileName);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            moduleName = context.getFilesDir().getAbsolutePath();
            File file = new File(moduleName + "/" + fileName);
            if (file.exists()) file.delete();
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mMapView.setCustomMapStylePath(moduleName + "/" + fileName);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setHttpsEnable(true);
        setMapCustomFile(this, "custom_map_config.json");

        setContentView(R.layout.activity_main);

        initView();
        drawer();
        initMap();
        mapClick();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        drawerLayout = findViewById(R.id.drawerLayout);
        mToolbar = findViewById(R.id.toolbar);
        search_menu = findViewById(R.id.search_menu);
        mMapView = findViewById(R.id.mapView);
        fab_menu_button_down = findViewById(R.id.fab_menu_button_down);

        switch1 = findViewById(R.id.switch1);
        rl = findViewById(R.id.rl);
        jt = findViewById(R.id.jt);

        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);

        fw = findViewById(R.id.fw);
        gs = findViewById(R.id.gs);
        dh = findViewById(R.id.dh);
        mr = findViewById(R.id.mr);

        fw.setOnClickListener(this);
        gs.setOnClickListener(this);
        dh.setOnClickListener(this);
        mr.setOnClickListener(this);
        jt.setOnCheckedChangeListener(this);
        rl.setOnCheckedChangeListener(this);
        switch1.setOnCheckedChangeListener(this);
        presenter = new Presenter(getApplicationContext(), this);


    }

    private void drawer() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("地图");

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View content = drawerLayout.getChildAt(0);
                View menu = drawerView;
                float scale = 1 - slideOffset;
                float lefScale = (float) (0.9f + 0.1 * scale);
                float rightScale = (float) (0.9f + 0.1 * scale);
                menu.setScaleX(lefScale);
                menu.setScaleY(lefScale);
                content.setScaleX(rightScale);
                content.setScaleY(rightScale);
                content.setTranslationX(menu.getMeasuredWidth() * slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    /**
     * 初始化地图
     */
    private void initMap() {
        mBaiduMap = mMapView.getMap();//获取地图控件引用
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//普通地图

        MyLocationConfiguration configuration4 = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
        mBaiduMap.setMyLocationConfiguration(configuration4);
        mBaiduMap.setMyLocationEnabled(true);
        initLocation();
        mLocationClient.start();//开启定位
    }

    /**
     * 配置定位参数
     */
    private void initLocation() {
        if (mLocationClient == null){
            mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        }
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        option.setLocationNotify(false);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.SetIgnoreCacheException(true);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setOpenAutoNotifyMode();
        option.setOpenAutoNotifyMode(1000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        mLocationClient.setLocOption(option);
    }

    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();
            city = bdLocation.getCity();
            latLng = new LatLng(latitude,longitude);
            MyLocationData locationData = new MyLocationData.Builder()
                    .accuracy(100)
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();
            mBaiduMap.setMyLocationData(locationData);

            if (isFirstLoc){
                isFirstLoc = false;
                fistLoc();
                if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                    presenter.tos("网络定位：" + bdLocation.getAddrStr());// 网络定位结果
                } else if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                    presenter.tos("GPS定位：" + bdLocation.getAddrStr());// Gps定位结果
                } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {
                    presenter.tos("离线定位：" + bdLocation.getAddrStr());// 离线定位结果
                } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {
                    presenter.tos("服务器错误，请检查");
                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
                    presenter.tos("网络错误，请检查");
                } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
                    presenter.tos("手机模式错误，请检查是否飞行");
                }
            }
        }
    }

    /**
     * 第一次定位
     */
    private void fistLoc(){
        update = MapStatusUpdateFactory.zoomTo(19f);
        mBaiduMap.setMapStatus(update);
        update = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(update);
        myOrientationListener = new MyOrientationListener(getApplicationContext());
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });
    }
    /**
     * 地图点击事件
     */
    private void mapClick() {
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                mBaiduMap.clear();
                icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
                OverlayOptions ooA = new MarkerOptions().position(point).icon(icon)
                        .zIndex(6).draggable(true).alpha(0.7f).flat(true);
                mBaiduMap.addOverlay(ooA);
                // 添加圆
                OverlayOptions ooCircle = new CircleOptions().fillColor(0XFF7DC5EB)
                        .center(point).stroke(new Stroke(2, 0xAA00FF00));
                mBaiduMap.addOverlay(ooCircle);
            }
            public boolean onMapPoiClick(MapPoi poi) {
                mBaiduMap.clear();
                icon1 = BitmapDescriptorFactory.fromResource(R.drawable.mappoi);
                TextView button = new TextView(getApplicationContext());
                button.setText(poi.getName());
                button.setTextColor(Color.RED);
                OverlayOptions ooA = new MarkerOptions().position(poi.getPosition()).icon(icon1)
                        .zIndex(6).draggable(true).alpha(0.7f).flat(true);
                mBaiduMap.addOverlay(ooA);
                InfoWindow mInfowindow = new InfoWindow(button,poi.getPosition(),-100);
                mBaiduMap.showInfoWindow(mInfowindow);
                return true;
            }
        });

        //室内
        mBaiduMap.setOnBaseIndoorMapListener(new BaiduMap.OnBaseIndoorMapListener() {
            @Override
            public void onBaseIndoorMapMode(boolean on, MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {
                if (on) {
                    // 进入室内图
                    // 通过获取回调参数 mapBaseIndoorMapInfo 便可获取室内图信
                    //息，包含楼层信息，室内ID等
                } else {
                    // 移除室内图
                }
            }
        });

    }



    /**
     * 定位、跟随、方向指示、默认导航模式
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fw:
                mBaiduMap.clear();
                fistLoc();
                break;
            case R.id.gs:
                MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true,null);
                mBaiduMap.setMyLocationConfiguration(configuration);
                fab_menu_button_down.collapse();
                break;
            case R.id.dh:
                MyLocationConfiguration configuration2 = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, true, null);
                mBaiduMap.setMyLocationConfiguration(configuration2);
                fab_menu_button_down.collapse();
                break;
            case R.id.mr:
                MyLocationConfiguration configuration3 = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
                mBaiduMap.setMyLocationConfiguration(configuration3);
                fab_menu_button_down.collapse();
                break;
        }
    }

    /**
     * 热力、交通、室内图切换
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.rl://开启热力图
                if (isChecked) {
                    mBaiduMap.setBaiduHeatMapEnabled(true);
                } else {
                    mBaiduMap.setBaiduHeatMapEnabled(false);
                }
                break;
            case R.id.switch1://室内图
                if (isChecked) {
                    mBaiduMap.setIndoorEnable(true);
                } else {
                    mBaiduMap.setIndoorEnable(false);
                }
                break;
            case R.id.jt://开启交通图
                if (isChecked) {
                    mBaiduMap.setTrafficEnabled(true);
                    mBaiduMap.setBaiduHeatMapEnabled(false);
                } else {
                    mBaiduMap.setTrafficEnabled(false);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 卫星、普通图切换
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.wx://卫星地图
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                mBaiduMap.setTrafficEnabled(false);
                mBaiduMap.setBaiduHeatMapEnabled(false);
                break;
            case R.id.pt://普通地图
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                mBaiduMap.setTrafficEnabled(false);
                mBaiduMap.setBaiduHeatMapEnabled(false);
                break;
        }
    }

    /**
     * 搜索
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        getMenuInflater().inflate(R.menu.search, menu);
        search_menu = (SearchView)menu.findItem(R.id.search_menu).getActionView();
        poisearch();
        return true;
    }

    private void poisearch(){
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
        search_menu.setSearchableInfo(info);
        search_menu.setIconifiedByDefault(true);
        search_menu.setSubmitButtonEnabled(true);
        search_menu.setBackgroundColor(Color.GRAY);
        search_menu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
                public boolean onQueryTextSubmit(String query) {
                mPoiSearch = PoiSearch.newInstance();
                mPoiSearch.setOnGetPoiSearchResultListener(listener);
                mPoiSearch.searchNearby(new PoiNearbySearchOption()
                        .location(latLng)
                        .radius(1000)
                        .keyword(query)
                        .pageCapacity(3));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    /**
     * POI 搜索监听
     */
    OnGetPoiSearchResultListener listener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                mBaiduMap.clear();
                PoiOverlay poiOverlay = new PoiOverlay(mBaiduMap);
                poiOverlay.setData(poiResult);
                poiOverlay.addToMap();
                poiOverlay.zoomToSpan();
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    /**
     * 地图切换
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.normal:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                mMapView.setMapCustomEnable(false);
                break;
            case R.id.style:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                mMapView.setMapCustomEnable(true);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        mPoiSearch.destroy();
        suggestionSearch.destroy();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        myOrientationListener.stop();
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
    }

    /**
     * 按两次返回键退出程序
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    private void exit() {
        if (!isExit) {
            isExit = true;
            presenter.tos("再按一次退出程序");
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            stopService(new Intent(this,MainActivity.class));
            AppManager.getAppManager().AppExit(this);
        }
    }

}
