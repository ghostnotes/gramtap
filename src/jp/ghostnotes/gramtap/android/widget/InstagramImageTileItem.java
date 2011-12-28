package jp.ghostnotes.gramtap.android.widget;

import jp.ghostnotes.gramtap.android.bean.InstagramImage;

public class InstagramImageTileItem{
    
    private static final int MAX_IMAGE_ITEM_SIZE = 4;
    private InstagramImage[] instagramImageArray = null;
    private boolean isDummy = false;
    
    public InstagramImageTileItem(){
    }

    public InstagramImageTileItem(boolean isDummy){
        this.isDummy = isDummy;
    }

    public boolean isDummy(){
        return isDummy;
    }
    
    private String nextUrl = null;
    public void setNextUrl(String nextUrl){
        this.nextUrl = nextUrl;
        
    }
    
    public String getNextUrl(){
        return nextUrl;
    }
    
    public int size(){
        return MAX_IMAGE_ITEM_SIZE;
    }
    
    public void addInstagramImage(int index, InstagramImage instagramImage){
        if(instagramImageArray == null){
            instagramImageArray = new InstagramImage[MAX_IMAGE_ITEM_SIZE];
        }
        
        instagramImageArray[index] = instagramImage;
    }
    
    public InstagramImage getInstagramImage(int index){
        if(instagramImageArray != null){
            return instagramImageArray[index];
        }
        
        return null;
    }
    

}
