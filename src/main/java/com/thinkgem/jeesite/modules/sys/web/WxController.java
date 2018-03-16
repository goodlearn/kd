package com.thinkgem.jeesite.modules.sys.web;

import java.io.File;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.config.WxGlobal;
import com.thinkgem.jeesite.common.entity.PhoneMsgCache;
import com.thinkgem.jeesite.common.entity.WxCodeCache;
import com.thinkgem.jeesite.common.utils.AliyunSendMsgUtils;
import com.thinkgem.jeesite.common.utils.BasePathUtils;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.common.utils.CasUtils;
import com.thinkgem.jeesite.common.utils.DeviceUtils;
import com.thinkgem.jeesite.common.utils.IdcardUtils;
import com.thinkgem.jeesite.common.utils.PhoneUtils;
import com.thinkgem.jeesite.common.utils.WxJsSkdUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.sys.entity.SysExpress;
import com.thinkgem.jeesite.modules.sys.entity.SysWxInfo;
import com.thinkgem.jeesite.modules.sys.entity.SysWxUser;
import com.thinkgem.jeesite.modules.sys.entity.SysWxUserCheck;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.manager.WxAccessTokenManager;
import com.thinkgem.jeesite.modules.sys.manager.WxMenuManager;
import com.thinkgem.jeesite.modules.sys.service.SysExpressService;
import com.thinkgem.jeesite.modules.sys.service.SysWxUserService;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.modules.sys.service.WxService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.modules.sys.view.JsonSysExpress;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value = "wx")
public class WxController extends BaseController {
	
	
	@Autowired
	private WxMenuManager wxMenuManager;
	
	@Autowired
	private WxService wxService;
	
	@Autowired
	private SystemService systemService;
	
	private final String MSG_PHONE_CODE_MSG = "验证码发送成功";
	private final String ERR_PHONE_NULL = "电话不能为空";
	private final String ERR_SAME_OLD_NEW_PHONE = "新旧手机号码一致";
	private final String ERR_PHONE_PATTERN = "手机号码格式不正确";
	private final String ERR_SAME_PHONE_NO_ACTIVE = "该号码已经注册，未审核激活，请携带身份证前往快递中心激活";
	private final String ERR_NEW_PHONE_PATTERN = "新手机号码格式不正确";
	private final String ERR_SAME_PHONE = "该号码已经注册，请绑定其它号码";
	private final String ERR_PROMPT_USER_CONFIRE = "您好，短信已发送，如果您没有收到，可能是网络有延迟，请耐心等待片刻";
	private final String ERR_TOO_MONEY = "今日次数已达上限";
	private final String ERR_SEND_MSG = "短信发送失败";
	private final String ERR_SEND_MSG_ALIYUN_LIMIT = "短信套餐量已经使用超量";
	private final String ERR_ID_CARD_PATTERN = "身份证号码格式不正确";
	private final String ERR_NAME_NULL = "姓名不能为空";
	private final String ERR_ID_CARD_NULL = "身份证号不能为空";

	//个人注册页面
	private final String WX_ID_CARD_USERINFO_ADD = "modules/wxp/wxIdCardUserInfoAdd";
	//修改个人信息
	private final String WX_ID_CARD_USERINFO_MODIFY = "modules/wxp/wxIdCardUserInfoModify";
	
	/**
	 * 测试使用(开发可删除)
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getJsApiSign",method=RequestMethod.GET)
	public String getJsApiSign(HttpServletRequest request, HttpServletResponse response,Model model) {
		Map<String, String> map = WxJsSkdUtils.getJsApiSign(request, response);
		String jsonResult = JSONObject.toJSONString(map);//将map对象转换成json类型数据
		System.out.println("jspApiTicket sign:"+jsonResult);
		return jsonResult;
	}
	
	@RequestMapping(value="/sendWxPhoneMsgCodeModify",method=RequestMethod.POST)
	@ResponseBody
	public String sendWxPhoneMsgCodeModify(HttpServletRequest request, HttpServletResponse response,Model model) {
		String userNewPhone = request.getParameter("usernewPhone").trim();
		String usernum = request.getParameter("usernum").trim();//老手机号
		String errCode_1 = "1";
		
		
		//电话号不能为空
		if(StringUtils.isEmpty(usernum)||StringUtils.isEmpty(userNewPhone)) {
			return backJsonWithCode(errCode_1,ERR_PHONE_NULL);
		}
		
		//手机号码格式不正确
		if(!PhoneUtils.validatePhone(usernum)||!PhoneUtils.validatePhone(userNewPhone)) {
			return backJsonWithCode(errCode_1,ERR_NEW_PHONE_PATTERN);
		}
		
		if(usernum.equals(userNewPhone)) {
			return backJsonWithCode(errCode_1,ERR_SAME_OLD_NEW_PHONE);
		}
		
		//用户已经注册（未激活）
		if(null!=wxService.findUserCheckByPhone(userNewPhone)) {
			return backJsonWithCode(errCode_1,ERR_SAME_PHONE_NO_ACTIVE);
		}
		
		if(null !=wxService.findByPhone(userNewPhone)) {
			return backJsonWithCode(errCode_1,ERR_SAME_PHONE);
		}
		
		return sendMsgCode(userNewPhone);
	}
	
	/**
	 * 发送微信验证码
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/sendWxPhoneMsgCode",method=RequestMethod.POST)
	@ResponseBody
	public String sendWxPhoneMsgCode(HttpServletRequest request, HttpServletResponse response,Model model) {
		String phoneNumber = request.getParameter("phone").trim();
		String errorCode = "1";
		
		//手机号不能为空
		if(StringUtils.isEmpty(phoneNumber)) {
			return backJsonWithCode(errorCode,ERR_PHONE_NULL);
		}
		
		//手机号码格式不正确
		if(!PhoneUtils.validatePhone(phoneNumber)) {
			return backJsonWithCode(errorCode,ERR_PHONE_PATTERN);
		}
		
		//用户已经注册（未激活）
		if(null!=wxService.findUserCheckByPhone(phoneNumber)) {
			return backJsonWithCode(errorCode,ERR_SAME_PHONE_NO_ACTIVE);
		}
		
		return sendMsgCode(phoneNumber);
	}
	
	//发送验证码
	private String sendMsgCode(String phoneNumber) {
		String successCode = "0";
		String errorCode = "1";
		String promotCode = "2";//提示用户耐心等待
		//手机号码前缀 moblie_123456789
		String cacheKey = Global.PREFIX_MOBLIE_CODE + phoneNumber;
		
		PhoneMsgCache phoneMsgCache = (PhoneMsgCache)CacheUtils.get(cacheKey);
		
		//已经发送过验证码
		if(null != phoneMsgCache) {
			
			//如果注册时间和当前不是同一天 注册次数恢复为初始值
			if(!CasUtils.isSameDay(phoneMsgCache.getRegTime(), System.currentTimeMillis())) {
				phoneMsgCache.resetSendTime();
			}
			
			//用户可能没收到短信，会点击多次，需要限制用户点击次数，不能超过规定次数
			
			//是否提示用户，如果没有，需要提示用户耐心等待
			if(!phoneMsgCache.isPrompt()) {
				//提示
				phoneMsgCache.setPrompt(true);//记录已经提示过了
				return backJsonWithCode(promotCode,ERR_PROMPT_USER_CONFIRE);
				
				
				
			}
			
			//提示过后仍然点击获取验证码
			//验证注册次数
			int sendTimes = phoneMsgCache.getSendTimes();
			if(sendTimes > Global.MOBILE_TIMES) {
				return backJsonWithCode(errorCode,ERR_TOO_MONEY);
			}
			
			String code = CasUtils.getRandomDigitalString(4);//验证码
			logger.info("验证码是:"+code);
			//发送验证码
			if(wxService.isAliyunMsgLimit()) {
				//超出限制
				return backJsonWithCode(errorCode,ERR_SEND_MSG_ALIYUN_LIMIT);
			}
			Map<String,String> map = new HashMap<String,String>();
			map.put("code", code);
			if(!AliyunSendMsgUtils.sendMsg(phoneNumber,map,0)) {
				//发送失败
				return backJsonWithCode(errorCode,ERR_SEND_MSG);
			}
			//发送成功 记录下发送的次数
			
			wxService.aliyunMsgNumAdd(UserUtils.get("1"));
			
			//添加缓存
			phoneMsgCache.setValue(code);
			phoneMsgCache.setSendTimes(++sendTimes);
			//注册时间
			phoneMsgCache.setRegTime(System.currentTimeMillis());
			//过期时间30分钟
			phoneMsgCache.setTimeOut(System.currentTimeMillis()+1000*60*30);
			return backJsonWithCode(successCode,MSG_PHONE_CODE_MSG);
		}else {
			//没发送过验证码，发送验证码
			String code = CasUtils.getRandomString(Global.MOBILE_CODE_SIZE);//验证码
			logger.info("验证码是:"+code);
			phoneMsgCache = new PhoneMsgCache(code);
			CacheUtils.put(cacheKey, phoneMsgCache);//缓存
			CacheUtils.putPhoneMsgCacheKey(cacheKey);//添加缓存key 方便之后移除
			//发送验证码
			if(wxService.isAliyunMsgLimit()) {
				//超出限制
				return backJsonWithCode(errorCode,ERR_SEND_MSG_ALIYUN_LIMIT);
			}
			Map<String,String> map = new HashMap<String,String>();
			map.put("code", code);
			if(!AliyunSendMsgUtils.sendMsg(phoneNumber,map,0)) {
				//发送失败
				return backJsonWithCode(errorCode,ERR_SEND_MSG);
			}
			//发送成功 记录下发送的次数
			wxService.aliyunMsgNumAdd(UserUtils.get("1"));
			return backJsonWithCode(successCode,MSG_PHONE_CODE_MSG);
		}
	}
	
	
	//获取页面(测试时使用，运行时可删除)
	@RequestMapping(value="/clickUrl",method=RequestMethod.GET)
	@ResponseBody
	public String clickUrl(HttpServletRequest request, HttpServletResponse response) {
		String urlParam = request.getParameter("url");
		String url = null;
		String jsonResult = null;
		switch(urlParam) {
			case "index":
				//首页
				url = WxGlobal.getUserClick("https://x.xlhtszgh.cn/kd/wx/oAuthRedirectSo",true);
				break;
			case "tiePerson":
				//绑定个人中心
				url = WxGlobal.getUserClick("https://x.xlhtszgh.cn/kd/wx/oAuthRedirectSo",false);
				break;
		}
		if(null != url) {
			jsonResult = JSONObject.toJSONString(url);
			logger.info("url是:"+url);
		}else {
			logger.info("url不存在");
		}
		return jsonResult;
	}
	
	/**
	 * 授权回调
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/oAuthRedirectSo",method=RequestMethod.GET)
	public String oAuthRedirectSo(HttpServletRequest request, HttpServletResponse response,Model model) {
		String retPage = null;
		try {
			// 将请求、响应的编码均设置为UTF-8（防止中文乱码）  
	        request.setCharacterEncoding("UTF-8");  
	        response.setCharacterEncoding("UTF-8"); 
	        String code=request.getParameter("code");//获取code
	        logger.info("code is " + code);
	        
	        SysWxInfo sysWxInfo = new SysWxInfo();
	        sysWxInfo = wxService.saveOpenId(code);
	        if(null == sysWxInfo) {
	        	retPage = "/error/500.jsp";
	        	return retPage;
	        }else {
	        	String idCard = sysWxInfo.getIdCard();
	        	if(null == idCard) {
	        		//没有身份信息
	        		model.addAttribute("sysWxInfo", sysWxInfo);
	        		model.addAttribute("openId", sysWxInfo.getOpenId());
	        		retPage = WX_ID_CARD_USERINFO_ADD;
		        	return retPage;
	        	}else {
	        		//有身份信息
	        		model.addAttribute("wxCode",CacheUtils.getCodeByOpenId(sysWxInfo.getOpenId()));
	        		model.addAttribute("sysWxUser", wxService.findByIdCard(idCard));
	        		retPage = WX_ID_CARD_USERINFO_MODIFY;
		        	return retPage;
	        	}
	        }
		}catch(Exception ex) {
			 logger.info("request getUserAccessToken error");
			 ex.printStackTrace();
		}
		return retPage;
	}
	
	/**
	 * 微信接口测试-创建菜单
	 * @param model
	 * @return
	 */
	//创建菜单
	@RequestMapping(value = {"createMenu"})
	public String wxCreateMenu(Model model) {
		wxMenuManager.createMenu();
		return null;
	}
	
	/**
	 * 生成access_token
	 * @param model
	 * @return
	 */
	@RequestMapping(value = {"getAt"})
	public String wxAccessToken(Model model) {
		WxAccessTokenManager wtUtils = WxAccessTokenManager.getInstance();
		wtUtils.getAccessToken();
		return null;
	}
	
	/**
	 * 接收微信消息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/doGet",method=RequestMethod.POST)
	public void wxAcceptMsg(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("微信消息进入");
	    try {
        	// 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        	request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            //解析微信返回的xml  
            String retMsg = wxService.WxPostMsgProcess(request);//返回消息
            PrintWriter out = response.getWriter();
            out.print(retMsg);
            out.close();
        }catch(Exception ex) {
        	System.out.println("接收微信消息出错");
        	ex.printStackTrace();
        }
	    System.out.println("微信消息处理完成");
	}
	
	/**
	 * 认证微信服务器
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/doGet",method=RequestMethod.GET)
	public void wxServerCertification(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("开始签名校验");
		try {
			//提取参数			
			String signature = request.getParameter("signature");
			String timestamp = request.getParameter("timestamp");
			String nonce = request.getParameter("nonce");
			String echostr = request.getParameter("echostr");
			 
			ArrayList<String> array = new ArrayList<String>();
			array.add(signature);
			array.add(timestamp);
			array.add(nonce);
			//排序
			String sortString = wxService.sort(timestamp, nonce);
			//加密
			String mytoken = wxService.sha1(sortString);
			//比对
			//校验签名
		    if (mytoken != null && mytoken != "" && mytoken.equals(signature)) {
		        System.out.println("签名校验通过。");
		        response.getWriter().println(echostr); //如果检验成功输出echostr，微信服务器接收到此输出，才会确认检验完成。
		    } else {
		        System.out.println("签名校验失败。");
		    }
		}catch(Exception ex) {
			System.out.println("签名校验失败，出现异常。");
			ex.printStackTrace();
		}
		System.out.println("签名校验结束");
	}
	
	
	
}
