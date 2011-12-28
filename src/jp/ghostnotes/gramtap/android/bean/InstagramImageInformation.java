package jp.ghostnotes.gramtap.android.bean;

import java.io.Serializable;

/**
 * Instagram画像情報。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class InstagramImageInformation implements Serializable{

    private static final long serialVersionUID = 1962114551507109176L;

    private InstagramImageSize imageSize = InstagramImageSize.None;
    private String url;
    private int width = -1;
    private int height = -1;
    
    public InstagramImageInformation(){
    }
    
    public InstagramImageSize getImageSize(){
        return imageSize;
    }
    
    public void setImageSize(InstagramImageSize imageSize){
        this.imageSize = imageSize;
    }
    
    public String getUrl(){
        return url;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public int getWidth(){
        return width;
    }
    public void setWidth(int width){
        this.width = width;
    }
    public int getHeight(){
        return height;
    }
    public void setHeight(int height){
        this.height = height;
    }

}
