package jp.ghostnotes.gramtap.android.api.convert;

import jp.ghostnotes.gramtap.android.api.InstagramResult;
import jp.ghostnotes.gramtap.android.bean.Caption;
import jp.ghostnotes.gramtap.android.bean.Comment;
import jp.ghostnotes.gramtap.android.bean.InstagramImage;
import jp.ghostnotes.gramtap.android.bean.InstagramImageInformation;
import jp.ghostnotes.gramtap.android.bean.InstagramImageSize;
import jp.ghostnotes.gramtap.android.bean.InstagramPagination;
import jp.ghostnotes.gramtap.android.bean.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Instagram画像データ変換。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class InstagramPaginationConverter implements Converter{
    /** JSONキー:  */
    private static final String JSON_KEY_META = "meta";
    private static final String JSON_KEY_META_CODE = "code";
    private static final String JSON_KEY_PAGINATION =  "pagination";
    private static final String JSON_KEY_NEXT_URL = "next_url";
    private static final String JSON_KEY_NEXT_MAX_ID = "next_max_id";
    private static final String JSON_KEY_NEXT_MIN_ID = "next_min_id";
    private static final String JSON_KEY_DATA = "data";
    private static final String JSON_KEY_LINK = "link";
    private static final String JSON_KEY_IMAGES =  "images";
    private static final String JSON_KEY_THUMBNAIL = "thumbnail";
    private static final String JSON_KEY_LOW_RESOLUTION = "low_resolution";
    private static final String JSON_KEY_STANDARD_RESOLUTION = "standard_resolution";
    private static final String JSON_KEY_URL = "url";
    private static final String JSON_KEY_WIDTH = "width";
    private static final String JSON_KEY_HEIGHT = "height";
    private static final String JSON_KEY_COMMENTS =  "comments";
    private static final String JSON_KEY_TEXT = "text";
    private static final String JSON_KEY_FROM = "from";
    private static final String JSON_KEY_LIKES = "likes";
    private static final String JSON_KEY_COUNT = "count";
    private static final String JSON_KEY_CAPTION = "caption";
    private static final String JSON_KEY_CREATED_TIME = "created_time";
    private static final String JSON_KEY_USER = "user";
//    private static final String JSON_KEY_USER_ID = "id";
//    private static final String JSON_KEY_USERNAME = "username";
//    private static final String JSON_KEY_FULL_NAME = "full_name";
//    private static final String JSON_KEY_PROFILE_PICTURE = "profile_picture";

//    /** Instagram画像タイプJSONキー配列 */
//    private static final String[] IMAGE_TYPES = {
//        JSON_KEY_THUMBNAIL,
//        JSON_KEY_LOW_RESOLUTION,
//        JSON_KEY_STANDARD_RESOLUTION
//    };
    
    
    private InstagramImageInformation convertInstagramImageInformation(JSONObject imageObj, InstagramImageSize imageSize) throws JSONException{
        InstagramImageInformation imageInfo = new InstagramImageInformation();
        imageInfo.setImageSize(imageSize);
        imageInfo.setUrl(imageObj.getString(JSON_KEY_URL));
        imageInfo.setWidth(imageObj.getInt(JSON_KEY_WIDTH));
        imageInfo.setHeight(imageObj.getInt(JSON_KEY_HEIGHT));
        return imageInfo;
    }
    
    public Object convert(String str, Object data) throws ConvertException{
        if(str == null || str.length() == 0){
            throw new ConvertException("Instagram request failed.");
        }
        
        try{
            JSONObject jsonObj = new JSONObject(str);
            JSONObject metaObj = jsonObj.getJSONObject(JSON_KEY_META);
            // 結果コード
            int code = metaObj.getInt(JSON_KEY_META_CODE);
            
            InstagramPagination instagramPagination = new InstagramPagination();
            if(!jsonObj.isNull(JSON_KEY_PAGINATION)){
                JSONObject paginationObj = jsonObj.getJSONObject(JSON_KEY_PAGINATION);
                if(!paginationObj.isNull(JSON_KEY_NEXT_URL)){
                    instagramPagination.setNextUrl(paginationObj.getString(JSON_KEY_NEXT_URL));
                }
                
                if(!paginationObj.isNull(JSON_KEY_NEXT_MAX_ID)){
                    instagramPagination.setNextMaxId(paginationObj.getLong(JSON_KEY_NEXT_MAX_ID));
                }
                
                if(!paginationObj.isNull(JSON_KEY_NEXT_MIN_ID)){
                    instagramPagination.setNextMinId(paginationObj.getLong(JSON_KEY_NEXT_MIN_ID));
                }
            }
            
            // 画像の配列
            JSONArray dataArray = jsonObj.getJSONArray(JSON_KEY_DATA);
//            List<InstagramImage> imageList = new ArrayList<InstagramImage>();
            for(int i = 0; i < dataArray.length(); i++){
                JSONObject dataObj = dataArray.getJSONObject(i);
                
                // Instagram画像
                InstagramImage instagramImage = new InstagramImage();
                instagramImage.setId(dataObj.getString("id"));
                
                // リンク
                instagramImage.setLink(dataObj.getString(JSON_KEY_LINK));
                
                // 画像
                JSONObject imagesObj = dataObj.getJSONObject(JSON_KEY_IMAGES);

//                JSONObject imageObj = null;
//                for(int j = 0; j < IMAGE_TYPES.length; j++){
//                    imageObj = imagesObj.getJSONObject(IMAGE_TYPES[j]);

//                    InstagramImageInformation imageInfo = new InstagramImageInformation();
//                    
//                    imageInfo.setImageSize(InstagramImageSize.Thumbnail);
//                    imageInfo.setImageSize(InstagramImageSize.LowResolution);
//                    imageInfo.setImageSize(InstagramImageSize.StandardResolution);
//                    
//                    imageInfo.setUrl(imageObj.getString(JSON_KEY_URL));
//                    imageInfo.setWidth(imageObj.getInt(JSON_KEY_WIDTH));
//                    imageInfo.setHeight(imageObj.getInt(JSON_KEY_HEIGHT));
//                    instagramImage.addImageInformation(imageInfo);
//                }
                
                instagramImage.addImageInformation(
                    convertInstagramImageInformation(
                        imagesObj.getJSONObject(JSON_KEY_THUMBNAIL),
                        InstagramImageSize.Thumbnail
                    )
                );
                instagramImage.addImageInformation(
                    convertInstagramImageInformation(
                        imagesObj.getJSONObject(JSON_KEY_LOW_RESOLUTION),
                        InstagramImageSize.LowResolution
                    )
                );
                instagramImage.addImageInformation(
                    convertInstagramImageInformation(
                        imagesObj.getJSONObject(JSON_KEY_STANDARD_RESOLUTION),
                        InstagramImageSize.StandardResolution
                    )
                );
                
//                JSONObject imageObj = imagesObj.getJSONObject(JSON_KEY_THUMBNAIL);
//                InstagramImageInformation imageInfo = new InstagramImageInformation();
//                imageInfo.setImageType(InstagramImageSize.Thumbnail);
//                imageInfo.setUrl(imageObj.getString(JSON_KEY_URL));
//                imageInfo.setWidth(imageObj.getInt(JSON_KEY_WIDTH));
//                imageInfo.setHeight(imageObj.getInt(JSON_KEY_HEIGHT));
//                instagramImage.addImageInformation(imageInfo);
//
//                imageObj = imagesObj.getJSONObject(JSON_KEY_LOW_RESOLUTION);
//                imageInfo = new InstagramImageInformation();
//                imageInfo.setImageType(InstagramImageSize.LowResolution);
//                imageInfo.setUrl(imageObj.getString(JSON_KEY_URL));
//                imageInfo.setWidth(imageObj.getInt(JSON_KEY_WIDTH));
//                imageInfo.setHeight(imageObj.getInt(JSON_KEY_HEIGHT ));
//                instagramImage.addImageInformation(imageInfo);
//
//                imageObj = imagesObj.getJSONObject(JSON_KEY_STANDARD_RESOLUTION);
//                imageInfo = new InstagramImageInformation();
//                imageInfo.setImageType(InstagramImageSize.StandardResolution);
//                imageInfo.setUrl(imageObj.getString(JSON_KEY_URL));
//                imageInfo.setWidth(imageObj.getInt(JSON_KEY_WIDTH));
//                imageInfo.setHeight(imageObj.getInt(JSON_KEY_HEIGHT ));
//                instagramImage.addImageInformation(imageInfo);
                
                // コメント
                JSONObject commentDataObj = dataObj.getJSONObject(JSON_KEY_COMMENTS);
                JSONArray commentArray = commentDataObj.getJSONArray(JSON_KEY_DATA);
                if(commentArray != null && commentArray.length() > 0){
                    for(int commentIndex = 0; commentIndex < commentArray.length(); commentIndex++){
                        JSONObject commentObj = commentArray.getJSONObject(commentIndex);
                        // コメント本文
                        Comment comment = new Comment();
                        comment.setText(commentObj.getString(JSON_KEY_TEXT));
                        
                        // コメントのユーザ
                        JSONObject fromUserObj = commentObj.getJSONObject(JSON_KEY_FROM);
                        User fromUser = ConvertUtil.convertUser(fromUserObj);
                        comment.setFrom(fromUser);

                        instagramImage.addComment(comment);
                    }
                }
                
                // likes
                JSONObject likesObj = dataObj.getJSONObject(JSON_KEY_LIKES);
                if(!likesObj.isNull(JSON_KEY_COUNT)){
                    instagramImage.setLikeCount(likesObj.getInt(JSON_KEY_COUNT));
                }
                if(!likesObj.isNull(JSON_KEY_DATA)){
                    JSONArray likeDataArray = likesObj.getJSONArray(JSON_KEY_DATA);
                    for(int j = 0; j < likeDataArray.length(); j++){
                        JSONObject likeDataObj = likeDataArray.getJSONObject(j);
                        instagramImage.addLikeUserId(likeDataObj.getLong("id"));
                    }
                }
                
                // キャプション
                if(!dataObj.isNull(JSON_KEY_CAPTION)){
                    JSONObject captionObj = dataObj.getJSONObject(JSON_KEY_CAPTION);
                    if(!captionObj.isNull(JSON_KEY_TEXT)){
                        Caption caption = new Caption();
                        caption.setText(captionObj.getString(JSON_KEY_TEXT));
                        instagramImage.setCaption(caption);
                    }
                }

                // 作成日
                String createTime = dataObj.getString(JSON_KEY_CREATED_TIME);
                instagramImage.setCreateTime(ConvertUtil.convertDate(createTime));
                
                // ユーザ
                JSONObject userObj = dataObj.getJSONObject(JSON_KEY_USER);
//                User user = new User();
//                user.setId(Long.parseLong(userObj.getString(JSON_KEY_USER_ID)));
//                user.setUserName(userObj.getString(JSON_KEY_USERNAME));
//                if(!userObj.isNull(JSON_KEY_FULL_NAME)){
//                    user.setFullName(userObj.getString(JSON_KEY_FULL_NAME));
//                }
//                if(!userObj.isNull(JSON_KEY_PROFILE_PICTURE)){
//                    user.setProfilePicture(userObj.getString(JSON_KEY_PROFILE_PICTURE));
//                }
                
                User user = ConvertUtil.convertUser(userObj);
                instagramImage.setUser(user);
                
                // 画像リストに追加
                instagramPagination.addInstagramImage(instagramImage);
            }

            // Instagramリクエスト結果
            InstagramResult instagramResult = new InstagramResult();
            instagramResult.setCode(code);
            // Instagram結果に画像リストを設定
            instagramResult.setResultObject(instagramPagination);
            return instagramResult;
        }catch(JSONException e){
            throw new ConvertException(e);
        }
    }

}
