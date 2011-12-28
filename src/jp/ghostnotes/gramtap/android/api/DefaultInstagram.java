package jp.ghostnotes.gramtap.android.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jp.ghostnotes.gramtap.android.api.convert.ConvertException;
import jp.ghostnotes.gramtap.android.api.convert.ConvertManager;
import jp.ghostnotes.gramtap.android.api.convert.ConvertType;
import jp.ghostnotes.gramtap.android.bean.AccessToken;
import jp.ghostnotes.gramtap.android.bean.InstagramImage;
import jp.ghostnotes.gramtap.android.bean.InstagramPagination;
import jp.ghostnotes.gramtap.android.bean.User;
import jp.ghostnotes.gramtap.android.util.GramtapConstants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.net.Uri;


/**
 * デフォルトデイズ。
 * 
 * @author taniguchi@paperboy.co.jp
 */
public class DefaultInstagram implements Instagram{
    /** APIフォーマット */
    private ApiFormat apiFormat = null;
    /** 変換マネージャ */
    private ConvertManager convertManager = null;

    /**
     * コンストラクタ。
     */
    public DefaultInstagram(){
        // JSON
        apiFormat = ApiFormat.JSON;
        // 変換マネージャ
        convertManager = new ConvertManager();
    }

//    public InstagramResult isAlreadyLiked(long loginUserId, String mediaId, String accessToken)
    public InstagramResult isAlreadyLiked(String mediaId, AccessToken accessToken)
        throws ConnectException, 
            AuthenticationException,
            ResponseException,
            InstagramMaintenanceException,
            ConvertException{
        String requestUrl = GramtapConstants.URL_LIKE.replace("{media-id}", mediaId);
        requestUrl += "?access_token=" + accessToken.getText();
        
        String responseJSON = requestJSONSSLFake(
            requestUrl,
            null,
            HttpRequestMethod.GET
        );
        
        User loginUser = accessToken.getUser();
        return (InstagramResult)convertManager.convert(ConvertType.AlreadyLiked, responseJSON, loginUser.getId());
    }

    
    public InstagramResult getLikeUserInformation(String mediaId, String accessToken)
        throws ConnectException, 
                AuthenticationException,
                ResponseException,
                InstagramMaintenanceException,
                ConvertException{
        String requestUrl = GramtapConstants.URL_LIKE.replace("{media-id}", mediaId);
        requestUrl += "?access_token=" + accessToken;
        
        String responseJSON = requestJSONSSLFake(
            requestUrl,
            null,
            HttpRequestMethod.GET
        );
        
        return (InstagramResult)convertManager.convert(ConvertType.LikeUserInfo, responseJSON);
    }

    
    public InstagramResult like(String mediaId, String accessToken)
        throws ConnectException,
            AuthenticationException, 
            ResponseException,
            InstagramMaintenanceException, 
            ConvertException{
        
        // いいねリクエストURL
        String requestUrl = GramtapConstants.URL_LIKE.replace("{media-id}", mediaId);

        String responseJSON = requestJSONSSLFake(
            requestUrl,
            "access_token=" + accessToken,
            HttpRequestMethod.POST
        );

        return (InstagramResult)convertManager.convert(ConvertType.Like, responseJSON);
    }
    
    public InstagramResult deleteLike(String mediaId, String accessToken)
        throws ConnectException,
            AuthenticationException, 
            ResponseException,
            InstagramMaintenanceException, 
            ConvertException{
        
        // いいね取消リクエストURL
        String requestUrl = GramtapConstants.URL_LIKE.replace("{media-id}", mediaId);
        requestUrl += "?access_token=" + accessToken;

        String responseJSON = requestJSONSSLFake(
            requestUrl,
            null,
            HttpRequestMethod.DELETE
        );
        
        return (InstagramResult)convertManager.convert(ConvertType.Like, responseJSON);
    }
    
    public InstagramResult xauth(String username, String password)
        throws ConnectException,
                AuthenticationException, 
                ResponseException,
                InstagramMaintenanceException, 
                ConvertException{
        
        String responseJSON = requestJSONSSLFake(
            "https://api.instagram.com/oauth/access_token/",
//            "client_id=3b23c1750cf94b5a817b54cd5e8ee415&client_secret=6469ec55b60c4700a9edfabeb6c318a7&grant_type=password&username=" + username + "&password=" + password + "&scope=likes",
            "client_id=" + GramtapConstants.CLIENT_ID + "&client_secret=" + GramtapConstants.CLIENT_SECRET + "&grant_type=password&username=" + username + "&password=" + password + "&scope=likes",
            
            
            HttpRequestMethod.POST
        );
        
        return (InstagramResult)convertManager.convert(ConvertType.XAuth, responseJSON);
    }
    
    
//    public InstagramResult getAccessToken(String code)
//        throws ConnectException,
//                AuthenticationException, 
//                ResponseException,
//                InstagramMaintenanceException, 
//                ConvertException{
//        
//        // https://api.instagram.com/oauth/access_token/?client_id=3b23c1750cf94b5a817b54cd5e8ee415&client_secret=6469ec55b60c4700a9edfabeb6c318a7&grant_type=authorization_code&redirect_uri=http://ghostnotes.jp&code=b15d78a0c06343418a3a1d8cee069c2d
//        
//        String responseJSON = requestJSONSSLFake(
//            "https://api.instagram.com/oauth/access_token/",
//            "client_id=3b23c1750cf94b5a817b54cd5e8ee415&client_secret=6469ec55b60c4700a9edfabeb6c318a7&grant_type=authorization_code&redirect_uri=http://ghostnotes.jp&code=" + code,
//            HttpRequestMethod.POST
//        );
//        
//        return (InstagramResult)convertManager.convert(ConvertType.AccessToken, responseJSON);
//    }
    

    public InstagramResult requestPagination(String url)
        throws ConnectException,
                AuthenticationException, 
                ResponseException,
                InstagramMaintenanceException, 
                ConvertException{
        
        String responseJSON = requestJSONSSLFake(
            url,
            null,
            HttpRequestMethod.GET
        );
        
        return (InstagramResult)convertManager.convert(ConvertType.Pagination, responseJSON);
    }
    
    public InstagramResult getSelfFeed(String accessToken)
        throws ConnectException,
                AuthenticationException, 
                ResponseException,
                InstagramMaintenanceException, 
                ConvertException{
        // https://api.instagram.com/v1/users/self/feed?access_token=202453.f59def8.5a2ee69b5a6c47cc8c120d40b3d0928f
        
        String responseJSON = requestJSONSSLFake(
            "https://api.instagram.com/v1/users/self/feed?access_token=" + accessToken,
            null,
            HttpRequestMethod.GET
        );

        return (InstagramResult)convertManager.convert(ConvertType.SelfFeed, responseJSON);
    }
    
    
    public InstagramResult popular(AccessToken accessToken) throws ConnectException,
                                              AuthenticationException, 
                                              ResponseException,
                                              InstagramMaintenanceException, 
                                              ConvertException{
        
//        String requestUrl = GramnotesConstants.URL_POPULAR + "?client_id=" + GramnotesConstants.CLIENT_ID;
//        String responseJSON = request(requestUrl);
        
        String responseJSON = requestJSONSSLFake(
//            GramtapConstants.URL_POPULAR, 
//            "client_id=" + GramtapConstants.CLIENT_ID,
            GramtapConstants.URL_POPULAR + "?client_id=" + GramtapConstants.CLIENT_ID,
            null,
            HttpRequestMethod.GET
        );
        
        return (InstagramResult)convertManager.convert(ConvertType.Popular, responseJSON);
//        InstagramResult popularResult = (InstagramResult)convertManager.convert(ConvertType.Popular, responseJSON);
//        InstagramPagination pagination = (InstagramPagination)popularResult.getResultObject();
//        if(accessToken == null || pagination == null || pagination.size() == 0){
//            return popularResult;
//        }
//        
//        User loginUser = accessToken.getUser();
//        for(int i = 0; i < pagination.size(); i++){
//            InstagramImage instaImage = pagination.getInstagramImage(i);
//            InstagramResult isAlreadyLikedResult = isAlreadyLiked(loginUser.getId(), instaImage.getId(), accessToken.getText());
//            boolean isAlreadyLiked = (Boolean)isAlreadyLikedResult.getResultObject();
//            if(isAlreadyLiked){
//                instaImage.addLikeUserId(loginUser.getId());
//            }
//        }
//        
//        return popularResult;
    }
    
    
    public InstagramResult prayForJapan() 
        throws ConnectException,
                AuthenticationException, 
                ResponseException,
                InstagramMaintenanceException, 
                ConvertException{
        
        String responseJSON = requestJSONSSLFake(
            GramtapConstants.URL_PRAY_FOR_JAPAN + "?client_id=" + GramtapConstants.CLIENT_ID, 
//            "client_id=" + GramtapConstants.CLIENT_ID, 
            null,
            HttpRequestMethod.GET
        );
        
        InstagramResult instagramResult = 
            (InstagramResult)convertManager.convert(ConvertType.PrayForJapan, responseJSON);
        
//        List<InstagramImage> imageList = (List<InstagramImage>)instagramResult.getResultObject();
        InstagramPagination instagramPagination = (InstagramPagination)instagramResult.getResultObject();
//        String nextUrl = instagramResult.getNextUrl();
//        String nextUrl = instagramPagination.getNextUrl();
//        
//        if(nextUrl != null && nextUrl.length() > 0){
////            int queryStartIndex = nextUrl.indexOf("?");
////            String requestUrl = nextUrl.substring(0, queryStartIndex - 1);
////            String query = nextUrl.substring(queryStartIndex + 1);
//            responseJSON = requestJSONSSLFake(
////                GramtapConstants.URL_PRAY_FOR_JAPAN, 
////                query, 
//                nextUrl,
//                null,
//                HttpRequestMethod.GET
//            );
//            
//            instagramResult = 
//                (InstagramResult)convertManager.convert(ConvertType.PrayForJapan, responseJSON);
//            
////            List<InstagramImage> secondImageList = (List<InstagramImage>)instagramResult.getResultObject();
//            InstagramPagination secondInstagramPagination = (InstagramPagination)instagramResult.getResultObject();
//
////            if(secondImageList.size() > 0){
//            if(secondInstagramPagination.size() > 0){
////                imageList.addAll(secondImageList);
//                
//                for(int i = 0; i < secondInstagramPagination.size(); i++){
//                    instagramPagination.addInstagramImage(secondInstagramPagination.getInstagramImage(i));
//                }
//            }
//        }
        
        InstagramResult ret = new InstagramResult();
//        ret.setResultObject(imageList);
        ret.setResultObject(instagramPagination);
        return ret;
    }

    private String request(String url)
        throws ConnectException, AuthenticationException, ResponseException, InstagramMaintenanceException{
        return request(url, null, HttpRequestMethod.GET);
    }

//    private String request(String url, List<NameValuePair> postData)
//        throws ConnectException, AuthenticationException, ResponseException, InstagramMaintenanceException{
//        return request(url, postData, HttpRequestMethod.POST);
//    }

    private String request(String url, List<NameValuePair> postData, HttpRequestMethod method) 
        throws ConnectException, AuthenticationException, ResponseException, InstagramMaintenanceException{
        switch(apiFormat){
        case JSON:
        default:
            // デフォルトAPIフォーマットは「JSON」
//            return requestJSON(url, method, postData);
            return requestJSONSSL(url, method, postData);
        }
    }
    private String requestJSONSSLFake(
        String url,
        String query,
        HttpRequestMethod method
    )throws ConnectException, AuthenticationException, ResponseException, InstagramMaintenanceException{
        String responseJSON = null;

        setHttpsURLConnectionDefaults();

        HttpsURLConnection con = null;
        BufferedReader reader = null;
        
        for(int i = 0; i < 5; i++){
            responseJSON = null;
            try{
//                con = (HttpsURLConnection)new URL(url + "?" + query).openConnection();
                con = (HttpsURLConnection)new URL(url).openConnection();
                
                switch(method){
                case POST:
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream());
                    osw.write(query);
                    osw.flush();
                    osw.close();
                    
                    break;
                case DELETE:
                    con.setRequestMethod("DELETE");
                    break;
                case GET:
                default:
                    con.setRequestMethod("GET");
                    break;
                }
                
                int responseCode = con.getResponseCode();
                String responseMessage = con.getResponseMessage();
    
                switch(responseCode){
                case HttpStatus.SC_OK:
                    // 成功
                    break;
                case HttpStatus.SC_BAD_REQUEST:
                    throw new ConnectException("bad request.");
                case HttpStatus.SC_UNAUTHORIZED:
                    // 認証失敗
                    throw new AuthenticationException(responseMessage);
                case HttpStatus.SC_NOT_FOUND:
                case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                    throw new ResponseException(responseCode, responseMessage);
                case HttpStatus.SC_SERVICE_UNAVAILABLE:
                    // メンテナンス中(または混雑)
                    throw new InstagramMaintenanceException(responseCode, responseMessage);
                default:
                    if(i < 4){
                        // リトライ
                        continue;
                    }else{
                        throw new ConnectException("request failed.");
                    }
                }

                reader =
                    new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                
                String buf = null;
                while((buf = reader.readLine()) != null){
                    if(responseJSON == null){
                        responseJSON = buf;
                    }else{
                        responseJSON += buf;
                    }
                }

                if(responseJSON != null && responseJSON.length() > 0){
                    // レスポンスを取得できた
                    break;
                }
                
                reader.close();
            }catch(MalformedURLException e){
                throw new ConnectException(e);
            }catch(IOException e){
                throw new ConnectException(e);
            }finally{
                if(reader != null){
                    try{
                        reader.close();
                    }catch(IOException e){
                    }
                    reader = null;
                }
                
                if(con != null){
                    con.disconnect();
                    con = null;
                }
            }
        }
        
        return responseJSON;
    }
    
    public static HostnameVerifier getHostnameVerifierAllowAllHosts() {
        return new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session){
                // 全て許可
                return true;
            }
        };
    }
    
    public static void setHttpsURLConnectionDefaults() {
        SSLContext sc = null; // SSLContext
        HostnameVerifier hv = null; // HostnameVerifier

        // SSLContext 作成
        try {
            sc = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SSLContext#getInstance: NoSuchAlgorithmException", e);
        }

        // SSLContext 初期化
        try {
            //System.out.println("DEBUG:SSLContext 初期化");
            sc.init(null, getTrustManagerAllowAllCerts(), null);
        } catch (KeyManagementException e) {
            throw new RuntimeException("SSLContext#init: KeyManagementException", e);
        }

        // HTTPSで使用する SSLソケットの作成(SSLSocketFactory) をセット
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // HostnameVerifier を作成
        hv = getHostnameVerifierAllowAllHosts();

        // HTTPSで使用する ホスト名の検証(HostnameVerifier) をセット
        // デフォルトの検証が失敗した場合(証明書の氏名[CN]が異なる場合など)に呼び出される
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
    
    public static TrustManager[] getTrustManagerAllowAllCerts() {
        // すべての証明書を受け付ける信頼マネージャ
        return new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                /*
                 * 認証するピアについて信頼されている、証明書発行局の証明書の配列を返します。
                 */
                // System.out.println("DEBUG:getAcceptedIssuers");
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                /*
                 * ピアから提出された一部のまたは完全な証明書チェーンを使用して、
                 * 信頼できるルートへの証明書パスを構築し、認証タイプに基づいて
                 * クライアント認証を検証できるかどうか、信頼できるかどうかを返します。
                 */
                // System.out.println("DEBUG:checkClientTrusted authType=" + authType);
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                /*
                 * ピアから提出された一部のまたは完全な証明書チェーンを使用して、
                 * 信頼できるルートへの証明書パスを構築し、認証タイプに基づいて
                 * サーバ認証を検証できるかどうか、また信頼できるかどうかを返します。
                 */
                // System.out.println("DEBUG:checkServerTrusted authType=" + authType);
            }
        } };
    }
    
    private String requestJSONSSL(
        String url,
        HttpRequestMethod method,
        List<NameValuePair> postData
    ) throws ConnectException, AuthenticationException, ResponseException, InstagramMaintenanceException{
        String responseJSON = "";

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
        schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));

        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.path(url);
        
        HttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry), params);
        HttpResponse res = null;
        switch(method){
        case POST:
            break;
        case GET:
        default:
//            HttpGet get = new HttpGet(uriBuilder.build().toString());
            HttpGet get = new HttpGet(url);
            
            
            try{
                res = client.execute(get);
            }catch(ClientProtocolException e){
                throw new ConnectException(e);
            }catch(IOException e){
                throw new ConnectException(e);
            }
            break;
        }

        // レスポンスコード
        int responseCode = res.getStatusLine().getStatusCode();
        
        switch(responseCode){
        case HttpStatus.SC_UNAUTHORIZED :
            // 認証失敗
            throw new AuthenticationException(res.getStatusLine().getReasonPhrase());
        case HttpStatus.SC_NOT_FOUND:
        case HttpStatus.SC_INTERNAL_SERVER_ERROR:
            throw new ResponseException(responseCode, res.getStatusLine().getReasonPhrase());
        case HttpStatus.SC_SERVICE_UNAVAILABLE:
            // メンテナンス中(または混雑)
            throw new InstagramMaintenanceException(responseCode, res.getStatusLine().getReasonPhrase());
        default:
            break;
        }
        
        try{
            // レスポンスJSONを取得
            HttpEntity entity = res.getEntity();
            responseJSON = EntityUtils.toString(entity);
        }catch(ParseException e){
            throw new ResponseException(e);
        }catch(IOException e){
            throw new ResponseException(e);
        }
    
        return responseJSON;
    }

    private String requestJSON(
        String url,
        HttpRequestMethod method,
        List<NameValuePair> postData
    )throws ConnectException, AuthenticationException, ResponseException, InstagramMaintenanceException{
        String responseJSON = "";
    
        HttpClient client = new DefaultHttpClient();
        HttpResponse res = null;
        switch(method){
        case POST:
            try{
                HttpPost post = new HttpPost(url);
                if(postData != null && postData.size() > 0){
                    // URLデコード
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData, HTTP.UTF_8);
                    post.setEntity(entity);
                }
                res = client.execute(post);
            }catch(UnsupportedEncodingException e){
                throw new ConnectException(e);
            }catch(ClientProtocolException e){
                throw new ConnectException(e);
            }catch(IOException e){
                throw new ConnectException(e);
            }
            break;
        case GET:
        default:
            HttpGet get = new HttpGet(url);
            try{
                res = client.execute(get);
            }catch(ClientProtocolException e){
                throw new ConnectException(e);
            }catch(IOException e){
                throw new ConnectException(e);
            }
            break;
        }
        
        // レスポンスコード
        int responseCode = res.getStatusLine().getStatusCode();
        
        switch(responseCode){
        case HttpStatus.SC_UNAUTHORIZED :
            // 認証失敗
            throw new AuthenticationException(res.getStatusLine().getReasonPhrase());
        case HttpStatus.SC_NOT_FOUND:
        case HttpStatus.SC_INTERNAL_SERVER_ERROR:
            throw new ResponseException(responseCode, res.getStatusLine().getReasonPhrase());
        case HttpStatus.SC_SERVICE_UNAVAILABLE:
            // メンテナンス中(または混雑)
            throw new InstagramMaintenanceException(responseCode, res.getStatusLine().getReasonPhrase());
        default:
            break;
        }
        
        try{
            // レスポンスJSONを取得
            HttpEntity entity = res.getEntity();
            responseJSON = EntityUtils.toString(entity);
        }catch(ParseException e){
            throw new ResponseException(e);
        }catch(IOException e){
            throw new ResponseException(e);
        }
    
        return responseJSON;
    }


}
