package jp.ghostnotes.gramtap.android.api.convert;

import jp.ghostnotes.gramtap.android.api.InstagramResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AlreadyLikedCheckConveter implements Converter{

    public Object convert(String str, Object data) throws ConvertException{
        
        long loginUserId = (Long)data;
        try{
            JSONObject jsonObj = new JSONObject(str);
            JSONArray userInfoArray = jsonObj.getJSONArray("data");

            boolean isAlreadyLiked = false;
            if(userInfoArray.length() > 0){
                
                
                for(int i = 0; i < userInfoArray.length(); i++){
                    JSONObject userObj = userInfoArray.getJSONObject(i);
                    long id = userObj.getLong("id");
                    if(id == loginUserId){
                        isAlreadyLiked = true;
                        break;
                    }
                }
            }
            
            InstagramResult instaResult = new InstagramResult();
            instaResult.setResultObject(isAlreadyLiked);
            
            return instaResult;
        }catch(JSONException e){
            throw new ConvertException(e);
        }
    }

}
