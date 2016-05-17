package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.coolweather.app.util.HttpUtil.HttpCallbackListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();

	// 省列表
	private List<Province> provinceList;
	// 市列表
	private List<City> cityList;
	// 县列表
	private List<County> countyList;
	// 选中的省份
	private Province selectedProvince;
	// 选中的城市
	private City selectedCity;
	// 选中当前级别
	private int currentLevel;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("city_selected", false))
		{
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}

		Log.d("data", "onCreate is coming");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Log.d("data", "1");

		setContentView(R.layout.choose_area);
		Log.d("data", "2");
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		Log.d("data", "3");
		// listview的适配器的构造
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);// /////////////////////////
		Log.d("data", "4");
		listView.setAdapter(adapter);
		Log.d("data", "5");
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		Log.d("data", "6");
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				// TODO Auto-generated method stub
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCities();

				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();

				} else if (currentLevel == LEVEL_COUNTY) {
					String countyCode = countyList.get(index).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this,
							WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();

				}

			}

		}

		);
		queryProvinces();

	}

	// 查询全国所有的省，优先从数据库查询，如果没有，查询到再去服务器查询
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());

			}
			// notifyDataSetChanged方法通过一个外部
			// 的方法控制如果适配器的内容改变时需要强制调用getView来刷新每个Item的内容。
			adapter.notifyDataSetChanged();
			listView.setSelection(0);// 表示将列表移动到指定的0处。
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;

		} else {
			queryFromServer(null, "province");
		}

	}

	// 查询选中省内所有市，优先从数据库查询，乳沟没有就查询到再去服务器查询
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {

			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());

			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;

		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}

	}

	// 查询所有县，优先从数据库查询，如果没有查询到再去服务器查询
	private void queryCounties() {
		countyList = coolWeatherDB.loadCountries(selectedCity.getId());

		Log.d("data", "countyQuery is coming");
		if (countyList.size() > 0) {
			Log.d("data", "countyList is coming");
			dataList.clear();// 清除之前的清单
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;

		} else {
			Log.d("data", "county Server is coming");

			queryFromServer(selectedCity.getCityCode(), "county");

		}

	}

	// 根据代号来从服务器查询县省和市的数据
	private void queryFromServer(final String code, final String type) {
		String address;

		if (!TextUtils.isEmpty(code)) {
			Log.d("data", "http  is coming");
			Log.d("data", "code is " + code);
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";

		} else {
			Log.d("data", "codeNull  is coming");
			address = "http://www.weather.com.cn/data/list3/city.xml";

		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,
							response);

				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							response, selectedProvince.getId());

				} else if ("county".equals(type)) {
					Log.d("data", "countyHandleCOU is coming");
					result = Utility.handleCountiesResponse(coolWeatherDB,
							response, selectedCity.getId());

				}

				if (result) {
					runOnUiThread(new Runnable()// 方法回到主线程处理逻辑
					{

						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if ("province".equals(type)) {
								Log.d("data", "Province is coming");
								queryProvinces();
							} else if ("city".equals(type)) {
								Log.d("data", "city  is coming");
								queryCities();
							} else if ("county".equals(type)) {
								Log.d("data", "county is coming");
								queryCounties();

							}

						}

					});

				}

			}

			public void onError(Exception e) {

				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败",
								Toast.LENGTH_SHORT).show();
					}
				});

			}

		}

		);

	}

	// 显示进度条
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载");
			progressDialog.setCanceledOnTouchOutside(false);// 就是在loading的时候，如果你触摸屏幕其它区域，就会让这个progressDialog消失

		}
		progressDialog.show();

	}

	private void closeProgressDialog() {

		if (progressDialog != null) {
			progressDialog.dismiss();
		}

	}

	// 捕获Back按键，根据当前级别来判断，此时返回是列表、省列表哈市直接退出
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else
			finish();

	}

}
