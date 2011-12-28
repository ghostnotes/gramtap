package jp.ghostnotes.gramtap.android.widget;

import java.util.ArrayList;
import java.util.List;

import jp.ghostnotes.gramtap.android.InstagramImageDetailActivity;
import jp.ghostnotes.gramtap.android.LayoutUtil;
import jp.ghostnotes.gramtap.android.R;
import jp.ghostnotes.gramtap.android.bean.InstagramImage;
import jp.ghostnotes.gramtap.android.bean.InstagramImageSize;
import jp.ghostnotes.gramtap.android.util.GramtapConstants;
import jp.ghostnotes.gramtap.android.util.InstagramImageManager;
import jp.ghostnotes.gramtap.android.worker.MultiPhotoRequestThread;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class InstagramImageTileAdapter extends ArrayAdapter<InstagramImageTileItem>{
    /** 画面 */
    private Activity activity = null;
    /** レイアウトリソースID */
    private int resId = -1;
    
    private static final int[] IMAGEVIEW_ID_ARRAY = {
        R.id.image_00,
        R.id.image_01,
        R.id.image_02,
        R.id.image_03
    };
    
    private static final int[] PROGRESS_ID_ARRAY = {
        R.id.progress_00,
        R.id.progress_01,
        R.id.progress_02,
        R.id.progress_03
    };
    
    /** ハンドラ */
    private Handler handler = null;
    /** もっと見る */
    private View seeMoreView = null;
    /** プログレスビュー */
    private View progressView = null;
    
//    private ListItemLayoutType listItemLayoutType = ListItemLayoutType.Tile;
//    
//    public void setListItemLayoutType(ListItemLayoutType listItemLayoutType){
//        this.listItemLayoutType = listItemLayoutType;
//    }
//    
//    public ListItemLayoutType getListItemLayoutType(){
//        return listItemLayoutType;
//    }
    
    /**
     * コンストラクタ。
     * 
     * @param activity 画面
     * @param resId リソースID
     */
    public InstagramImageTileAdapter(
        Activity activity,
        int resId,
        Handler handler
    ){
        super(activity, resId);
        this.activity = activity;
        this.resId = resId;
        this.handler = handler;
    }

    /**
     * 
     * 
     * @return もっと見るView
     */
    private View getSeeMoreView(){
        if(seeMoreView == null){
            seeMoreView = LayoutUtil.createView(
                getContext(), 
                R.layout.instagram_image_item_see_more
            );
        }
        return seeMoreView;
    }
    
    /**
     * プログレスビューを取得する。
     * 
     * @return プログレスビュー
     */
    private View getProgressView(){
        if(progressView == null){
            progressView = LayoutUtil.createView(
                getContext(),
                R.layout.instagram_image_item_progress
            );
        }
        return progressView;
    }
    
//    private View createView(int layoutResId){
//        LayoutInflater inflater = LayoutInflater.from(getContext());
//        return inflater.inflate(layoutResId, null);
//    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        InstagramImageTileItem instagramImageItem = getItem(position);
        
        if(instagramImageItem.isDummy()){
            // もっと見るViewを返す
            return getSeeMoreView();
        }

        View view = convertView;
        if(view == null || view == seeMoreView || view == progressView){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(resId, null);
        }

        List<InstagramImage> emptyImageList = new ArrayList<InstagramImage>(instagramImageItem.size());
        for(int i = 0; i < instagramImageItem.size(); i++){
            
            InstagramImage instagramImage = instagramImageItem.getInstagramImage(i);
            if(instagramImage ==null){
                break;
            }
            
            ImageView instagramImageView = (ImageView)view.findViewById(IMAGEVIEW_ID_ARRAY[i]);
            instagramImageView.setOnClickListener(null);
            instagramImageView.setOnClickListener(new InstagramImageOnClickListener(instagramImage));
            
            
            ProgressBar progressSpinView = (ProgressBar)view.findViewById(PROGRESS_ID_ARRAY[i]);

            Bitmap thumbnailImage = InstagramImageManager.getInstance().getThumbnail(instagramImage.getLink());
            if(thumbnailImage != null){
                instagramImageView.setImageBitmap(thumbnailImage);
                
                progressSpinView.setVisibility(View.GONE);
                instagramImageView.setVisibility(View.VISIBLE);
            }else{
                progressSpinView.setVisibility(View.VISIBLE);
                instagramImageView.setVisibility(View.GONE);
                
                emptyImageList.add(instagramImage);
                
//                new PhotoRequestThread(
//                    instagramImage,
//                    InstagramImageSize.Thumbnail,
//                    -1,
//                    -1,
//                    null
//                ).start();
//                
//                if(i == instagramImageItem.size() - 1){
//                    handler.sendEmptyMessage(0);
//                }
            }
        }
        
        if(emptyImageList.size() > 0){
            new MultiPhotoRequestThread(
                emptyImageList,
                InstagramImageSize.Thumbnail,
                handler
            ).start();
        }

        return view;
    }

    private class InstagramImageOnClickListener implements OnClickListener{
        private InstagramImage instagramImage = null;
        public InstagramImageOnClickListener(InstagramImage instagramImage){
            this.instagramImage = instagramImage;
        }

        public void onClick(View v){
//            Toast.makeText(activity, "image: " + instagramImage.getCaption().getText(), Toast.LENGTH_SHORT).show();
            Intent instagramImageDetailIntent = new Intent(activity, InstagramImageDetailActivity.class);
            //instagramImageDetailIntent.putExtra(GramtapConstants.BUNDLE_KEY_IMAGE, instagramImage);
            instagramImageDetailIntent.putExtra(GramtapConstants.BUNDLE_KEY_IMAGE_ID, instagramImage.getId());
            activity.startActivity(instagramImageDetailIntent);
        }
    }


}
