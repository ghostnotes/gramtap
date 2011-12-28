package jp.ghostnotes.gramtap.android.worker;

import jp.ghostnotes.gramtap.android.api.AuthenticationException;
import jp.ghostnotes.gramtap.android.api.ConnectException;
import jp.ghostnotes.gramtap.android.api.DefaultInstagram;
import jp.ghostnotes.gramtap.android.api.Instagram;
import jp.ghostnotes.gramtap.android.api.InstagramMaintenanceException;
import jp.ghostnotes.gramtap.android.api.InstagramResult;
import jp.ghostnotes.gramtap.android.api.ResponseException;
import jp.ghostnotes.gramtap.android.api.convert.ConvertException;
import jp.ghostnotes.gramtap.android.util.GramtapConstants;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * いいねリクエストスレッド。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class LikeRequestThread extends Thread{
    /** 画像ID */
    private String mediaId = null;
    /** いいねリクエスト種別 */
    private LikeRequest likeRequest = null;
    /** アクセストークン */
    private String accessToken = null;
    /** ハンドラ */
    private Handler handler = null;
    
    public LikeRequestThread(String mediaId, LikeRequest likeRequest, String accessToken, Handler handler){
        this.mediaId = mediaId;
        this.likeRequest = likeRequest;
        this.accessToken = accessToken;
        this.handler = handler;
    }
    
    @Override
    public void run(){
        Message message = new Message();
        Bundle data = new Bundle();
        data.putSerializable(GramtapConstants.API_RESULT_KEY_LIKE_REQUEST, likeRequest);
        data.putString(GramtapConstants.API_RESULT_KEY_LIKE_REQUEST_IMAGE_ID, mediaId);
        
        Instagram instagram = new DefaultInstagram();
        InstagramResult instagramResult = null;
        try{
            switch(likeRequest){
            case Like:
                // いいね
                instagramResult = instagram.like(mediaId, accessToken);
                break;
            case Delete:
                // いいね取消
                instagramResult = instagram.deleteLike(mediaId, accessToken);
                break;
            }
            
            data.putSerializable(GramtapConstants.API_RESULT_KEY_RESULT, instagramResult);
        }catch(ConnectException e){
            data.putSerializable(GramtapConstants.API_RESULT_KEY_EXCEPTION, e);
        }catch(AuthenticationException e){
            data.putSerializable(GramtapConstants.API_RESULT_KEY_EXCEPTION, e);
        }catch(ResponseException e){
            data.putSerializable(GramtapConstants.API_RESULT_KEY_EXCEPTION, e);
        }catch(InstagramMaintenanceException e){
            data.putSerializable(GramtapConstants.API_RESULT_KEY_EXCEPTION, e);
        }catch(ConvertException e){
            data.putSerializable(GramtapConstants.API_RESULT_KEY_EXCEPTION, e);
        }

        message.setData(data);
        handler.sendMessage(message);
    }

}
