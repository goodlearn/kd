/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.entity;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 快递信息表Entity
 * @author wzy
 * @version 2017-12-25
 */
public class SysExpress extends DataEntity<SysExpress> {
	
	private static final long serialVersionUID = 1L;
	private String expressId;		// 快递单号
	private String phone;		// 手机
	private String state;		// 快递状态
	private String msgState;	//短信状态
	private String company;		//快递公司
	private String pickUpCode;  //取货码
	private String enterTime;//入货时间2018-01-01
	
	
	
	private String pickUpCodeShow;
	private SysWxUser sysWxUser;//微信用户
	private String searchUnEndValue;//未取货搜索条件
	
	
	public SysExpress() {
		super();
	}

	public String getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(String enterTime) {
		this.enterTime = enterTime;
	}

	public SysExpress(String id){
		super(id);
	}
	
	public SysWxUser getSysWxUser() {
		return sysWxUser;
	}

	public void setSysWxUser(SysWxUser sysWxUser) {
		this.sysWxUser = sysWxUser;
	}

	@Length(min=0, max=100, message="快递单号长度必须介于 0 和 100 之间")
	public String getExpressId() {
		return expressId;
	}

	public void setExpressId(String expressId) {
		this.expressId = expressId;
	}
	
	@Length(min=0, max=200, message="手机长度必须介于 0 和 200 之间")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSearchUnEndValue() {
		return searchUnEndValue;
	}

	public void setSearchUnEndValue(String searchUnEndValue) {
		this.searchUnEndValue = searchUnEndValue;
	}

	public String getMsgState() {
		return msgState;
	}

	public void setMsgState(String msgState) {
		this.msgState = msgState;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getPickUpCode() {
		return pickUpCode;
	}

	public void setPickUpCode(String pickUpCode) {
		this.pickUpCode = pickUpCode;
	}

	public String getPickUpCodeShow() {
		return getEnterTime() + "-" + getPickUpCode();
	}

	public void setPickUpCodeShow(String pickUpCodeShow) {
		this.pickUpCodeShow = pickUpCodeShow;
	}
	

}