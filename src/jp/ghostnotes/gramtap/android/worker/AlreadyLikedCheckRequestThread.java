package jp.ghostnotes.gramtap.android.worker;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import jp.ghostnotes.gramtap.android.api.AuthenticationException;
import jp.ghostnotes.gramtap.android.api.ConnectException;
import jp.ghostnotes.gramtap.android.api.DefaultInstagram;
import jp.ghostnotes.gramtap.android.api.Instagram;
import jp.ghostnotes.gramtap.android.api.InstagramMaintenanceException;
import jp.ghostnotes.gramtap.android.api.InstagramResult;
import jp.ghostnotes.gramtap.android.api.ResponseException;
import jp.ghostnotes.gramtap.android.api.convert.ConvertException;
import jp.ghostnotes.gramtap.android.bean.AccessToken;
import jp.ghostnotes.gramtap.android.bean.InstagramImage;
import jp.ghostnotes.gramtap.android.bean.LikeState;
import jp.ghostnotes.gramtap.android.util.GramtapConstants;
import jp.ghostnotes.gramtap.android.util.InstagramImageManager;

/**
 * すでにいいねしたかチェックするスレッド。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class AlreadyLikedCheckRequestThread extends Thread{
    /** 画像ID */
    private String mediaId = null;
    /** ログインユーザID */
    private long loginUserId = -1l;
    /** アクセストークン */
    private AccessToken accessToken = null;
    /** ハンドラ */
    private Handler handler = null;
    
    /**
     * コンストラクタ。
     * 
     * @param mediaId 画像ID
     * @param loginUserId ログインユーザID
     * @param accessToken アクセストークン
     * @param handler ハンドラ
     */
    public AlreadyLikedCheckRequestThread(
        String mediaId, 
        long loginUserId, 
        AccessToken accessToken, 
        Handler handler
    ){
        this.mediaId = mediaId;
        this.loginUserId = loginUserId;
        this.accessToken = accessToken;
        this.handler = handler;
    }
    
    @Override
    public void run(){
        Message message = new Message();
        Bundle data = new Bundle();
        
        // Instagram画像
        InstagramImage instagramImage = InstagramImageManager.getInstance().getInstagramImage(mediaId);
        // Instagram API
        Instagram instagram = new DefaultInstagram();
        try{
            // いいねしたユーザのリストをリクエストし、ログインユーザIDが入っているかどうかチェックする
            InstagramResult instaResult = instagram.isAlreadyLiked(mediaId, accessToken);
            
            // いいねしているか
            boolean isLiked = (Boolean)instaResult.getResultObject();
            if(isLiked){
                // いいね済み
                instagramImage.addLikeUserId(loginUserId);
            }

            // すでにいいねしているかどうかチェック済み
            instagramImage.setLikeState(LikeState.Done);
            data.putSerializable(GramtapConstants.API_RESULT_KEY_RESULT, instaResult);
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
