package jp.ghostnotes.gramtap.android.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * コメント。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class Comment implements Serializable{

    private static final long serialVersionUID = -5299479843211733456L;
    
    /** コメントID */
    private long id = -1l;
    /** コメントテキスト */
    private String text = null;
    /** ユーザ */
    private User from = null;
    /** 作成日時 */
    private Date createdTime = null;
    
    /**
     * コンストラクタ。
     */
    public Comment(){
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

    public User getFrom(){
        return from;
    }

    public void setFrom(User from){
        this.from = from;
    }

    public Date getCreatedTime(){
        return createdTime;
    }

    public void setCreatedTime(Date createdTime){
        this.createdTime = createdTime;
    }

}
