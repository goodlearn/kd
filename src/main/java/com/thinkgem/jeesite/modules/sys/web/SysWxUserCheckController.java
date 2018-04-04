package com.thinkgem.jeesite.modules.sys.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.entity.SysWxUserCheck;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.SysWxUserCheckService;
import com.thinkgem.jeesite.modules.sys.service.WxService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 信信息检查表Controller
 * @author wzy
 * @version 2018-01-06
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/sysWxUserCheck")
public class SysWxUserCheckController extends BaseController {

	@Autowired
	private SysWxUserCheckService sysWxUserCheckService;
	
	@Autowired
	private WxService wxService;
	
	@ModelAttribute
	public SysWxUserCheck get(@RequestParam(required=false) String id) {
		SysWxUserCheck entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = sysWxUserCheckService.get(id);
		}
		if (entity == null){
			entity = new SysWxUserCheck();
		}
		return entity;
	}
	
	@RequiresPermissions("sys:sysWxUserCheck:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysWxUserCheck sysWxUserCheck, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysWxUserCheck> page = sysWxUserCheckService.findPage(new Page<SysWxUserCheck>(request, response), sysWxUserCheck); 
		model.addAttribute("page", page);
		return "modules/sys/sysWxUserCheckList";
	}

	
	/**
	 * 激活
	 * @param sysWxUserCheck
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:sysWxUserCheck:edit")
	@RequestMapping(value = "refuse")
	public String refuse(SysWxUserCheck sysWxUserCheck, Model model,RedirectAttributes redirectAttributes) {
		
		String retPath = "redirect:"+Global.getAdminPath()+"/sys/sysWxUserCheck/?repage";
		
		//验证
		if (!beanValidator(model, sysWxUserCheck)){
			return retPath;
		}
		
		if(null == sysWxUserCheck.getOpenId()) {
			addMessage(redirectAttributes, "OpenId数据错误,空值");
			return retPath;
		}
		
		String state = sysWxUserCheck.getState();
		if(null == state) {
			addMessage(redirectAttributes, "数据错误,空值");
			return retPath;
		}
		
		if(!"0".equals(state)) {
			addMessage(redirectAttributes, "该用户已激活");
			return retPath;
		}
		User user = UserUtils.getUser();
		String userName = user.getName();
		wxService.sendMessageCheck(sysWxUserCheck.getOpenId(),userName);
		addMessage(redirectAttributes, "信息已发送");
		model.addAttribute("sysWxUserCheck", sysWxUserCheck);
		return retPath;
	}
	
	/**
	 * 激活
	 * @param sysWxUserCheck
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:sysWxUserCheck:edit")
	@RequestMapping(value = "active")
	public String active(SysWxUserCheck sysWxUserCheck, Model model,RedirectAttributes redirectAttributes) {
		
		String retPath = "redirect:"+Global.getAdminPath()+"/sys/sysWxUserCheck/?repage";
		
		//验证
		if (!beanValidator(model, sysWxUserCheck)){
			return retPath;
		}
		
		String state = sysWxUserCheck.getState();
		if(null == state) {
			addMessage(redirectAttributes, "数据错误,空值");
			return retPath;
		}
		
		if(!"0".equals(state)) {
			addMessage(redirectAttributes, "该用户已激活");
			return retPath;
		}
		
		sysWxUserCheck.setState(DictUtils.getDictValue("已激活", "userCheckState", "1"));
		sysWxUserCheckService.save(sysWxUserCheck);
		addMessage(redirectAttributes, "用户已激活");
		model.addAttribute("sysWxUserCheck", sysWxUserCheck);
		return retPath;
	}

	

	@RequiresPermissions("sys:sysWxUserCheck:view")
	@RequestMapping(value = "form")
	public String form(SysWxUserCheck sysWxUserCheck, Model model) {
		model.addAttribute("sysWxUserCheck", sysWxUserCheck);
		return "modules/sys/sysWxUserCheckForm";
	}

	@RequiresPermissions("sys:sysWxUserCheck:edit")
	@RequestMapping(value = "save")
	public String save(SysWxUserCheck sysWxUserCheck, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysWxUserCheck)){
			return form(sysWxUserCheck, model);
		}
		sysWxUserCheckService.save(sysWxUserCheck);
		addMessage(redirectAttributes, "保存信信息检查表成功");
		return "redirect:"+Global.getAdminPath()+"/sys/sysWxUserCheck/?repage";
	}
	
	@RequiresPermissions("sys:sysWxUserCheck:delete")
	@RequestMapping(value = "delete")
	public String delete(SysWxUserCheck sysWxUserCheck, RedirectAttributes redirectAttributes) {
		sysWxUserCheckService.delete(sysWxUserCheck);
		addMessage(redirectAttributes, "删除信信息检查表成功");
		return "redirect:"+Global.getAdminPath()+"/sys/sysWxUserCheck/?repage";
	}

}