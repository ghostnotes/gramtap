package jp.ghostnotes.gramtap.android.bean;

import java.util.ArrayList;
import java.util.List;

public class InstagramPagination{
    
    private String nextUrl = null;
    private long nextMaxId = -1l;
    private long nextMinId = -1l;

    private List<InstagramImage> instagramImageList = null;
    
    public InstagramPagination(){
        instagramImageList = new ArrayList<InstagramImage>();
    }
    
    public void setNextUrl(String nextUrl){
        this.nextUrl = nextUrl;
    }
    
    public String getNextUrl(){
        return nextUrl;
    }
    
    public long getNextMaxId(){
        return nextMaxId;
    }

    public void setNextMaxId(long nextMaxId){
        this.nextMaxId = nextMaxId;
    }

    public long getNextMinId(){
        return nextMinId;
    }

    public void setNextMinId(long nextMinId){
        this.nextMinId = nextMinId;
    }

    public void addInstagramImage(InstagramImage instagramImage){
        instagramImageList.add(instagramImage);
    }
    
    public int size(){
        return instagramImageList.size();
    }
    
    public InstagramImage getInstagramImage(int index){
        return instagramImageList.get(index);
    }

}
