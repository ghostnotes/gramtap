package jp.ghostnotes.gramtap.android.worker;

import java.io.IOException;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import jp.ghostnotes.gramtap.android.bean.InstagramImage;
import jp.ghostnotes.gramtap.android.bean.InstagramImageSize;
import jp.ghostnotes.gramtap.android.util.GramtapConstants;
import jp.ghostnotes.gramtap.android.util.ImageUtil;
import jp.ghostnotes.gramtap.android.util.InstagramImageManager;

public class MultiPhotoRequestThread extends Thread{
    /** 最大画像リクエストリトライ回数 */
    private static final int MAX_REQUEST_RETRY_COUNT = 5;
    
    private List<InstagramImage> instagramImageList = null;
    /** Instagram画像タイプ */
    private InstagramImageSize instagramImageSize = null;
    private Handler handler = null;
    
    public MultiPhotoRequestThread(
        List<InstagramImage> instagramImageList, 
        InstagramImageSize instagramImageSize,
        Handler handler){
        this.instagramImageList = instagramImageList;
        this.instagramImageSize = instagramImageSize;
        this.handler = handler;
    }
    
    @Override
    public void run(){
        Message message = null;
        Bundle data = null;
        if(handler != null){
            message = new Message();
            data = new Bundle();
        }

        Exception tmpException = null;
        for(int i = 0; i < instagramImageList.size(); i++){
            InstagramImage instagramImage = instagramImageList.get(i);
            String instagramImageUrl = instagramImage.getUrl(instagramImageSize);
            
            for(int j = 0; j < MAX_REQUEST_RETRY_COUNT; j++){
                try{
                    Bitmap photo = ImageUtil.requestImage(instagramImageUrl);

                    InstagramImageManager.getInstance().putInstagramImage(
                        instagramImage.getLink(),
                        instagramImageSize,
                        photo
                    );

                    if(handler != null){
                        data.putString(GramtapConstants.API_RESULT_KEY_INSTAGRAM_LINK, instagramImage.getLink());
                    }
                    break;
                }catch(IOException e){
                    tmpException = e;
                }
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
