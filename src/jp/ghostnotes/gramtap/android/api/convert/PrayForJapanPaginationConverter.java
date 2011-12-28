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
 * #prayforjapan変換クラス。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class PrayForJapanPaginationConverter implements Converter{

    public Object convert(String str, Object data) throws ConvertException{
        if(str == null || str.length() == 0){
            throw new ConvertException("Instagram request failed.");
        }

        InstagramResult instagramResult = new InstagramResult();
        try{
            JSONObject jsonObj = new JSONObject(str);
            
            InstagramPagination instagramPagination = new InstagramPagination();
            
            // ページング
            if(!jsonObj.isNull("pagination")){
                JSONObject paginationObj = jsonObj.getJSONObject("pagination");
                if(!paginationObj.isNull("next_url")){
                    instagramPagination.setNextUrl(paginationObj.getString("next_url"));
                }
                
                if(!paginationObj.isNull("next_max_id")){
                    instagramPagination.setNextMaxId(paginationObj.getLong("next_max_id"));
                }
                
                if(!paginationObj.isNull("next_min_id")){
                    instagramPagination.setNextMinId(paginationObj.getLong("next_min_id"));
                }
            }
            
            JSONObject metaObj = jsonObj.getJSONObject("meta");
            int code = metaObj.getInt("code");
            instagramResult.setCode(code);
            
            // 画像の配列
            JSONArray dataArray = jsonObj.getJSONArray("data");
//            List<InstagramImage> imageList = new ArrayList<InstagramImage>();
            for(int i = 0; i < dataArray.length(); i++){
                JSONObject dataObj = dataArray.getJSONObject(i);
                
                // ポピュラー画像
                InstagramImage prayForJapanImage = new InstagramImage();
                prayForJapanImage.setId(dataObj.getString("id"));
                
                // リンク
                prayForJapanImage.setLink(dataObj.getString("link"));
                
                // 画像
                JSONObject imagesObj = dataObj.getJSONObject("images");
                
                // サムネイル
                JSONObject imageObj = imagesObj.getJSONObject("thumbnail");
                InstagramImageInformation imageInfo = new InstagramImageInformation();
                imageInfo.setImageSize(InstagramImageSize.Thumbnail);
                imageInfo.setUrl(imageObj.getString("url"));
                imageInfo.setWidth(imageObj.getInt("width"));
                imageInfo.setHeight(imageObj.getInt("height"));
                prayForJapanImage.addImageInformation(imageInfo);

                // 画像(小)
                imageObj = imagesObj.getJSONObject("low_resolution");
                imageInfo = new InstagramImageInformation();
                imageInfo.setImageSize(InstagramImageSize.LowResolution);
                imageInfo.setUrl(imageObj.getString("url"));
                imageInfo.setWidth(imageObj.getInt("width"));
                imageInfo.setHeight(imageObj.getInt("height"));
                prayForJapanImage.addImageInformation(imageInfo);

                // 画像(スタンダード)
                imageObj = imagesObj.getJSONObject("standard_resolution");
                imageInfo = new InstagramImageInformation();
                imageInfo.setImageSize(InstagramImageSize.StandardResolution);
                imageInfo.setUrl(imageObj.getString("url"));
                imageInfo.setWidth(imageObj.getInt("width"));
                imageInfo.setHeight(imageObj.getInt("height"));
                prayForJapanImage.addImageInformation(imageInfo);
                
                // コメント
                JSONObject commentDataObj = dataObj.getJSONObject("comments");
                JSONArray commentArray = commentDataObj.getJSONArray("data");
                if(commentArray != null && commentArray.length() > 0){
                    for(int commentIndex = 0; commentIndex < commentArray.length(); commentIndex++){
                        JSONObject commentObj = commentArray.getJSONObject(commentIndex);
                        
                        Comment comment = new Comment();
                        comment.setText(commentObj.getString("text"));
                        
                        // コメントのユーザ
                        JSONObject fromUserObj = commentObj.getJSONObject("from");
                        User fromUser = ConvertUtil.convertUser(fromUserObj);
                        comment.setFrom(fromUser);

                        prayForJapanImage.addComment(comment);
                    }
                }
                
                // likes
                JSONObject likesObj = dataObj.getJSONObject("likes");
                if(!likesObj.isNull("count")){
                    prayForJapanImage.setLikeCount(likesObj.getInt("count"));
                }
                if(!likesObj.isNull("data")){
                    JSONArray likeDataArray = likesObj.getJSONArray("data");
                    for(int j = 0; j < likeDataArray.length(); j++){
                        JSONObject likeDataObj = likeDataArray.getJSONObject(j);
                        prayForJapanImage.addLikeUserId(likeDataObj.getLong("id"));
                    }
                }
                
                // キャプション
                if(!dataObj.isNull("caption")){
                    JSONObject captionObj = dataObj.getJSONObject("caption");
                    if(!captionObj.isNull("text")){
                        Caption caption = new Caption();
                        caption.setText(captionObj.getString("text"));
                        prayForJapanImage.setCaption(caption);
                    }
                }

                // 作成日
                String createTime = dataObj.getString("created_time");
                prayForJapanImage.setCreateTime(ConvertUtil.convertDate(createTime));
                
                // ユーザ
                JSONObject userObj = dataObj.getJSONObject("user");
//                User user = new User();
//                user.setId(Long.parseLong(userObj.getString("id")));
//                user.setUserName(userObj.getString("username"));
//                if(!userObj.isNull("full_name")){
//                    user.setFullName(userObj.getString("full_name"));
//                }
//                if(!userObj.isNull("profile_picture")){
//                    user.setProfilePicture(userObj.getString("profile_picture"));
//                }
                
                User user = ConvertUtil.convertUser(userObj);
                prayForJapanImage.setUser(user);
                
                // 画像リストに追加
//                imageList.add(popularImage);
                instagramPagination.addInstagramImage(prayForJapanImage);
            }

            // Instagram結果に画像リストを設定
//            instagramResult.setResultObject(imageList);
            instagramResult.setResultObject(instagramPagination);
            return instagramResult;
        }catch(JSONException e){
            throw new ConvertException(e);
        }
    }

}
