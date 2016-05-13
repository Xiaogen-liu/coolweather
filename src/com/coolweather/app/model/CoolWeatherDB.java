package com.coolweather.app.model;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.db.CoolWeatherOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CoolWeatherDB {
	
	public static final String DB_NAME="cool_weather";//���ݿ���
	public static final int VERSION=2;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;
	
	//���췽��˽�л�
	private CoolWeatherDB(Context context)
	{
		CoolWeatherOpenHelper dbHelper=new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
		db=dbHelper.getWritableDatabase();
	}
	
	//��ȡCoolWeatherDb��ʵ��
	public synchronized static CoolWeatherDB getInstance(Context context)
	{
		if(coolWeatherDB==null)
		{
			coolWeatherDB=new CoolWeatherDB(context);
		}
		return coolWeatherDB;
		
	}
	
	//��provinceʵ���洢�����ݿ�
	public void saveProvince(Province province)
	{
		if(province !=null)
		{
			ContentValues values=new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
			
			
		}
		
		
	}
	
	
	//�����ݿ��ȡȫ�����е�ʡ����Ϣ
	public List<Province>loadProvinces()
	{
		List<Province>list=new ArrayList<Province>();
		Cursor cursor=db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst())
		{
			do{
				Province province=new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
				
				
			}while(cursor.moveToNext());


			
			
		}
		if(cursor!=null)
		{
			cursor.close();
			
		}
		
		return list;
		
	}
	//��Cityʵ���浽���ݿ�
	public void saveCity(City city)
	{
		if(city!=null)
		{
			ContentValues values=new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id",city.getProvinceId());
			db.insert("City", null, values);
			
			
		}
		
		
		
	}
	//��Country��ȡĳ���еĽֵ�
	public void saveCountry(County county)
	{
		if(county!=null)
		{
			ContentValues values=new ContentValues();
			values.put("County_name", county.getCountyName());
			values.put("County_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
        Log.d("data","County id is "+county.getCityId());///////////////////////////
			db.insert("County", null, values);
			
			
		}
		
		
		
	}
	//�����ݶ�ȡ�����ص���Ϣ
	public List<County>loadCountries(int cityId)
	{
		List<County>list=new ArrayList<County>();
		Cursor cursor=db.query("County", null, null, null, null, null, null);
		if(cursor.moveToFirst())
		{
			do{
				County country=new County();
				country.setId(cursor.getInt(cursor.getColumnIndex("id")));
				country.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				country.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				country.setCityId(cityId);
				list.add(country);
				
				
			}while(cursor.moveToNext());
			

			
			
			
		}
		if(cursor!=null)
		{
			cursor.close();
		}
		return list;
		
		
		
	}

		
		//�����ݿ��ȡĳʡ�����еĳ�����Ϣ
	public List<City>loadCities(int provinceId)
	{
		List<City>list=new ArrayList<City>();
		Cursor cursor=db.query("City", null, "province_id=?", 
				new String[]{String.valueOf(provinceId)}, null, null, null);
		if(cursor.moveToFirst())
		{
			do{City city=new City();
			city.setId(cursor.getInt(cursor.getColumnIndex("id")));
			city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
			city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
			city.setProvinceId(provinceId);
			list.add(city);
			}while(cursor.moveToNext());
			}
		if(cursor!=null)
		{
			cursor.close();
		}
			return list;
			
			
			
			
		}
		
		

		
		
	
		
	
	
	
	
	
	
	
	
	

}