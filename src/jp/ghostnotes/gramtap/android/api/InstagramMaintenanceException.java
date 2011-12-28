package jp.ghostnotes.gramtap.android.api;

/**
 * メンテナンス例外。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class InstagramMaintenanceException extends Exception{

    private static final long serialVersionUID = 1531529301517046416L;
    
    /** レスポンスコード */
    private int responseCode = 0;
    /** レスポンスメッセージ */
    private String responseMessage = null;

    /**
     * コンストラクタ。
     */
    public InstagramMaintenanceException(){
        super();
    }
    
    /**
     * コンストラクタ。
     * 
     * @param responseCode レスポンスコード
     * @param responseMessage レスポンスメッセージ
     */
    public InstagramMaintenanceException(int responseCode, String responseMessage){
        super(responseCode + ": " + responseMessage);
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }
    
    /**
     * コンストラクタ。
     * 
     * @param message 例外メッセージ
     */
    public InstagramMaintenanceException(String message){
        super(message);    
    }
    
    /**
     * コンストラクタ。
     * 
     * @param message 例外メッセージ
     * @param cause 原因
     */
    public InstagramMaintenanceException(String message, Throwable cause){
        super(message, cause);
    }
    
    /**
     * コンストラクタ。
     * 
     * @param cause 原因
     */
    public InstagramMaintenanceException(Throwable cause){
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
