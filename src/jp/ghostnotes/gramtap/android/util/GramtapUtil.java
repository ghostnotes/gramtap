package jp.ghostnotes.gramtap.android.util;

import jp.ghostnotes.gramtap.android.InstagramImageDetailActivity;
import jp.ghostnotes.gramtap.android.R;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class GramtapUtil{
    private static ProgressDialog progressDialog = null;

    private static SharedPreferences createSharedPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    public static void removeAccessToken(Context context){
        SharedPreferences settings = createSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(GramtapConstants.SHARED_PREF_KEY_LOGIN_USER_ID);
        editor.remove(GramtapConstants.SHARED_PREF_KEY_LOGIN_USERNAME);
        editor.remove(GramtapConstants.SHARED_PREF_KEY_LOGIN_FULL_NAME);
        editor.remove(GramtapConstants.SHARED_PREF_KEY_LOGIN_PROFILE_PICTURE);
        editor.remove(GramtapConstants.SHARED_PREF_KEY_ACCESS_TOKEN);
        // いいねAPI認証
        editor.remove(GramtapConstants.SHARED_PREF_KEY_SCOPE_LIKE);
        editor.commit();
    }

    private static long getLong(String key, Context context){
      SharedPreferences settings = createSharedPreferences(context);
      return settings.getLong(key, -1l);
    }
    
    private static String getString(String key, Context context){
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences settings = createSharedPreferences(context);
        return settings.getString(key, null);
    }
    
    private static boolean getBoolean(String key, Context context){
        SharedPreferences settings = createSharedPreferences(context);
        return settings.getBoolean(key, false);
    }
    
    public static long getLoginUserId(Context context){
        return getLong(GramtapConstants.SHARED_PREF_KEY_LOGIN_USER_ID, context);
    }
    
    public static String getLoginUserName(Context context){
        return getString(GramtapConstants.SHARED_PREF_KEY_LOGIN_USERNAME, context);
    }
    
    public static String getAccessToken(Context context){
        return getString(GramtapConstants.SHARED_PREF_KEY_ACCESS_TOKEN, context);
    }
    
    private static boolean isAuthLike(Context context){
        return getBoolean(GramtapConstants.SHARED_PREF_KEY_SCOPE_LIKE, context);
    }
    
    public static boolean isAuthAllScope(Context context){
        boolean isAuthLike = isAuthLike(context);
        return isAuthLike;
    }
    
    private static boolean isLoginUserChanged = false;
    public static void setLoginUserChanged(boolean isChanged){
        isLoginUserChanged = isChanged;
    }
    
    public static boolean isLoginUserChanged(){
        return isLoginUserChanged;
    }

    public static void showProgress(Activity activity){
        activity.setProgressBarIndeterminateVisibility(true);
        
        if(progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
        
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(activity.getString(R.string.message_please_wait));
        progressDialog.show();
    }
    
    public static void closeProgress(Activity activity){
        // プログレスを閉じる
        activity.setProgressBarIndeterminateVisibility(false);
        if(progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
    
    public static void notify(String message, Context context){
        NotificationManager notificationManager = 
            (NotificationManager)context.getSystemService(Activity.NOTIFICATION_SERVICE);
        
        Notification notification = new Notification(
            R.drawable.ic_notification, 
            message, 
            System.currentTimeMillis()
        );
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        
        Intent intent = new Intent(Intent.ACTION_VIEW);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        notification.setLatestEventInfo(
            context,
            "gramtap",
            message,
            pendingIntent);
        notificationManager.notify(R.string.app_name, notification);
        notificationManager.cancel(R.string.app_name);
    }

}
