package jp.ghostnotes.gramtap.android.bean;

import java.io.Serializable;

/**
 * ユーザ。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class User implements Serializable{

    private static final long serialVersionUID = -1418122785323415615L;
    
    /** ユーザID */
    private long id = -1;
    /** ユーザ名 */
    private String userName = null;
    /** フルネーム */
    private String fullName = null;
    /** プロフィール画像 */
    private String profilePicture = null;
    
    /**
     * コンストラクタ。
     */
    public User(){
    }
    
    public long getId(){
        return id;
    }
    public void setId(long id){
        this.id = id;
    }
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
    public String getFullName(){
        return fullName;
    }
    public void setFullName(String fullName){
        this.fullName = fullName;
    }
    public String getProfilePicture(){
        return profilePicture;
    }
    public void setProfilePicture(String profilePicture){
        this.profilePicture = profilePicture;
    }

}
