package jp.ghostnotes.gramtap.android.api;

/**
 * レスポンス例外。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class ResponseException extends Exception{

    private static final long serialVersionUID = -1652611442839532608L;
    
    /** レスポンスコード */
    private int responseCode = 0;
    /** レスポンスメッセージ */
    private String responseMessage = null;

    /**
     * コンストラクタ。
     */
    public ResponseException(){
        super();
    }
    
    /**
     * コンストラクタ。
     * 
     * @param responseCode レスポンスコード
     * @param responseMessage レスポンスメッセージ
     */
    public ResponseException(int responseCode, String responseMessage){
        super(responseCode + ": " + responseMessage);
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    /**
     * コンストラクタ。
     * 
     * @param message 例外メッセージ
     */
    public ResponseException(String message){
        super(message);    
    }
    
    /**
     * コンストラクタ。
     * 
     * @param message 例外メッセージ
     * @param cause 原因
     */
    public ResponseException(String message, Throwable cause){
        super(message, cause);
    }
    
    /**
     * コンストラクタ。
     * 
     * @param cause 原因
     */
    public ResponseException(Throwable cause){
        super(cause);
    }

    /**
     * レスポンスコードを取得する。
     * 
     * @return レスポンスコード
     */
    public int getResponseCode(){
        return responseCode;
    }

    /**
     * レスポンスコードを設定する。
     * 
     * @param responseCode レスポンスコード
     */
    public void setResponseCode(int responseCode){
        this.responseCode = responseCode;
    }

    /**
     * レスポンスメッセージを取得する。
     * 
     * @return レスポンスメッセージ
     */
    public String getResponseMessage(){
        return responseMessage;
    }

    /**
     * レスポンスメッセージを設定する。
     * 
     * @param responseMessage レスポンスメッセージ
     */
    public void setResponseMessage(String responseMessage){
        this.responseMessage = responseMessage;
    }

}
