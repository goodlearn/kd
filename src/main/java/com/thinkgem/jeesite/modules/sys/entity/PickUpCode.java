/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.entity;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 取货码信息Entity
 * @author wzy
 * @version 2018-04-04
 */
public class PickUpCode extends DataEntity<PickUpCode> {
	
	private static final long serialVersionUID = 1L;
	private String codeKey;		// 快递键
	private String codeValue;		// 快递值
	private String companyKey;		// 快递公司
	
	
	public String getCode() {
		StringBuilder sb = new StringBuilder();
		sb.append(codeKey);
		sb.append("-");
		sb.append(codeValue);
		String result = sb.toString();
		return result;
	}
	
	public PickUpCode() {
		super();
	}

	public PickUpCode(String id){
		super(id);
	}

	@Length(min=1, max=100, message="快递键长度必须介于 1 和 100 之间")
	public String getCodeKey() {
		return codeKey;
	}

	public void setCodeKey(String codeKey) {
		this.codeKey = codeKey;
	}
	
	@Length(min=1, max=100, message="快递值长度必须介于 1 和 100 之间")
	public String getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}
	
	@Length(min=1, max=100, message="快递公司长度必须介于 1 和 100 之间")
	public String getCompanyKey() {
		return companyKey;
	}

	public void setCompanyKey(String companyKey) {
		this.companyKey = companyKey;
	}
	
}