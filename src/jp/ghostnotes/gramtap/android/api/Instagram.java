package jp.ghostnotes.gramtap.android.api;

import jp.ghostnotes.gramtap.android.api.convert.ConvertException;
import jp.ghostnotes.gramtap.android.bean.AccessToken;


/***
 * インスタグラムAPI。
 * 
 * @author ghostnotesdot@gmail.com
 */
public interface Instagram{
    
    InstagramResult getLikeUserInformation(String mediaId, String accessToken)
        throws ConnectException, 
                AuthenticationException,
                ResponseException,
                InstagramMaintenanceException,
                ConvertException;
    
//    InstagramResult isAlreadyLiked(long loginUserId, String mediaId, String accessToken)
    InstagramResult isAlreadyLiked(String mediaId, AccessToken accessToken)
        throws ConnectException, 
            AuthenticationException,
            ResponseException,
            InstagramMaintenanceException,
            ConvertException;
    
    InstagramResult like(String mediaId, String accessToken)
        throws ConnectException, 
                AuthenticationException,
                ResponseException,
                InstagramMaintenanceException,
                ConvertException;
    
    InstagramResult deleteLike(String mediaId, String accessToken)
        throws ConnectException, 
                AuthenticationException,
                ResponseException,
                InstagramMaintenanceException,
                ConvertException;

    InstagramResult popular(AccessToken accessToken)
        throws ConnectException, 
                AuthenticationException,
                ResponseException,
                InstagramMaintenanceException,
                ConvertException;
    
    InstagramResult prayForJapan()
        throws ConnectException, 
                AuthenticationException,
                ResponseException,
                InstagramMaintenanceException,
                ConvertException;

    InstagramResult getSelfFeed(String accessToken)
        throws ConnectException, 
                AuthenticationException,
                ResponseException,
                InstagramMaintenanceException,
                ConvertException;
    
    InstagramResult requestPagination(String url)
        throws ConnectException, 
                AuthenticationException,
                ResponseException,
                InstagramMaintenanceException,
                ConvertException;
    
    // https://api.instagram.com/oauth/access_token/?client_id=3b23c1750cf94b5a817b54cd5e8ee415&client_secret=6469ec55b60c4700a9edfabeb6c318a7&grant_type=authorization_code&redirect_uri=http://ghostnotes.jp&code=b15d78a0c06343418a3a1d8cee069c2d
//    InstagramResult getAccessToken(String code)
//        throws ConnectException, 
//                AuthenticationException,
//                ResponseException,
//                InstagramMaintenanceException,
//                ConvertException;
    
    InstagramResult xauth(String username, String password)
        throws ConnectException, 
                AuthenticationException,
                ResponseException,
                InstagramMaintenanceException,
                ConvertException;
}
