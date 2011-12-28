package jp.ghostnotes.gramtap.android.api;

/**
 * 接続例外。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class ConnectException extends Exception{

    private static final long serialVersionUID = -8388976130587095832L;

    /**
     * コンストラクタ。
     */
    public ConnectException(){
        super();
    }

    /**
     * コンストラクタ。
     * 
     * @param message 例外メッセージ
     */
    public ConnectException(String message){
        super(message);    
    }
    
    /**
     * コンストラクタ。
     * 
     * @param message 例外メッセージ
     * @param cause 原因
     */
    public ConnectException(String message, Throwable cause){
        super(message, cause);
    }
    
    /**
     * コンストラクタ。
     * 
     * @param cause 原因
     */
    public ConnectException(Throwable cause){
        super(cause);
    }

}
