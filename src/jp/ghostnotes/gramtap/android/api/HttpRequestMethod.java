package jp.ghostnotes.gramtap.android.api;

/**
 * HTTPリクエストメソッド。
 * 
 * @author ghostnotesdot@gmail.com
 */
public enum HttpRequestMethod{
    /** HTTPリクエストメソッド: GET */
    GET("GET"),
    /** HTTPリクエストメソッド: POST */
    POST("POST"),
    /** HTTPリクエストメソッド: DELETE */
    DELETE("DELETE");
    
    private String text = null;
    private HttpRequestMethod(String text){
        this.text = text;
    }
    
    /**
     * HTTPリクエストメソッド文字列を取得する。
     * 
     * @return HTTPリクエストメソッド文字列
     */
    public String getText(){
        return text;
    }
}
