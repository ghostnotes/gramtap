package jp.ghostnotes.gramtap.android;

/**
 * Instagram画像タイプ。
 * 
 * @author ghostnotesdot@gmail.com
 */
public enum InstagramImageType{
    /** Instagram画像タイプ: フィード */
    SelfFeed("feed"),
    /** Instagram画像タイプ: ポピュラー */
    Popular("popular"),
    /** Instagram画像タイプ: #prayforjapan */
    PrayForJapan("#prayforjapan");
    
    private String text = null;
    private InstagramImageType(String text){
        this.text = text;
    }
    
    /**
     * Instagram画像タイプ文字列を取得する。
     * 
     * @return Instagram画像タイプ
     */
    public String getText(){
        return text;
    }
}
