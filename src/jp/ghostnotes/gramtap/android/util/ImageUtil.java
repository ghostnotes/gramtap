package jp.ghostnotes.gramtap.android.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 画像ユーティリティクラス。
 * 
 * @author taniguchi@paperboy.co.jp
 */
public class ImageUtil{

    /**
     * 指定されたURLの画像を取得する。
     * 
     * @param imageUrl 画像URL
     * @return Bitmap
     * @throws IOException
     * @throws ExpiredSessionIdException 
     */
    public static Bitmap requestImage(String imageUrl) throws IOException{
        byte[] imageBytes = requestImageBytes(imageUrl);
        if(imageBytes != null){
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }else{
            return null;
        }
    }
    
    /**
     * 指定されたURLの画像を取得する。(byte配列)
     * 
     * @param imageUrl 画像URL
     * @return
     * @throws IOException
     * @throws ExpiredSessionIdException 
     */
    public static byte[] requestImageBytes(String imageUrl) throws IOException{
        byte[] ret = null;
        HttpURLConnection httpConn = null;
        InputStream is = null;
        try {
            URL url = new URL(imageUrl);
            httpConn = (HttpURLConnection)url.openConnection();
            httpConn.setDoInput(true);
            httpConn.connect();
            
            is = httpConn.getInputStream();
            byte[] buf = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int n;
            while((n = is.read(buf, 0, buf.length)) != -1){
                baos.write(buf, 0, n);
            }
            
            ret = baos.toByteArray();
        }catch(IOException e){
            switch(httpConn.getResponseCode()){
            case HttpStatus.SC_FORBIDDEN:
            default:
                throw e;
            }
        }finally{
            if(is != null){
                try{
                    is.close();
                }catch (IOException e){
                }
            }
        }
        return ret;
    }

}
