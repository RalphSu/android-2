package org.app.netmusic.tool;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;

public class HttpTool {
public static class MemoryCache{
	private Map<String, SoftReference<Bitmap>> cache=Collections.synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());//软引用  
    
    public Bitmap get(String id){  
        if(!cache.containsKey(id))  
            return null;  
        SoftReference<Bitmap> ref=cache.get(id);  
        return ref.get();  
    }  
      
    public void put(String id, Bitmap bitmap){  
        cache.put(id, new SoftReference<Bitmap>(bitmap));  
    }  
  
    public void clear() {  
        cache.clear();  
    }  
}
public static class FileCache{
	 private File cacheDir;  
	 public FileCache(Context context){  
	        //找一个用来缓存图片的路径  
	        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))  
	            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"musicLists");  
	        else  
	            cacheDir=context.getCacheDir();  
	        if(!cacheDir.exists())  
	            cacheDir.mkdirs();  
	    }  
	      
	    public File getFile(String url){  
	          
	        String filename=String.valueOf(url.hashCode());  
	        File f = new File(cacheDir, filename);  
	        return f;  
	          
	    }  
	      
	    public void clear(){  
	        File[] files=cacheDir.listFiles();  
	        if(files==null)  
	            return;  
	        for(File f:files)  
	            f.delete();  
	    }  
	  
}
public static class Utils{
	public static void CopyStream(InputStream is, OutputStream os)  
    {  
        final int buffer_size=1024;  
        try  
        {  
            byte[] bytes=new byte[buffer_size];  
            for(;;)  
            {  
              int count=is.read(bytes, 0, buffer_size);  
              if(count==-1)  
                  break;  
              os.write(bytes, 0, count);  
             
            }  
            is.close();  
            os.close();  
        }  
        catch(Exception ex){}  
    }  
}
}
