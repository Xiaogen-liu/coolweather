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
	
	public static final String DB_NAME="cool_weather";//数据库名
	public static final int VERSION=2;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;
	
	//构造方法私有化
	private CoolWeatherDB(Context context)
	{
		CoolWeatherOpenHelper dbHelper=new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
		db=dbHelper.getWritableDatabase();
	}
	
	//获取CoolWeatherDb的实例
	public synchronized static CoolWeatherDB getInstance(Context context)
	{
		if(coolWeatherDB==null)
		{
			coolWeatherDB=new CoolWeatherDB(context);
		}
		return coolWeatherDB;
		
	}
	
	//将province实例存储到数据库
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
	
	
	//从数据库读取全国所有的省份信息
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
	//将City实例存到数据库
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
	//将Country读取某城市的街道
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
	//从数据读取所有县的信息
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

		
		//从数据库读取某省下所有的城市信息
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
