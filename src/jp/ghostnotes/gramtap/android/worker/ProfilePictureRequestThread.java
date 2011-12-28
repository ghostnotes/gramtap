package jp.ghostnotes.gramtap.android.worker;

import java.io.IOException;

import jp.ghostnotes.gramtap.android.bean.User;
import jp.ghostnotes.gramtap.android.util.GramtapConstants;
import jp.ghostnotes.gramtap.android.util.ImageUtil;
import jp.ghostnotes.gramtap.android.util.UserManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ProfilePictureRequestThread extends Thread{
    private User user = null;
    private Handler handler = null;
    
    public ProfilePictureRequestThread(User user){
        this.user = user;
    }
    
    public ProfilePictureRequestThread(User user, Handler handler){
        this.user = user;
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

        if(!UserManager.getInstance().existsProfilePicture(user)){
            try{
                Bitmap profilePicture = ImageUtil.requestImage(user.getProfilePicture());
                UserManager.getInstance().putProfilePicture(user, profilePicture);
                
                if(handler != null){
                    data.putSerializable(GramtapConstants.API_RESULT_KEY_USER, user);
                }
            }catch(IOException e){
                if(handler != null){
                    data.putSerializable(GramtapConstants.API_RESULT_KEY_EXCEPTION, e);
                }
            }
        }

        if(handler != null){
            message.setData(data);
            handler.sendMessage(message);
        }
    }

}
