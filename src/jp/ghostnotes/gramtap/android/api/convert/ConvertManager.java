package jp.ghostnotes.gramtap.android.api.convert;

import java.util.HashMap;
import java.util.Map;

/**
 * 変換マネージャ。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class ConvertManager{
    /** キーに変換タイプ、値に変換クラス */
    private Map<ConvertType, Converter> converters = null;

    /**
     * コンストラクタ。
     */
    public ConvertManager(){
        converters = new HashMap<ConvertType, Converter>();
        // ポピュラー変換
        converters.put(ConvertType.Popular, new InstagramPaginationConverter());
        // pray for japan
//        converters.put(ConvertType.PrayForJapan, new PrayForJapanPaginationConverter());
        converters.put(ConvertType.PrayForJapan, new InstagramPaginationConverter());
        // 自分のフィード
        converters.put(ConvertType.SelfFeed, new InstagramPaginationConverter());
        // アクセストークン
        converters.put(ConvertType.AccessToken, new AccessTokenConverter());
        // XAuthリクエスト
        converters.put(ConvertType.XAuth, new AccessTokenConverter());
        // ページネーション
        converters.put(ConvertType.Pagination, new InstagramPaginationConverter());
        // いいね
        converters.put(ConvertType.Like, new LikeConverter());
        // いいねしたユーザ情報
//        converters.put(ConvertType.LikeUserInfo, new LikeUserInfoConverter());
        converters.put(ConvertType.AlreadyLiked, new AlreadyLikedCheckConveter());
    }

    /**
     * 指定された変換タイプで、文字列を変換する。
     * 
     * @param convertType 変換タイプ
     * @param str 変換対象文字列
     * @return 変換後オブジェクト
     * @throws ConvertException
     */
    public Object convert(ConvertType convertType, String str) throws ConvertException{
//        if(converters.containsKey(convertType)){
//            Converter converter = converters.get(convertType);
//            return converter.convert(str);
//        }
//        
//        return null;
        return convert(convertType, str, null);
    }
    
    public Object convert(ConvertType convertType, String str, Object data) throws ConvertException{
        if(converters.containsKey(convertType)){
            Converter converter = converters.get(convertType);
            return converter.convert(str, data);
        }
        
        return null;
    }

}
