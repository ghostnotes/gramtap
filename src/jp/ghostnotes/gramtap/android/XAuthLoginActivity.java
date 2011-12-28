package jp.ghostnotes.gramtap.android;

import jp.ghostnotes.gramtap.android.api.InstagramResult;
import jp.ghostnotes.gramtap.android.bean.AccessToken;
import jp.ghostnotes.gramtap.android.bean.User;
import jp.ghostnotes.gramtap.android.util.GramtapConstants;
import jp.ghostnotes.gramtap.android.util.GramtapUtil;
import jp.ghostnotes.gramtap.android.worker.XAuthRequestThread;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * XAuthログイン画面。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class XAuthLoginActivity extends Activity{
//    private  ProgressDialog progressDialog = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.xauth_login);
        
        // ログインボタン
        Button buttonXAuth = (Button)findViewById(R.id.button_xauth);
        buttonXAuth.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                // ユーザ名
                EditText editUserName = (EditText)findViewById(R.id.edit_username);
                // パスワード
                EditText editPassword = (EditText)findViewById(R.id.edit_password);
                
                
                String userName = editUserName.getText().toString();
                userName = userName.trim();
                String password = editPassword.getText().toString();
                password = password.trim();
                if(userName == null || userName.length() == 0 || password == null || password.length() == 0){
                    return;
                }

                //showProgress();
                GramtapUtil.showProgress(XAuthLoginActivity.this);
                
                new XAuthRequestThread(
                    userName,
                    password,
                    xauthHandler
                ).start();
            }
        });
    }
  
//  private  void showProgress(){
//      setProgressBarIndeterminateVisibility(true);
//
//      progressDialog = new ProgressDialog(this);
//      progressDialog.setCancelable(false);
//      progressDialog.setMessage(getString(R.string.message_please_wait));
//      progressDialog.show();
//  }
//  
//  private  void closeProgress(){
//      // プログレスを閉じる
//      setProgressBarIndeterminateVisibility(false);
//      
//      if(progressDialog != null){
//          progressDialog.dismiss();
//          progressDialog = null;
//      }
//  }

    private Handler xauthHandler = new Handler(){
        @Override
        public void handleMessage(Message message){
            //closeProgress();
            GramtapUtil.closeProgress(XAuthLoginActivity.this);
            
            Bundle data = message.getData();
            if(data.containsKey(GramtapConstants.API_RESULT_KEY_RESULT)){
                // XAuthリクエスト結果
                InstagramResult instagramResult = 
                    (InstagramResult)data.getSerializable(GramtapConstants.API_RESULT_KEY_RESULT);
                
                // アクセストークン
                AccessToken accessToken = (AccessToken)instagramResult.getResultObject();
                // ログインユーザ情報
                User loginUser = accessToken.getUser();
                
                // プレファレンスに保存
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(XAuthLoginActivity.this);
                SharedPreferences.Editor editor = settings.edit();

                // ユーザID
                editor.putLong(GramtapConstants.SHARED_PREF_KEY_LOGIN_USER_ID, loginUser.getId());
                // ユーザ名
                editor.putString(GramtapConstants.SHARED_PREF_KEY_LOGIN_USERNAME, loginUser.getUserName());
                if(loginUser.getFullName() != null){
                    // フルネーム
                    editor.putString(GramtapConstants.SHARED_PREF_KEY_LOGIN_FULL_NAME, loginUser.getFullName());
                }
                if(loginUser.getProfilePicture() != null){
                    // プロフィール画像URL
                    editor.putString(GramtapConstants.SHARED_PREF_KEY_LOGIN_PROFILE_PICTURE, loginUser.getProfilePicture());
                }
                
                // アクセストークン
                editor.putString(GramtapConstants.SHARED_PREF_KEY_ACCESS_TOKEN, accessToken.getText());
                // SCOPE:いいねの認証済みか
                editor.putBoolean(GramtapConstants.SHARED_PREF_KEY_SCOPE_LIKE, true);
                
                editor.commit();
                
                // ログイン成功しました。
                Toast.makeText(
                    XAuthLoginActivity.this, 
                    R.string.message_login_successful,
                    Toast.LENGTH_SHORT
                ).show();
                
                GramtapUtil.setLoginUserChanged(true);
                finish();
            }else{
                // ログインに失敗しました。
                Toast.makeText(
                    XAuthLoginActivity.this, 
                    R.string.error_message_login_failed, 
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
    };

}
