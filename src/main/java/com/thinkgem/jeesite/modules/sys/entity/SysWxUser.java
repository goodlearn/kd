package com.thinkgem.jeesite.modules.sys.entity;


import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.common.supcan.annotation.treelist.cols.SupCol;

/**
 * 微信用户表Entity
 * @author wzy
 * @version 2017-12-25
 */
public class SysWxUser extends DataEntity<SysWxUser> {
	
	private static final long serialVersionUID = 1L;
	private String idCard;		// 身份证号
	private String name;		// 姓名
	private String phone;		// 手机
	private String idcardImg;//图片信息
	
	public SysWxUser() {
		super();
	}

	@Length(min=0, max=255, message="姓名长度必须介于 0 和 255之间")
	public String getIdcardImg() {
		return idcardImg;
	}



	public void setIdcardImg(String idcardImg) {
		this.idcardImg = idcardImg;
	}



	public SysWxUser(String id){
		super(id);
	}

	@SupCol(isUnique="true")
	@Length(min=1, max=100, message="身份证号长度必须介于 1 和 100 之间")
	public String getIdCard() {
		return idCard;
	}

	@SupCol(isUnique="true")
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	
	@Length(min=0, max=100, message="姓名长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@SupCol(isUnique="true")
	@Length(min=1, max=200, message="手机长度必须介于 1 和 200 之间")
	public String getPhone() {
		return phone;
	}

	@SupCol(isUnique="true")
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}