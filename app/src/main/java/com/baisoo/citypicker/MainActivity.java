package com.baisoo.citypicker;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baisoo.citypicker.adapter.CityRecyclerAdapter;
import com.baisoo.citypicker.db.DBManager;
import com.baisoo.citypicker.model.City;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private RecyclerView mRecyCity;
    private DBManager dbManager;
    private List<City> allCities;
    private SideBar mContactSideber;
    private CityRecyclerAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private final int RC_SETTINGS_SCREEN = 125;
    private final int RC_LOCATION_CONTACTS_PERM = 124;
    private AMapLocationClient mLocationClient;

    String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initLocation();
    }


    private void initView() {

        dbManager = new DBManager(this);
        dbManager.copyDBFile();

        mRecyCity = (RecyclerView) findViewById(R.id.recy_city);
        TextView mContactDialog = (TextView) findViewById(R.id.contact_dialog);
        mContactSideber = (SideBar) findViewById(R.id.contact_sidebar);
        mContactSideber.setTextView(mContactDialog);
    }

    @AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
    private void initLocation() {
        //高德定位
        mLocationClient = new AMapLocationClient(this);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setOnceLocation(true);
        mLocationClient.setLocationOption(option);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        String city = aMapLocation.getCity();
                        String district = aMapLocation.getDistrict();
                        Log.e("onLocationChanged", "city: " + city);
                        Log.e("onLocationChanged", "district: " + district);
                        String location = extractLocation(city, district);
                        adapter.updateLocateState(CityRecyclerAdapter.SUCCESS, location);
                    } else {
                        //定位失败
                        adapter.updateLocateState(CityRecyclerAdapter.FAILED, null);
                    }
                }
            }
        });

        //权限判断
        if (EasyPermissions.hasPermissions(this, perms)) {

            mLocationClient.startLocation();
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, "定位需要相关权限",
                    RC_LOCATION_CONTACTS_PERM, perms);
        }
    }

    @AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
    private void initData() {
        allCities = dbManager.getAllCities();
        adapter = new CityRecyclerAdapter(MainActivity.this, allCities);
        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        mRecyCity.setLayoutManager(linearLayoutManager);
        mRecyCity.setAdapter(adapter);

        adapter.setOnCityClickListener(new CityRecyclerAdapter.OnCityClickListener() {
            @Override
            public void onCityClick(String name) {
                Log.e("MainActivity", "onCityClick:" + name);
                Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLocateClick() {
                //重新定位
                Log.e("MainActivity", "onLocateClick");

                adapter.updateLocateState(CityRecyclerAdapter.LOCATING, null);

                if (EasyPermissions.hasPermissions(MainActivity.this, perms)) {

                    mLocationClient.startLocation();
                } else {
                    // Ask for both permissions
                    EasyPermissions.requestPermissions(this, "定位需要相关权限",
                            RC_LOCATION_CONTACTS_PERM, perms);
                }
            }
        });


        mContactSideber.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {

                int position = adapter.getPositionForSection(s);
                if (position != -1) {
//                    mRecyCity.scrollToPosition(position);
                    linearLayoutManager.scrollToPositionWithOffset(position, 0);
                }

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }


    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, getString(R.string.rationale_ask_again))
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel), null /* click listener */)
                    .setRequestCode(RC_SETTINGS_SCREEN)
                    .build()
                    .show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SETTINGS_SCREEN) {
            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(this, R.string.returned_from_app_settings_to_activity, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * 提取出城市或者县
     *
     * @param city
     * @param district
     * @return
     */
    public static String extractLocation(final String city, final String district) {
        return district.contains("县") ? district.substring(0, district.length() - 1) : city.substring(0, city.length() - 1);
    }
}
