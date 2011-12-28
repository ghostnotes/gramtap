package jp.ghostnotes.gramtap.android.util;

import java.util.HashMap;
import java.util.Map;

import jp.ghostnotes.gramtap.android.bean.AccessToken;
import jp.ghostnotes.gramtap.android.bean.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

public class UserManager{
    
    private static UserManager instance = null;
    
    private AccessToken loginUserAccessToken = null;
    
    public AccessToken getLoginUserAccessToken(Context context){
        if(loginUserAccessToken == null){
            createAccessTokenFromPreference(context);
        }
        
        return loginUserAccessToken;
    }
    
    public long getLoginUserId(Context context){
        if(loginUserAccessToken == null){
            createAccessTokenFromPreference(context);
        }

        if(loginUserAccessToken != null){
            User loginUser = loginUserAccessToken.getUser();
            return loginUser.getId();
        }else{
            return -1l;
        }
    }
    
    public boolean isLoggedIn(Context context){
        if(loginUserAccessToken == null){
            createAccessTokenFromPreference(context);
        }

        return loginUserAccessToken != null;
    }
    
    public AccessToken createAccessTokenFromPreference(Context context){
        AccessToken retAccessToken = null;
        
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        if(settings.contains(GramtapConstants.SHARED_PREF_KEY_ACCESS_TOKEN)){

            retAccessToken = new AccessToken();
            retAccessToken.setText(settings.getString(GramtapConstants.SHARED_PREF_KEY_ACCESS_TOKEN, null));
            retAccessToken.setAuthLike(settings.getBoolean(GramtapConstants.SHARED_PREF_KEY_SCOPE_LIKE, false));
            
            // ログインしているユーザ
            User loginUser = new User();
            // ユーザID
            loginUser.setId(settings.getLong(GramtapConstants.SHARED_PREF_KEY_LOGIN_USER_ID, -1l));
            // ユーザ名
            loginUser.setUserName(settings.getString(GramtapConstants.SHARED_PREF_KEY_LOGIN_USERNAME, null));
            // フルネーム
            loginUser.setFullName(settings.getString(GramtapConstants.SHARED_PREF_KEY_LOGIN_FULL_NAME, null));
            // プロファイル画像URL
            loginUser.setProfilePicture(settings.getString(GramtapConstants.SHARED_PREF_KEY_LOGIN_PROFILE_PICTURE, null));
            
            
            // アクセストークンにログインユーザを設定
            retAccessToken.setUser(loginUser);
        }
        
        loginUserAccessToken = retAccessToken;
        return retAccessToken;
    }

    //    private AccessToken accessToken = null;
//    
//    public void setAccessToken(AccessToken accessToken){
//        this.accessToken = accessToken;
//    }
//    
//    public AccessToken getAccessToken(){
//        return accessToken;
//    }

    private Map<Long, User> users = null;
    private Map<Long, Bitmap> profilePictures = null; 
    private UserManager(){
        users = new HashMap<Long, User>();
        profilePictures = new HashMap<Long, Bitmap>();
    }
    
    public static synchronized UserManager getInstance(){
        if(instance == null){
            instance = new UserManager();
        }
        
        return instance;
    }
    
    public void putUser(User user){
        users.put(user.getId(), user);
    }
    
    public void putProfilePicture(User user, Bitmap profilePicture){
        profilePictures.put(user.getId(), profilePicture);
        putUser(user);
    }
    
    public boolean existsProfilePicture(User user){
        return profilePictures.containsKey(user.getId());
    }
    
    public Bitmap getProfilePicture(User user){
        if(profilePictures.containsKey(user.getId())){
            return profilePictures.get(user.getId());
        }
        
        return null;
    }
    
    public void clearUsers(){
        users.clear();
    }
    
    public void clearProfilePictures(){
        profilePictures.clear();
    }

}
