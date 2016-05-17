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

	// ʡ�б�
	private List<Province> provinceList;
	// ���б�
	private List<City> cityList;
	// ���б�
	private List<County> countyList;
	// ѡ�е�ʡ��
	private Province selectedProvince;
	// ѡ�еĳ���
	private City selectedCity;
	// ѡ�е�ǰ����
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
		// listview���������Ĺ���
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

	// ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û�У���ѯ����ȥ��������ѯ
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());

			}
			// notifyDataSetChanged����ͨ��һ���ⲿ
			// �ķ���������������������ݸı�ʱ��Ҫǿ�Ƶ���getView��ˢ��ÿ��Item�����ݡ�
			adapter.notifyDataSetChanged();
			listView.setSelection(0);// ��ʾ���б��ƶ���ָ����0����
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;

		} else {
			queryFromServer(null, "province");
		}

	}

	// ��ѯѡ��ʡ�������У����ȴ����ݿ��ѯ���鹵û�оͲ�ѯ����ȥ��������ѯ
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

	// ��ѯ�����أ����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ��������ѯ
	private void queryCounties() {
		countyList = coolWeatherDB.loadCountries(selectedCity.getId());

		Log.d("data", "countyQuery is coming");
		if (countyList.size() > 0) {
			Log.d("data", "countyList is coming");
			dataList.clear();// ���֮ǰ���嵥
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

	// ���ݴ������ӷ�������ѯ��ʡ���е�����
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
					runOnUiThread(new Runnable()// �����ص����̴߳����߼�
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
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��",
								Toast.LENGTH_SHORT).show();
					}
				});

			}

		}

		);

	}

	// ��ʾ������
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���");
			progressDialog.setCanceledOnTouchOutside(false);// ������loading��ʱ������㴥����Ļ�������򣬾ͻ������progressDialog��ʧ

		}
		progressDialog.show();

	}

	private void closeProgressDialog() {

		if (progressDialog != null) {
			progressDialog.dismiss();
		}

	}

	// ����Back���������ݵ�ǰ�������жϣ���ʱ�������б�ʡ�б����ֱ���˳�
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else
			finish();

	}

}
