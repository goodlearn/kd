package com.thinkgem.jeesite.modules.sys.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
import com.thinkgem.jeesite.common.utils.BasePathUtils;
import com.thinkgem.jeesite.common.utils.Encodes;
import com.thinkgem.jeesite.common.utils.IdcardUtils;
import com.thinkgem.jeesite.common.utils.PhoneUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.entity.SysWxUser;
import com.thinkgem.jeesite.modules.sys.service.SysWxUserService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;

/**
 * 微信用户表Controller
 * @author wzy
 * @version 2017-12-25
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/sysWxUser")
public class SysWxUserController extends BaseController {

	@Autowired
	private SysWxUserService sysWxUserService;
	
	@ModelAttribute
	public SysWxUser get(@RequestParam(required=false) String id) {
		SysWxUser entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = sysWxUserService.get(id);
		}
		if (entity == null){
			entity = new SysWxUser();
		}
		return entity;
	}
	
	@RequiresPermissions("sys:sysWxUser:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysWxUser sysWxUser, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysWxUser> page = sysWxUserService.findPage(new Page<SysWxUser>(request, response), sysWxUser); 
		
		String isServer = DictUtils.getDictValue("isServer", "systemControl", "0");
		String httpProtocol = DictUtils.getDictValue("httpProtocol", "systemControl", "http");
		String url = null;
		if("0".equals(isServer)) {
			url = BasePathUtils.getBasePathNoServer(request,true);
		}else {
			url = BasePathUtils.getBasePathNoServer(request,false);
		}
	    if("https".equals(httpProtocol)) {
	    	url = url.replace("http", "https");
	    }
	    
	    //附件URL
	    List<SysWxUser> userList = page.getList();
	    if(null!=userList && userList.size() > 0) {
	    	for(SysWxUser forSysWxUser : userList) {
	    		String imgIdCard = forSysWxUser.getIdcardImg();
	    		if(null!=imgIdCard) {
	    			imgIdCard = url + imgIdCard;
	    			forSysWxUser.setIdcardImg(imgIdCard);
	    		}
	    	}
	    }
	    
		model.addAttribute("page", page);
		return "modules/sys/sysWxUserList";
	}

	@RequiresPermissions("sys:sysWxUser:view")
	@RequestMapping(value = "form")
	public String form(SysWxUser sysWxUser, Model model) {
		model.addAttribute("sysWxUser", sysWxUser);
		return "modules/sys/sysWxUserForm";
	}
	
	@RequiresPermissions("sys:sysWxUser:edit")
	@RequestMapping(value = "idcardimg")
	public String idcardimg(SysWxUser sysWxUser, Model model,HttpServletResponse response,RedirectAttributes redirectAttributes) {
		try {
			File filename = null;
		/*	if(null == filename) {
				addMessage(redirectAttributes, "无附件");
				return null;
			}*/
			response.reset();
	        response.setContentType("application/octet-stream; charset=utf-8");
	        response.setHeader("Content-Disposition", "attachment; filename="+Encodes.urlEncode(filename.getName()));
	        //打开本地文件流
	        InputStream inputStream = new FileInputStream(filename);
	        //激活下载操作
	        OutputStream os = response.getOutputStream(); 
	        //循环写入输出流
	        byte[] b = new byte[2048];
	        int length;
	        while ((length = inputStream.read(b)) > 0) {
	            os.write(b, 0, length);
	        }

	        // 这里主要关闭。
	        os.close();
	        inputStream.close();
	        return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/sysWxUser/?repage";
	}
	
	@RequiresPermissions("sys:sysWxUser:edit")
	@RequestMapping(value = "update")
	public String update(SysWxUser sysWxUser, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysWxUser)){
			return form(sysWxUser, model);
		}
		
		//验证手机号
		String phone = sysWxUser.getPhone();
		if(null == phone) {
			addMessage(model, "手机号不能为空");
			return form(sysWxUser, model);
		}
		if(!PhoneUtils.validatePhone(phone)) {
			addMessage(model, "手机号格式错误");
			return form(sysWxUser, model);
		}
		
		//验证快递单号
		String idCard = sysWxUser.getIdCard();
		if(null==idCard) {
			addMessage(model, "身份证不能为空");
			return form(sysWxUser, model);
		}
		if(!IdcardUtils.validateCard(idCard)) {
			addMessage(model, "身份证格式错误");
			return form(sysWxUser, model);
		}
		
		
		String idImg = sysWxUser.getIdcardImg();
		if(null!=idImg&&idImg.startsWith("|")) {
			idImg = idImg.substring(1,idImg.length());
			sysWxUser.setIdcardImg(idImg);
		}
		
		sysWxUserService.save(sysWxUser);
		addMessage(redirectAttributes, "保存微信用户表成功");
		return "redirect:"+Global.getAdminPath()+"/sys/sysWxUser/?repage";
	}

	@RequiresPermissions("sys:sysWxUser:edit")
	@RequestMapping(value = "save")
	public String save(SysWxUser sysWxUser, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysWxUser)){
			return form(sysWxUser, model);
		}
		
		//验证手机号
		String phone = sysWxUser.getPhone();
		if(null == phone) {
			addMessage(model, "手机号不能为空");
			return form(sysWxUser, model);
		}
		if(!PhoneUtils.validatePhone(phone)) {
			addMessage(model, "手机号格式错误");
			return form(sysWxUser, model);
		}
		
		//验证快递单号
		String idCard = sysWxUser.getIdCard();
		if(null==idCard) {
			addMessage(model, "身份证不能为空");
			return form(sysWxUser, model);
		}
		if(!IdcardUtils.validateCard(idCard)) {
			addMessage(model, "身份证格式错误");
			return form(sysWxUser, model);
		}
		
		if(null!=sysWxUserService.getByPhone(phone)) {
			addMessage(model, "手机号已存在");
			return form(sysWxUser, model);
		}
		
		sysWxUserService.save(sysWxUser);
		addMessage(redirectAttributes, "保存微信用户表成功");
		return "redirect:"+Global.getAdminPath()+"/sys/sysWxUser/?repage";
	}
	
	@RequiresPermissions("sys:sysWxUser:edit")
	@RequestMapping(value = "delete")
	public String delete(SysWxUser sysWxUser, RedirectAttributes redirectAttributes) {
		sysWxUserService.delete(sysWxUser);
		addMessage(redirectAttributes, "删除微信用户表成功");
		return "redirect:"+Global.getAdminPath()+"/sys/sysWxUser/?repage";
	}

}