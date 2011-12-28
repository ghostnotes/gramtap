package jp.ghostnotes.gramtap.android.util;

/**
 * gramtap定数クラス。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class GramtapConstants{
    public static final String CLIENT_ID = "3b23c1750cf94b5a817b54cd5e8ee415";
    public static final String CLIENT_SECRET = "6469ec55b60c4700a9edfabeb6c318a7";
    
    public static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    
    public static final String URL_BASE = "https://api.instagram.com/v1/media";
    public static final String URL_POPULAR = URL_BASE + "/popular";
    
    public static final String URL_PRAY_FOR_JAPAN = "https://api.instagram.com/v1/tags/prayforjapan/media/recent";
    
    public static final String URL_LIKE = "https://api.instagram.com/v1/media/{media-id}/likes";
    
    
    public static final String API_RESULT_KEY_RESULT = "api_result_key_result";
    public static final String API_RESULT_KEY_INSTAGRAM_LINK = "api_result_key_instagram_link";
    public static final String API_RESULT_KEY_VERTICAL_INDEX = "api_result_key_vertical_index";
    public static final String API_RESULT_KEY_HORIZONTAL_INDEX = "api_result_key_horizontal_index";
    public static final String API_RESULT_KEY_USER = "api_result_key_user";
//    public static final String API_RESULT_KEY_IMAGE = "api_result_key_image";
//    public static final String API_RESULT_KEY_INSTAGRAM_IMAGE = "api_result_key_instagram_image";
    public static final String API_RESULT_KEY_LIKE_REQUEST = "api_result_key_like_request";
    public static final String API_RESULT_KEY_LIKE_REQUEST_IMAGE_ID = "api_request_key_like_request_image_id";
    
    
    public static final String API_RESULT_KEY_EXCEPTION = "api_result_key_exception";

//    public static final String BUNDLE_KEY_IMAGE = "bundle_key_image";
    public static final String BUNDLE_KEY_IMAGE_ID = "bundle_key_image_id";
    
    public static final String SHARED_PREF_KEY_LOGIN_USER_ID = "shared_pref_key_login_user_id";
    public static final String SHARED_PREF_KEY_LOGIN_USERNAME = "shared_pref_key_login_username";
    public static final String SHARED_PREF_KEY_LOGIN_FULL_NAME = "shared_pref_key_login_full_name";
    public static final String SHARED_PREF_KEY_LOGIN_PROFILE_PICTURE = "shared_pref_key_profile_picture";
    public static final String SHARED_PREF_KEY_ACCESS_TOKEN = "shared_pref_key_access_token";
    public static final String SHARED_PREF_KEY_SCOPE_LIKE = "shared_pref_key_scope_like";
}
