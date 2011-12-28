package jp.ghostnotes.gramtap.android.widget;

import jp.ghostnotes.gramtap.android.R;
import jp.ghostnotes.gramtap.android.bean.Comment;
import jp.ghostnotes.gramtap.android.bean.User;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * コメントアダプタ。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class CommentAdapter extends ArrayAdapter<Comment>{
    /** 画面 */
//    private Activity activity = null;
    /** レイアウトリソースID */
    private int resId = -1;
    
    /**
     * コンストラクタ。
     * 
     * @param activity 画面
     * @param resId リソースID
     */
    public CommentAdapter(
        Context context,
        int resId
    ){
        super(context, resId);
//        this.activity = activity;
        this.resId = resId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(resId, null);
        }
        
        // コメントを取得
        Comment comment = getItem(position);
        User fromUser = comment.getFrom();

        // コメント文字列を設定
        TextView commentView = (TextView)view.findViewById(R.id.comment);
        
        String userName = "";
        if(fromUser != null){
            userName = "[" + fromUser.getUserName() + "] ";
        }
        
        commentView.setText(userName + comment.getText());

        return view;
    }

}
