package jp.ghostnotes.gramtap.android;

import jp.ghostnotes.gramtap.android.api.InstagramResult;
import jp.ghostnotes.gramtap.android.bean.AccessToken;
import jp.ghostnotes.gramtap.android.bean.InstagramImage;
import jp.ghostnotes.gramtap.android.bean.InstagramImageSize;
import jp.ghostnotes.gramtap.android.bean.InstagramPagination;
import jp.ghostnotes.gramtap.android.util.GramtapConstants;
import jp.ghostnotes.gramtap.android.util.GramtapUtil;
import jp.ghostnotes.gramtap.android.util.InstagramImageManager;
import jp.ghostnotes.gramtap.android.util.UserManager;
import jp.ghostnotes.gramtap.android.widget.InstagramImageDetailAdapter;
import jp.ghostnotes.gramtap.android.widget.InstagramImageTileAdapter;
import jp.ghostnotes.gramtap.android.widget.InstagramImageTileItem;
import jp.ghostnotes.gramtap.android.widget.ListItemLayoutType;
import jp.ghostnotes.gramtap.android.worker.InstagramPaginationRequestThread;
import jp.ghostnotes.gramtap.android.worker.PopularDataRequestThread;
import jp.ghostnotes.gramtap.android.worker.PrayForJapanDataRequestThread;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Instagram画像画面。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class InstagramImageListActivity extends Activity{
    /** メニューID: 更新 */
    private static final int MENU_ID_REFRESH = Menu.FIRST + 1;
//    /** メニューID: #prayforjapan */
//    private static final int MENU_ID_PRAY_FOR_JAPAN = Menu.FIRST + 2;
//    /** メニューID: ポピュラー */
//    private static final int MENU_ID_POPULAR = Menu.FIRST + 3;
    /** メニューID: 設定 */
    private static final int MENU_ID_SETTINGS = Menu.FIRST + 4;
    /** メニューID: タイルリスト */
    private static final int MENU_ID_LIST_LAYOUT_TILE = Menu.FIRST + 5;
    /** メニューID: 詳細リスト */
    private static final int MENU_ID_LIST_LAYOUT_DETAIL = Menu.FIRST + 6;

    /** Instagram画像ListView */
//    private ListView instagramImageListTile = null;
//    private ListView instagramImageListDetail = null;
    private ListView instagramImageList = null;
    
    /** Instagram画像タイルアダプタ */
    private InstagramImageTileAdapter instagramImageTileAdapter = null;
    /** Instagram画像詳細アダプタ */
    private InstagramImageDetailAdapter instagramImageDetailAdapter = null;

    /** アクセストークン */
    private AccessToken accessToken = null;
    /** 現在のInstagram画像タイプ */
    private InstagramImageType currentInstagramImageType = null;
    private ListItemLayoutType currentListItemLayoutType = ListItemLayoutType.Tile;


    private void innerChangeButtonClickColor(boolean isOn, Button button){
        button.setTextColor(isOn ? Color.BLACK : Color.WHITE);
        button.setBackgroundColor(isOn ? Color.WHITE : Color.BLACK);
    }
    
    private void changeButtonClickColor(){
        // feedボタン
        Button buttonFeed = (Button)findViewById(R.id.button_feed);
        // popularボタン
        Button buttonPopular = (Button)findViewById(R.id.button_popular);
        // #prayforjapanボタン
        Button buttonPrayForJapan = (Button)findViewById(R.id.button_prayforjapan1);

        innerChangeButtonClickColor(currentInstagramImageType == InstagramImageType.SelfFeed, buttonFeed);
        innerChangeButtonClickColor(currentInstagramImageType == InstagramImageType.Popular, buttonPopular);
        innerChangeButtonClickColor(currentInstagramImageType == InstagramImageType.PrayForJapan, buttonPrayForJapan);
    }
    
//    private TextView mainNoDataTextView = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.instagram_image_list);
        
//        mainNoDataTextView = (TextView)findViewById(R.id.instagram_image_empty);
//        mainNoDataTextView.setVisibility(View.VISIBLE);
        
        String accessTokenString = GramtapUtil.getAccessToken(this);
        boolean isAuthAllScope = GramtapUtil.isAuthAllScope(this);

        if(accessTokenString != null && !isAuthAllScope){
            // いいねAPIの認証を済ませていない
            GramtapUtil.removeAccessToken(this);
            
            // 再ログイン
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_title_please_login_again);
            builder.setMessage(R.string.message_app_uses_new_feature);
            builder.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    // 設定画面を起動
                    startSettingsActivity();
                }
            });
            builder.setNegativeButton(R.string.button_no, null);
            builder.create().show();
        }
        
        // feedボタン
        final Button buttonFeed = (Button)findViewById(R.id.button_feed);
        // popularボタン
        final Button buttonPopular = (Button)findViewById(R.id.button_popular);
        // #prayforjapanボタン
        final Button buttonPrayForJapan = (Button)findViewById(R.id.button_prayforjapan1);

        // feedボタンクリックイベント
        buttonFeed.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                if(currentInstagramImageType != InstagramImageType.SelfFeed){
                    if(accessToken != null){
                        // ログインしている
                        currentInstagramImageType = InstagramImageType.SelfFeed;
                        currentListItemLayoutType = ListItemLayoutType.Detail;
                        
                        // ボタンの色を変更
                        changeButtonClickColor();
                        
                        // Instagram画像をリフレッシュ
                        showInstagramImage();
                    }else{
                        // ログインしていない
                        AlertDialog.Builder builder = new AlertDialog.Builder(InstagramImageListActivity.this);
                        builder.setTitle(R.string.dialog_title_login);
                        builder.setMessage(R.string.message_login_to_intagram);
                        builder.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which){
                                // 設定画面に遷移
                                Intent settingsIntent = new Intent(InstagramImageListActivity.this, SettingsActivity.class);
                                startActivity(settingsIntent);
                            }
                        });
                        builder.setNegativeButton(R.string.button_no, null);
                        builder.create().show();
                    }
                }
            }
        });

        // ポピュラーボタンクリックイベント
        buttonPopular.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                if(currentInstagramImageType != InstagramImageType.Popular){
                    currentInstagramImageType = InstagramImageType.Popular;

                    changeButtonClickColor();                    
                    
                    // Instagram画像を表示
                    showInstagramImage();
                }
            }
        });

        // #prayforjapanボタンクリックイベント
        buttonPrayForJapan.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                if(currentInstagramImageType != InstagramImageType.PrayForJapan){
                    currentInstagramImageType = InstagramImageType.PrayForJapan;

                    changeButtonClickColor();
                    
                    // Instagram画像をリフレッシュ
                    showInstagramImage();
                }
            }
        });

        TextView noDataView = new TextView(this);
        noDataView.setText(R.string.message_no_data);
        noDataView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 50f));
        noDataView.setGravity(Gravity.CENTER);
        noDataView.setVisibility(View.GONE);

        // タイルレイアウト
//        currentListItemLayoutType = ListItemLayoutType.Tile;
//        instagramImageListTile = (ListView)findViewById(R.id.list_instagram_image_tile);
//        instagramImageListTile.setEmptyView(noDataTextView);
        
        instagramImageList = (ListView)findViewById(R.id.list_instagram_image);
//      ViewGroup vg = (ViewGroup)instagramImageListTile.getParent();
        ViewGroup vg = (ViewGroup)instagramImageList.getParent();
//        vg.addView(noDataView);
        vg.addView(noDataView, 0);
//        instagramImageListTile.setEmptyView(noDataView);
        instagramImageList.setEmptyView(noDataView);
        
        instagramImageTileAdapter = new InstagramImageTileAdapter(
            this,
            R.layout.instagram_image_item_tile,
            tileListViewRefreshHandler
        );
//        instagramImageListTile.setAdapter(instagramImageTileAdapter);
//        instagramImageList.setAdapter(instagramImageTileAdapter);
//        instagramImageListTile.setOnItemClickListener(new OnItemClickListener(){
        instagramImageList.setOnItemClickListener(new OnItemClickListener(){
            public void onItemClick(
                AdapterView<?> parent,
                View view,
                int position,
                long id
            ){
                
                Object listItem = instagramImageList.getItemAtPosition(position);
                if(listItem instanceof InstagramImageTileItem){
//                    InstagramImageTileItem instagramImageItem = instagramImageTileAdapter.getItem(position);
                    InstagramImageTileItem instagramImageItem = (InstagramImageTileItem)listItem;
                    if(instagramImageItem.isDummy()){
                        switch(currentInstagramImageType){
                        case SelfFeed:
                            requestPaginationNext(instagramImageItem.getNextUrl(), selfFeedDataRequestHandler);
                            break;
                        case PrayForJapan:
                            requestPaginationNext(instagramImageItem.getNextUrl(), prayForJapanDataRequestHandler);
                            break;
                        case Popular:
                        default:
                            break;
                        }
                    }
                }else if(listItem instanceof InstagramImage){
//                    InstagramImage instagramImage = instagramImageDetailAdapter.getItem(position);
                    InstagramImage instagramImage = (InstagramImage)listItem;
                    if(instagramImage.isDummy()){
                        requestPaginationNext(instagramImage.getNextUrl(), selfFeedDataRequestHandler);
                    }
                }
            }
        });

        // 詳細レイアウト
//        instagramImageListDetail = (ListView)findViewById(R.id.list_instagram_image_detail);
//        instagramImageListDetail.setEmptyView(noDataTextView);
//        instagramImageListDetail.setEmptyView(noDataView);
        
        instagramImageDetailAdapter = new InstagramImageDetailAdapter(
            this, 
            R.layout.instagram_image_item_detail,
            detailListViewRefreshHandler
        );
//        instagramImageListDetail.setAdapter(instagramImageDetailAdapter);
//        instagramImageListDetail.setOnItemClickListener(new OnItemClickListener(){
//            public void onItemClick(
//                AdapterView<?> parent,
//                View view,
//                int position,
//                long id
//            ){
//                InstagramImage instagramImage = instagramImageDetailAdapter.getItem(position);
//                if(instagramImage.isDummy()){
//                    requestPaginationNext(instagramImage.getNextUrl(), selfFeedDataRequestHandler);
//                }
//            }
//        });

//        instagramImageListDetail.setVisibility(View.GONE);    

        // ログインしているユーザのアクセストークンを作成しておく
        accessToken = UserManager.getInstance().createAccessTokenFromPreference(this);
        if(accessToken != null){
            currentListItemLayoutType = ListItemLayoutType.Detail;
            // 詳細レイアウトのリストアイテムを設定
            instagramImageList.setAdapter(instagramImageDetailAdapter);
            
            // 画面のタイトルを設定
//            setTitle(getString(R.string.app_name) + " [ " + InstagramImageType.SelfFeed.getText() + " ]");
            setTitle(getString(R.string.app_name) + " [ " + getString(R.string.activity_title_feed) + " ]");
            // 現在の画像リストタイプ: 自分のフィード
            currentInstagramImageType = InstagramImageType.SelfFeed;
            // ボタンの色を変更
            changeButtonClickColor();
            // フィード画像取得
            requestSelfFeed();
        }else{
            currentListItemLayoutType = ListItemLayoutType.Tile;
            // タイルレイアウトのリストアイテムを設定
            instagramImageList.setAdapter(instagramImageTileAdapter);

            // 画面のタイトルを設定
//            setTitle(getString(R.string.app_name) + " [ " + InstagramImageType.Popular.getText() + " ]");
            setTitle(getString(R.string.app_name) + " [ " + getString(R.string.activity_title_popular) + " ]");
            // 現在の画像リストタイプ: ポピュラー
            currentInstagramImageType = InstagramImageType.Popular;
            // ボタンの色を変更
            changeButtonClickColor();
            // ポピュラー画像取得
            requestPopular();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
    
        if(GramtapUtil.isLoginUserChanged()){
            // ログインユーザ情報が変更された
            GramtapUtil.setLoginUserChanged(false);

            // アクセストークンを作り直す
            // アカウント削除した場合は、アクセストークンがnullとなる。
            accessToken = UserManager.getInstance().createAccessTokenFromPreference(this);
            
//            if(accessToken == null){
    //            prevAccessToken = null;
                // キャッシュしている画像を一旦クリアする
                InstagramImageManager instagramImageManager = InstagramImageManager.getInstance();
                instagramImageManager.clearPaginationAndImage(InstagramImageSize.StandardResolution, InstagramImageType.SelfFeed);
                instagramImageManager.clearPaginationAndImage(InstagramImageSize.LowResolution, InstagramImageType.SelfFeed);
                instagramImageManager.clearPaginationAndImage(InstagramImageSize.Thumbnail, InstagramImageType.SelfFeed);
                UserManager.getInstance().clearUsers();
                UserManager.getInstance().clearProfilePictures();
                
                if(currentInstagramImageType == InstagramImageType.SelfFeed){
                    // ポピュラーに移動
                    currentInstagramImageType = InstagramImageType.Popular;
                    currentListItemLayoutType = ListItemLayoutType.Tile;
                    changeButtonClickColor();                    
                    showInstagramImage();
                }
    //        }else if(prevAccessToken != null){
    //            User preLoginUser = prevAccessToken.getUser();
    //            User loginUser = accessToken.getUser();
    //            if(preLoginUser.getId() != loginUser.getId()){
    //                // フィードを削除
    //                InstagramImageManager.getInstance().clearPagination(InstagramImageSize.Thumbnail, InstagramImageType.SelfFeed);
    //                
    //                // ポピュラーに移動
    //                currentInstagramImageType = InstagramImageType.Popular;
    //                changeButtonClickColor();                    
    //                showInstagramImage();
    //            }
                
//            }
        }
    }

    
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu){
//        menu.removeItem(MENU_ID_REFRESH);
//        menu.removeItem(MENU_ID_SETTINGS);
//        menu.removeItem(MENU_ID_LIST_LAYOUT_TILE);
//        menu.removeItem(MENU_ID_LIST_LAYOUT_DETAIL);
//
//
//        menu.add(Menu.NONE, MENU_ID_REFRESH, Menu.NONE, "refresh").setIcon(R.drawable.ic_menu_refresh);
//        
//        if(currentInstagramImageType == InstagramImageType.SelfFeed){
//            switch(currentListItemLayoutType){
//            case Tile:
//                menu.add(Menu.NONE, MENU_ID_LIST_LAYOUT_DETAIL, Menu.NONE, "detail view").setIcon(android.R.drawable.ic_menu_view);
//                break;
//            case Detail:
//                menu.add(Menu.NONE, MENU_ID_LIST_LAYOUT_TILE, Menu.NONE, "tile view").setIcon(android.R.drawable.ic_menu_view);
//                break;
//            }
//        }
//        
//        menu.add(Menu.NONE, MENU_ID_SETTINGS, Menu.NONE, "settings").setIcon(android.R.drawable.ic_menu_preferences);
//        return super.onPrepareOptionsMenu(menu);
//    }    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(Menu.NONE, MENU_ID_REFRESH, Menu.NONE, R.string.option_menu_refresh).setIcon(R.drawable.ic_menu_refresh);
        menu.add(Menu.NONE, MENU_ID_SETTINGS, Menu.NONE, R.string.option_menu_settings).setIcon(android.R.drawable.ic_menu_preferences);
        
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
//        case MENU_ID_POPULAR:
//            currentInstagramImageType = InstagramImageType.Popular;
//            break;
//        case MENU_ID_PRAY_FOR_JAPAN:
//            currentInstagramImageType = InstagramImageType.PrayForJapan;
//            break;
        case MENU_ID_REFRESH:
            // 更新なので、画像を削除しておく
            switch(currentInstagramImageType){
            case SelfFeed:
                InstagramImageManager.getInstance().clearPaginationAndImage(
                    InstagramImageSize.StandardResolution,
                    currentInstagramImageType
                );
                break;
            case Popular:
            case PrayForJapan:
                InstagramImageManager.getInstance().clearPaginationAndImage(
                    InstagramImageSize.Thumbnail,
                    currentInstagramImageType
                );
                break;
            }

            requestInstagramImage();
//            return super.onOptionsItemSelected(item);
            break;
        case MENU_ID_SETTINGS:
            // 設定
//            Intent settingsIntent = new Intent(this, SettingsActivity.class);
//            startActivity(settingsIntent);
            startSettingsActivity();
            
//            return super.onOptionsItemSelected(item);
            break;
        case MENU_ID_LIST_LAYOUT_TILE:
            currentListItemLayoutType = ListItemLayoutType.Tile;
            
            showInstagramImage();
            break;
        case MENU_ID_LIST_LAYOUT_DETAIL:
            currentListItemLayoutType = ListItemLayoutType.Detail;
            
            showInstagramImage();
            break;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * 
     */
    private void startSettingsActivity(){
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
    
//    private void requestSelfFeedNext(String url){
//        // プログレス表示
//        showProgress();
//        
//        int index = url.indexOf("?");
//        url = url.substring(0, index - 1);
//        String query = url.substring(index + 1);
//        new InstagramPaginationRequestThread(
//            url,
//            query,
//            selfFeedDataRequestHandler
//        ).start();
//    }
//
//    private void requestPrayForJapanNext(String url){
//        // プログレス表示
//        showProgress();
//        
//        int index = url.indexOf("?");
//        url = url.substring(0, index - 1);
//        String query = url.substring(index + 1);
//        new InstagramPaginationRequestThread(
//            url,
//            query,
//            prayForJapanDataRequestHandler
//        ).start();
//    }

     private void requestPaginationNext(String url, Handler handler){
         // プログレス表示
         GramtapUtil.showProgress(this);

         // ログインユーザID
//         long loginUserId = GramtapUtil.getLoginUserId(this);
//         // アクセストークン取得
//         String accessToken = GramtapUtil.getAccessToken(this);
//         long loginUserId = -1l;
//         String accessTokenStr = null;
//         if(accessToken != null){
//             loginUserId = accessToken.getUser().getId();
//             accessTokenStr = accessToken.getText();
//         }

         // Instagramページネーションリクエスト開始
         new InstagramPaginationRequestThread(
             accessToken,
             url,
             handler
         ).start();
    }
      
    private void requestSelfFeed(){
        // プログレス表示
//        showProgress();
        GramtapUtil.showProgress(this);

        // アクセストークン取得
//        String accessToken = GramtapUtil.getAccessToken(this);  
        new InstagramPaginationRequestThread(
            accessToken,
            InstagramImageType.SelfFeed, 
            selfFeedDataRequestHandler
        ).start();
    }

    private void requestPopular(){
//        showProgress();
        GramtapUtil.showProgress(this);
        
        new PopularDataRequestThread(accessToken, popularDataRequestHandler).start();
    }
    
    private void requestPrayForJapan(){
//        showProgress();
        GramtapUtil.showProgress(this);

        new PrayForJapanDataRequestThread(prayForJapanDataRequestHandler).start();
    }
    
    
    private void showInstagramImage(){
        InstagramImageManager instagramImageManager = InstagramImageManager.getInstance();
        boolean hasCache = instagramImageManager.hasCache(currentInstagramImageType);

        if(!hasCache){
            if(currentInstagramImageType == InstagramImageType.SelfFeed){
//                mainNoDataTextView.setVisibility(View.GONE);
//                instagramImageListDetail.setVisibility(View.GONE);
//                instagramImageListTile.setVisibility(View.VISIBLE);
                instagramImageList.setAdapter(instagramImageTileAdapter);
            }
            
            // キャッシュを持っていないので、APIリクエスト
            requestInstagramImage();
            return;
        }else{
//            setTitle(getString(R.string.app_name) + " [ " + currentInstagramImageType.getText() + " ]");
            setActivityTitle();
            
            if(currentInstagramImageType == InstagramImageType.SelfFeed){
//                mainNoDataTextView.setVisibility(View.VISIBLE);
//                instagramImageListDetail.setVisibility(View.GONE);
//                instagramImageListTile.setVisibility(View.GONE);
                instagramImageList.setVisibility(View.GONE);
                
                instagramImageDetailAdapter.clear();
                instagramImageTileAdapter.clear();
                
                switch(currentListItemLayoutType){
                case Tile:
//                    instagramImageListDetail.setVisibility(View.GONE);
//                    instagramImageTileAdapter.clear();
                    
                    setCacheInstagramImageToTile();
//                    mainNoDataTextView.setVisibility(View.GONE);
//                    instagramImageListTile.setVisibility(View.VISIBLE);
                    //instagramImageTileAdapter.notifyDataSetChanged();
                    instagramImageList.setAdapter(instagramImageTileAdapter);
                    instagramImageList.setVisibility(View.VISIBLE);
                    
                    break;
                case Detail:
//                    instagramImageDetailAdapter.clear();
                    setCacheInstagramImageToDetail();
                    
//                    mainNoDataTextView.setVisibility(View.GONE);
//                    instagramImageListDetail.setVisibility(View.VISIBLE);
//                    instagramImageDetailAdapter.notifyDataSetChanged();
                    instagramImageList.setAdapter(instagramImageDetailAdapter);
                    instagramImageList.setVisibility(View.VISIBLE);
                    
                    
                    break;
                }
            }else{
                
                instagramImageTileAdapter.clear();
                
//                mainNoDataTextView.setVisibility(View.GONE);
//                instagramImageListDetail.setVisibility(View.GONE);
//                instagramImageListTile.setVisibility(View.VISIBLE);
                instagramImageList.setAdapter(instagramImageTileAdapter);
                instagramImageList.setVisibility(View.VISIBLE);
                
                
                setCacheInstagramImageToTile();

//                instagramImageTileAdapter.notifyDataSetChanged();
            }
        }
    }
    
    private void setActivityTitle(){
        String imageTypeText = null;
        switch(currentInstagramImageType){
        case SelfFeed:
            imageTypeText = getString(R.string.activity_title_feed).toString();
            break;
        case Popular:
            imageTypeText = getString(R.string.activity_title_popular).toString();
            break;
        case PrayForJapan:
            imageTypeText = getString(R.string.activity_title_prayforjapan).toString();
            break;
        }
        
        setTitle(getString(R.string.app_name) + " [ " + imageTypeText + " ]");
    }
    
    private void setCacheInstagramImageToDetail(){
        InstagramImageManager instaImageManager = InstagramImageManager.getInstance();

        InstagramPagination lastPagination = null;
        int paginationSize = instaImageManager.paginationSize(currentInstagramImageType);
        for(int i = 0; i < paginationSize; i++){
            InstagramPagination pagination = 
                instaImageManager.getPagination(i, currentInstagramImageType);
            for(int j = 0; j < pagination.size(); j++){
                InstagramImage instaImage = pagination.getInstagramImage(j);
                instagramImageDetailAdapter.add(instaImage);
            }
            
            lastPagination = pagination;
        }
        
        if(currentInstagramImageType == InstagramImageType.SelfFeed
            && lastPagination != null
            && lastPagination.getNextUrl() != null){
            
            // もっと見るViewを最後に追加
            InstagramImage dummy = new InstagramImage();
            dummy.setDummy(true);
            dummy.setNextUrl(lastPagination.getNextUrl());
            instagramImageDetailAdapter.add(dummy);
        }
    }
    
    private void setCacheInstagramImageToTile(){
        InstagramImageManager instaImageManager = InstagramImageManager.getInstance();

        int paginationSize = instaImageManager.paginationSize(currentInstagramImageType);
        for(int i = 0; i < paginationSize; i++){
            InstagramPagination instagramPagination = 
                instaImageManager.getPagination(i, currentInstagramImageType); 
            addInstagramImageTileAdapter(instagramPagination);
        }
    }
    
    private Handler cachedInstagramPaginationHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            setCacheInstagramImageToTile();
            
//            closeProgress();
            GramtapUtil.closeProgress(InstagramImageListActivity.this);
        }
    };
   
    private void requestInstagramImage(){
        instagramImageTileAdapter.clear();
        instagramImageDetailAdapter.clear();
        
        // 取得したサムネイルを削除
//        InstagramImageManager.getInstance().clear(InstagramImageSize.Thumbnail);
        
//        ScrollView scrollView = (ScrollView)findViewById(R.id.scrollview);
//        scrollView.scrollTo(0, 0);

        //setTitle(getString(R.string.app_name) + " [ " + currentInstagramImageType.getText() + " ]");
        setActivityTitle();
        
        switch(currentInstagramImageType){
        case SelfFeed:
            // フィード
//            setTitle("gramtap [ feed ]");
            requestSelfFeed();
            break;
        case Popular:
            // ポピュラー
//            setTitle("gramtap [ popular ]");
            requestPopular();
            break;
        case PrayForJapan:
            // #prayforjapan
//            setTitle("gramtap [ #prayforjapan ]");
            requestPrayForJapan();
            break;
        }
    }

//    private void showProgress(){
//        setProgressBarIndeterminateVisibility(true);
//        
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage(getString(R.string.message_please_wait));
//        progressDialog.show();
//    }
//
//    private void closeProgress(){
//        // プログレスを閉じる
//        setProgressBarIndeterminateVisibility(false);
//        if(progressDialog != null){
//            progressDialog.dismiss();
//            progressDialog = null;
//        }
//    }

    private void checkAndShowError(Bundle data){
        if(data.containsKey(GramtapConstants.API_RESULT_KEY_EXCEPTION)){
//            Exception e = 
//                (Exception)data.getSerializable(GramtapConstants.API_RESULT_KEY_EXCEPTION);
            Toast.makeText(this, R.string.error_message_instagram_request_failed, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, R.string.error_message_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }
    
    private Handler tileListViewRefreshHandler = new Handler(){
        @Override
        public void handleMessage(Message message){
            instagramImageTileAdapter.notifyDataSetChanged();
        }
    };

    private Handler detailListViewRefreshHandler = new Handler(){
        @Override
        public void handleMessage(Message message){
            instagramImageDetailAdapter.notifyDataSetChanged();
        }
    };
    
    private Handler selfFeedDataRequestHandler = new Handler(){
        @Override
        public void handleMessage(Message message){
            // プログレスを閉じる
//            closeProgress();
            GramtapUtil.closeProgress(InstagramImageListActivity.this);
            
            Bundle data = message.getData();
            if(data.containsKey(GramtapConstants.API_RESULT_KEY_RESULT)){
                InstagramResult instaResult = 
                    (InstagramResult)data.getSerializable(GramtapConstants.API_RESULT_KEY_RESULT);
                InstagramPagination selfFeedPagination = (InstagramPagination)instaResult.getResultObject();
                
                if(selfFeedPagination.size() > 0){
                    // キャッシュしておく
                    InstagramImageManager.getInstance().addInstagramPagination(selfFeedPagination, InstagramImageType.SelfFeed);
                }
                
                switch(currentListItemLayoutType){
                case Tile:
                    // タイルリスト
                    addInstagramImageTileAdapter(selfFeedPagination);
  
//                    mainNoDataTextView.setVisibility(View.GONE);
//                    instagramImageListDetail.setVisibility(View.GONE);
//                    instagramImageListTile.setVisibility(View.VISIBLE);
//                    instagramImageTileAdapter.notifyDataSetChanged();
                    
                    instagramImageList.setAdapter(instagramImageTileAdapter);
                    break;
                case Detail:
                    // 詳細リスト
//                    instagramImageDetailAdapter.clear();
                    addInstagramImageDetailAdapter(selfFeedPagination);
                    
//                    mainNoDataTextView.setVisibility(View.GONE);
//                    instagramImageListTile.setVisibility(View.GONE);
//                    instagramImageListDetail.setVisibility(View.VISIBLE);
//                    instagramImageDetailAdapter.notifyDataSetChanged();
                    instagramImageList.setAdapter(instagramImageDetailAdapter);
                    break;
                }
            }else{
                // InstagramAPIリクエストに失敗した
                checkAndShowError(data);
            }
        }
    };

    private Handler popularDataRequestHandler = new Handler(){
        @Override
        public void handleMessage(Message message){
            // プログレスを閉じる
//            closeProgress();
            GramtapUtil.closeProgress(InstagramImageListActivity.this);

            Bundle data = message.getData();
            if(data.containsKey(GramtapConstants.API_RESULT_KEY_RESULT)){
                // ポピュラーリストリクエスト成功
                InstagramResult instaResult = 
                    (InstagramResult)data.getSerializable(GramtapConstants.API_RESULT_KEY_RESULT);
                InstagramPagination popularPagination = (InstagramPagination)instaResult.getResultObject();
                
                // キャッシュしておく
                InstagramImageManager.getInstance().addInstagramPagination(popularPagination, InstagramImageType.Popular);
                addInstagramImageTileAdapter(popularPagination);

                ListAdapter listAdapter = instagramImageList.getAdapter();
                if(listAdapter instanceof InstagramImageDetailAdapter){
                    instagramImageList.setAdapter(instagramImageTileAdapter);
                }
            }else{
                // InstagramAPIリクエストに失敗した
                checkAndShowError(data);
            }
        }
    };

    private Handler prayForJapanDataRequestHandler = new Handler(){
        @Override
        public void handleMessage(Message message){
            // プログレスを閉じる
//            closeProgress();
            GramtapUtil.closeProgress(InstagramImageListActivity.this);
            
            Bundle data = message.getData();
            if(data.containsKey(GramtapConstants.API_RESULT_KEY_RESULT)){
                InstagramResult instaResult = 
                    (InstagramResult)data.getSerializable(GramtapConstants.API_RESULT_KEY_RESULT);
                InstagramPagination p4jPagination = (InstagramPagination)instaResult.getResultObject();

                // キャッシュしておく
                InstagramImageManager.getInstance().addInstagramPagination(p4jPagination, InstagramImageType.PrayForJapan);
                addInstagramImageTileAdapter(p4jPagination);
                
                ListAdapter listAdapter = instagramImageList.getAdapter();
                if(listAdapter instanceof InstagramImageDetailAdapter){
                    instagramImageList.setAdapter(instagramImageTileAdapter);
                }
            }else{
                // InstagramAPIリクエストに失敗した
                checkAndShowError(data);
            }
        }
    };

    /**
     * Instagram画像詳細アダプタに画像を追加する。
     * 
     * @param pagination Instagram画像
     */
    private void addInstagramImageDetailAdapter(InstagramPagination pagination){
        if(pagination.size() == 0){
            return;
        }

        if(instagramImageDetailAdapter.getCount() > 0){
            InstagramImage lastItem = 
                instagramImageDetailAdapter.getItem(instagramImageDetailAdapter.getCount() - 1);
            if(lastItem.isDummy()){
                instagramImageDetailAdapter.remove(lastItem);
            }
        }
        
        for(int i = 0; i < pagination.size(); i++){
            InstagramImage instaImage = pagination.getInstagramImage(i);
            instagramImageDetailAdapter.add(instaImage);
        }
        
        if(currentInstagramImageType == InstagramImageType.SelfFeed
            && pagination.getNextUrl() != null){
            
            // もっと見るViewを最後に追加
            InstagramImage dummy = new InstagramImage();
            dummy.setDummy(true);
            dummy.setNextUrl(pagination.getNextUrl());
            instagramImageDetailAdapter.add(dummy);
        }
    }
    
    
    private void addInstagramImageTileAdapter(InstagramPagination pagination){
        if(pagination.size() == 0){
            return;
        }
        
        if(instagramImageTileAdapter.getCount() > 0){
            InstagramImageTileItem lastItem = instagramImageTileAdapter.getItem(instagramImageTileAdapter.getCount() - 1);
            if(lastItem.isDummy()){
                instagramImageTileAdapter.remove(lastItem);
            }
        }

        double tmpCount = pagination.size() / 4d;
        int loopCount = (int)Math.ceil(tmpCount);
        for(int i = 0; i < loopCount; i++){
            int startIndex = i * 4;
            
            InstagramImageTileItem instagramImageItem = new InstagramImageTileItem();
//            instagramImageItem.addInstagramImage(0, instagramPagination.getInstagramImage(startIndex + 0));
//            instagramImageItem.addInstagramImage(1, instagramPagination.getInstagramImage(startIndex + 1));
//            instagramImageItem.addInstagramImage(2, instagramPagination.getInstagramImage(startIndex + 2));
//            instagramImageItem.addInstagramImage(3, instagramPagination.getInstagramImage(startIndex + 3));
            for(int j = 0; j < 4; j++){
                int imageIndex = startIndex + j;
                if(imageIndex < pagination.size()){
                    instagramImageItem.addInstagramImage(j, pagination.getInstagramImage(imageIndex));
                }else{
                    break;
                }
            }
            
            instagramImageTileAdapter.add(instagramImageItem);
        }
        
        if(currentInstagramImageType != InstagramImageType.Popular 
            && pagination.getNextUrl() != null
            && instagramImageTileAdapter.getCount() < 25){
            InstagramImageTileItem dummy = new InstagramImageTileItem(true);
            dummy.setNextUrl(pagination.getNextUrl());
            instagramImageTileAdapter.add(dummy);
        }
    }

}
