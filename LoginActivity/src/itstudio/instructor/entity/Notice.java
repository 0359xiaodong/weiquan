package itstudio.instructor.entity;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Notice entity. @author MyEclipse Persistence Tools
 */

public class Notice implements java.io.Serializable {

	// Fields

		private Integer id;
		private String adminId;
		private String authorName;
		private String title;
		private String content;
		private Date postTime;
		private String noticePics;


		public String getAdminId() {
			return adminId;
		}

		public void setAdminId(String adminId) {
			this.adminId = adminId;
		}

		public String getNoticePics() {
			return noticePics;
		}

		public void setNoticePics(String noticePics) {
			this.noticePics = noticePics;
		}



		/** default constructor */
		public Notice() {
		}


		// Property accessors

		public Integer getId() {
			return this.id;
		}

		public void setId(Integer id) {
			this.id = id;
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

		

}