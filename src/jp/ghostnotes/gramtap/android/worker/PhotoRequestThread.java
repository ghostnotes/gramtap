package jp.ghostnotes.gramtap.android.worker;

import java.io.IOException;

import jp.ghostnotes.gramtap.android.bean.InstagramImage;
import jp.ghostnotes.gramtap.android.bean.InstagramImageSize;
import jp.ghostnotes.gramtap.android.util.GramtapConstants;
import jp.ghostnotes.gramtap.android.util.InstagramImageManager;
import jp.ghostnotes.gramtap.android.util.ImageUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 画像リクエストスレッド。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class PhotoRequestThread extends Thread{
    /** 最大画像リクエストリトライ回数 */
    private static final int MAX_REQUEST_RETRY_COUNT = 5;

    /** Instagram画像 */
    private InstagramImage instagramImage = null;
    /** Instagram画像タイプ */
    private InstagramImageSize instagramImageSize = null;
    /** 縦インデックス */
    private int verticalIndex = -1;
    /** 横インデックス */
    private int horizontalIndex = -1;
    /** ハンドラ */
    private Handler handler = null;
    
    /**
     * 
     * 
     * @param instagramImage
     * @param instagramImageType
     * @param verticalIndex
     * @param horizontalIndex
     * @param handler
     */
    public PhotoRequestThread(
        InstagramImage instagramImage, 
        InstagramImageSize instagramImageType, 
        int verticalIndex, 
        int horizontalIndex, 
        Handler handler
    ){
        this.instagramImage = instagramImage;
        this.instagramImageSize = instagramImageType;
        this.verticalIndex = verticalIndex;
        this.horizontalIndex = horizontalIndex;
        this.handler = handler;
    }
    
    public PhotoRequestThread(
        InstagramImage instagramImage, 
        InstagramImageSize instagramImageType, 
        Handler handler
    ){
        this.instagramImage = instagramImage;
        this.instagramImageSize = instagramImageType;
        this.handler = handler;
    }
    
    
    @Override
    public void run(){
        Message message = null;
        Bundle data = null;
        
        if(handler != null){
            message = new Message();
            data = new Bundle();
            // 縦インデックスを保存
            data.putInt(GramtapConstants.API_RESULT_KEY_VERTICAL_INDEX, verticalIndex);
            // 横インデックスを保存
            data.putInt(GramtapConstants.API_RESULT_KEY_HORIZONTAL_INDEX, horizontalIndex);
        }

        // Instagram画像URLをサイズ指定で取得
        String instagramImageUrl = instagramImage.getUrl(instagramImageSize);
        
        Exception tmpException = null;
        for(int i = 0; i < MAX_REQUEST_RETRY_COUNT; i++){
            try{
                Bitmap photo = ImageUtil.requestImage(instagramImageUrl);
                
//                switch(instagramImageType){
//                case StandardResolution:
//                    InstagramImageManager.getInstance().putStandard(instagramImage.getLink(), photo);
//                    break;
//                case LowResolution:
//                    InstagramImageManager.getInstance().putLow(instagramImage.getLink(), photo);
//                    break;
//                case Thumbnail:
//                    InstagramImageManager.getInstance().putThumbnail(instagramImage.getLink(), photo);
//                    break;
//                }
                InstagramImageManager.getInstance().putInstagramImage(
                    instagramImage.getLink(),
                    instagramImageSize,
                    photo
                );

                if(handler != null){
                    data.putString(GramtapConstants.API_RESULT_KEY_INSTAGRAM_LINK, instagramImage.getLink());
//                    data.putSerializable(GramtapConstants.API_RESULT_KEY_INSTAGRAM_IMAGE, instagramImage);
                }
                break;
            }catch(IOException e){
                tmpException = e;
            }
        }
        
        if(handler != null){
            if(tmpException != null){
                data.putSerializable(GramtapConstants.API_RESULT_KEY_EXCEPTION, tmpException);
            }
            
            message.setData(data);
            handler.sendMessage(message);
        }
    }
}
