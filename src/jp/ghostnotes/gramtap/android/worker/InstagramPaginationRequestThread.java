package jp.ghostnotes.gramtap.android.worker;

import jp.ghostnotes.gramtap.android.InstagramImageType;
import jp.ghostnotes.gramtap.android.api.AuthenticationException;
import jp.ghostnotes.gramtap.android.api.ConnectException;
import jp.ghostnotes.gramtap.android.api.DefaultInstagram;
import jp.ghostnotes.gramtap.android.api.Instagram;
import jp.ghostnotes.gramtap.android.api.InstagramMaintenanceException;
import jp.ghostnotes.gramtap.android.api.InstagramResult;
import jp.ghostnotes.gramtap.android.api.ResponseException;
import jp.ghostnotes.gramtap.android.api.convert.ConvertException;
import jp.ghostnotes.gramtap.android.bean.AccessToken;
import jp.ghostnotes.gramtap.android.util.GramtapConstants;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Instagramページネーションリクエストスレッド。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class InstagramPaginationRequestThread extends Thread{
//    private long loginUserId = -1l;
//    private String accessToken = null;
    /** アクセストークン */
    private AccessToken accessToken = null;
    /** Instagram画像タイプ */
    private InstagramImageType instagramImageType = null;
    /** ハンドラ */
    private Handler handler = null;
    
    private String url = null;
//    private String query = null;
    
    public InstagramPaginationRequestThread(
//        long loginUserId,
//        String accessToken,
        AccessToken accessToken,
        InstagramImageType instagramImageType, 
        Handler handler
    ){
//        this.loginUserId = loginUserId;
        this.accessToken = accessToken;
        this.instagramImageType = instagramImageType;
        this.handler = handler;
    }

//    public InstagramPaginationRequestThread(
//        long loginUserId,
//        String accessToken,
//        InstagramImageType instagramImageType,
//        Handler handler
//    ){
//        this.loginUserId = loginUserId;
//        this.accessToken = accessToken;
//        this.instagramImageType = instagramImageType;
//        this.handler = handler;
//    }
    
    public InstagramPaginationRequestThread(
//        long loginUserId,
//        String accessToken,
        AccessToken accessToken,
        String url, 
        Handler handler
    ){
//        this.loginUserId = loginUserId;
        this.accessToken = accessToken;
        this.url = url;
        this.handler = handler;
    }
    

    @Override
    public void run(){
        Message message = new Message();
        Bundle data = new Bundle();

        Instagram instagram = new DefaultInstagram();
        InstagramResult instaResult = null;
        
//        User loginUser = accessToken.getUser();
        try{
            if(url != null){
                instaResult = instagram.requestPagination(url);
            }else{
                
                
                switch(instagramImageType){
                case SelfFeed:
                    // フィード
                    instaResult = instagram.getSelfFeed(accessToken.getText());
                    break;
                case Popular:
                    // ポピュラー
//                    instaResult = instagram.popular();
//                    instaResult = instagram.popular(loginUser.getId(), accessToken.getText());
                    instaResult = instagram.popular(accessToken);
                    break;
                case PrayForJapan:
                    // #prayforjapan
                    instaResult = instagram.prayForJapan();
                    break;
                default:
                    break;
                }
            }
            
            // API結果を設定
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
