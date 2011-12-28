package jp.ghostnotes.gramtap.android.handler;

import jp.ghostnotes.gramtap.android.LayoutUtil;
import jp.ghostnotes.gramtap.android.api.InstagramResult;
import jp.ghostnotes.gramtap.android.bean.InstagramImage;
import jp.ghostnotes.gramtap.android.bean.LikeState;
import jp.ghostnotes.gramtap.android.util.GramtapConstants;
import jp.ghostnotes.gramtap.android.util.UserManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

public class AlreadyLikedCheckHandler extends Handler{
    private Context context = null;
    private Button buttonLike = null;
    private InstagramImage instagramImage = null;
    private Handler likeRequestHandler = null;
    
    public AlreadyLikedCheckHandler(
        Context context,
        Button buttonLike,
        InstagramImage instagramImage,
        Handler likeRequestHandler
    ){
        this.context = context;
        this.buttonLike = buttonLike;
        this.instagramImage = instagramImage;
        this.likeRequestHandler = likeRequestHandler;
    }
    
    public void handleMessage(Message message){
        Bundle data = message.getData();
        if(data.containsKey(GramtapConstants.API_RESULT_KEY_RESULT)){
            InstagramResult instaResult = 
                (InstagramResult)data.getSerializable(GramtapConstants.API_RESULT_KEY_RESULT);
            boolean isLiked = (Boolean)instaResult.getResultObject();
            if(isLiked){
                // ログインユーザが「いいね」していることを設定しておく
                instagramImage.addLikeUserId(UserManager.getInstance().getLoginUserId(context));
            }
            // いいねチェック済みフラグ
//            instagramImage.setLikeChecked(true);
            instagramImage.setLikeState(LikeState.Done);

//            View scrollView = InstagramImageDetailActivity.this.findViewById(R.id.image_detail_scroll_view);
            LayoutUtil.setLikeButton(instagramImage, buttonLike, likeRequestHandler, context);
        }else{
            // いいねボタン
//            Button buttonLike = (Button)findViewById(R.id.button_like);
            buttonLike.setVisibility(View.GONE);
        }
    }

}
