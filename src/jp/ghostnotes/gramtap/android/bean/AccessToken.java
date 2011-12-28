package jp.ghostnotes.gramtap.android.bean;

import java.io.Serializable;

/**
 * アクセストークン。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class AccessToken implements Serializable{

    private static final long serialVersionUID = -2444805159991803872L;
    
    /** アクセストークン */
    private String text = null;
    /** ユーザ */
    private User user = null;
    
    private boolean isAuthLike = false;
    
    /**
     * コンストラクタ。
     */
    public AccessToken(){
    }
    
    /**
     * アクセストークンを設定する。
     * 
     * @param text アクセストークン
     */
    public void setText(String text){
        this.text = text;
    }
    
    /**
     * アクセストークンを取得する。
     * 
     * @return アクセストークン
     */
    public String getText(){
        return text;
    }
    
    /**
     * ユーザを設定する。
     * 
     * @param user ユーザ
     */
    public void setUser(User user){
        this.user = user;
    }
    
    /**
     * ユーザを取得する。
     * 
     * @return ユーザ
     */
    public User getUser(){
        return user;
    }
    
    public void setAuthLike(boolean isAuthLike){
        this.isAuthLike = isAuthLike;
    }
    
    public boolean isAuthLike(){
        return isAuthLike;
    }

}
