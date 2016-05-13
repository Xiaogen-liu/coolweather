package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	
	//从网络中读取数据		
				public static void sendHttpRequest(final String address,final HttpCallbackListener listener)
				{
					new Thread(new Runnable()
					{
						public void run()
						{
							HttpURLConnection connection=null;
							try{
								URL url=new URL(address);
								connection=(HttpURLConnection)url.openConnection();
								connection.setRequestMethod("GET");
								connection.setConnectTimeout(8000);
								connection.setReadTimeout(8000);
								InputStream in=connection.getInputStream();
								BufferedReader reader=new BufferedReader(new InputStreamReader(in));
								StringBuilder response=new StringBuilder();
								String line;
								while((line=reader.readLine())!=null)
								{
									response.append(line);
								}
								if(listener!=null)
								{
									//回调onFinish（）
									listener.onFinish(response.toString());
								}
								
							}catch(Exception e){
								e.printStackTrace();
								
							}
							finally{
								if(connection!=null)
								{
									connection.disconnect();
								}
							}
					
							
						}
						
					}
							).start();
					
					
					
					
					
				}
		
	
//接口实现来回调服务返回的结果
public interface HttpCallbackListener{
	
	void onFinish(String response);
	void onError(Exception e);
	 
}
	
	
}

