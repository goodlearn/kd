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
import com.thinkgem.jeesite.modules.sys.entity.PickUpCode;
import com.thinkgem.jeesite.modules.sys.service.PickUpCodeService;

/**
 * 取货码信息Controller
 * @author wzy
 * @version 2018-04-04
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/pickUpCode")
public class PickUpCodeController extends BaseController {

	@Autowired
	private PickUpCodeService pickUpCodeService;
	
	@ModelAttribute
	public PickUpCode get(@RequestParam(required=false) String id) {
		PickUpCode entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = pickUpCodeService.get(id);
		}
		if (entity == null){
			entity = new PickUpCode();
		}
		return entity;
	}
	
	@RequiresPermissions("sys:pickUpCode:view")
	@RequestMapping(value = {"list", ""})
	public String list(PickUpCode pickUpCode, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PickUpCode> page = pickUpCodeService.findPage(new Page<PickUpCode>(request, response), pickUpCode); 
		model.addAttribute("page", page);
		return "modules/sys/pickUpCodeList";
	}

	@RequiresPermissions("sys:pickUpCode:view")
	@RequestMapping(value = "form")
	public String form(PickUpCode pickUpCode, Model model) {
		model.addAttribute("pickUpCode", pickUpCode);
		return "modules/sys/pickUpCodeForm";
	}

	@RequiresPermissions("sys:pickUpCode:edit")
	@RequestMapping(value = "save")
	public String save(PickUpCode pickUpCode, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, pickUpCode)){
			return form(pickUpCode, model);
		}
		pickUpCodeService.save(pickUpCode);
		addMessage(redirectAttributes, "保存取货码信息成功");
		return "redirect:"+Global.getAdminPath()+"/sys/pickUpCode/?repage";
	}
	
	@RequiresPermissions("sys:pickUpCode:edit")
	@RequestMapping(value = "delete")
	public String delete(PickUpCode pickUpCode, RedirectAttributes redirectAttributes) {
		pickUpCodeService.delete(pickUpCode);
		addMessage(redirectAttributes, "删除取货码信息成功");
		return "redirect:"+Global.getAdminPath()+"/sys/pickUpCode/?repage";
	}

}