package jp.ghostnotes.gramtap.android.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * キャプション。
 * 
 * @author ghostnotes@gmail.com
 */
public class Caption implements Serializable{
    private static final long serialVersionUID = -8569749489329612654L;

    /** ID */
    private long id = -1l;
    /** ユーザ */
    private User from = null;
    /** キャプション */
    private String text = null;
    /** 作成日 */
    private Date createdDate = null;
    
    /**
     * コンストラクタ。
     */
    public Caption(){
    }

    /**
     * キャプションIDを取得する。
     * 
     * @return キャプションID
     */
    public long getId(){
        return id;
    }
    
    /**
     * キャプションIDを設定する。
     * 
     * @param id キャプションID
     */
    public void setId(long id){
        this.id = id;
    }
    
    /**
     * ユーザを取得する。
     * 
     * @return ユーザ
     */
    public User getFrom(){
        return from;
    }
    
    /**
     * ユーザを設定する。
     * 
     * @param from ユーザ
     */
    public void setFrom(User from){
        this.from = from;
    }
    
    /**
     * キャプションテキストを取得する。
     * 
     * @return キャプションテキスト
     */
    public String getText(){
        return text;
    }
    
    /**
     * キャプションテキストを設定する。
     * 
     * @param text キャプションテキスト
     */
    public void setText(String text){
        this.text = text;
    }
    
    /**
     * 作成日時を取得する。
     * 
     * @return 作成日時
     */
    public Date getCreatedDate(){
        return createdDate;
    }
    
    /**
     * 作成日時を設定する。
     * 
     * @param createdDate 作成日時
     */
    public void setCreatedDate(Date createdDate){
        this.createdDate = createdDate;
    }
    
    public static long getSerialversionuid(){
        return serialVersionUID;
    }

}
