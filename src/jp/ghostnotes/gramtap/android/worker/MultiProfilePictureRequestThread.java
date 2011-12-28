package jp.ghostnotes.gramtap.android.worker;

import java.io.IOException;
import java.util.List;

import jp.ghostnotes.gramtap.android.bean.User;
import jp.ghostnotes.gramtap.android.util.GramtapConstants;
import jp.ghostnotes.gramtap.android.util.ImageUtil;
import jp.ghostnotes.gramtap.android.util.UserManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MultiProfilePictureRequestThread extends Thread{
    List<User> userList = null;
    private Handler handler = null;
    
    public MultiProfilePictureRequestThread(List<User> userList, Handler handler){
        this.userList = userList;
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

        UserManager userManager = UserManager.getInstance();
        for(int i = 0; i < userList.size(); i++){
            User user = userList.get(i);
            if(!userManager.existsProfilePicture(user)){
                try{
                    Bitmap profilePicture = ImageUtil.requestImage(user.getProfilePicture());
                    UserManager.getInstance().putProfilePicture(user, profilePicture);
                    
                    if(handler != null){
                        data.putSerializable(GramtapConstants.API_RESULT_KEY_USER, user);
                    }
                }catch(IOException e){
//                    if(handler != null){
//                        data.putSerializable(GramtapConstants.API_RESULT_KEY_EXCEPTION, e);
//                    }
                }
            }
        }

        if(handler != null){
            message.setData(data);
            handler.sendMessage(message);
        }
    }

}
