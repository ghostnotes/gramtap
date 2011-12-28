package jp.ghostnotes.gramtap.android.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ghostnotes.gramtap.android.util.UserManager;

import android.content.Context;

/**
 * Instagarm画像クラス。
 * 
 * @author ghostnotesdot@gmail.com
 */
public class InstagramImage implements Serializable{
    private static final long serialVersionUID = 483145208721076341L;
    
    /**  */
    private Map<InstagramImageSize, InstagramImageInformation> imageInfos = null;
    private String link = null;

    /** 画像ID */
    private String id = null;
    /** いいね数 */
    private int likeCount = 0;
    /** いいね */
    private List<Long> likeUserIdList = null;
    private List<Comment> commentList = null;
    private Date createTime = null;
    private Caption caption = null;
    
    private User user = null;
    private boolean isDummy = false;
    private String nextUrl = null;
    
    private LikeState likeState = LikeState.None;
    
    public void setLikeState(LikeState likeState){
        this.likeState = likeState;
    }
    
    public LikeState getLikeState(){
        return likeState;
    }
    
//    private boolean isRequestingLike = false;
    
//    private boolean isCheckingLike = false;
//    private boolean isLikeChecked = false;
    
    
    
//    public void setLikeChecked(boolean isLikeChecked){
//        this.isLikeChecked = isLikeChecked;
//    }
//    
//    public boolean isLikeChecked(){
//        return isLikeChecked;
//    }

    /**
     * コンストラクタ。
     */
    public InstagramImage(){
        imageInfos = new HashMap<InstagramImageSize, InstagramImageInformation>();
        commentList = new ArrayList<Comment>();
        likeUserIdList = new ArrayList<Long>();
    }
    
    public void setId(String id){
        this.id = id;
    }
    
    public String getId(){
        return id;
    }
    
    public boolean isLiked(Context context){
        long loginUserId = UserManager.getInstance().getLoginUserId(context);
        
        return likeUserIdList.contains(loginUserId);
    }
    
    public void addImageInformation(InstagramImageInformation imageInfo){
        imageInfos.put(imageInfo.getImageSize(), imageInfo);
    }
    
    public String getUrl(InstagramImageSize imageType){
        if(imageInfos.containsKey(imageType)){
            InstagramImageInformation imageInfo = imageInfos.get(imageType);
            return imageInfo.getUrl();
        }
        
        return null;
    }
    
    public void setLink(String link){
        this.link = link;
    }
    
    public String getLink(){
        return link;
    }
    
//    public void setCommentSize(int size){
//        if(commentList != null){
//            commentList.clear();
//            commentList = null;
//        }
//    }
    
    public void setLikeCount(int count){
        likeCount = count;
    }
    
    public int getLikeCount(){
        return likeCount;
    }
    
    public void incrementLikeCount(){
        likeCount++;
    }
    
    public void decrementLikeCount(){
        likeCount--;
    }
    
    public void addLikeUserId(long id){
        if(!likeUserIdList.contains(id)){
            likeUserIdList.add(id);
            
//            likeCount++;
        }
    }
    
    
    public boolean removeLikeUserId(long id){
        if(likeUserIdList.contains(id)){
//            likeCount--;
           return likeUserIdList.remove(new Long(id));
        }
        
        return false;
    }
    
    public boolean containsLikeUserId(long id){
        return likeUserIdList.contains(id);
    }

    public void addComment(Comment comment){
        commentList.add(comment);
    }
    
    
    public List<Comment> getCommentList(){
        return commentList;
    }
    
    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }
    
    public Date getCreateTime(){
        return createTime;
    }
    
    public void setUser(User user){
        this.user = user;
    }
    
    public User getUser(){
        return user;
    }
    
    public void setCaption(Caption caption){
        this.caption = caption;
    }
    
    public Caption getCaption(){
        return caption;
    }
    
    public void setNextUrl(String nextUrl){
        this.nextUrl = nextUrl;
    }
    
    public String getNextUrl(){
        return nextUrl;
    }

    public void setDummy(boolean isDummy){
        this.isDummy = isDummy;
    }
    
    public boolean isDummy(){
        return isDummy;
    }
    
//    public void setRequestingLike(boolean isRequestingLike){
//        this.isRequestingLike = isRequestingLike;
//    }
//    
//    public boolean isRequestingLike(){
//        return isRequestingLike;
//    }

}
