package com.thinkgem.jeesite.modules.sys.web;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
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
import com.thinkgem.jeesite.common.entity.Qrecord;
import com.thinkgem.jeesite.common.entity.WxCodeCache;
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
import com.thinkgem.jeesite.modules.sys.service.WxService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.modules.sys.view.JsonSysExpress;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value = "ul")
public class UtilsController extends BaseController {
	
	@Autowired
	private WxService wxService;
	
	

	
	//首页
	private final String WX_PERSON_INDEX = "modules/wxp/wxPersonIndex";
	//认证页面
	private final String WX_USER_CHECK_START = "modules/wxp/userCheckStart";
	//修改个人信息
	private final String WX_ID_CARD_USERINFO_MODIFY = "modules/wxp/wxIdCardUserInfoModify";
	//个人中心页面
	private final String WX_USER_HOME = "modules/wxp/wxUserhome";
	//快递助手页面
	private final String WX_EXPRESS_ASSIST = "modules/wxp/expressAssist";
	//懒人排行页面
	private final String WX_LAZY_BOARD = "modules/wxp/lazyboard";
	//录入快递页面
	private final String WX_EXPRESS_ADD = "modules/wxp/addExpress";
	//取快递页面
	private final String WX_EXPRESS_PICK = "modules/wxp/pickExpress";
	//审核页面
	private final String WX_WAIT_VALIDATE = "modules/wxp/waitValidate";
	//二维码页面
	private final String WX_Q_RECORD = "modules/wxp/personQRcode";
	//错误页面
	private final String WX_ERROR = "modules/wxp/500";

	/**
	 * 错误信息
	 */
	private final String ERR_OPEN_ID_NOT_GET = "微信号未获取";
	private final String ERR_MEDIA_ID_NOT_GET = "微信图片号未获取";
	private final String ERR_ID_CARD_NULL = "身份证号不能为空";
	private final String ERR_Q_RECORD_NULL = "无身份信息";
	private final String ERR_ID_EXPRESS_NULL = "快递号不能为空";
	private final String ERR_EXPRESS_NOT_ARRIVE = "快递还未到达，请您耐心等候";
	private final String ERR_EXPRESS_EXIST = "快递已存在";
	private final String ERR_USER_ID_NULL = "用户不存在";
	private final String ERR_USER_NOT_REG = "用户未注册";
	private final String ERR_EXPREE_ID_NULL = "快递单号不能为空";
	private final String ERR_NAME_NULL = "姓名不能为空";
	private final String ERR_CODE_NULL = "验证码不能为空";
	private final String ERR_UP_IMAGE_NULL = "上传图片不能为空";
	private final String ERR_CODE_SIZE = "验证码长度错误";
	private final String ERR_CODE = "请获取验证码";
	private final String ERR_CODE_TIME_OUT = "验证码已超时";
	private final String ERR_CODE_INPUT = "验证码错误";
	private final String ERR_PHONE_NULL = "电话不能为空";
	private final String ERR_SAME_IDCARD_PHONE_CHECK = "该电话号码和身份证已经注册，请更换其它身份证和电话号码，如被他人绑定，请前往快递点联系快递人员处理";
	private final String ERR_NO_WXUER = "系统异常，不存在操作用户，请前往快递点联系快递人员处理";
	private final String ERR_PHONE_ORIGIN = "请输入原先的电话号码进行验证";
	private final String ERR_PHONE_PATTERN = "手机号码格式不正确";
	private final String ERR_ID_CARD_PATTERN = "身份证号码格式不正确";
	private final String ERR_SAME_PHONE_NO_ACTIVE = "该号码已经注册，未审核激活，请携带身份证前往快递中心激活";
	private final String ERR_SAME_PHONE = "该号码已经注册，请绑定其它号码";
	private final String ERR_PROMPT_USER_CONFIRE = "您好，短信已发送，如果您没有收到，可能是网络有延迟，请耐心等待片刻";
	private final String ERR_TOO_MONEY = "今日次数已达上限";
	private final String ERR_NO_ACTIVE = "用户未审核激活，请前往快递中心审核激活";
	private final String ERR_INPUT_OLD_PHONE = "用户已绑定，请输入原手机号码";
	private final String ERR_NOT_SAME_OLD_PHONE = "绑定原手机号码输入错误";
	private final String ERR_NOT_SAME_OLD_NAME = "与原姓名关联不匹配";
	private final String ERR_NO_USER = "未检测到操作用户";
	private final String ERR_OLD_PHONE_PATTERN = "旧手机号码格式不正确";
	private final String ERR_NEW_PHONE_PATTERN = "新手机号码格式不正确";
	private final String ERR_SAME_OLD_NEW_PHONE = "新旧手机号码一致";
	private final String ERR_EXPREE_NOT_ID_CARD = "未查询到快递信息";
	private final String ERR_EXPREE_NOT_EXIST = "快递不存在";
	private final String ERR_EXPREE_NOT_ENTER = "快递状态非入库状态";
	private final String ERR_REDIRECT_URL = "重定向地址未获取";
	private final String ERR_CLIENT_MECHINE = "请在微信客户端打开";
	private final String ERR_NO_QREORD = "二维码已失效";
	private final String ERR_NO_QREORD_INFO = "无二维码信息";
	private final String ERR_NO_EXPRESS_INFO = "无快递状态信息";
	
	private final String MSG_PHONE_CODE_MSG = "验证码发送成功";
	private final String MSG_USER_SAVE_SUCCESS = "用户注册成功,请前往快递中心审核身份信息";
	private final String MSG_EXPRESS_SAVE_SUCCESS = "快递录入成功";
	private final String MSG_EXPRESS_QUERY_SUCCESS = "查找成功";
	private final String MSG_EXPRESS_ARRIVER_SUCCESS = "快递已到达快递中心，请您尽快前往取件";
	private final String MSG_EXPRESS_END_SUCCESS = "快递已取走，谢谢您的合作";
	
	
	/**
	 * 测试页面（上线可删除）
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public String index(HttpServletRequest request, HttpServletResponse response,Model model) {
		 // AliyunSendMsgUtils.sendMsg("15904793789", "1234");
		 return null;
	}
	
	@ModelAttribute
	public String init(HttpServletRequest request, HttpServletResponse response,Model model) {
		try {
			
			String openId = null;
			//测试模式
			if(null != Global.TEST_OPEN_ID) {
				openId = Global.TEST_OPEN_ID;
			}else {
				if(!DeviceUtils.isWeChat(request)) {
					logger.info("不是微信浏览器访问");
					model.addAttribute("message",ERR_CLIENT_MECHINE);
					model.addAttribute("errUrl",WX_ERROR);
					return null;
				}
				//获取微信号
				openId = getOpenId(request, response);//获取微信号
			}
			//String openId = WxGlobal.getTestOpenId();
			//微信号为空
			if(StringUtils.isEmpty(openId)) {
				model.addAttribute("message",ERR_OPEN_ID_NOT_GET);
				model.addAttribute("errUrl",WX_ERROR);
				return WX_ERROR;
			}else {
				model.addAttribute("openId",openId);
			}
		    logger.info("openId is " + openId);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	//获取openId
	private String getOpenId(HttpServletRequest request, HttpServletResponse response) {
		
		//测试模式
		if(null != Global.TEST_OPEN_ID) {
			return Global.TEST_OPEN_ID;
		}
		
		 String openId = null;
		 
		 try {
			  request.setCharacterEncoding("UTF-8");  
		      response.setCharacterEncoding("UTF-8"); 
		      
		      String code = null;
		      Cookie[] cookies = request.getCookies();
		      if(cookies!=null){
		      		for(Cookie cookie : cookies){
		      			if(cookie.getName().equals("code")){
		      				code = cookie.getValue();
		      				logger.info("初始化获取Code:"+code);
		      				//如果服务器已经移除code 那么code要重新请求
		      				 WxCodeCache wxCodeCache = (WxCodeCache)CacheUtils.get(code);
		      				  if(null == wxCodeCache) {
		    		    		  //服务器已经移除
		      					  logger.info("服务器移除了Code:"+code);
		    		    		  code = null;//置空
		    		    		  cookie.setMaxAge(0);//移除cookie
		    		    	  }
		      			}
		      		}
		      }
		      
		      
		      if(null == code) {
		    	  code = request.getParameter("code");//微信服务器返回了code
		    	  logger.info("微信服务器返回Code:"+code);
		      }
		      
		      StringBuffer sb = request.getRequestURL();
		      String redirectUrl = sb.toString();
		      if(StringUtils.isEmpty(redirectUrl)) {
		    	  logger.info("初始化跳转页面异常");
		    	  return null;//异常错误
		      }
		      logger.info("Code是:"+code);
		      //这一句纯属为了打印日志
		      if(StringUtils.isEmpty(code)) {
		    	  	logger.info("前往微信服务器获取Code");
		    	  	String redirectAddress = WxGlobal.getUserClick(redirectUrl,false);
		    	  	logger.info("前往微信服务器获取Code地址："+redirectAddress);
		      }
		      if(StringUtils.isEmpty(code)) {
		        	response.sendRedirect(WxGlobal.getUserClick(redirectUrl,false));
		        	return null;
		      }else {
		    	  /**
		    	   * 不为空的情况两种一种是微信服务器返回的新code 一种是用户强制刷新的旧code
		    	   * 旧code 获取缓存
		    	   * 新code 添加缓存 
		    	   */
		    	  
		    	  //是否是旧缓存
		    	  WxCodeCache wxCodeCache = (WxCodeCache)CacheUtils.get(code);
		    	  if(null == wxCodeCache) {
		    		  logger.info("No Code Cache,New Code Cache:"+code);
		    		  //没有缓存过 添加缓存
		    		  //获取openID
		    		  Map<String,String> map = wxService.getOpenIdInfo(code);
				      if(null != map) {
				        	openId = map.get("openId");
				        	wxService.saveWxInfo(map);  //用户数据保存一次
				      }
				      if(null !=openId) {
				    	  logger.info("Add New Code Cache:"+openId);
				    	  //获取openID之后 缓存数据
			    		  wxCodeCache = new WxCodeCache(openId);
			    		  //记录键值 为之后删除
			    		  CacheUtils.putWxCodeKey(code, openId);  
			    		  CacheUtils.put(code, wxCodeCache);
			    		  
			    		  /**
			    		   * 存放cookie
			    		   */
			    		  Cookie userCookie=new Cookie("code",code);
			    		  userCookie.setMaxAge(Global.WX_CODE_TIME_OUT_INT());
			    		  userCookie.setPath("/");
			    		  response.addCookie(userCookie);
				      }
		    	  }else {
		    		  logger.info("Code Cache:"+code);
		    		  //缓存过
		    		  //查看过期时间
		    		  long timeOut = wxCodeCache.getTimeOut();
		    		  if(System.currentTimeMillis() > timeOut) {
		    			   //移除缓存 过时了
		    			  CacheUtils.clearWxCodeCacheKeies();//清除过期的微信code
		    			  CacheUtils.remove(code);
		    			  logger.info("缓存过时，前往微信服务器获取Code");
				          response.sendRedirect(WxGlobal.getUserClick(redirectUrl,true));
		    		  }else {
			    		  openId = wxCodeCache.getOpenId();//缓存的openId
			    		  logger.info("Cahce OpenId Is " + openId);
			    		  logger.info("没过时");
		    		  }
		    		 
		    	  }
		    	  CacheUtils.clearWxCodeCacheKeies();//清除过期的微信code
		    	  logger.info("code is " + code);
		      }
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
		 return openId;
	}
		
	
	
	/**
	 * 依据微信号查询是否已经注册并且激活
	 * 用户是新用户，需要跳转到注册页面
	 * 用户已经注册，需要查看是否激活，没有激活跳转到审核页面，激活后跳转到对应页面
	 * @param openId 查询微信号
	 * @return
	 * 如果微信号查询不到，返回注册页面
	 * 如果微信号查询到，但是没有激活，返回未审核页面
	 * 如果用户已注册，也已经激活，返回空值
	 */
	private String validateRegAndActiveByOpenId(String openId,HttpServletRequest request, HttpServletResponse response,Model model) {
		
		if(null == openId) {
			model.addAttribute("message",ERR_OPEN_ID_NOT_GET);
			model.addAttribute("errUrl",WX_ERROR);
			return WX_ERROR;//微信号为空
		}
		
		SysWxUser sysWxUser = wxService.getSysWxUser(openId);
		//判断
		if(null != sysWxUser) {
			return null;//用户已注册，也已经激活，返回空值
		}
		
		SysWxUserCheck sysWxUserCheck = wxService.findByOpenId(openId);
		if(null == sysWxUserCheck) {
			//获取jsApiTicket
			Map<String, String> map = WxJsSkdUtils.getJsApiSign(request, response);
			String retCode = map.get("code");
			if("0".equals(retCode)) {
				model.addAttribute("appId",WxGlobal.getAppId());
				model.addAttribute("timestamp",map.get("timestamp"));
				model.addAttribute("nonceStr",map.get("nonceStr"));
				model.addAttribute("signature",map.get("signature"));
			}else {
				logger.info("jsApiTicket is null");
			}
			model.addAttribute("message",ERR_USER_ID_NULL);
			model.addAttribute("errUrl",WX_USER_CHECK_START);
			model.addAttribute("wxCode",CacheUtils.getCodeByOpenId(openId));
			return WX_USER_CHECK_START;//用户不存在,返回注册页面
		}
		String state = sysWxUserCheck.getState();
		if("0".equals(state)) {
			model.addAttribute("message",ERR_NO_ACTIVE);
			model.addAttribute("errUrl",WX_ERROR);
			return WX_ERROR;//用户已注册，但未激活，返回审核等待状态
		}
		return null;//用户已注册，也已经激活，返回空值
	}
	
	/**
	 * 验证微信号是否获取，如果微信号未获取，系统后续操作无法进行，提示微信号未获取
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * 如果微信号未获取，返回错误页面
	 * 如果微信号获取，返回空值
	 */
	private String validateOpenId(String openId,Model model) {
		if(null == openId) {
			model.addAttribute("message", ERR_OPEN_ID_NOT_GET);
			return WX_ERROR;//返回错误页面
		}
		return null;//微信号 标识微信号已获取
	}
	
	/**
	 * 页面跳转-获取懒人排行页面
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/reqLazyboard",method=RequestMethod.GET)
	public String reqLazyboard(HttpServletRequest request, HttpServletResponse response,Model model) {
		String errUrl = (String)model.asMap().get("errUrl");
		if(null != errUrl) {
			return errUrl;
		}
		//是否已经注册并且激活
	    String openId = (String)model.asMap().get("openId");
		String isRegAndActive = validateRegAndActiveByOpenId(openId,request,response,model);
		if(null!=isRegAndActive) {
			//未注册或者未激活 跳转到指定页面
			return isRegAndActive;
		}
		List<SysWxInfo> totalData = wxService.findSysWxInfoTotal();
		List<SysWxInfo> yearData = wxService.findSysWxInfoByYear();
		List<SysWxInfo> monthData = wxService.findSysWxInfoByMonth();
		model.addAttribute("totalData",totalData);
		model.addAttribute("yearData",yearData);
		model.addAttribute("monthData",monthData);
		return WX_LAZY_BOARD;
	}

	
	/**
	 * 页面跳转-获取二维码页面
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/reqPersonQRcode",method=RequestMethod.GET)
	public String reqPersonQRcode(HttpServletRequest request, HttpServletResponse response,Model model) {
		String ret = null;
		try {
			String errUrl = (String)model.asMap().get("errUrl");
			if(null != errUrl) {
				return errUrl;
			}
			//是否已经注册并且激活
		    String openId = (String)model.asMap().get("openId");
			String isRegAndActive = validateRegAndActiveByOpenId(openId,request,response,model);
			if(null!=isRegAndActive) {
				//未注册或者未激活 跳转到指定页面
				return isRegAndActive;
			}
			
			//获取身份证号
			SysWxUser sysWxUser = wxService.findSysUserByOpenId(openId);
			if(null == sysWxUser) {
				model.addAttribute("message",ERR_USER_NOT_REG);
				return WX_ERROR;
			}
			String idCard = sysWxUser.getIdCard();
			if(null == idCard) {
				model.addAttribute("message",ERR_USER_ID_NULL);
				return WX_ERROR;
			}
			
			//清除重复用户缓存和超时缓存
			CacheUtils.clearQRecordRepeatCacheKeies(idCard);
			
			
			//添加缓存
			Qrecord qrecord = new Qrecord(idCard);
			String now = Long.valueOf(System.currentTimeMillis()).toString();
			String md5 = CasUtils.md5Validate(qrecord+now);
			String cacheKey = "qr_" + md5;
			CacheUtils.put(cacheKey, qrecord);//缓存
			CacheUtils.putQRecordCacheKey(cacheKey);//添加缓存key 方便之后移除
			model.addAttribute("rTextData",md5);
			model.addAttribute("name",sysWxUser.getName());
			model.addAttribute("phone",sysWxUser.getPhone());
			ret = WX_Q_RECORD;
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 页面跳转-完善个人信息页面
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/reqUserInfoEdit",method=RequestMethod.GET)
	public String reqUserInfoEdit(HttpServletRequest request, HttpServletResponse response,Model model) {
		String errUrl = (String)model.asMap().get("errUrl");
		if(null != errUrl) {
			return errUrl;
		}
		//是否已经注册并且激活
	    String openId = (String)model.asMap().get("openId");
		String isRegAndActive = validateRegAndActiveByOpenId(openId,request,response,model);
		if(null!=isRegAndActive) {
			//未注册或者未激活 跳转到指定页面
			return isRegAndActive;
		}
		SysWxUser sysWxUser = wxService.findSysUserByOpenId(openId);
		if(null != sysWxUser) {
			String phone = sysWxUser.getPhone();
			phone = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
			sysWxUser.setPhone(phone);
			model.addAttribute("sysWxUser",sysWxUser);
		}
		model.addAttribute("wxCode",CacheUtils.getCodeByOpenId(openId));
		return WX_ID_CARD_USERINFO_MODIFY;
	}
	
	/**
	 * 页面跳转-获取快递助手页面
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/reqExpressAssist",method=RequestMethod.GET)
	public String reqExpressAssist(HttpServletRequest request, HttpServletResponse response,Model model) {
		String errUrl = (String)model.asMap().get("errUrl");
		if(null != errUrl) {
			return errUrl;
		}
		//是否已经注册并且激活
	    String openId = (String)model.asMap().get("openId");
		String isRegAndActive = validateRegAndActiveByOpenId(openId,request,response,model);
		if(null!=isRegAndActive) {
			//未注册或者未激活 跳转到指定页面
			return isRegAndActive;
		}
		return WX_EXPRESS_ASSIST;
	}
	
	/**
	 * 页面跳转-获取快递添加页面
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/reqAddExpress",method=RequestMethod.GET)
	public String reqAddExpress(HttpServletRequest request, HttpServletResponse response,Model model) {
		String errUrl = (String)model.asMap().get("errUrl");
		if(null != errUrl) {
			return errUrl;
		}
		//是否已经注册并且激活
	    String openId = (String)model.asMap().get("openId");
		String isRegAndActive = validateRegAndActiveByOpenId(openId,request,response,model);
		if(null!=isRegAndActive) {
			//未注册或者未激活 跳转到指定页面
			return isRegAndActive;
		}
		
		//获取jsApiTicket
		Map<String, String> map = WxJsSkdUtils.getJsApiSign(request, response);
		String retCode = map.get("code");
		if("0".equals(retCode)) {
			model.addAttribute("appId",WxGlobal.getAppId());
			model.addAttribute("timestamp",map.get("timestamp"));
			model.addAttribute("nonceStr",map.get("nonceStr"));
			model.addAttribute("signature",map.get("signature"));
		}else {
			logger.info("jsApiTicket is null");
		}
		
		model.addAttribute("wxCode",CacheUtils.getCodeByOpenId(openId));
		return WX_EXPRESS_ADD;
	}
	
	/**
	 * 页面跳转-获取取快递页面
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/reqPickExpress",method=RequestMethod.GET)
	public String reqPickExpress(HttpServletRequest request, HttpServletResponse response,Model model) {
		String errUrl = (String)model.asMap().get("errUrl");
		if(null != errUrl) {
			return errUrl;
		}
		//是否已经注册并且激活
	    String openId = (String)model.asMap().get("openId");
		String isRegAndActive = validateRegAndActiveByOpenId(openId,request,response,model);
		if(null!=isRegAndActive) {
			//未注册或者未激活 跳转到指定页面
			return isRegAndActive;
		}
		
		//查询操作人员
		User user = wxService.findOperator(openId);
		if(null == user) {
			model.addAttribute("message",ERR_NO_USER);
			return WX_ERROR;//用户已注册，但未激活，返回审核等待状态
		}
		
		//获取jsApiTicket
		Map<String, String> map = WxJsSkdUtils.getJsApiSign(request, response);
		String retCode = map.get("code");
		if("0".equals(retCode)) {
			model.addAttribute("appId",WxGlobal.getAppId());
			model.addAttribute("timestamp",map.get("timestamp"));
			model.addAttribute("nonceStr",map.get("nonceStr"));
			model.addAttribute("signature",map.get("signature"));
		}else {
			logger.info("jsApiTicket is null");
		}
		
		model.addAttribute("wxCode",CacheUtils.getCodeByOpenId(openId));
		return WX_EXPRESS_PICK;
	}
	
	/**
	 * 页面跳转-点击个人中心，未注册进入注册页面
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/reqUserCheckState",method=RequestMethod.GET)
	public String reqUserCheckState(HttpServletRequest request, HttpServletResponse response,Model model) {
		String errUrl = (String)model.asMap().get("errUrl");
		if(null != errUrl) {
			return errUrl;
		}
		//是否已经注册并且激活
	    String openId = (String)model.asMap().get("openId");
		String isRegAndActive = validateRegAndActiveByOpenId(openId,request,response,model);
		if(null!=isRegAndActive) {
			//未注册或者未激活 跳转到指定页面
			return isRegAndActive;
		}
	    logger.info("openId is " + openId);
	    model.addAttribute("wxCode",CacheUtils.getCodeByOpenId(openId));
	    
		//获取jsApiTicket
		Map<String, String> map = WxJsSkdUtils.getJsApiSign(request, response);
		String retCode = map.get("code");
		if("0".equals(retCode)) {
			model.addAttribute("appId",WxGlobal.getAppId());
			model.addAttribute("timestamp",map.get("timestamp"));
			model.addAttribute("nonceStr",map.get("nonceStr"));
			model.addAttribute("signature",map.get("signature"));
		}else {
			logger.info("jsApiTicket is null");
		}
	    
		return WX_USER_CHECK_START;
	}
	
	/**
	 * 页面跳转-注册完成后进入个人首页
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/reqPersonIndex",method=RequestMethod.GET)
	public String reqPersonIndex(HttpServletRequest request, HttpServletResponse response,Model model) {
		String errUrl = (String)model.asMap().get("errUrl");
		if(null != errUrl) {
			return errUrl;
		}
		//是否已经注册
	    String openId = (String)model.asMap().get("openId");
		//是否获取到微信号
		String isGetOpenId = validateOpenId(openId,model);
		if(null!=isGetOpenId) {
			//没有获取到，跳转到错误页面
			return isGetOpenId;
		}
	    logger.info("openId is " + openId);
		return WX_PERSON_INDEX;
	}
		
	
	/**
	 * 获取个人首页
	 * 微信点击个人首页后，微信重定向访问地址
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/getPersonIndex",method=RequestMethod.GET)
	public String getPersonIndex(HttpServletRequest request, HttpServletResponse response,Model model) {
		try {
			String errUrl = (String)model.asMap().get("errUrl");
			if(null != errUrl) {
				return errUrl;
			}
			//是否已经注册并且激活
		    String openId = (String)model.asMap().get("openId");
	        //查询是否是快递管理人员
	        if(null!=wxService.findUserManagerByOpenId(openId)) {
	        	model.addAttribute("isManager",1);//是快递人员
	        }
		}catch(Exception e) {
			e.printStackTrace();
		}
		return WX_PERSON_INDEX;
	}
	
	
	
	
	/**
	 * 页面跳转-获取个人中心页面
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/userHome",method=RequestMethod.GET)
	public String userHome(HttpServletRequest request, HttpServletResponse response,Model model) throws Exception {
		
		String errUrl = (String)model.asMap().get("errUrl");
		if(null != errUrl) {
			return errUrl;
		}
		//是否已经注册并且激活
		String openId = (String)model.asMap().get("openId");
		String isRegAndActive = validateRegAndActiveByOpenId(openId,request,response,model);
		if(null!=isRegAndActive) {
			//未注册或者未激活 跳转到指定页面
			return isRegAndActive;
		}
		 
		//依据openId获取个人信息 如果没有个人信息 那么跳转到个人信息注册页面 如果有跳转到个人信息userHome页面
		SysWxUser sysWxUser = wxService.getSysWxUser(openId);
		//判断
		if(null == sysWxUser) {
			//没有个人信息
			model.addAttribute("openId",openId);
			//获取jsApiTicket
			Map<String, String> map = WxJsSkdUtils.getJsApiSign(request, response);
			String retCode = map.get("code");
			if("0".equals(retCode)) {
				model.addAttribute("appId",WxGlobal.getAppId());
				model.addAttribute("timestamp",map.get("timestamp"));
				model.addAttribute("nonceStr",map.get("nonceStr"));
				model.addAttribute("signature",map.get("signature"));
			}else {
				logger.info("jsApiTicket is null");
			}
			return WX_USER_CHECK_START;
		}else {
			//已注册个人信息
			model.addAttribute("openId",openId);
			model.addAttribute("sysWxUser",sysWxUser);
			return WX_USER_HOME;
		}
	}
	
	
	/**
	 * 上传个人图片信息
	 */
	@ResponseBody
	@RequestMapping(value="/upIdCard",method=RequestMethod.POST)
	public String upIdCard(HttpServletRequest request, HttpServletResponse response,Model model, RedirectAttributes redirectAttributes) {
		final String errCode_1 = "1";
		Map<String, Object> filterData = model.asMap();
		String errUrl = (String)filterData.get("errUrl");
		if(null != errUrl) {
			return backJsonWithCode(errCode_1,(String)filterData.get("message"));
		}
		//是否已经注册并且激活
		String openId = (String)filterData.get("openId");
		//微信号为空
		if(StringUtils.isEmpty(openId)) {
			return backJsonWithCode(errCode_1,ERR_OPEN_ID_NOT_GET);
		}
		
		//微信图片未获取
		String mediaId = request.getParameter("serverId");
		if(StringUtils.isEmpty(mediaId)) {
			return backJsonWithCode(errCode_1,ERR_MEDIA_ID_NOT_GET);
		}
		logger.info("mediaId:"+mediaId);
		
		String reqUrl = null;
		try {
			request.setCharacterEncoding("UTF-8");
			//获取用户的身份证ID
			String dirParam = headPhotoPath();
			String filePath = wxService.downloadMedia(mediaId, dirParam,openId);
			logger.info("filePath:"+filePath);
			String isServer = DictUtils.getDictValue("isServer", "systemControl", "0");
			String httpProtocol = DictUtils.getDictValue("httpProtocol", "systemControl", "http");
			String url = null;
			String[] paths = null;
			if("0".equals(isServer)) {
				url = BasePathUtils.getBasePathNoServer(request,true);
			}else {
				url = BasePathUtils.getBasePathNoServer(request,false);
			}
			String pattern = Pattern.quote(System.getProperty("file.separator"));
			paths = filePath.split(pattern);
		    if("https".equals(httpProtocol)) {
		    	url = url.replace("http", "https");
		    }
		    reqUrl = url + request.getContextPath() + Global.USER_ID_CARD + "/" + paths[paths.length-1];
		    logger.info(reqUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String retResult = backJsonWithCode("0",reqUrl);
		logger.info(retResult);
		return retResult;
	}
	
	private String headPhotoPath(){
    	StringBuilder sb;
		try {
			sb = new StringBuilder(getSavePath(Global.USER_ID_CARD));
/*			sb.append(File.separator);
			sb.append(idCard);*/
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	 }
	
	 /**
     * 返回上传文件保存的位置
     * 
     * @return
     * @throws Exception
     */
    private String getSavePath(String savePath) throws Exception {
        return ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") + savePath;
    }
	
	/**
	 * 注册个人信息
	 * @param request
	 * @param response
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/savePersonUserInfo",method=RequestMethod.POST)
	public String savePersonUserInfo(HttpServletRequest request, HttpServletResponse response,Model model, RedirectAttributes redirectAttributes) {
		
		final String errCode_1 = "1";
		Map<String, Object> filterData = model.asMap();
		String errUrl = (String)filterData.get("errUrl");
		if(null != errUrl) {
			return backJsonWithCode(errCode_1,(String)filterData.get("message"));
		}
		//是否已经注册并且激活
		String openId = (String)filterData.get("openId");
		//微信号为空
		if(StringUtils.isEmpty(openId)) {
			return backJsonWithCode(errCode_1,ERR_OPEN_ID_NOT_GET);
		}
		
		String name = request.getParameter("name").trim();
		String idCard = request.getParameter("idCard").trim();
		String phone = request.getParameter("phone").trim();
		String msg = request.getParameter("msg").trim();
		String oldPhone = request.getParameter("oldPhone").trim();//老手机号
		String idcardImg = request.getParameter("idcardImg").trim();//url地址
		
		final String successCode = "0";//成功码
		final String errCode_2 = "2";//验证码长度为固定值
		final String errCode_3 = "3";//姓名不能为空
		final String errCode_4 = "4";//身份证不能为空
		final String errCode_5 = "5";//电话号不能为空
		final String errCode_6 = "6";//请获取验证码
		final String errCode_7 = "7";//验证码已超时
		final String errCode_8 = "8";//验证码错误
		final String errCode_9 = "9";//身份处于审核状态
		final String errCode_10 = "10";//请输入原手机号码
		final String errCode_11 = "11";//原手机号码输入错误
		final String errCode_12 = "12";//无操作用户
		final String errCode_13 = "13";//手机号码格式不正确
		
		//微信号为空
		if(StringUtils.isEmpty(openId)) {
			return backJsonWithCode(errCode_1,ERR_OPEN_ID_NOT_GET);
		}

		//验证码不能为空
		if(StringUtils.isEmpty(msg)) {
			return backJsonWithCode(errCode_1,ERR_CODE_NULL);
		}
		
		//验证码长度为固定值
		if(msg.length()!=Global.MOBILE_CODE_SIZE) {
			return backJsonWithCode(errCode_2,ERR_CODE_SIZE);
		}
		
		//姓名不能为空
		if(StringUtils.isEmpty(name)) {
			return backJsonWithCode(errCode_3,ERR_NAME_NULL);
		}
		
		//身份证不能为空
		if(StringUtils.isEmpty(idCard)) {
			return backJsonWithCode(errCode_4,ERR_ID_CARD_NULL);
		}
		
		//身份证号格式
		if(!IdcardUtils.validateCard(idCard)) {
			return backJsonWithCode(errCode_4,ERR_ID_CARD_PATTERN);
		}
		
		//电话号不能为空
		if(StringUtils.isEmpty(phone)) {
			return backJsonWithCode(errCode_5,ERR_PHONE_NULL);
		}
		
		//手机号码格式不正确
		if(!PhoneUtils.validatePhone(phone)) {
			return backJsonWithCode(errCode_13,ERR_NEW_PHONE_PATTERN);
		}
		
		//上传图片不能为空
		if(StringUtils.isEmpty(idcardImg)) {
			return backJsonWithCode(errCode_1,ERR_UP_IMAGE_NULL);
		}
		
		//上传图片不能为空
		String imageDefaultPath = "/kd/static/wx/wximages/defaultimage.jpg";
		if(imageDefaultPath.equals(idcardImg)) {
			return backJsonWithCode(errCode_1,ERR_UP_IMAGE_NULL);
		}
		
		//验证码缓存
		String cacheKey = Global.PREFIX_MOBLIE_CODE + phone;
		PhoneMsgCache phoneMsgCache = (PhoneMsgCache)CacheUtils.get(cacheKey);
		if(null==phoneMsgCache) {
			return backJsonWithCode(errCode_6,ERR_CODE);
		}
		
		//是否已经超时
		long timeOut = phoneMsgCache.getTimeOut();
		if(System.currentTimeMillis() > timeOut) {
			//移除缓存验证码 已经完成验证了
			CacheUtils.remove(cacheKey);
			return backJsonWithCode(errCode_7,ERR_CODE_TIME_OUT);
		}
		
		//验证码错误
		String cacheCode = (String)phoneMsgCache.getValue();
		if(!msg.equalsIgnoreCase(cacheCode)) {
			return backJsonWithCode(errCode_8,ERR_CODE_INPUT);
		}
		
		//该身份证号是否处于审核状态
		SysWxUserCheck sysWxUserCheck = wxService.findUserCheckByIdCard(idCard);
		if(null != sysWxUserCheck) {
			//有信息，处于审核状态
			return backJsonWithCode(errCode_9,ERR_NO_ACTIVE);
		}
		
		SysWxUser sysWxUser = null;
		//如果该身份证已经注册，需要提示用户输入之前注册的手机号以进行验证
		sysWxUser = wxService.findByIdCard(idCard);
		if(null != sysWxUser) {
			//不为空，表示用户存在
			if(StringUtils.isEmpty(oldPhone)) {
				//没有输入了旧手机号码 提示用户输入旧手机
				return backJsonWithCode(errCode_10,ERR_INPUT_OLD_PHONE);
			}else {
				//手机号码格式不正确
				if(!PhoneUtils.validatePhone(oldPhone)) {
					return backJsonWithCode(errCode_13,ERR_OLD_PHONE_PATTERN);
				}
				//输入旧手机号码
				String originPhone = sysWxUser.getPhone();//原先电话
				if(!originPhone.equals(oldPhone)) {
					//旧手机号码验证错误
					return backJsonWithCode(errCode_11,ERR_NOT_SAME_OLD_PHONE);
				}
				String originName = sysWxUser.getName();//原先名字
				if(!originName.equals(name)) {
					//旧手机号码验证错误
					return backJsonWithCode(errCode_11,ERR_NOT_SAME_OLD_NAME);
				}
				//号码输入正确，更改信息
				sysWxUser.setPhone(phone);
				if(null!=wxService.saveWxUserInfo(sysWxUser,openId)) {
					//移除缓存验证码 已经完成验证了
					CacheUtils.remove(cacheKey);
					CacheUtils.clearPhoneMsgCacheKeies();//清除其余缓存
					return backJsonWithCode(successCode,MSG_USER_SAVE_SUCCESS);
				}else {
					//操作异常
					return backJsonWithCode(errCode_12,ERR_NO_USER);
				}
			}
		}
		
		//接下来是信息不存在 可以直接保存了
		
		//注册信息填写
		SysWxUserCheck param = new SysWxUserCheck();
		param.setName(name);
		param.setIdCard(idCard);
		param.setPhone(phone);
		param.setOpenId(openId);
		param.setRemarks(idcardImg);//存放图片URl
		param.setState(DictUtils.getDictValue("未激活", "userCheckState", "0"));

		//如果身份信息不存在 进行保存操作
		SysWxUserCheck result = wxService.saveSysWxUserCheck(param);
		if(null == result) {
			//操作异常
			return backJsonWithCode(errCode_12,ERR_NO_USER);
		}else {
			return backJsonWithCode(successCode,MSG_USER_SAVE_SUCCESS);
		}
	}
	
	/**
	 * 修改个人信息
	 * @param request
	 * @param response
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/modifyPersonUserInfo",method=RequestMethod.POST)
	public String modifyPersonUserInfo(HttpServletRequest request, HttpServletResponse response,Model model, RedirectAttributes redirectAttributes) {
		
		final String errCode_1 = "1";
		Map<String, Object> filterData = model.asMap();
		String errUrl = (String)filterData.get("errUrl");
		if(null != errUrl) {
			return backJsonWithCode(errCode_1,(String)filterData.get("message"));
		}
		//是否已经注册并且激活
		String openId = (String)filterData.get("openId");
		//微信号为空
		if(StringUtils.isEmpty(openId)) {
			return backJsonWithCode(errCode_1,ERR_OPEN_ID_NOT_GET);
		}
		
		String userId = request.getParameter("userId").trim();
		String userNewPhone = request.getParameter("usernewPhone").trim();
		String msg = request.getParameter("username").trim();
		String usernum = request.getParameter("usernum").trim();//老手机号
		
		final String successCode = "0";//成功码
		final String errCode_2 = "2";//验证码长度为固定值
		final String errCode_3 = "3";//姓名不能为空
		final String errCode_4 = "4";//身份证不能为空
		final String errCode_5 = "5";//电话号不能为空
		final String errCode_6 = "6";//请获取验证码
		final String errCode_7 = "7";//验证码已超时
		final String errCode_8 = "8";//验证码错误
		final String errCode_9 = "9";//身份处于审核状态
		final String errCode_10 = "10";//请输入原手机号码
		final String errCode_11 = "11";//原手机号码输入错误
		final String errCode_12 = "12";//无操作用户
		final String errCode_13 = "13";//手机号码格式不正确
		
		//微信号为空
		if(StringUtils.isEmpty(openId)) {
			return backJsonWithCode(errCode_1,ERR_OPEN_ID_NOT_GET);
		}

		//验证码不能为空
		if(StringUtils.isEmpty(msg)) {
			return backJsonWithCode(errCode_1,ERR_CODE_NULL);
		}
		
		//验证码长度为固定值
		if(msg.length()!=Global.MOBILE_CODE_SIZE) {
			return backJsonWithCode(errCode_2,ERR_CODE_SIZE);
		}
		
		//ID不能为空
		if(StringUtils.isEmpty(userId)) {
			return backJsonWithCode(errCode_4,ERR_USER_ID_NULL);
		}
		
		//电话号不能为空
		if(StringUtils.isEmpty(usernum)||StringUtils.isEmpty(userNewPhone)) {
			return backJsonWithCode(errCode_5,ERR_PHONE_NULL);
		}
		
		//手机号码格式不正确
		if(!PhoneUtils.validatePhone(usernum)||!PhoneUtils.validatePhone(userNewPhone)) {
			return backJsonWithCode(errCode_13,ERR_NEW_PHONE_PATTERN);
		}
		
		if(usernum.equals(userNewPhone)) {
			return backJsonWithCode(errCode_13,ERR_SAME_OLD_NEW_PHONE);
		}
		
		//验证码缓存
		String cacheKey = Global.PREFIX_MOBLIE_CODE + userNewPhone;
		PhoneMsgCache phoneMsgCache = (PhoneMsgCache)CacheUtils.get(cacheKey);
		if(null==phoneMsgCache) {
			return backJsonWithCode(errCode_6,ERR_CODE);
		}
		
		//是否已经超时
		long timeOut = phoneMsgCache.getTimeOut();
		if(System.currentTimeMillis() > timeOut) {
			//移除缓存验证码 已经完成验证了
			CacheUtils.remove(cacheKey);
			return backJsonWithCode(errCode_7,ERR_CODE_TIME_OUT);
		}
		
		//验证码错误
		String cacheCode = (String)phoneMsgCache.getValue();
		if(!msg.equalsIgnoreCase(cacheCode)) {
			return backJsonWithCode(errCode_8,ERR_CODE_INPUT);
		}
		
		SysWxUser modifyWxUser = wxService.findSysUserById(userId);
		//用户不能为空
		if(null == modifyWxUser) {
			return backJsonWithCode(errCode_4,ERR_USER_ID_NULL);
		}
		String idCard = modifyWxUser.getIdCard();
		
		//该身份证号是否处于审核状态(非正常手段绕过之前的页面监测)
		SysWxUserCheck sysWxUserCheck = wxService.findUserCheckByIdCard(idCard);
		if(null != sysWxUserCheck) {
			//有信息，处于审核状态
			return backJsonWithCode(errCode_9,ERR_NO_ACTIVE);
		}
		
		if(null !=wxService.findByPhone(userNewPhone)) {
			return backJsonWithCode(errCode_1,ERR_SAME_PHONE);
		}
		
		//手机号校验
		String originPhone = modifyWxUser.getPhone();
		if(!originPhone.equals(usernum)) {
			//旧手机号码验证错误
			return backJsonWithCode(errCode_11,ERR_NOT_SAME_OLD_PHONE);
		}
		
		//号码输入正确，更改信息
		modifyWxUser.setPhone(userNewPhone);
		if(null!=wxService.modifyWxUser(modifyWxUser,openId)) {
			//移除缓存验证码 已经完成验证了
			CacheUtils.remove(cacheKey);
			CacheUtils.clearPhoneMsgCacheKeies();//清除其余缓存
			return backJsonWithCode(successCode,MSG_USER_SAVE_SUCCESS);
		}else {
			//操作异常
			return backJsonWithCode(errCode_12,ERR_NO_USER);
		}
	}

	
	//录入快递信息
	@ResponseBody
	@RequestMapping(value="/saveExpress",method=RequestMethod.POST)
	public String saveExpress(HttpServletRequest request, HttpServletResponse response,Model model) {
		
		final String errCode_1 = "1";
		Map<String, Object> filterData = model.asMap();
		String errUrl = (String)filterData.get("errUrl");
		if(null != errUrl) {
			return backJsonWithCode(errCode_1,(String)filterData.get("message"));
		}
		//是否已经注册并且激活
		String openId = (String)model.asMap().get("openId");
		//微信号为空
		if(StringUtils.isEmpty(openId)) {
			return backJsonWithCode(errCode_1,ERR_OPEN_ID_NOT_GET);
		}
		
		String phone=request.getParameter("phone");//获取phone
		String expressId=request.getParameter("expressId");//获取快递单号
		String company=request.getParameter("company");//获取公司
		
		final String successCode = "0";//成功码
		final String errCode_2 = "2";//手机号不能为空
		final String errCode_3 = "3";//手机号格式错误
		final String errCode_4 = "4";//未检测到操作人员
		
		//微信号为空
		if(StringUtils.isEmpty(openId)) {
			return backJsonWithCode(errCode_1,ERR_OPEN_ID_NOT_GET);
		}

		//快递单号不能为空
		if(StringUtils.isEmpty(expressId)) {
			return backJsonWithCode(errCode_1,ERR_EXPREE_ID_NULL);
		}
		
		//电话号不能为空
		if(StringUtils.isEmpty(phone)) {
			return backJsonWithCode(errCode_2,ERR_PHONE_NULL);
		}
		
		//手机号码格式不正确
		if(!PhoneUtils.validatePhone(phone)) {
			return backJsonWithCode(errCode_3,ERR_NEW_PHONE_PATTERN);
		}
		
		//快递单号查询
		if(null!=wxService.findExpressByExpressId(expressId)) {
			return backJsonWithCode(errCode_1,ERR_EXPRESS_EXIST);
		}
		
		//查询操作人员
		User user = wxService.findOperator(openId);
		if(null == user) {
			return backJsonWithCode(errCode_4,ERR_NO_USER);
		}
		
		
		
		SysExpress sysExpress = new SysExpress();
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
		wxService.saveExpress(sysExpress,user);
		/**
		 * 发送模板消息
		 */
		String sendOpenId = wxService.getOpenIdForMsg(sysExpress);
		if(null == sendOpenId) {
			//微信发送失败
			//改用短信发送
			String returnMsg = wxService.sendAliyunMsgTemplate(sysExpress, user);
			if(null!=returnMsg) {
				return backJsonWithCode(successCode,returnMsg + " 快递已入库");
			}else {
				return backJsonWithCode(successCode,"消息发送成功,快递已入库");
			}
		}else {
			String userName = user.getName();
			wxService.sendMessageExpress(sendOpenId,userName,sysExpress,user);	
		}
		model.addAttribute("openId",openId);
		return backJsonWithCode(successCode,MSG_EXPRESS_SAVE_SUCCESS);
	}
	
	//首页查询快递
	@ResponseBody
	@RequestMapping(value="/indexQueryExpress",method=RequestMethod.GET)
	public String indexQueryExpress(HttpServletRequest request, HttpServletResponse response,Model model) {
		final String errCode_1 = "1";
		Map<String, Object> filterData = model.asMap();
		String errUrl = (String)filterData.get("errUrl");
		if(null != errUrl) {
			return backJsonWithCode(errCode_1,(String)filterData.get("message"));
		}
		
		//是否已经注册并且激活
		String openId = (String)filterData.get("openId");
		//微信号为空
		if(StringUtils.isEmpty(openId)) {
			return backJsonWithCode(errCode_1,ERR_OPEN_ID_NOT_GET);
		}
		
		String expressNum=request.getParameter("expressNum");//获取idCard
		//快递号
		if(StringUtils.isEmpty(expressNum)) {
			return backJsonWithCode(errCode_1,ERR_ID_EXPRESS_NULL);
		}
		final String successCode = "0";//成功码
		//查询快递
		
		/*	SysExpress sysExpress = wxService.findExpressByExpressId(expressNum);
		if(null == sysExpress) {
			return backJsonWithCode(errCode_1,ERR_EXPRESS_NOT_ARRIVE);
		}
		
		String expressState = sysExpress.getState();
		String startState = DictUtils.getDictValue("已入库", "expressState", "0");
		if(expressState.equals(startState)) {
			return backJsonWithCode(successCode,MSG_EXPRESS_ARRIVER_SUCCESS);
		}
		String endState = DictUtils.getDictValue("已完结", "expressState", "0");
		if(expressState.equals(endState)) {
			return backJsonWithCode(successCode,MSG_EXPRESS_END_SUCCESS);
		}*/
		if(wxService.findExpressState(expressNum)) {
			return backJsonWithCode(successCode,MSG_EXPRESS_ARRIVER_SUCCESS);
		}
		return backJsonWithCode(errCode_1,ERR_NO_EXPRESS_INFO);
	}
	
	//解析二维码
	@ResponseBody
	@RequestMapping(value="/queryQRecordData",method=RequestMethod.POST)
	public String queryQRecordData(HttpServletRequest request, HttpServletResponse response,Model model) {
		final String errCode_1 = "1";
		Map<String, Object> filterData = model.asMap();
		String errUrl = (String)filterData.get("errUrl");
		if(null != errUrl) {
			return backJsonWithCode(errCode_1,(String)filterData.get("message"));
		}
		
		//是否已经注册并且激活
		String openId = (String)filterData.get("openId");
		//微信号为空
		if(StringUtils.isEmpty(openId)) {
			return backJsonWithCode(errCode_1,ERR_OPEN_ID_NOT_GET);
		}
		
		String qRecordData=request.getParameter("qRecordData");//获取idCard
		//身份证号
		if(StringUtils.isEmpty(qRecordData)) {
			return backJsonWithCode(errCode_1,ERR_Q_RECORD_NULL);
		}
		
		//无二维码信息
		String cacheKey = "qr_"+qRecordData;
		Qrecord qrecord = (Qrecord)CacheUtils.get(cacheKey);
		if(null == qrecord) {
			return backJsonWithCode(errCode_1,ERR_NO_QREORD_INFO);
		}
		//是否已经超时
		long timeOut = qrecord.getTimeOut();
		if(System.currentTimeMillis() > timeOut) {
			//移除缓存验证码 已经完成验证了
			CacheUtils.remove(cacheKey);
			return backJsonWithCode(errCode_1,ERR_NO_QREORD);
		}
		
		CacheUtils.clearQRecordCacheKeies();//清除超时缓存
		
		final String successCode = "0";//成功码
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("code", successCode);
		map.put("QRecordIdCard", qrecord.getIdCard());
		String jsonResult = JSONObject.toJSONString(map);//将map对象转换成json类型数据
		logger.info("二维码信息是："+jsonResult);
		return jsonResult;
	}
	//查询快递
	@ResponseBody
	@RequestMapping(value="/queryExpress",method=RequestMethod.POST)
	public String queryExpress(HttpServletRequest request, HttpServletResponse response,Model model) {
		final String errCode_1 = "1";
		Map<String, Object> filterData = model.asMap();
		String errUrl = (String)filterData.get("errUrl");
		if(null != errUrl) {
			return backJsonWithCode(errCode_1,(String)filterData.get("message"));
		}
		//是否已经注册并且激活
		String openId = (String)filterData.get("openId");
		//微信号为空
		if(StringUtils.isEmpty(openId)) {
			return backJsonWithCode(errCode_1,ERR_OPEN_ID_NOT_GET);
		}
		
		String idCard=request.getParameter("idCard");//获取idCard
		
		final String successCode = "0";//成功码
		final String errCode_2 = "2";//身份证号格式错误
		

		//身份证号
		if(StringUtils.isEmpty(idCard)) {
			return backJsonWithCode(errCode_1,ERR_ID_CARD_NULL);
		}
		
		//身份证号格式
		if(!IdcardUtils.validateCard(idCard)) {
			return backJsonWithCode(errCode_2,ERR_ID_CARD_PATTERN);
		}
		
		//查询操作人员
		User user = wxService.findOperator(openId);
		if(null == user) {
			return backJsonWithCode(errCode_2,ERR_NO_USER);
		}
		
		//验证快递单号
		List<SysExpress> sysExpresses = wxService.findNoActiveByIdCard(idCard);
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("code", successCode);
		
		if(null == sysExpresses || sysExpresses.size() == 0) {
			map.put("num", 0);
		}else {
			List<JsonSysExpress> jsonList = new ArrayList<JsonSysExpress>();
			for(SysExpress index:sysExpresses) {
				JsonSysExpress temp = new JsonSysExpress();
				temp.setExpressId(index.getExpressId());
				temp.setAddress(index.getPickUpCodeShow());//取货码
				temp.setName(index.getSysWxUser().getName());
				temp.setPhone(index.getPhone());
				temp.setCompany(DictUtils.getDictLabel(index.getCompany(), "expressCompany", "其它"));
				jsonList.add(temp);
			}
			
			map.put("num", sysExpresses.size());
			map.put("expressList", jsonList);
		}
		
		String jsonResult = JSONObject.toJSONString(map);//将map对象转换成json类型数据
		logger.info("快递信息是："+jsonResult);
		return jsonResult;
	}
	
	//收取快递信息
	@ResponseBody
	@RequestMapping(value="/endExpress",method=RequestMethod.POST)
	public String endExpress(HttpServletRequest request, HttpServletResponse response,Model model) {
		final String errCode_1 = "1";
		Map<String, Object> filterData = model.asMap();
		String errUrl = (String)filterData.get("errUrl");
		if(null != errUrl) {
			return backJsonWithCode(errCode_1,(String)filterData.get("message"));
		}
		//是否已经注册并且激活
		String openId = (String)filterData.get("openId");
		//微信号为空
		if(StringUtils.isEmpty(openId)) {
			return backJsonWithCode(errCode_1,ERR_OPEN_ID_NOT_GET);
		}
		
		String expNum = request.getParameter("expNum");//获取userId
		
		final String successCode = "0";//成功码
		
		//微信号为空
		if(StringUtils.isEmpty(openId)) {
			return backJsonWithCode(errCode_1,ERR_OPEN_ID_NOT_GET);
		}
		
		//快递号不能为空
		if(StringUtils.isEmpty(openId)) {
			return backJsonWithCode(errCode_1,ERR_ID_EXPRESS_NULL);
		}
		
		//查询操作人员
		User user = wxService.findOperator(openId);
		if(null == user) {
			return backJsonWithCode(errCode_1,ERR_NO_USER);
		}
		
		//快递为空
		SysExpress sysExpress = wxService.findExpressByExpressId(expNum);
		if(null == sysExpress) {
			return backJsonWithCode(errCode_1,ERR_EXPREE_NOT_EXIST);
		}
		
		//快递非入库状态
		String expressState = sysExpress.getState();
		String startState = DictUtils.getDictValue("已入库", "expressState", "0");
		if(null == expressState || !expressState.equals(startState)) {
			return backJsonWithCode(errCode_1,ERR_EXPREE_NOT_ENTER);
		}
		
		wxService.endExpress(expNum, openId);
		return backJsonWithCode(successCode,MSG_EXPRESS_QUERY_SUCCESS);
	}

	
}
