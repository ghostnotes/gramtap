package jp.ghostnotes.gramtap.android.widget;

import java.text.SimpleDateFormat;
import java.util.List;

import jp.ghostnotes.gramtap.android.LayoutUtil;
import jp.ghostnotes.gramtap.android.R;
import jp.ghostnotes.gramtap.android.bean.AccessToken;
import jp.ghostnotes.gramtap.android.bean.Caption;
import jp.ghostnotes.gramtap.android.bean.Comment;
import jp.ghostnotes.gramtap.android.bean.InstagramImage;
import jp.ghostnotes.gramtap.android.bean.InstagramImageSize;
import jp.ghostnotes.gramtap.android.bean.LikeState;
import jp.ghostnotes.gramtap.android.bean.User;
import jp.ghostnotes.gramtap.android.util.GramtapConstants;
import jp.ghostnotes.gramtap.android.util.GramtapUtil;
import jp.ghostnotes.gramtap.android.util.InstagramImageManager;
import jp.ghostnotes.gramtap.android.util.UserManager;
import jp.ghostnotes.gramtap.android.worker.AlreadyLikedCheckRequestThread;
import jp.ghostnotes.gramtap.android.worker.LikeRequest;
import jp.ghostnotes.gramtap.android.worker.LikeRequestThread;
import jp.ghostnotes.gramtap.android.worker.PhotoRequestThread;
import jp.ghostnotes.gramtap.android.worker.ProfilePictureRequestThread;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Instagram画像詳細アダプタ。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class InstagramImageDetailAdapter extends ArrayAdapter<InstagramImage>{
    /** もっと見る */
    private View seeMoreView = null;
    /** 日付フォーマット */
    private SimpleDateFormat dateFormat = null;
    /** ハンドラ */
    private Handler handler = null;

    /**
     * コンストラクタ。
     * 
     * @param context コンテキスト
     * @param textViewResourceId リソースID
     * @param handler ハンドラ
     */
    public InstagramImageDetailAdapter(Context context, int textViewResourceId, Handler handler){
        super(context, textViewResourceId);
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        
        this.handler = handler;
    }
    
    
    private View getSeeMoreView(){
        if(seeMoreView == null){
            seeMoreView = LayoutUtil.createView(
                getContext(), 
                R.layout.instagram_image_item_see_more
            );
        }
        return seeMoreView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final InstagramImage instagramImage = getItem(position);

        if(instagramImage.isDummy()){
            return getSeeMoreView();
        }
        
        View view = convertView;
        if(view == null || view == seeMoreView){
            view = LayoutUtil.createView(getContext(), R.layout.instagram_image_item_detail);
        }

        
        Caption caption = instagramImage.getCaption();
        TextView captionView = (TextView)view.findViewById(R.id.caption);
        if(caption != null){
            captionView.setText(caption.getText());
            captionView.setVisibility(View.VISIBLE);
        }else{
            captionView.setVisibility(View.GONE);
        }
        
        TextView createdTimeView = (TextView)view.findViewById(R.id.created_time);
        createdTimeView.setText(dateFormat.format(instagramImage.getCreateTime()));
        
        Bitmap instagramImageBitmap = InstagramImageManager.getInstance().getStandard(instagramImage.getLink());
//        Bitmap instagramImageBitmap = InstagramImageManager.getInstance().getLow(instagramImage.getLink());
        
        LinearLayout instaImageLayout = (LinearLayout)view.findViewById(R.id.instagram_image_layout);
        
        ImageView instagramImageView = (ImageView)view.findViewById(R.id.instagram_image);
        ProgressBar progresSpin = (ProgressBar)view.findViewById(R.id.progress_spin); 
        if(instagramImageBitmap != null){
            instagramImageView.setImageBitmap(instagramImageBitmap);
            progresSpin.setVisibility(View.GONE);
            instagramImageView.setVisibility(View.VISIBLE);
            instaImageLayout.setBackgroundColor(Color.WHITE);
        }else{
            instagramImageView.setVisibility(View.GONE);
            progresSpin.setVisibility(View.VISIBLE);
            instaImageLayout.setBackgroundColor(Color.BLACK);
            
            if(position % 4 == 0){
                new PhotoRequestThread(
                    instagramImage,
                    InstagramImageSize.StandardResolution,
                    handler
                ).start();
            }else{
                new PhotoRequestThread(
                    instagramImage,
                    InstagramImageSize.StandardResolution,
                    null
                ).start();
            }
        }
        
        // ユーザ取得
        User user = instagramImage.getUser();
        ImageView profilePictureView = (ImageView)view.findViewById(R.id.profile_picture);
        if(user.getProfilePicture() != null){
            Bitmap profilePicture = UserManager.getInstance().getProfilePicture(user);
            if(profilePicture != null){
                profilePictureView.setImageBitmap(profilePicture);
            }else{
                // いったんImageViewを空にしておく
                profilePictureView.setImageBitmap(null);
                
                // ユーザのプロフィール画像リクエスト開始
                new ProfilePictureRequestThread(
                    user, 
                    null
                ).start();
            }
        }

        // ユーザ名
        TextView usernameView = (TextView)view.findViewById(R.id.username);
        usernameView.setText(user.getUserName());

        // LIKEカウントを設定
        if(instagramImage.getLikeCount() > 0){
            TextView likeCountView = (TextView)view.findViewById(R.id.likes);
            likeCountView.setText("♥ " + instagramImage.getLikeCount());
        }
        
//        // いいねボタン
//        final Button buttonLike = (Button)view.findViewById(R.id.button_like);
//        if(instagramImage.isRequestingLike()){
//            // 
//            buttonLike.setVisibility(View.GONE);
//        }else{
//            if(instagramImage.isLiked(getContext())){
//                // いいね済み
//                buttonLike.setBackgroundColor(Color.WHITE);
//                buttonLike.setTextColor(Color.RED);
//            }else{
//                // 未いいね
//                buttonLike.setBackgroundColor(Color.GRAY);
//                buttonLike.setTextColor(Color.BLACK);
//            }
//            buttonLike.setVisibility(View.VISIBLE);
//            buttonLike.setEnabled(true);
//            
//            buttonLike.setOnClickListener(new View.OnClickListener(){
//                public void onClick(View v){
//                    buttonLike.setEnabled(false);
//                    
//                    String notifyMessage = null;
//                    LikeRequest likeRequest = null;
//                    if(instagramImage.isLiked(getContext())){
//                        // いいね取消リクエスト
//                        notifyMessage = getContext().getString(R.string.message_deleting_like);
//                        likeRequest = LikeRequest.Delete;
//                    }else{
//                        // いいねリクエスト
//                        notifyMessage = getContext().getString(R.string.message_requesting_like);
//                        likeRequest = LikeRequest.Like;
//                    }
//                    
//                    AccessToken accessToken = 
//                        UserManager.getInstance().getLoginUserAccessToken(getContext());
//                    
//                    new LikeRequestThread(
//                        instagramImage.getId(),
//                        likeRequest,
//                        accessToken.getText(),
//                        likeRequestHandler
//                    ).start();
//                    
//                    // いいねリクエスト開始をユーザに通知
//                    GramtapUtil.notify(notifyMessage, getContext());
//                }
//            });
//        }

        // いいねボタン
        final Button buttonLike = (Button)view.findViewById(R.id.button_like);
        final ProgressBar progressLike = (ProgressBar)view.findViewById(R.id.progress_like);
        
        AccessToken accessToken = UserManager.getInstance().getLoginUserAccessToken(getContext());
        long loginUserId = UserManager.getInstance().getLoginUserId(getContext());
        if(accessToken != null 
//            && (instagramImage.getLikeState() != LikeState.Done && instagramImage.getLikeState() != LikeState.Checking)
            && instagramImage.getLikeState() == LikeState.None
            && !instagramImage.isLiked(getContext())){
            
//            AlreadyLikedCheckHandler alreadyLikedCheckHandler = 
//                new AlreadyLikedCheckHandler(getContext(), buttonLike, instagramImage, likeRequestHandler); 
            
            // ボタンを隠す
            buttonLike.setVisibility(View.GONE);
            // プログレススピンを表示
            progressLike.setVisibility(View.VISIBLE);
//            instagramImage.setLikeChecked(true);
            // ログインユーザがいいねしたかチェック中とする
            instagramImage.setLikeState(LikeState.Checking);
            
            // いいねしたかリクエストしてチェック
            new AlreadyLikedCheckRequestThread(
                instagramImage.getId(),
                loginUserId,
                accessToken, 
                handler
            ).start();
            
            
        }else{
//            View scrollView = view.findViewById(R.id.image_detail_scroll_view);
//            LayoutUtil.setLikeButton(
//                instagramImage,
//                buttonLike,
//                likeRequestHandler,
//                getContext()
//            );

            switch(instagramImage.getLikeState()){
            case Checking:
            case Posting:
            case Deleting:
                // いいねチェック中
                buttonLike.setVisibility(View.GONE);
                progressLike.setVisibility(View.VISIBLE);
                break;
            case Done:
                // いいねチェック済み
                final Context context = getContext();
                if(UserManager.getInstance().isLoggedIn(context)){
                    if(instagramImage.isLiked(getContext())){
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
                            buttonLike.setVisibility(View.GONE);
                            progressLike.setVisibility(View.VISIBLE);
                            
                            String notifyMessage = null;
                            LikeRequest likeRequest = null;
                            if(instagramImage.isLiked(context)){
                                // いいね取消リクエスト
                                notifyMessage = getContext().getString(R.string.message_deleting_like);
                                likeRequest = LikeRequest.Delete;
                                instagramImage.setLikeState(LikeState.Deleting);
                            }else{
                                // いいねリクエスト
                                notifyMessage = context.getString(R.string.message_requesting_like);
                                likeRequest = LikeRequest.Like;
                                instagramImage.setLikeState(LikeState.Posting);
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
                
                progressLike.setVisibility(View.GONE);
                buttonLike.setVisibility(View.VISIBLE);
                break;
            }
        }

        // コメントをのせるレイアウト
        LinearLayout commentLayout = (LinearLayout)view.findViewById(R.id.comment_layout);
        commentLayout.removeAllViews();
        
        List<Comment> commentList = instagramImage.getCommentList();
        if(commentList.size() > 0){
            for(int i = 0; i < commentList.size(); i++){
                Comment comment = commentList.get(i);
                User commentUser = comment.getFrom();
                
                View commentView = LayoutUtil.createView(getContext(), R.layout.comment_item2);
                
                TextView commentUsernameView = (TextView)commentView.findViewById(R.id.comment_username);
                commentUsernameView.setText(commentUser.getUserName());
                
                TextView textView = (TextView)commentView.findViewById(R.id.comment);
                textView.setText(comment.getText());
                
                Bitmap commentUserProfilePicture = 
                    UserManager.getInstance().getProfilePicture(commentUser);
                
                if(commentUserProfilePicture != null){
                    ImageView commentProfilePictureView = 
                        (ImageView)commentView.findViewById(R.id.comment_user_profile_picture);
                    commentProfilePictureView.setImageBitmap(commentUserProfilePicture);
                }else{
                    // ユーザのプロフィール画像リクエスト開始
                    new ProfilePictureRequestThread(commentUser).start();
                }

                commentLayout.addView(commentView);
            }
        }
        
        return view;
    }

    private Handler likeRequestHandler = new Handler(){
        @Override
        public void handleMessage(Message message){
            Bundle data = message.getData();
            
            long loginUserId = 
                UserManager.getInstance().getLoginUserId(getContext());
            
            // いいねリクエスト種別
            LikeRequest likeRequest = 
                (LikeRequest)data.getSerializable(GramtapConstants.API_RESULT_KEY_LIKE_REQUEST);
            String imageId = data.getString(GramtapConstants.API_RESULT_KEY_LIKE_REQUEST_IMAGE_ID);
            InstagramImage instagramImage = InstagramImageManager.getInstance().getInstagramImage(imageId);

            if(data.containsKey(GramtapConstants.API_RESULT_KEY_RESULT)){
                switch(likeRequest){
                case Like:
                    // いいねリクエスト成功
                    // ログインユーザIDをInstagram画像に追加しておく
                    instagramImage.addLikeUserId(loginUserId);
                    // いいね数を1つ増やす
                    instagramImage.incrementLikeCount();
                    break;
                case Delete:
                    // いいね取消リクエスト成功
                    instagramImage.removeLikeUserId(loginUserId);
                    // いいね数を1つ減らす
                    instagramImage.decrementLikeCount();
                    break;
                }
            }else{
                // いいねリクエスト失敗
                String failedMessage = null;
                switch(likeRequest){
                case Like:
                    // 未いいね
                    // いいねリクエスト失敗
                    failedMessage = getContext().getString(
                        R.string.error_message_like_request_failed
                    );
                    break;
                case Delete:
                    // いいね済み
                    // いいね取消リクエスト失敗
                    failedMessage = getContext().getString(
                        R.string.error_message_delete_like_request_failed
                    );
                    break;
                }

                // ユーザにいいねリクエスト失敗を通知
                GramtapUtil.notify(failedMessage, getContext());
            }
            
            // いいねリクエスト終了
            instagramImage.setLikeState(LikeState.Done);
        }
    };

}
