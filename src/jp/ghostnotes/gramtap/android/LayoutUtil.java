package jp.ghostnotes.gramtap.android;

import jp.ghostnotes.gramtap.android.bean.AccessToken;
import jp.ghostnotes.gramtap.android.bean.InstagramImage;
import jp.ghostnotes.gramtap.android.util.GramtapUtil;
import jp.ghostnotes.gramtap.android.util.UserManager;
import jp.ghostnotes.gramtap.android.worker.LikeRequest;
import jp.ghostnotes.gramtap.android.worker.LikeRequestThread;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * レイアウトユーティリティ。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class LayoutUtil{
    
    public static View createView(Context context, int layoutResId){
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(layoutResId, null);
    }
    
    public static void setLikeButton(
        final InstagramImage instagramImage,
        final Button buttonLike,
        final Handler likeRequestHandler,
        final Context context
    ){
        // いいねボタン
//        final Button buttonLike = (Button)view.findViewById(R.id.button_like);
        if(UserManager.getInstance().isLoggedIn(context)){
            if(instagramImage.isLiked(context)){
                // いいね済み
                buttonLike.setBackgroundColor(Color.WHITE);
                buttonLike.setTextColor(Color.RED);
            }else{
                // 未いいね
                buttonLike.setBackgroundColor(Color.GRAY);
                buttonLike.setTextColor(Color.BLACK);
            }
            buttonLike.setVisibility(View.VISIBLE);
            
            buttonLike.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    
//                    buttonLike.setEnabled(false);
                    
                    
                    String notifyMessage = null;
                    LikeRequest likeRequest = null;
                    if(instagramImage.isLiked(context)){
                        // いいね取消リクエスト
                        notifyMessage = context.getString(R.string.message_deleting_like);
                        likeRequest = LikeRequest.Delete;
                    }else{
                        // いいねリクエスト
                        notifyMessage = context.getString(R.string.message_requesting_like);
                        likeRequest = LikeRequest.Like;
                    }
                    
                    AccessToken accessToken = 
                        UserManager.getInstance().getLoginUserAccessToken(context);
                    
                    new LikeRequestThread(
                        instagramImage.getId(),
                        likeRequest,
                        accessToken.getText(),
                        likeRequestHandler
                    ).start();
                    
                    // いいねリクエスト開始をユーザに通知
                    GramtapUtil.notify(notifyMessage, context);
                }
            });
        }else{
            // ログインしていないのでいいねボタンを見せない
            buttonLike.setVisibility(View.GONE);
        }
    }


    
//    public static View createCommentView(){
//        View commentView = createView(R.layout.comment_item2);
//        
//        ImageView commentProfilePictureView = (ImageView)commentView.findViewById(R.id.comment_user_profile_picture);
//        commentProfilePictureViewList.add(commentProfilePictureView);
//
////        TextView textView = new TextView(PopularActivity.this);
//        TextView commentUsernameView = (TextView)commentView.findViewById(R.id.comment_username);
//        commentUsernameView.setText(commentUser.getUserName());
//        
//        TextView textView = (TextView)commentView.findViewById(R.id.comment);
//        textView.setText(comment.getText());
//        
//        return commentView;
//    }
}
