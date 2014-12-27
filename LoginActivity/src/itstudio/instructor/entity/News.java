package itstudio.instructor.entity;

import java.util.Date;
import java.util.List;
import java.util.Set;

import android.graphics.Bitmap;

/**
 * News entity. @author MyEclipse Persistence Tools
 */

public class News implements java.io.Serializable {

	// Fields
	private Integer id;
	private String authorName;
	private String title;
	private String content;
	private Date postTime;
	private short status;
	private Integer commentNum;
	private User user;// 消息发布者
	private String newsPics;
	private String date;
	private String[] picList;

	public String[] getPicList() {
		return picList;
	}

	public void setPicList(String[] picList) {
		this.picList = picList;
	}

	
	public String getNewsPics() {
		return newsPics;
	}

	public void setNewsPics(String newsPics) {
		this.newsPics = newsPics;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	

	// 图片地址 级联 采用懒加载
	// private List<NewsPic> newsPic;
	/*
	 * private Set<NewsPic> newsPic;
	 * 
	 * public Set<NewsPic> getNewsPic() { return newsPic; }
	 * 
	 * public void setNewsPic(Set<NewsPic> newsPic) { this.newsPic = newsPic; }
	 */

	

	/** default constructor */
	public News() {

	}

	public News(String title, String content, String newsPics, Integer newsid,String date) {
		super();
		this.title = title;
		this.content = content;
		this.newsPics = newsPics;
		this.id = newsid;
		this.date=date;
	}

	/** minimal constructor */
	public News(Integer id, User user, String authorName, String title,
			String content, Date postTime, short status) {
		this.id = id;
		this.user = user;
		this.authorName = authorName;
		this.title = title;
		this.content = content;
		this.postTime = postTime;
		this.status = status;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getAuthorName() {
		return this.authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getPostTime() {
		return this.postTime;
	}

	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}

	public short getStatus() {
		return this.status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	public Integer getCommentNum() {
		return this.commentNum;
	}

	public void setCommentNum(Integer commentNum) {
		this.commentNum = commentNum;
	}

}