package jp.ghostnotes.gramtap.android.api.convert;

/**
 * 変換例外。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class ConvertException extends Exception{
    private static final long serialVersionUID = -4036460583148641758L;

    /**
     * コンストラクタ。
     */
    public ConvertException(){
        super();
    }
    
    /**
     * コンストラクタ。
     * 
     * @param message 例外メッセージ
     */
    public ConvertException(String message){
        super(message);    
    }
    
    /**
     * コンストラクタ。
     * 
     * @param message 例外メッセージ
     * @param cause 原因
     */
    public ConvertException(String message, Throwable cause){
        super(message, cause);
    }
    
    /**
     * コンストラクタ。
     * 
     * @param cause 原因
     */
    public ConvertException(Throwable cause){
        super(cause);
    }

}
