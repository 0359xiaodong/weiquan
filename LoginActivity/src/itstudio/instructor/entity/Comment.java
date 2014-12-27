package itstudio.instructor.entity;

import java.util.Date;

/**
 * Comment entity. @author MyEclipse Persistence Tools
 */

public class Comment implements java.io.Serializable {

	// Fields

	private Integer id;
	private News news;// 根据评论可以找到 消息
	private String authorName;
	private String authorId;
	private String replyId;// 被回复者的id
	private String replyName;
	private String content;
	private Date postTime;
	
	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}


	// Constructors



	/** default constructor */
	public Comment() {
	}

	/** full constructor */
	public Comment(Integer id, String authorId, News news, String authorName,
			String replyId, String replyName, String content, Date postTime) {
		this.id = id;
		this.authorId = authorId;
		this.news = news;
		this.authorName = authorName;
		this.replyId = replyId;
		this.replyName = replyName;
		this.content = content;
		this.postTime = postTime;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}



	public News getNews() {
		return this.news;
	}

	public void setNews(News news) {
		this.news = news;
	}

	public String getAuthorName() {
		return this.authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getReplyId() {
		return this.replyId;
	}

	public void setReplyId(String replyId) {
		this.replyId = replyId;
	}

	public String getReplyName() {
		return this.replyName;
	}

	public void setReplyName(String replyName) {
		this.replyName = replyName;
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

}