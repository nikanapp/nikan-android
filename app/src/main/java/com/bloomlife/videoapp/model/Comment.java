/**
 * 
 */
package com.bloomlife.videoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2014年12月2日 下午5:39:49
 */
public class Comment implements Parcelable{
	
	private String commentid;
	private String content;
	private String count;
	private String color;
	private String font;
	
	private int position;
	
	public Comment(){
		
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getCount() {
		return count;
	}
	
	public void setCount(String count) {
		this.count = count;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getCommentid() {
		return commentid;
	}

	public void setCommentid(String commentid) {
		this.commentid = commentid;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}
	
	public static Comment makeComment(Commenttags commenttags, String count, int position){
		Comment newComment = new Comment();
		newComment.setColor(commenttags.getColor());
		newComment.setCommentid(commenttags.getCommentid());
		newComment.setContent(commenttags.getContent());
		newComment.setFont(commenttags.getFont());
		newComment.setCount(count);
		newComment.setPosition(position);
		return newComment;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(commentid);
		dest.writeString(content);
		dest.writeString(count);
		dest.writeString(color);
		dest.writeString(font);
	}
	
	public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {

		@Override
		public Comment createFromParcel(Parcel source) {
			Comment comment = new Comment();
			comment.setCommentid(source.readString());
			comment.setContent(source.readString());
			comment.setCount(source.readString());
			comment.setColor(source.readString());
			comment.setFont(source.readString());
			return comment;
		}

		@Override
		public Comment[] newArray(int size) {
			return new Comment[size];
		}
	};

	@Override
	public boolean equals(Object o) {
		if (o instanceof Comment){
			return ((Comment)o).getContent().equals(content);
		}
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return content == null ? 0 : content.hashCode();
	}

}
