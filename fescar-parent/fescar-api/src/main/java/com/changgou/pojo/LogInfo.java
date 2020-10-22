package com.changgou.pojo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/****
 * @Author:shenkunlin
 * @Description:CouponInfo构建
 * @Date 2019/6/14 19:13
 *****/
@Table(name="log_info")
public class LogInfo implements Serializable{

	@Id
    @Column(name = "id")
	private Integer id;//

    @Column(name = "createtime")
	private Date createtime;//

    @Column(name = "content")
	private String content;//

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
