package com.thinkgem.jeesite.modules.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.sys.entity.PickUpCode;
import com.thinkgem.jeesite.modules.sys.dao.PickUpCodeDao;

/**
 * 取货码信息Service
 * @author wzy
 * @version 2018-04-04
 */
@Service
@Transactional(readOnly = true)
public class PickUpCodeService extends CrudService<PickUpCodeDao, PickUpCode> {

	public PickUpCode get(String id) {
		return super.get(id);
	}
	
	public List<PickUpCode> findList(PickUpCode pickUpCode) {
		return super.findList(pickUpCode);
	}
	
	public Page<PickUpCode> findPage(Page<PickUpCode> page, PickUpCode pickUpCode) {
		return super.findPage(page, pickUpCode);
	}
	
	@Transactional(readOnly = false)
	public void save(PickUpCode pickUpCode) {
		super.save(pickUpCode);
	}
	
	@Transactional(readOnly = false)
	public void delete(PickUpCode pickUpCode) {
		super.delete(pickUpCode);
	}
	
}