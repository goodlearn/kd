/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.modules.sys.entity.SysWxInfo;
import com.thinkgem.jeesite.modules.sys.entity.SysWxUser;
import com.thinkgem.jeesite.modules.sys.entity.SysWxUserCheck;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.modules.sys.dao.SysWxUserCheckDao;

/**
 * 信信息检查表Service
 * @author wzy
 * @version 2018-01-06
 */
@Service
@Transactional(readOnly = true)
public class SysWxUserCheckService extends CrudService<SysWxUserCheckDao, SysWxUserCheck> {

	@Autowired
	private WxService wxService;
	
	public SysWxUserCheck get(String id) {
		return super.get(id);
	}
	
	public List<SysWxUserCheck> findList(SysWxUserCheck sysWxUserCheck) {
		return super.findList(sysWxUserCheck);
	}
	
	public Page<SysWxUserCheck> findPage(Page<SysWxUserCheck> page, SysWxUserCheck sysWxUserCheck) {
		return super.findPage(page, sysWxUserCheck);
	}
	
	@Transactional(readOnly = false)
	public void save(SysWxUserCheck sysWxUserCheck) {
		super.save(sysWxUserCheck);
		
		//添加用户表和微信表
		SysWxUser sysWxUser = new SysWxUser();
		SysWxInfo sysWxInfo = new SysWxInfo();
		User user = UserUtils.getUser();
		
		//添加操作信息
		sysWxUser.setId(IdGen.uuid());
		sysWxUser.setName(sysWxUserCheck.getName());
		sysWxUser.setIdCard(sysWxUserCheck.getIdCard());
		sysWxUser.setPhone(sysWxUserCheck.getPhone());
		sysWxUser.setIdcardImg(sysWxUserCheck.getRemarks());
		sysWxUser.setUpdateBy(user);
		sysWxUser.setUpdateDate(new Date());
		sysWxUser.setCreateBy(user);
		sysWxUser.setCreateDate(new Date());
		
		//可能之前授权过了 此处要检查更新
		String openId = sysWxUserCheck.getOpenId();
		SysWxInfo entityWxInfo = wxService.findSysWxInfoByOpenId(openId);
		if(null == entityWxInfo) {
			sysWxInfo.setId(IdGen.uuid());
			sysWxInfo.setIdCard(sysWxUserCheck.getIdCard());
			sysWxInfo.setOpenId(sysWxUserCheck.getOpenId());
			sysWxInfo.setExpressNum("0");
			sysWxInfo.setUpdateBy(user);
			sysWxInfo.setUpdateDate(new Date());
			sysWxInfo.setCreateBy(user);
			sysWxInfo.setCreateDate(new Date());
			wxService.saveSysWxInfoInfo(sysWxInfo);
		}else {
			entityWxInfo.setIdCard(sysWxUserCheck.getIdCard());
			entityWxInfo.setUpdateBy(user);
			entityWxInfo.setUpdateDate(new Date());
			wxService.updateSysWxInfoInfo(entityWxInfo);
		}
		
		//保存信息
		wxService.saveSysWxUserInfo(sysWxUser);
		
		//发送消息
		String name = sysWxUserCheck.getName();
		if(null!=openId) {
			wxService.sendMessageActive(openId, name);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(SysWxUserCheck sysWxUserCheck) {
		super.delete(sysWxUserCheck);
	}
	
}