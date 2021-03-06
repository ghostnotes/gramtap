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

public class PrayForJapanDataRequestThread extends Thread{
    private Handler handler = null;
    
    public PrayForJapanDataRequestThread(Handler handler){
        this.handler = handler;
    }

    @Override
    public void run(){
        Message message = new Message();
        Bundle data = new Bundle();
        
        
        Instagram instagram = new DefaultInstagram();
        InstagramResult instaResult = null;
        try{
            instaResult = instagram.prayForJapan();
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
