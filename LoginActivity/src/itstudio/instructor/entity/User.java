package itstudio.instructor.entity;

import com.easemob.chat.EMContact;

public class User extends EMContact  implements java.io.Serializable {
    
 // Fields

    private String id;
    private String name;
    private String password;
    private String email;
    private Integer followNum;
    private Integer fansNum;
    private String intro;
    private String headUrl;
    private short status;
    private Integer newsNum;
    private Integer newReply;
    private String userJson;


    private boolean focus=false;// 是否已经关注   不做数据库映射
    public static final short STATUS_STUDENT = 0;// 学生
    public static final short STATUS_TREACHER = 1;//老师
    public static final short STATUS_PUBLIC = 2;//公众号
	private int unreadMsgCount;
	private String header;

    public String getUserJson() {
        return userJson;
    }

    public void setUserJson(String userJson) {
        this.userJson = userJson;
    }
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public int getUnreadMsgCount() {
		return unreadMsgCount;
	}

	public void setUnreadMsgCount(int unreadMsgCount) {
		this.unreadMsgCount = unreadMsgCount;
	}

	@Override
	public int hashCode() {
		return 17 * getUsername().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof User)) {
			return false;
		}
		return getUsername().equals(((User) o).getUsername());
	}

	@Override
	public String toString() {
		return nick == null ? username : nick;
	}
	public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }


    // Constructors

    /** default constructor */
    public User() {
        
    }

    /** minimal constructor */
    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;

    }

    /** full constructor */
    public User(String id, String name, String password, String email,
            Integer followNum, Integer fansNum, String intro,
            String headUrl, short status, Integer newsNum, Integer newReply) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.followNum = followNum;
        this.fansNum = fansNum;
        this.intro = intro;
        this.headUrl = headUrl;
        this.status = status;
        this.newsNum = newsNum;
        this.newReply = newReply;
    }

    // Property accessors

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getFollowNum() {
        return this.followNum;
    }

    public void setFollowNum(Integer followNum) {
        this.followNum = followNum;
    }

    public Integer getFansNum() {
        return this.fansNum;
    }

    public void setFansNum(Integer fansNum) {
        this.fansNum = fansNum;
    }

    public String getIntro() {
        return this.intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getHeadUrl() {
        return this.headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public short getStatus() {
        return this.status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public Integer getNewsNum() {
        return this.newsNum;
    }

    public void setNewsNum(Integer newsNum) {
        this.newsNum = newsNum;
    }

    public Integer getNewReply() {
        return this.newReply;
    }

    public void setNewReply(Integer newReply) {
        this.newReply = newReply;
    }
}
