package com.thinkgem.jeesite.modules.sys.test;

import java.util.UUID;

import org.springframework.util.StringUtils;

import com.thinkgem.jeesite.modules.sys.entity.SysExpress;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.WxService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
* @author wzy
* @version 创建时间：2018年4月4日 下午5:33:30
* @ClassName 类名称
* @Description 类描述
*/
public class PickUpCodeThread2 implements Runnable{
	
	private WxService wxService;
	
	public PickUpCodeThread2(WxService wxService) {
		this.wxService = wxService;
	}

	@Override
	public void run() {
		int count = 10;
		while(count>0) {
			System.out.println("thread2 start"+count);
			String expressId = UUID.randomUUID().toString();
			String phone = "15904793789";
			String company = "4";//韵达
			SysExpress sysExpress = new SysExpress();
			User user = UserUtils.get("1");
			//默认保存快递状态为已入库
			String state = DictUtils.getDictValue("已入库", "expressState", "0");
			sysExpress.setState(state);
			sysExpress.setExpressId(expressId);
			sysExpress.setPhone(phone);
			if(!StringUtils.isEmpty(company)) {
				sysExpress.setCompany(company);
			}else {
				sysExpress.setCompany("0");
			}
			wxService.saveExpress(sysExpress, user);
			count--;
		}
	}

}
