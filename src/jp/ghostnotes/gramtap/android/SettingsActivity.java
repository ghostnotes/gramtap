package jp.ghostnotes.gramtap.android;

import jp.ghostnotes.gramtap.android.util.GramtapConstants;
import jp.ghostnotes.gramtap.android.util.GramtapUtil;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;

/**
 * gramtap設定画面。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
    
    /** ログインプリファレンス */
    private Preference loginPref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // プリファレンススクリーンを設定
        setPreferenceScreen(createPreferenceScreen());
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        
        getPreferenceScreen().removeAll();
        setPreferenceScreen(createPreferenceScreen());
    }
    
    @Override
    protected void onPause(){
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
    
    private PreferenceScreen createPreferenceScreen(){
        // ルートプレファレンススクリーン
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
     
        
        // ユーザ
        PreferenceCategory userCategory = new PreferenceCategory(this);
        userCategory.setTitle(R.string.category_title_account);
        root.addPreference(userCategory);
        
        loginPref = new Preference(this);
        loginPref.setTitle(R.string.pref_title_login);
        
        String loginUserName = GramtapUtil.getLoginUserName(this);
        if(loginUserName != null){
            loginPref.setSummary(getString(R.string.pref_summary_logged_in) + " [ " + loginUserName + " ]");
        }else{
            loginPref.setSummary(R.string.pref_summary_no_login);
        }
        loginPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
            public boolean onPreferenceClick(Preference preference){
                
                String tmpUserName = GramtapUtil.getLoginUserName(SettingsActivity.this);
                if(tmpUserName != null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                    builder.setTitle(R.string.dialog_title_login);
                    builder.setMessage(R.string.message_another_account_login);
                    builder.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            // ログイン
                            startXAuthLoginActivity();                        
                        }
                    });
                    builder.setNegativeButton(R.string.button_no, null);
                    builder.create().show();
                }else{
                    // ログインしていないので無条件にInstagram認証へ
                    startXAuthLoginActivity();
                }
                return false;
            }
        });
        userCategory.addPreference(loginPref);
        
        if(loginUserName != null){
            // アカウント削除メニュー
            Preference deletePref = new Preference(this);
            deletePref.setTitle(R.string.pref_title_delete_account);
            deletePref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
                public boolean onPreferenceClick(Preference preference){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                    builder.setTitle(R.string.dialog_title_delete_account);
                    builder.setMessage(R.string.message_delete_account);
                    builder.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
//                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
//                            SharedPreferences.Editor editor = settings.edit();
//                            editor.remove(GramtapConstants.SHARED_PREF_KEY_LOGIN_USER_ID);
//                            editor.remove(GramtapConstants.SHARED_PREF_KEY_LOGIN_USERNAME);
//                            editor.remove(GramtapConstants.SHARED_PREF_KEY_LOGIN_FULL_NAME);
//                            editor.remove(GramtapConstants.SHARED_PREF_KEY_LOGIN_PROFILE_PICTURE);
//                            editor.remove(GramtapConstants.SHARED_PREF_KEY_ACCESS_TOKEN);
//                            editor.remove(GramtapConstants.SHARED_PREF_KEY_SCOPE_AUTH);
//                            editor.commit();
                            // デフォルトプリファレンスに保存しているログイン情報を削除
                            GramtapUtil.removeAccessToken(SettingsActivity.this);
                            // ログイン状態が変更されたことを保持
                            GramtapUtil.setLoginUserChanged(true);
                        }
                    });
                    builder.setNegativeButton(R.string.button_no, null);
                    builder.create().show();
                    
                    return false;
                }
            });
            
            userCategory.addPreference(deletePref);
        }
        
        
        // ユーザ
        PreferenceCategory aboutCategory = new PreferenceCategory(this);
        aboutCategory.setTitle(R.string.category_title_about);
        root.addPreference(aboutCategory);

        
        Preference versionPref = new Preference(this);
        try{
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
            versionPref.setTitle(R.string.pref_title_version);
            versionPref.setSummary(packageInfo.versionName);
        }catch(NameNotFoundException e){
            // 自分のバージョン取得時に例外は起きないはず
            versionPref.setSummary("-");
        }
        aboutCategory.addPreference(versionPref);
        
        return root;
    }

    public void onSharedPreferenceChanged(
        SharedPreferences sharedPreferences,
        String key
    ){
        if(key.equals(GramtapConstants.SHARED_PREF_KEY_ACCESS_TOKEN)){
//            String loginUserName = sharedPreferences.getString(GramtapConstants.SHARED_PREF_KEY_LOGIN_USERNAME, null);
//            loginPref.setSummary("login [ " + loginUserName + " ]");
            
            getPreferenceScreen().removeAll();
            setPreferenceScreen(createPreferenceScreen());
        }
    }
    
    private void startXAuthLoginActivity(){
        Intent loginIntent = new Intent(SettingsActivity.this, XAuthLoginActivity.class);
        startActivity(loginIntent);
    }

}
