package jp.ghostnotes.gramtap.android.api;

/**
 * 認証例外。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class AuthenticationException extends Exception{
    private static final long serialVersionUID = -116516663145280931L;

    /**
     * コンストラクタ。
     */
    public AuthenticationException(){
        super();
    }
    
    /**
     * コンストラクタ。
     * 
     * @param message 例外メッセージ
     */
    public AuthenticationException(String message){
        super(message);    
    }
    
    /**
     * コンストラクタ。
     * 
     * @param message 例外メッセージ
     * @param cause 原因
     */
    public AuthenticationException(String message, Throwable cause){
        super(message, cause);
    }
    
    /**
     * コンストラクタ。
     * 
     * @param cause 原因
     */
    public AuthenticationException(Throwable cause){
        super(cause);
    }

}
