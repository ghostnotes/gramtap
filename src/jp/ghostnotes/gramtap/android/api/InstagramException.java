package jp.ghostnotes.gramtap.android.api;

/**
 * Instagram例外。
 * 
 * @author taniguchi@paperboy.co.jp
 */
public class InstagramException extends Exception{
    private static final long serialVersionUID = -659337502345273407L;

    /**
     * コンストラクタ。
     */
    public InstagramException(){
        super();
    }
    
    /**
     * コンストラクタ。
     * 
     * @param message 例外メッセージ
     */
    public InstagramException(String message){
        super(message);    
    }
    
    /**
     * コンストラクタ。
     * 
     * @param message 例外メッセージ
     * @param cause 原因
     */
    public InstagramException(String message, Throwable cause){
        super(message, cause);
    }
    
    /**
     * コンストラクタ。
     * 
     * @param cause 原因
     */
    public InstagramException(Throwable cause){
        super(cause);
    }

}
