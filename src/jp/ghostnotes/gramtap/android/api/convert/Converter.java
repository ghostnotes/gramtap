package jp.ghostnotes.gramtap.android.api.convert;

/**
 * 変換インターフェース。
 * 
 * 
 * @author taniguchi@paperboy.co.jp
 */
public interface Converter{

    /**
     * 指定された文字列をコンバートする。
     * 
     * @param str コンバート対象文字列
     * @param data コンバートに必要なデータ
     * @return コンバートオブジェクト
     * @throws ConvertException
     */
    Object convert(String str, Object data) throws ConvertException;
}
