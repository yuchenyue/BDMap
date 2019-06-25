package com.example.bdmap;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.bdmap.contract.MainContract;
import com.example.bdmap.presenter.Presenter;
import com.example.bdmap.utils.MyApplication;
import com.example.bdmap.utils.PoiOverlay;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener
        , CompoundButton.OnCheckedChangeListener, MainContract.MainView, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "MainActivity";
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Toolbar mToolbar;
    private SearchView search_menu;
    private Switch switch1, rl,jt;
    private FloatingActionButton fw, gs, dh, mr;
    private FloatingActionsMenu fab_menu_button_down;
    private RadioGroup radioGroup;
    public LocationClient mLocationClient;
    public BDAbstractLocationListener myListener = new MyLocationListener();
    private LatLng latLng;
    boolean isFirstLoc = true; // 是否首次定位
    BitmapDescriptor mIconLocation, icon;
    float mCurrentX;
    private DrawerLayout drawerLayout;
    private Presenter presenter;
    MapStatusUpdate update = null;
    PoiSearch mPoiSearch;
    private String city;
    String busLineId;

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
    protected void onCreate(Bundle savedInstanceState) {
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
        mIconLocation = BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps);//初始化图标
        MyLocationConfiguration configuration4 = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mIconLocation);
        mBaiduMap.setMyLocationConfiguration(configuration4);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        initLocation();//配置定位SDK参数
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient.start();//开启定位
    }

    /**
     * 地图点击事件
     */
    private void mapClick() {
//        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
//            public void onMapClick(LatLng point) {
//                mBaiduMap.clear();
//                icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
//                // 添加圆
//                OverlayOptions ooCircle = new CircleOptions().fillColor(0xAAFFFF00)
//                        .center(point).stroke(new Stroke(2, 0xAA00FF00))
//                        .radius(50);
//                mBaiduMap.addOverlay(ooCircle);
//
//                Button button = new Button(getApplicationContext());
//                button.setText("ssss");
//                InfoWindow mInfowindow = new InfoWindow(button,point,-100);
//                OverlayOptions ooA = new MarkerOptions().position(point).icon(icon)
//                        .zIndex(6).draggable(true).alpha(0.7f).flat(true);
//                mBaiduMap.showInfoWindow(mInfowindow);
//                mBaiduMap.addOverlay(ooA);
//            }
//            public boolean onMapPoiClick(MapPoi poi) {
//
//                return true;
//            }
//        });

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
     * 配置定位参数
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(3000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.SetIgnoreCacheException(true);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setOpenAutoNotifyMode();
        option.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        mLocationClient.setLocOption(option);
    }

    /**
     * 实现BDLocationListener接口,BDLocationListener为结果监听接口，异步获取定位结果
     */
    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //map view 销毁后不再处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            float radius = location.getRadius();
            latLng = new LatLng(latitude, longitude);
            update = MapStatusUpdateFactory.zoomTo(19f);
            MyLocationData locData = new MyLocationData.Builder()// 构造定位数据
                    .accuracy(radius)
                    .direction(100).latitude(latitude)
                    .longitude(longitude)
                    .build();
            mBaiduMap.setMyLocationData(locData);// 设置定位数据
//            Log.d(TAG, "经度"+ latitude + "纬度" + longitude + location.getAddrStr());
            //判断是否是第一次定位
            if (isFirstLoc) {
                isFirstLoc = false;
                mBaiduMap.animateMapStatus(update);
                update = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(update);
                city = location.getCity();
                MyApplication.setCity(city);
                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    presenter.tos("Gps" + location.getAddrStr());// Gps定位结果
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    presenter.tos("网络" + location.getAddrStr());// 网络定位结果
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                    presenter.tos("离线" + location.getAddrStr());// 离线定位结果
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    presenter.tos("服务器错误，请检查");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    presenter.tos("网络错误，请检查");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    presenter.tos("手机模式错误，请检查是否飞行");
                }
            } else {
                if (location.getLocType() == BDLocation.GPS_ACCURACY_BAD) {
                    presenter.tos("GPS信号弱");
                } else if (location.getLocType() == BDLocation.INDOOR_NETWORK_STATE_LOW) {
                    presenter.tos("网络信号弱");
                }
            }

        }
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fw:
                mBaiduMap.clear();
                mBaiduMap.animateMapStatus(update);
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(mapStatusUpdate);
                presenter.tos(latLng.toString());
                break;

            case R.id.gs:
                MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, mIconLocation);
                mBaiduMap.setMyLocationConfiguration(configuration);
                fab_menu_button_down.collapse();
                break;
            case R.id.dh:
                MyLocationConfiguration configuration2 = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, true, mIconLocation);
                mBaiduMap.setMyLocationConfiguration(configuration2);
                fab_menu_button_down.collapse();
                break;
            case R.id.mr:
                MyLocationConfiguration configuration3 = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mIconLocation);
                mBaiduMap.setMyLocationConfiguration(configuration3);
                fab_menu_button_down.collapse();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.rl://开启热力图
                if (isChecked){
                    mBaiduMap.setBaiduHeatMapEnabled(true);
                }else {
                    mBaiduMap.setBaiduHeatMapEnabled(false);
                }
                break;
            case R.id.switch1:
                if (isChecked) {
                    mBaiduMap.setIndoorEnable(true);
                } else {
                    mBaiduMap.setIndoorEnable(false);
                }
                break;
            case R.id.jt://开启交通图
                if (isChecked){
                    mBaiduMap.setTrafficEnabled(true);
                    mBaiduMap.setBaiduHeatMapEnabled(false);
                }else {
                    mBaiduMap.setTrafficEnabled(false);
                }
                break;
                default:
                    break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        RadioButton radioButton = findViewById(checkedId);
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
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search_menu = (SearchView) menu.findItem(R.id.search_menu).getActionView();
        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
        search_menu.setSearchableInfo(info);
        search_menu.setIconifiedByDefault(true);
        search_menu.setSubmitButtonEnabled(true);
        search_menu.setBackgroundColor(Color.GRAY);
        search_menu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.tos(query);
                mPoiSearch = PoiSearch.newInstance();
                mPoiSearch.setOnGetPoiSearchResultListener(listener);
                mPoiSearch.searchInCity(new PoiCitySearchOption()
                        .city(city)
                        .keyword(query)
                        .pageCapacity(2));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

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
     *
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
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        mLocationClient.restart();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }


}
