package jp.ghostnotes.gramtap.android.api.convert;

/**
 * 変換タイプ。
 * 
 * @author ghostnotesdot@gmail.com
 */
public enum ConvertType{
    /** 変換タイプ: ポピュラー */
    Popular,
    /** 変換タイプ: #prayforjapan */
    PrayForJapan,
    /** 変換タイプ: フィード */
    SelfFeed,
    /** 変換タイプ: アクセストークン */
    AccessToken,
    /** 変換タイプ: XAuth */
    XAuth,
    /** 変換タイプ: ページネーション */
    Pagination,
    /** 変換タイプ: いいね */
    Like,
    
    LikeUserInfo,
    AlreadyLiked
}
