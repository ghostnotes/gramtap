package jp.ghostnotes.gramtap.android.api;

import java.io.Serializable;

/**
 * InstagramAPI結果。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class InstagramResult implements Serializable{

    private static final long serialVersionUID = 1291439886786960890L;

    /** 結果コード */
    private int code = -1;
    /** 結果オブジェクト */
    private Object resultObject = null;
    

    public void setCode(int code){
        this.code = code;
    }
    
    public int getCode(){
        return code;
    }

    /**
     * 結果オブジェクトを設定する。
     * 
     * @param resultObject 結果オブジェクト
     */
    public void setResultObject(Object resultObject){
        this.resultObject = resultObject;
    }
    
    /**
     * 結果オブジェクトを取得する。
     * 
     * @return 結果オブジェクト
     */
    public Object getResultObject(){
        return resultObject;
    }

}
