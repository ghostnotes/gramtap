package jp.ghostnotes.gramtap.android.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ghostnotes.gramtap.android.InstagramImageType;
import jp.ghostnotes.gramtap.android.bean.InstagramImage;
import jp.ghostnotes.gramtap.android.bean.InstagramImageSize;
import jp.ghostnotes.gramtap.android.bean.InstagramPagination;

import android.graphics.Bitmap;

/**
 * Instagram画像マネージャ。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class InstagramImageManager{
    private static final int MAX_STANDARD_CACHE_SIZE = 20;
    
    /** singleton */
    private static InstagramImageManager instance = null;
    
    /**  */
    private List<InstagramPagination> feedPaginationList  = null; 
    /**  */
    private List<InstagramPagination> popularPaginationList  = null; 
    /**  */
    private List<InstagramPagination> prayForJapanPaginationList  = null;
    
    private List<List<InstagramPagination>> paginationListList = null;

    /**  */
    private Map<String, Bitmap> standards = null;
//    private List<String> standardLinkList = null;
    
    /**  */
    private Map<String, Bitmap> lows = null;;
    /**  */
    private Map<String, Bitmap> thumbnails = null;

    private InstagramImageManager(){
        feedPaginationList = new ArrayList<InstagramPagination>();
        popularPaginationList = new ArrayList<InstagramPagination>();
        prayForJapanPaginationList = new ArrayList<InstagramPagination>();
        paginationListList = new ArrayList<List<InstagramPagination>>(3);
        paginationListList.add(feedPaginationList);
        paginationListList.add(popularPaginationList);
        paginationListList.add(prayForJapanPaginationList);
        
        
        standards = new HashMap<String, Bitmap>();
//        standardLinkList = new ArrayList<String>();
        
        lows = new HashMap<String, Bitmap>();
        thumbnails = new HashMap<String, Bitmap>();
    }
    
    public InstagramPagination getPagination(int index, InstagramImageType instagramImageType){
        switch(instagramImageType){
        case SelfFeed:
            return feedPaginationList.get(index);
        case Popular:
            return popularPaginationList.get(index);
        case PrayForJapan:
            return prayForJapanPaginationList.get(index);
        default:
            return null;
        }
    }
    
    public InstagramImage getInstagramImage(String id){
        for(List<InstagramPagination> paginationList : paginationListList){
            if(paginationList.size() > 0){
                for(InstagramPagination pagination : paginationList){
                    for(int i = 0; i < pagination.size(); i++){
                        InstagramImage instagramImage = pagination.getInstagramImage(i);
                        if(instagramImage.getId().equals(id)){
                            return instagramImage;
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    public int paginationSize(InstagramImageType instagramImageType){
        switch(instagramImageType){
        case SelfFeed:
            return feedPaginationList.size();
        case Popular:
            return popularPaginationList.size();
        case PrayForJapan:
            return prayForJapanPaginationList.size();
        default:
            return -1;
        }
    }
    
    private void clearBitmapByImageSize(InstagramImageSize instagramImageSize, List<InstagramPagination> paginationList){
        Map<String, Bitmap> targetMap = null;
        switch(instagramImageSize){
        case Thumbnail:
            targetMap = thumbnails;
            break;
        case StandardResolution:
            targetMap = standards;
            break;
        case LowResolution:
            targetMap = lows;
            break;
        }
        
        for(int i = 0; i < paginationList.size(); i++){
            InstagramPagination pagination = paginationList.get(i);
            for(int j = 0; j < pagination.size(); j++){
                InstagramImage instagramImage = pagination.getInstagramImage(j);
                String instagramLink = instagramImage.getLink();
                if(targetMap.containsKey(instagramLink)){
                    targetMap.remove(instagramLink);
                }
            }
        }
    }
    
    public void clearPaginationAndImage(InstagramImageSize instagramImageSize, InstagramImageType instagramImageType){
        switch(instagramImageType){
        case SelfFeed:
            // フィード
            clearBitmapByImageSize(instagramImageSize, feedPaginationList);
            feedPaginationList.clear();
            break;
        case Popular:
            clearBitmapByImageSize(instagramImageSize, popularPaginationList);
            popularPaginationList.clear();
            break;
        case PrayForJapan:
            clearBitmapByImageSize(instagramImageSize, prayForJapanPaginationList);
            prayForJapanPaginationList.clear();
            break;
        }
    }

    
    public static synchronized InstagramImageManager getInstance(){
        if(instance == null){
            instance = new InstagramImageManager();
        }
        
        return instance;
    }
    
    public void addInstagramPagination(InstagramPagination instagramPagination, InstagramImageType instagramImageType){
        switch(instagramImageType){
        case SelfFeed:
            feedPaginationList.add(instagramPagination);
            break;
        case Popular:
            popularPaginationList.add(instagramPagination);
            break;
        case PrayForJapan:
            prayForJapanPaginationList.add(instagramPagination);
            break;
        default:
            break;
        }
    }
    
    public boolean hasCache(InstagramImageType instagramImageType){
        switch(instagramImageType){
        case SelfFeed:
            return feedPaginationList.size() > 0;
        case Popular:
            return popularPaginationList.size() > 0;
        case PrayForJapan:
            return prayForJapanPaginationList.size() > 0;
        default:
            return false;
        }
    }

    public void putInstagramImage(String instagramLink, InstagramImageSize instagramImageSize, Bitmap image){
        switch(instagramImageSize){
        case Thumbnail:
            thumbnails.put(instagramLink, image);
            break;
        case StandardResolution:
            standards.put(instagramLink, image);

            if(standards.size() > MAX_STANDARD_CACHE_SIZE){
                int bitmapIndex = 
                    getInstagramLinkIndex(instagramLink, InstagramImageSize.StandardResolution);
                int feedInstagramImageSize = feedPaginationList.size() * DEFAULT_PAGINATION_SIZE;
                
                if(feedInstagramImageSize - bitmapIndex > bitmapIndex){
                    for(int i = 0; i < feedPaginationList.size(); i++){
                        boolean isDeleted = false;
                        InstagramPagination lastPagination = feedPaginationList.get(feedPaginationList.size() - (i + 1));
                        for(int j = 0; j < lastPagination.size(); j++){
                            InstagramImage lastInstagramImage = lastPagination.getInstagramImage(lastPagination.size() - (j + 1));
                            Bitmap removedBitmap = standards.remove(lastInstagramImage.getLink());
                            if(removedBitmap != null){
                                isDeleted = true;
                                break;
                            }
                        }
                        
                        if(isDeleted){
                            break;
                        }
                    }
                }else{
                    for(int i = 0; i < feedPaginationList.size(); i++){
                        boolean isDeleted = false;

                        InstagramPagination firstPagination = feedPaginationList.get(i);
                        for(int j = 0; j < firstPagination.size(); j++){
                            InstagramImage firstInstagramImage = firstPagination.getInstagramImage(j);
                            Bitmap removedBitmap = standards.remove(firstInstagramImage.getLink());
                            if(removedBitmap != null){
                                isDeleted = true;
                                break;
                            }
                        }

                        if(isDeleted){
                            break;
                        }
                    }
                }
            }            break;
        case LowResolution:
            lows.put(instagramLink, image);
            break;
        }
    }

    private static final int DEFAULT_PAGINATION_SIZE = 20;
    private int getInstagramLinkIndex(String instagramLink, InstagramImageSize imageSize){
        
        int retIndex = -1;
        switch(imageSize){
        case StandardResolution:
            for(int i = 0; i < feedPaginationList.size(); i++){
                InstagramPagination pagination = feedPaginationList.get(i);
                for(int j = 0; j < pagination.size(); j++){
                    InstagramImage instagramImage = pagination.getInstagramImage(j);
                    if(instagramLink.equals(instagramImage.getLink())){
                        retIndex = i * DEFAULT_PAGINATION_SIZE + j;
                        break;
                    }
                }
            }
            break;
        }
        
        return retIndex;
    }
    
//    public void putStandard(String instagramLink, Bitmap image){
//        standards.put(instagramLink, image);
//    }
//    
//    public void putLow(String instagramLink, Bitmap image){
//        lows.put(instagramLink, image);
//    }
//
//    public void putThumbnail(String instagramLink, Bitmap image){
//        thumbnails.put(instagramLink, image);
//    }
    
    public Bitmap getStandard(String instagramLink){
        if(standards.containsKey(instagramLink)){
            return standards.get(instagramLink);
        }
        return null;
    }

    public Bitmap getLow(String instagramLink){
        if(lows.containsKey(instagramLink)){
            return lows.get(instagramLink);
        }
        return null;
    }

    public Bitmap getThumbnail(String instagramLink){
        if(thumbnails.containsKey(instagramLink)){
            return thumbnails.get(instagramLink);
        }
        return null;
    }
    
    public void clearBitmap(InstagramImageSize instagramImageSize){
        switch(instagramImageSize){
        case Thumbnail:
            thumbnails.clear();
            break;
        case StandardResolution:
            standards.clear();
            break;
        case LowResolution:
            lows.clear();
            break;
        }
    }

}
