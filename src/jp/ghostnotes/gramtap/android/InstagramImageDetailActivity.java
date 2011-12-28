package jp.ghostnotes.gramtap.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jp.ghostnotes.gramtap.android.api.InstagramResult;
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
import jp.ghostnotes.gramtap.android.worker.MultiProfilePictureRequestThread;
import jp.ghostnotes.gramtap.android.worker.PhotoRequestThread;
import jp.ghostnotes.gramtap.android.worker.ProfilePictureRequestThread;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Instagram画像詳細画面。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class InstagramImageDetailActivity extends Activity{
    /** Instagram画像データ */
    private InstagramImage instagramImage = null;
    /** 画像作成日フォーマット */
    private SimpleDateFormat dateFormat = null;
    /** コメントユーザリスト */
    private List<User> commentUserList = null;
    /** コメントユーザプロフィール画像Viewリスト */
    private List<ImageView> commentProfilePictureViewList = null;
    
    private void setLikeCountToView(){
        TextView likeCountView = (TextView)findViewById(R.id.likes);
        
        // LIKEカウントを設定
        if(instagramImage.getLikeCount() > 0){
            likeCountView.setText("♥ " + instagramImage.getLikeCount());
            likeCountView.setVisibility(View.VISIBLE);
        }else{
            likeCountView.setVisibility(View.GONE);
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        // 画面レイアウトXMLを設定
        setContentView(R.layout.instagram_image_detail);
        
        // 日付フォーマットを取得
        dateFormat = new SimpleDateFormat(GramtapConstants.DEFAULT_DATE_FORMAT);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            // ポピュラー画像情報
            String imageId = extras.getString(GramtapConstants.BUNDLE_KEY_IMAGE_ID);
            instagramImage = InstagramImageManager.getInstance().getInstagramImage(imageId);

            GramtapUtil.showProgress(this);

            // ポピュラー画像のユーザ
            User user = instagramImage.getUser();
            
            // ユーザ名
            TextView usernameView = (TextView)findViewById(R.id.username);
            usernameView.setText(user.getUserName());

            // LIKEカウントを設定
            if(instagramImage.getLikeCount() > 0){
                TextView likeCountView = (TextView)findViewById(R.id.likes);
                likeCountView.setText("♥ " + instagramImage.getLikeCount());
                likeCountView.setVisibility(View.GONE);
            }
//            setLikeCountToView();
            
            // ポピュラー画像(StandaradResolution)取得リクエスト開始
            new PhotoRequestThread(
                instagramImage,
                InstagramImageSize.StandardResolution,
                -1,
                -1, 
                popularImageHandler
            ).start();


            if(user.getProfilePicture() != null){
                // ユーザのプロフィール画像リクエスト開始
                new ProfilePictureRequestThread(
                    user, 
                    profilePictureHandler
                ).start();
            }
            
            // いいねボタン
            Button buttonLike = (Button)findViewById(R.id.button_like);
            buttonLike.setVisibility(View.GONE);
            
            AccessToken accessToken = UserManager.getInstance().getLoginUserAccessToken(this);
            long loginUserId = UserManager.getInstance().getLoginUserId(this);
            if(accessToken != null
                && instagramImage.getLikeState() == LikeState.None
                && !instagramImage.isLiked(this)){
                new AlreadyLikedCheckRequestThread(
                    instagramImage.getId(),
                    loginUserId,
                    accessToken, 
                    alreadyLikedCheckHandler
                ).start();
            }else{
//                View scrollView = InstagramImageDetailActivity.this.findViewById(R.id.image_detail_scroll_view);
                LayoutUtil.setLikeButton(
                    instagramImage,
                    buttonLike,
                    likeRequestHandler,
                    getApplicationContext()
                );
            }
        }
    }
    
//    private void setLikeButton(){
//        // いいねボタン
//        final Button buttonLike = (Button)findViewById(R.id.button_like);
//        if(UserManager.getInstance().isLoggedIn(InstagramImageDetailActivity.this)){
//            if(instagramImage.isLiked(InstagramImageDetailActivity.this)){
//                // いいね済み
//                buttonLike.setBackgroundColor(Color.WHITE);
//                buttonLike.setTextColor(Color.RED);
//            }else{
//                // 未いいね
//                buttonLike.setBackgroundColor(Color.GRAY);
//                buttonLike.setTextColor(Color.BLACK);
//            }
//            buttonLike.setVisibility(View.VISIBLE);
//            
//            buttonLike.setOnClickListener(new View.OnClickListener(){
//                public void onClick(View v){
//                    buttonLike.setEnabled(false);
//                    String notifyMessage = null;
//                    LikeRequest likeRequest = null;
//                    if(instagramImage.isLiked(InstagramImageDetailActivity.this)){
//                        // いいね取消リクエスト
//                        notifyMessage = getString(R.string.message_deleting_like);
//                        likeRequest = LikeRequest.Delete;
//                    }else{
//                        // いいねリクエスト
//                        notifyMessage = getString(R.string.message_requesting_like);
//                        likeRequest = LikeRequest.Like;
//                    }
//                    
//                    AccessToken accessToken = 
//                        UserManager.getInstance().getLoginUserAccessToken(InstagramImageDetailActivity.this);
//                    
//                    new LikeRequestThread(
//                        instagramImage.getId(),
//                        likeRequest,
//                        accessToken.getText(),
//                        likeRequestHandler
//                    ).start();
//                    
//                    // いいねリクエスト開始をユーザに通知
//                    GramtapUtil.notify(notifyMessage, InstagramImageDetailActivity.this);
//                }
//            });
//        }else{
//            // ログインしていないのでいいねボタンを見せない
//            buttonLike.setVisibility(View.GONE);
//        }
//    }

    private Handler alreadyLikedCheckHandler = new Handler(){
        @Override
        public void handleMessage(Message message){
            Bundle data = message.getData();
            if(data.containsKey(GramtapConstants.API_RESULT_KEY_RESULT)){
                InstagramResult instaResult = 
                    (InstagramResult)data.getSerializable(GramtapConstants.API_RESULT_KEY_RESULT);
                boolean isLiked = (Boolean)instaResult.getResultObject();
                if(isLiked){
                    instagramImage.addLikeUserId(UserManager.getInstance().getLoginUserId(InstagramImageDetailActivity.this));
                }
                
                // いいねチェック完了
                instagramImage.setLikeState(LikeState.Done);
                
//                View scrollView = InstagramImageDetailActivity.this.findViewById(R.id.image_detail_scroll_view);
                Button buttonLike = (Button)findViewById(R.id.button_like);
                LayoutUtil.setLikeButton(
                    instagramImage,
                    buttonLike,
                    likeRequestHandler,
                    getApplicationContext()
                );
            }else{
                // いいねボタン
                Button buttonLike = (Button)findViewById(R.id.button_like);
                buttonLike.setVisibility(View.GONE);
            }
        }
    };
        
    private Handler popularImageHandler = new Handler(){
        @Override
        public void handleMessage(Message message){
            // プログレスを閉じる
//            setProgressBarIndeterminateVisibility(false);
//            if(progressDialog != null){
//                progressDialog.dismiss();
//                progressDialog = null;
//            }
            GramtapUtil.closeProgress(InstagramImageDetailActivity.this);

            Bundle data = message.getData();
            if(data.containsKey(GramtapConstants.API_RESULT_KEY_INSTAGRAM_LINK)){
                // ポピュラー画像取得成功
                String instagramLink = data.getString(GramtapConstants.API_RESULT_KEY_INSTAGRAM_LINK);
                
                Bitmap popularImage = InstagramImageManager.getInstance().getStandard(instagramLink);
                ImageView popularImageView = (ImageView)findViewById(R.id.popular_image);
                popularImageView.setImageBitmap(popularImage);
                
                LinearLayout popularImageLayout = (LinearLayout)findViewById(R.id.popular_image_layout);
                TextView usernameView = (TextView)findViewById(R.id.username);
                ImageView profilePictureView = (ImageView)findViewById(R.id.profile_picture);
                TextView likeCountView = (TextView)findViewById(R.id.likes);

                // 画面のコンポーネントを表示できるように
                popularImageLayout.setVisibility(View.VISIBLE);
                profilePictureView.setVisibility(View.VISIBLE);
                usernameView.setVisibility(View.VISIBLE);
                likeCountView.setVisibility(View.VISIBLE);
                
                List<Comment> commentList = instagramImage.getCommentList();
                if(commentList != null && commentList.size() > 0){
                    LinearLayout commentLayout = (LinearLayout)findViewById(R.id.comment_layout);
                    commentUserList = new ArrayList<User>(commentList.size());
                    commentProfilePictureViewList = new ArrayList<ImageView>(commentList.size());
                    
                    for(int i = 0; i < commentList.size(); i++){
                        Comment comment = commentList.get(i);
                        User commentUser = comment.getFrom();
                        commentUserList.add(commentUser);
                        
//                        LinearLayout out = new LinearLayout(PopularActivity.this);
//                        
//                        LinearLayout inner = new LinearLayout(PopularActivity.this);
//                        inner.setBackgroundColor(Color.RED);
//                        out.addView(inner, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                        
                        
//                        line.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
//                        line.setBackgroundColor(Color.RED);
//                        line.setMinimumHeight(10);
//                        commentLayout.addView(out);

                        View commentView = LayoutUtil.createView(InstagramImageDetailActivity.this, R.layout.comment_item2);
                        
                        ImageView commentProfilePictureView = (ImageView)commentView.findViewById(R.id.comment_user_profile_picture);
                        commentProfilePictureViewList.add(commentProfilePictureView);

//                        TextView textView = new TextView(PopularActivity.this);
                        TextView commentUsernameView = (TextView)commentView.findViewById(R.id.comment_username);
                        commentUsernameView.setText(commentUser.getUserName());
                        
                        TextView textView = (TextView)commentView.findViewById(R.id.comment);
                        textView.setText(comment.getText());
                        commentLayout.addView(commentView);
                    }
                    
                    // ユーザのプロフィール画像リクエスト開始
                    new MultiProfilePictureRequestThread(
                        commentUserList, 
                        commentProfilePictureHandler
                    ).start();
                }

                // キャプション
                Caption caption = instagramImage.getCaption();
                if(caption != null){
                    TextView captionView = (TextView)findViewById(R.id.caption);
                    captionView.setText(caption.getText());
                    captionView.setVisibility(View.VISIBLE);
                }
                
                // 作成日時
                String createTimeStr = dateFormat.format(instagramImage.getCreateTime());
                TextView createTimeView = (TextView)findViewById(R.id.created_time);
                createTimeView.setText(createTimeStr);
                
//                // いいねボタン
//                final Button buttonLike = (Button)findViewById(R.id.button_like);
//                if(UserManager.getInstance().isLoggedIn(InstagramImageDetailActivity.this)){
//                    if(instagramImage.isLiked(InstagramImageDetailActivity.this)){
//                        // いいね済み
//                        buttonLike.setBackgroundColor(Color.WHITE);
//                        buttonLike.setTextColor(Color.RED);
//                    }else{
//                        // 未いいね
//                        buttonLike.setBackgroundColor(Color.GRAY);
//                        buttonLike.setTextColor(Color.BLACK);
//                    }
//                    buttonLike.setVisibility(View.VISIBLE);
//                    
//                    buttonLike.setOnClickListener(new View.OnClickListener(){
//                        public void onClick(View v){
//                            buttonLike.setEnabled(false);
//                            String notifyMessage = null;
//                            LikeRequest likeRequest = null;
//                            if(instagramImage.isLiked(InstagramImageDetailActivity.this)){
//                                // いいね取消リクエスト
//                                notifyMessage = getString(R.string.message_deleting_like);
//                                likeRequest = LikeRequest.Delete;
//                            }else{
//                                // いいねリクエスト
//                                notifyMessage = getString(R.string.message_requesting_like);
//                                likeRequest = LikeRequest.Like;
//                            }
//                            
//                            AccessToken accessToken = 
//                                UserManager.getInstance().getLoginUserAccessToken(InstagramImageDetailActivity.this);
//                            
//                            new LikeRequestThread(
//                                instagramImage.getId(),
//                                likeRequest,
//                                accessToken.getText(),
//                                likeRequestHandler
//                            ).start();
//                            
//                            // いいねリクエスト開始をユーザに通知
//                            GramtapUtil.notify(notifyMessage, InstagramImageDetailActivity.this);
//                        }
//                    });
//                }else{
//                    // ログインしていないのでいいねボタンを見せない
//                    buttonLike.setVisibility(View.GONE);
//                }
            }else if(data.containsKey(GramtapConstants.API_RESULT_KEY_EXCEPTION)){
                // 例外発生
//                Exception e = (Exception)data.get(GramtapConstants.API_RESULT_KEY_EXCEPTION);
//                Toast.makeText(PopularActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(
                    InstagramImageDetailActivity.this, 
                    R.string.error_message_instagram_request_failed, 
                    Toast.LENGTH_SHORT
                ).show();
            }else{
                // 予期せぬエラー
                Toast.makeText(
                    InstagramImageDetailActivity.this,
                    R.string.error_message_unexpected_error, 
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
    };

    private Handler likeRequestHandler = new Handler(){
        @Override
        public void handleMessage(Message message){
            Bundle data = message.getData();
            
            long loginUserId = 
                UserManager.getInstance().getLoginUserId(InstagramImageDetailActivity.this);
            
            // いいねリクエスト種別
            LikeRequest likeRequest = 
                (LikeRequest)data.getSerializable(GramtapConstants.API_RESULT_KEY_LIKE_REQUEST);

            // いいねボタン
            Button buttonLike = (Button)findViewById(R.id.button_like);
            if(data.containsKey(GramtapConstants.API_RESULT_KEY_RESULT)){
                // いいねリクエスト成功
                switch(likeRequest){
                case Like:
                    // いいね済み
                    buttonLike.setBackgroundColor(Color.WHITE);
                    buttonLike.setTextColor(Color.RED);
                    
                    instagramImage.addLikeUserId(loginUserId);
                    instagramImage.incrementLikeCount();
                    break;
                case Delete:
                    // 未いいね
                    buttonLike.setBackgroundColor(Color.GRAY);
                    buttonLike.setTextColor(Color.BLACK);
                    
                    instagramImage.removeLikeUserId(loginUserId);
                    instagramImage.decrementLikeCount();
                    break;
                }
                
                // いいねのカウントに反映
                setLikeCountToView();
            }else{
                // いいねリクエスト失敗
                String failedMessage = null;
                switch(likeRequest){
                case Like:
                    // 未いいね
                    buttonLike.setBackgroundColor(Color.GRAY);
                    buttonLike.setTextColor(Color.BLACK);
                    // いいねリクエスト失敗
                    failedMessage = getString(R.string.error_message_like_request_failed);
                    break;
                case Delete:
                    // いいね済み
                    buttonLike.setBackgroundColor(Color.WHITE);
                    buttonLike.setTextColor(Color.RED);
                    // いいね取消リクエスト失敗
                    failedMessage = getString(R.string.error_message_delete_like_request_failed);
                    break;
                }
                
                // ユーザにいいねリクエスト失敗を通知
                GramtapUtil.notify(failedMessage, InstagramImageDetailActivity.this);
            }
            
            instagramImage.setLikeState(LikeState.Done);
            buttonLike.setEnabled(true);
        }
    };
    
    /**
     * プロフィール画像設定ハンドラ。
     */
    private Handler profilePictureHandler = new Handler(){
        @Override
        public void handleMessage(Message message){
            Bundle data = message.getData();
            if(data.containsKey(GramtapConstants.API_RESULT_KEY_USER)){
                User user = (User)data.get(GramtapConstants.API_RESULT_KEY_USER);
                Bitmap profilePicture = UserManager.getInstance().getProfilePicture(user);
                
                ImageView profilePictureView = (ImageView)findViewById(R.id.profile_picture);
                profilePictureView.setImageBitmap(profilePicture);
            }
        }
    };
    
    private Handler commentProfilePictureHandler = new Handler(){
        @Override
        public void handleMessage(Message message){
            for(int i = 0; i < commentUserList.size(); i++){
                User user = commentUserList.get(i);
                Bitmap profilePicture = UserManager.getInstance().getProfilePicture(user);
                
                ImageView profilePictureView = commentProfilePictureViewList.get(i);
                profilePictureView.setImageBitmap(profilePicture);
            }
        }
    };
    
    
}
