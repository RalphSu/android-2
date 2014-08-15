package com.aozhi.client.util;

import java.io.IOException;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import com.google.common.collect.Maps;

/**
 * httpClient工具类
 * @author luxh
 *
 */
public class HttpClientUtil {
	public static String post(String url,Map<String,ContentBody> params){
		  String result = "";
		  HttpClient httpclient = new DefaultHttpClient();  
	      try {  
	            HttpPost httppost = new HttpPost(url);
	            
	            MultipartEntity reqEntity = new MultipartEntity();  
	            
	            //设置参数
	            for(Map.Entry<String,ContentBody> map:params.entrySet()){
	            	reqEntity.addPart(map.getKey(), map.getValue());
	            }
	            httppost.setEntity(reqEntity);  
	          
	            HttpResponse response = httpclient.execute(httppost);
	            int statusCode=response.getStatusLine().getStatusCode();
				//logger.info("  ================>响应状态码: "+statusCode);
	            if(HttpStatus.SC_OK==statusCode){  
		            HttpEntity entity = response.getEntity();  
		            if (entity != null) {  
		            	result = IOUtils.toString(entity.getContent());
		            }  
		        }
	              
	        }catch(IOException e){
	        	e.printStackTrace();
	        }finally{
	        	ClientConnectionManager connectionManager = httpclient.getConnectionManager();
	        	if(connectionManager!=null){
	        		connectionManager.shutdown();
	        	}
	        } 
	        return result;    
	}
	
    public static    String convert(String utfString){  
        StringBuilder sb = new StringBuilder();  
        int i = -1;  
        int pos = 0;  
          
        while((i=utfString.indexOf("\\u", pos)) != -1){  
            sb.append(utfString.substring(pos, i));  
            if(i+5 < utfString.length()){  
                pos = i+6;  
                sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));  
            }  
        }  
          
        return sb.toString();  
    } 
    
    public static String ascii2Native(String str) {   
        StringBuilder sb = new StringBuilder();   
        int begin = 0;   
        int index = str.indexOf("\\u");   
        while (index != -1) {   
            sb.append(str.substring(begin, index));   
            sb.append(ascii2Char(str.substring(index, index + 6)));   
            begin = index + 6;   
            index = str.indexOf("\\u", begin);   
        }   
        sb.append(str.substring(begin));   
        return sb.toString();   
    }  
	private static char ascii2Char(String str) {   
        if (str.length() != 6) {   
            throw new IllegalArgumentException(   
                    "Ascii string of a native character must be 6 character.");   
        }   
        if (!"\\u".equals(str.substring(0, 2))) {   
            throw new IllegalArgumentException(   
                    "Ascii string of a native character must start with \"\\u\".");   
        }   
        String tmp = str.substring(2, 4);   
        int code = Integer.parseInt(tmp, 16) << 8;   
        tmp = str.substring(4, 6);   
        code += Integer.parseInt(tmp, 16);   
        return (char) code;   
    }  
	public static void main(String[] args) throws Exception {
		String t="erqweq\u3001\u63d0\u5347\u5e94";
		System.out.println(ascii2Native(t));
		String url;
		//post
		url="http://app.imusicapp.cn/fengchao/rpc_ajax?appid=5883&op=getversion&IMSI=873366511151048";
		//get
		//url="http://app.imusicapp.cn/fengchao/preindex?appid=5883&IMSI=873366511151048&versioncode=1";
		
		String message="";
		Map<String, ContentBody> params = Maps.newHashMap();


		StringBody app_id = new StringBody("5883");
		StringBody op = new StringBody("getversion");
		//params.put("appid", app_id);
		//params.put("op", op);
	//	String result=post(url, params);
		//System.out.println("++>"+result);
	}
 
}
