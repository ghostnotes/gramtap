package jp.ghostnotes.gramtap.android.api.convert;

import jp.ghostnotes.gramtap.android.api.InstagramResult;
import jp.ghostnotes.gramtap.android.bean.AccessToken;
import jp.ghostnotes.gramtap.android.bean.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * アクセストークン変換。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class AccessTokenConverter implements Converter{

    public Object convert(String str, Object data) throws ConvertException{
        try{
            JSONObject jsonObj = new JSONObject(str);
            AccessToken accessToken = new AccessToken();
            accessToken.setText(jsonObj.getString("access_token"));

            JSONObject userObj = jsonObj.getJSONObject("user");
            User user = new User();
            user.setId(userObj.getLong("id"));
            user.setUserName(userObj.getString("username"));
            if(!userObj.isNull("full_name")){
                user.setFullName(userObj.getString("full_name"));
            }
            if(!userObj.isNull("profile_picture")){
                user.setProfilePicture(userObj.getString("profile_picture"));
            }

            accessToken.setUser(user);
            
            InstagramResult instagramResult = new InstagramResult();
            instagramResult.setResultObject(accessToken);
            return instagramResult;
        }catch(JSONException e){
            throw new ConvertException(e);
        }
    }

}
