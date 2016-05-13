package com.coolweather.app.util;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.text.TextUtils;
import android.util.Log;

public class Utility {
//解析当前收到的数据
	
	
	//1.解析当前省 的数据
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response)
	{
		if(!TextUtils.isEmpty(response))
		{
			String allProvinces[]=response.split(",");
			if(allProvinces!=null&&allProvinces.length>0)
			{
				for(String p:allProvinces)
				{
					String[]array=p.split("\\|");
					Province province=new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					
					//存储在数库  Province表中
					coolWeatherDB.saveProvince(province);
					
				}
				
				return true;
			}
			
			
			
		}
		
		
		return false;
	}

	
	//2.解析处理服务器返回的市级数据
	
	
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
			String response,int provinceId)
	{
		if(!TextUtils.isEmpty(response))
		{
			String allCities[]=response.split(",");
			if(allCities!=null&&allCities.length>0)
			{
				for(String c:allCities)
				{
					String[]array=c.split("\\|");
					City city=new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					
					//存储在数库  City表中
					coolWeatherDB.saveCity(city);
					
				}
				
				return true;
			}
			
			
			
		}
		
		
		return false;
	}
	
	
	//解析和处理服务器返回的县级数据
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId)
	{
		if(!TextUtils.isEmpty(response))
		{
			Log.d("data","response is  "+response);
			String []allCounties=response.split(",");
			if(allCounties!=null&&allCounties.length>0)
			{
				for(String c:allCounties)
				{
					String[] array=c.split("\\|");
					County county=new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					Log.d("data","arra 0 is "+array[0]);
					county.setCityId(cityId);
					//解析数据放在County表中
					coolWeatherDB.saveCountry(county);
					
					
					
					
				}
				return true;
			}
				
		}
		return false;
	}
	
	
	
	
	
	
}
