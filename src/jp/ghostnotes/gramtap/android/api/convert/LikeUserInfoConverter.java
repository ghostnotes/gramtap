package jp.ghostnotes.gramtap.android.api.convert;

import java.util.ArrayList;
import java.util.List;

import jp.ghostnotes.gramtap.android.api.InstagramResult;
import jp.ghostnotes.gramtap.android.bean.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LikeUserInfoConverter implements Converter{
    public Object convert(String str, Object data) throws ConvertException{
        /*
         * {

            "data": [{
                "username": "jack",
                "first_name": "Jack",
                "last_name": "Dorsey",
                "type": "user",
                "id": "66"
            },
            {
                "username": "sammyjack",
                "first_name": "Sammy",
                "last_name": "Jack",
                "type": "user",
                "id": "29648"
            }]
            }
         *
         */
        
        try{
            JSONObject jsonObj = new JSONObject(str);
            JSONArray userInfoArray = jsonObj.getJSONArray("data");
            List<User> likeUserList = null;
            
            if(userInfoArray.length() > 0){
                likeUserList = new ArrayList<User>();
                
                for(int i = 0; i < userInfoArray.length(); i++){
                    JSONObject userObj = userInfoArray.getJSONObject(i);
                    User user = new User();
                    user.setId(userObj.getLong("id"));
                    user.setUserName(userObj.getString("username"));
                    
                    likeUserList.add(user);
                }
            }
            
            InstagramResult instaResult = new InstagramResult();
            instaResult.setResultObject(likeUserList);
            
            return instaResult;
        }catch(JSONException e){
            throw new ConvertException(e);
        }
    }

}
