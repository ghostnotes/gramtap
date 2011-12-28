package jp.ghostnotes.gramtap.android.api.convert;

import java.util.Date;

import jp.ghostnotes.gramtap.android.bean.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 変換ユーティリティ。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class ConvertUtil{
    private static final String JSON_KEY_USER_ID = "id";
    private static final String JSON_KEY_USERNAME = "username";
    private static final String JSON_KEY_FULL_NAME = "full_name";
    private static final String JSON_KEY_PROFILE_PICTURE = "profile_picture";

    /**
     * 指定されたJSONオブジェクトをユーザオブジェクトに変換する。
     * 
     * @param userObj ユーザJSONオブジェクト
     * @return ユーザ
     * @throws JSONException
     */
    public static User convertUser(JSONObject userObj) throws JSONException{
        // 返却するユーザオブジェクト
        User retUser = new User();
        // ユーザID
        retUser.setId(userObj.getLong(JSON_KEY_USER_ID));
        // ユーザ名
        retUser.setUserName(userObj.getString(JSON_KEY_USERNAME));
        if(!userObj.isNull(JSON_KEY_FULL_NAME)){
            // フルネーム
            retUser.setFullName(userObj.getString(JSON_KEY_FULL_NAME));
        }
        if(!userObj.isNull(JSON_KEY_PROFILE_PICTURE)){
            // プロフィール画像
            retUser.setProfilePicture(userObj.getString(JSON_KEY_PROFILE_PICTURE));
        }

        return retUser;
    }
    
    /**
     * 指定された時間文字列をDateオブジェクトに変換する。
     * 
     * @param timeStr 時間文字列
     * @return java.util.Date
     */
    public static Date convertDate(String timeStr){
        long millis = Long.parseLong(timeStr) * 1000l;
        return new Date(millis);
//        String aaa = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
    }
    
    
}
