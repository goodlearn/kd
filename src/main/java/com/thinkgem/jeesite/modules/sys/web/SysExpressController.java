package com.thinkgem.jeesite.modules.sys.web;

import java.util.List;

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
import com.thinkgem.jeesite.common.utils.PhoneUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.entity.PickUpCode;
import com.thinkgem.jeesite.modules.sys.entity.SysExpress;
import com.thinkgem.jeesite.modules.sys.service.SysExpressService;
import com.thinkgem.jeesite.modules.sys.service.WxService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 快递信息表Controller
 * @author wzy
 * @version 2017-12-25
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/sysExpress")
public class SysExpressController extends BaseController {

	@Autowired
	private SysExpressService sysExpressService;
	
	@Autowired
	private WxService wxService;
	
	@ModelAttribute
	public SysExpress get(@RequestParam(required=false) String id) {
		SysExpress entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = sysExpressService.get(id);
		}
		if (entity == null){
			entity = new SysExpress();
		}
		return entity;
	}
	
	@RequiresPermissions("sys:sysExpress:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysExpress sysExpress, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysExpress> page = sysExpressService.findPage(new Page<SysExpress>(request, response), sysExpress); 
		model.addAttribute("page", page);
		return "modules/sys/sysExpressList";
	}
	
	@RequiresPermissions("sys:sysExpress:view")
	@RequestMapping(value = "detailForm")
	public String detailForm(SysExpress sysExpress, Model model) {
		sysExpress = sysExpressService.findDetailExpressInfo(sysExpress);
		model.addAttribute("sysExpress", sysExpress);
		return "modules/sys/sysExpressDetailForm";
	}

	@RequiresPermissions("sys:sysExpress:view")
	@RequestMapping(value = "editForm")
	public String editForm(SysExpress sysExpress, Model model) {
		model.addAttribute("sysExpress", sysExpress);
		return "modules/sys/sysExpressEditForm";
	}
	
	
	
	@RequiresPermissions("sys:sysExpress:edit")
	@RequestMapping(value = "addForm")
	public String addForm(SysExpress sysExpress, Model model) {
		model.addAttribute("sysExpress", sysExpress);
		return "modules/sys/sysExpressAddForm";
	}
	
	//取货
	@RequiresPermissions("sys:sysExpress:edit")
	@RequestMapping(value = "endExpress")
	public String endExpress(SysExpress sysExpress, Model model) {
		if (!beanValidator(model, sysExpress)){
			return editForm(sysExpress, model);
		}
		String redirectUrl = "redirect:"+Global.getAdminPath()+"/sys/sysExpress/endFormList";
		//验证快递单号
		String expressId = sysExpress.getExpressId();
		if(null==expressId) {
			addMessage(model, "快递单号不能为空");
			return redirectUrl;
		}
		
		String expressState = sysExpress.getState();
		String startState = DictUtils.getDictValue("已入库", "expressState", "0");
		if(null == expressState || !expressState.equals(startState)) {
			addMessage(model, "快递状态非入库状态，快递单号为:"+expressId);
			return redirectUrl;
		}
		String state = DictUtils.getDictValue("已完结", "expressState", "1");
		sysExpress.setState(state);
		sysExpressService.end(sysExpress,UserUtils.getUser());
		
		return redirectUrl;
	}
	
	/**
	 * 获取已取货页面
	 * @param sysExpress
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:sysExpress:view")
	@RequestMapping(value = {"endForm", "endFormList"})
	public String endForm(SysExpress sysExpress, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		//获取搜索值
		String searchValue = request.getParameter("searchValue");
		sysExpress.setSearchUnEndValue(searchValue);
		//如果是已经完结取货的不再查询
		String state = DictUtils.getDictValue("已完结", "expressState", "0");
		sysExpress.setState(state);
		Page<SysExpress> page = sysExpressService.findUnEndPage(new Page<SysExpress>(request, response), sysExpress); 
		model.addAttribute("page", page);
		if(null!=searchValue) {
			model.addAttribute("searchValue", searchValue);
		}
		return "modules/sys/sysExpressEndForm";
	}
	
	@RequiresPermissions("sys:sysExpress:edit")
	@RequestMapping(value = "endBatchForm")
	public String endBatchForm(HttpServletRequest request, HttpServletResponse response, Model model) {
		String[] sysExpressIds = request.getParameterValues("sysExpressIds");
		if(null!=sysExpressIds&&sysExpressIds.length>0) {
			sysExpressService.saveBatchEnd(sysExpressIds);
		}
		return "redirect:"+Global.getAdminPath()+"/sys/sysExpress/endFormList";
	}

	
	//入库
	@RequiresPermissions("sys:sysExpress:edit")
	@RequestMapping(value = "save")
	public String save(SysExpress sysExpress, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysExpress)){
			return addForm(sysExpress, model);
		}
		
		//验证手机号
		String phone = sysExpress.getPhone();
		if(null == phone) {
			addMessage(model, "手机号不能为空");
			return addForm(sysExpress, model);
		}
		if(!PhoneUtils.validatePhone(phone)) {
			addMessage(model, "手机号格式错误");
			return addForm(sysExpress, model);
		}
		
		//验证快递单号
		String expressId = sysExpress.getExpressId();
		if(null==expressId) {
			addMessage(model, "快递单号不能为空");
			return addForm(sysExpress, model);
		}
		
		try {
			PickUpCode queryPickUpCode = new PickUpCode();
			queryPickUpCode.setCompanyKey(sysExpress.getCompany());
			List<PickUpCode> resultPickUpCodeList = wxService.findPickUpCode(queryPickUpCode);
			if(null == resultPickUpCodeList || resultPickUpCodeList.size() == 0||resultPickUpCodeList.size()>1) {
				addMessage(model, "没有对应的取货码");
				return addForm(sysExpress, model);
			}
			
			//默认保存快递状态为已入库
			String state = DictUtils.getDictValue("已入库", "expressState", "0");
			sysExpress.setState(state);
			//保存成功
			sysExpress = wxService.saveExpress(sysExpress,UserUtils.getUser());
			//发送消息
			if(null == sysExpressService.sendMsgTemplate(sysExpress, UserUtils.getUser()) ) {
				//微信发送失败
				//改用短信发送
				String returnMsg = wxService.sendAliyunMsgTemplate(sysExpress, UserUtils.getUser());
				if(null!=returnMsg) {
					addMessage(redirectAttributes, returnMsg + " 快递已入库");
				}else {
					addMessage(redirectAttributes, "消息发送成功,快递已入库");
				}
			}else {
				addMessage(redirectAttributes, "快递已入库");
			}
		}catch(Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "请稍后操作，有其它用户正在保存数据");
			return "redirect:"+Global.getAdminPath()+"/sys/sysExpress/?repage";
		}
		return "redirect:"+Global.getAdminPath()+"/sys/sysExpress/?repage";
	}
	
	@RequiresPermissions("sys:sysExpress:edit")
	@RequestMapping(value = "edit")
	public String edit(SysExpress sysExpress, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysExpress)){
			return editForm(sysExpress, model);
		}
		
		//验证手机号
		String phone = sysExpress.getPhone();
		if(null == phone) {
			addMessage(redirectAttributes, "手机号不能为空");
			return editForm(sysExpress, model);
		}
		if(!PhoneUtils.validatePhone(phone)) {
			addMessage(redirectAttributes, "手机号格式错误");
			return editForm(sysExpress, model);
		}
		
		//验证快递单号
		String expressId = sysExpress.getExpressId();
		if(null==expressId) {
			addMessage(redirectAttributes, "快递单号不能为空");
			return editForm(sysExpress, model);
		}
		
		sysExpressService.updateExpress(sysExpress);
		addMessage(redirectAttributes, "保存快递表成功");
		return "redirect:"+Global.getAdminPath()+"/sys/sysExpress/?repage";
	}
	
	@RequiresPermissions("sys:sysExpress:delete")
	@RequestMapping(value = "delete")
	public String delete(SysExpress sysExpress, RedirectAttributes redirectAttributes) {
		sysExpressService.delete(sysExpress);
		addMessage(redirectAttributes, "删除快递表成功");
		return "redirect:"+Global.getAdminPath()+"/sys/sysExpress/?repage";
	}

}