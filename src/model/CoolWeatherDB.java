package model;

import java.util.ArrayList;
import java.util.List;

import db.CoolWeatherOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	
	public static final String DB_NAME="cool_weather";//���ݿ���
	public static final int VERSION=1;
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
	public void saveCountry(Country country)
	{
		if(country!=null)
		{
			ContentValues values=new ContentValues();
			values.put("country_name", country.getCountryName());
			values.put("country_code", country.getCountyCode());
			values.put("city_id", country.getCityId());
		}
		
		
		
	}
	//�����ݶ�ȡ�����ص���Ϣ
	public List<Country>loadCountries(int cityId)
	{
		List<Country>list=new ArrayList<Country>();
		Cursor cursor=db.query("Country", null, null, null, null, null, null);
		if(cursor.moveToFirst())
		{
			do{
				Country country=new Country();
				country.setId(cursor.getInt(cursor.getColumnIndex("id")));
				country.setCountryName(cursor.getString(cursor.getColumnIndex("country_code")));
				country.setCountyCode(cursor.getString(cursor.getColumnIndex("country_code")));
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

		
		
		
	
	
	
	
	
	
	
	
	

}
