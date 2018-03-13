package com.thinkgem.jeesite.modules.sys.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.config.WxGlobal;
import com.thinkgem.jeesite.common.persistence.BaseEntity;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.AliyunSendMsgUtils;
import com.thinkgem.jeesite.common.utils.CasUtils;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.utils.TimeUtils;
import com.thinkgem.jeesite.common.utils.WxUrlUtils;
import com.thinkgem.jeesite.modules.sys.dao.DictDao;
import com.thinkgem.jeesite.modules.sys.dao.SysExpressDao;
import com.thinkgem.jeesite.modules.sys.dao.SysWxInfoDao;
import com.thinkgem.jeesite.modules.sys.dao.SysWxUserCheckDao;
import com.thinkgem.jeesite.modules.sys.dao.SysWxUserDao;
import com.thinkgem.jeesite.modules.sys.dao.UserDao;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.entity.SysExpress;
import com.thinkgem.jeesite.modules.sys.entity.SysWxInfo;
import com.thinkgem.jeesite.modules.sys.entity.SysWxUser;
import com.thinkgem.jeesite.modules.sys.entity.SysWxUserCheck;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.entity.wx.WechatMsg;
import com.thinkgem.jeesite.modules.sys.entity.wx.WechatTextMsg;
import com.thinkgem.jeesite.modules.sys.entity.wx.WxTemplate;
import com.thinkgem.jeesite.modules.sys.entity.wx.WxTemplateData;
import com.thinkgem.jeesite.modules.sys.manager.WxAccessTokenManager;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thoughtworks.xstream.XStream;

import net.sf.json.JSONObject;

/**
 * 微信信息处理
 * @author wzy
 *
 */
@Service
public class WxService extends BaseService implements InitializingBean {
	
	private final String DEFAULT_ID_SYS_MANAGER = "1";//系统管理员默认ID
	
	@Autowired
	private SysWxUserDao sysWxUserDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SysExpressDao sysExpressDao;
	
	@Autowired
	private SysWxInfoDao sysWxInfoDao;
	
	@Autowired
	private SysWxUserCheckDao sysWxUserCheckDao;
	
	@Autowired
	private DictDao dictDao;

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
	
	
	/**
	 * 依据电话和身份证查询(未激活的)
	 * @param idCard
	 * @return
	 */
	public SysWxUserCheck findUserCheck(String idCard,String phone) {
		return sysWxUserCheckDao.findByIdCardAndPhone(idCard,phone,DictUtils.getDictValue("未激活", "userCheckState", "0"));
	}
	
	/**
	 * 依据电话查询(未激活的)
	 * @param idCard
	 * @return
	 */
	public SysWxUserCheck findUserCheckByPhone(String phone) {
		return sysWxUserCheckDao.findByPhone(phone,DictUtils.getDictValue("未激活", "userCheckState", "0"));
	}
	
	/**
	 * 依据身份证查询(未激活的)
	 * @param idCard
	 * @return
	 */
	public SysWxUserCheck findUserCheckByIdCard(String idCard) {
		return sysWxUserCheckDao.findByIdCard(idCard,DictUtils.getDictValue("未激活", "userCheckState", "0"));
	}
	
	/**
	 * 依据微信号查询审核信息
	 */
	public SysWxUserCheck findByOpenId(String openId) {
		return sysWxUserCheckDao.findByOpenId(openId);
	}
	
	//查询总数量
	public List<SysWxInfo> findSysWxInfoTotal(){
		return sysWxInfoDao.findByTotal();
	} 
	
	//查询年数量
	public List<SysWxInfo> findSysWxInfoByYear(){
		Date startTime =  CasUtils.getCurrentYearStartTime();
		Date endTime =  CasUtils.getCurrentYearEndTime();
		logger.info("年起始时间:" + startTime);
		logger.info("年结束时间:" + endTime);
		if(null == startTime || null == endTime) {
			return null;
		}
		return sysWxInfoDao.findByTime(CasUtils.convertDate2HMSString(startTime),CasUtils.convertDate2HMSString(endTime));
	} 
	
	//查询月数量
	public List<SysWxInfo> findSysWxInfoByMonth(){
		Date startTime =  CasUtils.getCurrentMonthStartTime();
		Date endTime =  CasUtils.getCurrentMonthEndTime();
		logger.info("月起始时间:" + startTime);
		logger.info("月结束时间:" + endTime);
		if(null == startTime || null == endTime) {
			return null;
		}
		return sysWxInfoDao.findByTime(CasUtils.convertDate2HMSString(startTime),CasUtils.convertDate2HMSString(endTime));
	} 
	
	/**
	 * 依据微信号查询审核信息
	 */
	public User findUserManagerByOpenId(String openId) {
		if(StringUtils.isEmpty(openId)) {
			return null;
		}
		
		SysWxInfo sysWxInfo =  sysWxInfoDao.findOperatorByOpenId(openId);
		if(null == sysWxInfo) {
			return null;
		}
		String idCard = sysWxInfo.getIdCard();
		return userDao.findByIdCard(idCard);
	}
	
	
	/**
	 * 保存注册信息 等待审核
	 * @param param
	 * @return
	 */
	@Transactional(readOnly = false)
	public SysWxUserCheck saveSysWxUserCheck(SysWxUserCheck param) {
		 //不存在  保存
		//获取操作人信息 默认为微信用户
		String openId = param.getOpenId();
		if(null == openId) {
			return null;
		}
		User user = UserUtils.get(DEFAULT_ID_SYS_MANAGER);
		if(null == user) {
			logger.info("No wxuser");
			return null;
		}
		param.setId(IdGen.uuid());
		param.setCreateBy(user);
		param.setCreateDate(new Date());
		param.setUpdateBy(user);
		param.setUpdateDate(new Date());
		sysWxUserCheckDao.insert(param);
		return param;
	}
	
	/**
	 * 依据ID查询用户信息
	 * @param idCard
	 * @return
	 */
	public SysWxUser findSysUserById(String id) {
		if(null!=id) {
			return sysWxUserDao.get(id);
		}
		return null;
	}
	
	/**
	 * 依据身份证查询用户信息
	 * @param idCard
	 * @return
	 */
	public SysWxUser findByIdCard(String idCard) {
		if(null!=idCard) {
			return sysWxUserDao.findByIdCard(idCard);
		}
		return null;
	}
	
	/**
	 * 依据微信号查询信息
	 * @param openId
	 * @return
	 */
	public SysWxUser findSysUserByOpenId(String openId) {
		//没有微信号
		if(null == openId) {
			return null;
		}
		SysWxInfo sysWxInfo = sysWxInfoDao.findByOpenId(openId);
		//没有查到微信信息
		if(null == sysWxInfo) {
			return null;
		}
		
		String idCard = sysWxInfo.getIdCard();
		//身份证号为空
		if(null == idCard) {
			return null;
		}
		
		SysWxUser sysWxUser = sysWxUserDao.findByIdCard(idCard);
		//个人信息为空
		if(null == sysWxUser) {
			return null;
		}
		return sysWxUser;
	}
	
	
	/**
	 * 依据身份证查询微信用户
	 * @param phone
	 * @return
	 */
	public SysWxInfo findWxInfoByIdCard(String idCard) {
		if(null!=idCard) {
			return sysWxInfoDao.findByIdCard(idCard);
		}
		return null;
	}
	
	/**
	 * 依据电话查询用户信息
	 * @param phone
	 * @return
	 */
	public SysWxUser findByPhone(String phone) {
		if(null!=phone) {
			return sysWxUserDao.findByPhone(phone);
		}
		return null;
	}
	
	/**
	 * 依据身份证和电话查询信息是否存在
	 */
	public SysWxUser findWxUser(String phone,String idCard) {
		if(null!=phone&&null!=idCard) {sysWxUserDao.findByIdCardAndPhone(idCard, phone);}return null;
	}
	
	/**
	 * 查询快递
	 */
	public List<SysExpress> findExpressByIdCard(String idCard) {
		return sysExpressDao.findByIdCard(idCard);
	}
	
	/**
	 * 查询快递
	 */
	public List<SysExpress> findNoActiveByIdCard(String idCard) {
		return sysExpressDao.findNoActiveByIdCard(idCard);
	}
	
	
	/**
	 * 保存快递
	 */
	@Transactional(readOnly = false)
	public SysExpress saveExpress(SysExpress sysExpress,User user) {
		
		String today = CasUtils.convertDate2DefaultString(new Date());//当下的日期
		String company = sysExpress.getCompany();//公司
		
		//默认保存数据
		sysExpress.setId(IdGen.uuid());
		sysExpress.setMsgState(queryMsgState(sysExpress));
		sysExpress.setCreateBy(user);
		sysExpress.setCreateDate(new Date());
		sysExpress.setUpdateBy(user);
		sysExpress.setUpdateDate(new Date());
		sysExpress.setEnterTime(today);
		
		/**
		 * 自动生成取货码
		 * 该公司当天最大的取货号，后续自动增加一个
		 */
		Integer pickUpCode = sysExpressDao.findMaxPickUp(company, today);
		if(null == pickUpCode) {
			pickUpCode = 0;
		}
		pickUpCode++;//自增一个
		sysExpress.setPickUpCode(pickUpCode.toString());
		sysExpressDao.insert(sysExpress);
		return sysExpress;
	}
	
	
	//记录快递数量
	@Transactional(readOnly = false)
	public void recordExpressNum(SysExpress sysExpress,User user) {
		String phone = sysExpress.getPhone();
		if(null == phone) {
			logger.info("快递手机号不存在");
			return;
		}
		
		SysWxUser sysWxUser = findByPhone(phone);
		if(null == sysWxUser) {
			logger.info("快递无关联用户");
			return;
		}
		
		String idCard = sysWxUser.getIdCard();
		if(null == idCard) {
			logger.info("快递无关联用户身份证号");
			return;
		}
		
		SysWxInfo sysWxInfo = findWxInfoByIdCard(idCard);
		if(null == sysWxInfo) {
			logger.info("快递无关联微信用户");
			return;
		}
		
		//快递数量加一
		Integer expressNum = Integer.valueOf(sysWxInfo.getExpressNum());
		expressNum++;
		sysWxInfo.setExpressNum(expressNum.toString());
		sysWxInfo.setUpdateBy(user);
		sysWxInfo.setUpdateDate(new Date());
		sysWxInfoDao.update(sysWxInfo);
	}
	
	//快递信息状态查询
	public String queryMsgState(SysExpress sysExpress) {
		
		String dictType = "expressMsgState";
		
		String phone = sysExpress.getPhone();
		
		//如果快递没有电话 那么不进行消息发送
		if(null == phone) {
			return DictUtils.getDictValue("快递单未绑定电话", dictType, "0");
		}
		
		//查询个人信息
		SysWxUser sysWxUser = sysWxUserDao.findByPhone(phone);
		//如果快递没有用户  那么不进行消息发送
		if(null == sysWxUser) {
			return DictUtils.getDictValue("没有关联的用户", dictType, "0");
		}
		
		String idCard = sysWxUser.getIdCard();
		//如果用户没有绑定身份信息  那么不进行消息发送
		if(null == idCard) {
			return DictUtils.getDictValue("用户未绑定身份信息", dictType, "0");
		}
		
		//查询微信号 微信没有绑定 不进行消息发送
		SysWxInfo sysWxInfo = sysWxInfoDao.findByIdCard(idCard);
		if(null == sysWxInfo) {
			return DictUtils.getDictValue("用户未关联微信", dictType, "0");
		}
		String openId = sysWxInfo.getOpenId();
		//如果用户没有绑定微信信息  那么不进行消息发送
		if(null == openId) {
			return DictUtils.getDictValue("用户未进行微信授权", dictType, "0");
		}
		
		return DictUtils.getDictValue("信息已发送", dictType, "0");
	}
	
	//消息发送获取openId
	public String getOpenIdForMsg(SysExpress sysExpress) {
		String phone = sysExpress.getPhone();
		
		//如果快递没有电话 那么不进行消息发送
		if(null == phone) {
			return null;
		}
		logger.info("phone is "+phone);
		
		//查询个人信息
		SysWxUser sysWxUser = sysWxUserDao.findByPhone(phone);
		//如果快递没有用户  那么不进行消息发送
		if(null == sysWxUser) {
			return null;
		}
		
		String idCard = sysWxUser.getIdCard();
		//如果用户没有绑定身份信息  那么不进行消息发送
		if(null == idCard) {
			return null;
		}
		logger.info("idCard is "+idCard);
		
		//查询微信号 微信没有绑定 不进行消息发送
		SysWxInfo sysWxInfo = sysWxInfoDao.findByIdCard(idCard);
		if(null == sysWxInfo) {
			return null;
		}
		String openId = sysWxInfo.getOpenId();
		//如果用户没有绑定微信信息  那么不进行消息发送
		if(null == openId) {
			return null;
		}
		logger.info("openId is "+openId);
		return openId;
	}
	
	/**
	 * 获取access_token和openId
	 * @param code
	 * @return
	 */
	public Map<String,String> getOpenIdInfo(String code) {
		Map<String,String> ret = new HashMap<String,String>();
		String url = String.format(WxGlobal.getUserInfoTokenUrl(),WxGlobal.getAppId(),WxGlobal.getAppSecret(),code);
		logger.info("request accessToken from url: {}", url);
		JSONObject jsonObject = WxUrlUtils.httpRequest(url, Global.GET_METHOD, null);
		logger.info("返回的JSON是："+jsonObject);
		if(null != jsonObject) {
  		  	String accessToken = jsonObject.getString("access_token");
  		  	String openId = jsonObject.getString("openid");
  		  	logger.info(" access_token is " + accessToken + " openId is "+openId);
  		  	ret.put("access_toke", accessToken);
  		    ret.put("openId", openId);
		}else {
			logger.info("get accessToken by code is error");
		}
		return ret;
	}
	
	public Map<String,String> getWxUserInfo(String access_toke,String openId) {
		Map<String,String> ret = new HashMap<String,String>();
		String url = String.format(WxGlobal.getUserInfoUrl(),access_toke,openId);
		logger.info("request WxUserInfo from url: {}", url);
		JSONObject jsonObject = WxUrlUtils.httpRequest(url, Global.GET_METHOD, null);
		logger.info("返回的JSON是："+jsonObject);
		if(null != jsonObject) {
  		  	String nickname = jsonObject.getString("nickname");
  		  	String headimgurl = jsonObject.getString("headimgurl");
  		  	String sex = jsonObject.getString("sex");
  		  	logger.info(" nickname is " + nickname + " headimgurl is "+headimgurl+" sex is " + sex);
  		  	ret.put("nickname", nickname);
  		    ret.put("headimgurl", headimgurl);
  		    ret.put("sex", sex);
		}else {
			logger.info("get accessToken by code is error");
		}
		return ret;
	}
	
	//保存用户信息(头像等)
	@Transactional(readOnly = false)
	public void saveWxInfo(Map<String,String> param) {
		if(null == param) {
			logger.info("用户信息为空，无法更新");
			return;
		}
		String openId = param.get("openId");
		if(null == openId) {
			logger.info("openId为空，无法更新");
			return;
		}
		String accessToken = param.get("access_toke");
		if(null == accessToken) {
			logger.info("accessToken为空，无法更新");
			return;
		}
		Map<String,String> map = getWxUserInfo(accessToken,openId);
		String nickname = map.get("nickname");
		String sex = map.get("sex");
		String headimgurl = map.get("headimgurl");
		SysWxInfo sysWxInfo = sysWxInfoDao.findByOpenId(openId);
		if(null != sysWxInfo) {
			logger.info("nickname是:"+nickname);
			logger.info("headimgurl是:"+headimgurl);
			logger.info("sex是"+sex);
			boolean isUpdate = false;
			if(!StringUtils.isEmpty(nickname)) {
				isUpdate = true;
				sysWxInfo.setNickname(nickname);
			}
			if(!StringUtils.isEmpty(sex)) {
				isUpdate = true;
				sysWxInfo.setSex(sex);
			}
			if(!StringUtils.isEmpty(headimgurl)) {
				isUpdate = true;
				sysWxInfo.setHeadimgurl(headimgurl);
			}
			if(isUpdate) {
				User user = UserUtils.get("1");
				sysWxInfo.setUpdateBy(user);
				sysWxInfo.setUpdateDate(new Date());
				sysWxInfoDao.update(sysWxInfo);
			}
		}else {
			sysWxInfo = new SysWxInfo();
			logger.info("nickname是:");
			logger.info("headimgurl是:");
			logger.info("sex是");
			User user = UserUtils.get("1");
			//第一次操作
			sysWxInfo.setId(IdGen.uuid());
			sysWxInfo.setIdCard(null);
			sysWxInfo.setOpenId(openId);
			sysWxInfo.setExpressNum("0");
			sysWxInfo.setUpdateBy(user);
			sysWxInfo.setUpdateDate(new Date());
			sysWxInfo.setCreateBy(user);
			sysWxInfo.setCreateDate(new Date());
			if(!StringUtils.isEmpty(nickname)) {
				sysWxInfo.setNickname(nickname);
			}
			if(!StringUtils.isEmpty(sex)) {
				sysWxInfo.setSex(sex);
			}
			if(!StringUtils.isEmpty(headimgurl)) {
				sysWxInfo.setHeadimgurl(headimgurl);
			}
			sysWxInfoDao.insert(sysWxInfo);
		}
	}
	
	//查找微信信息
	public SysWxInfo findSysWxInfoByOpenId(String openId) {
		return sysWxInfoDao.findByOpenId(openId);
	}
	
	//短信是否超出限额
	public boolean isAliyunMsgLimit() {
			String sendMaxNum = DictUtils.getDictValue("ALIYUN_MSG_MAX", "systemControl", "5000");//最大次数
			String currentNum = DictUtils.getDictValue("ALIYUN_MSG_SEND_MSG", "systemControl", sendMaxNum);//目前次数
			if(Integer.valueOf(currentNum) < Integer.valueOf(sendMaxNum)) {
				return false;//没有超出 可以发送
			}
			return false;
	}
		
	//阿里云短信发送次数增加一次，防止短信次数超出套餐限额
	@Transactional(readOnly = false)
	public void aliyunMsgNumAdd(User user) {
			//已经发送次数
			List<Dict> dicts = DictUtils.getDictList("systemControl");
			if(null == dicts) {
				logger.info("systemControl字典不存在");
				return;
			}
			Dict sendDict = null;
			//找到该参数
			Iterator<Dict> its = dicts.iterator();
			while(its.hasNext()) {
				Dict it = its.next();
				String label = it.getLabel();
				if("ALIYUN_MSG_SEND_MSG".equals(label)) {
					sendDict = it;
					break;
				}
			}
			if(null == sendDict) {
				return;//没有找到
			}
			
			//先转换下数值
			String value = sendDict.getValue();
			Integer intValue = Integer.valueOf(value);
			
			//要求发送的次数
			String sendNum = DictUtils.getDictValue("ALIYUN_MSG_MAX", "systemControl", "5000");
			Integer sendNumValue = Integer.valueOf(sendNum);
			if(intValue >= sendNumValue) {
				//超出次数 不能增加
				return;
			}
			
			intValue++;//一个
			sendDict.setValue(intValue.toString());
			sendDict.setUpdateBy(user);
			sendDict.setUpdateDate(new Date());
			dictDao.update(sendDict);//保存次数
	}
	
	//发送短信消息
	@Transactional(readOnly = false)
	public String sendAliyunMsgTemplate(SysExpress sysExpress,User user) {
		//发送验证码
		if(isAliyunMsgLimit()) {
			//超出限制
			return "消息套餐已用完";
		}
		Map<String,String> map = new HashMap<String,String>();
		map.put("company", DictUtils.getDictLabel(sysExpress.getCompany(),"expressCompany", "0"));
		map.put("num", sysExpress.getPickUpCode());
		if(!AliyunSendMsgUtils.sendMsg(sysExpress.getPhone(), map,1)) {
			//发送失败
			return "消息接口调用发送失败";
		}
		//发送成功 记录下发送的次数
		aliyunMsgNumAdd(user);
		return null;//成功
	}
	
	
	 /**
	  * 保存微信号
	  * @param code
	  * @return
	  */
	 @Transactional(readOnly = false)
	 public SysWxInfo saveOpenId(String code) {
		 //获取access_token和openId
		  Map<String,String> maps = getOpenIdInfo(code);
		// Map<String,String> maps = new HashMap<String,String>();
		 //maps.put("openId", "wzy");
		 String openId = maps.get("openId");
		 if(null == openId) {
			 return null;//openId获取失败
		 }
		 SysWxInfo queryResult = sysWxInfoDao.findByOpenId(openId);
		 if(null == queryResult) {
			 //不存在  保存
			//获取操作人信息 默认为微信用户
			User user = UserUtils.get(DEFAULT_ID_SYS_MANAGER);
			if(null == user) {
				logger.info("No wxuser");
				return null;
			}
			SysWxInfo sysWxInfo = new SysWxInfo();
			sysWxInfo.setId(IdGen.uuid());
			sysWxInfo.setOpenId(openId);
			sysWxInfo.setCreateBy(user);
			sysWxInfo.setCreateDate(new Date());
			sysWxInfo.setUpdateBy(user);
			sysWxInfo.setUpdateDate(new Date());
			sysWxInfoDao.insert(sysWxInfo);
			return sysWxInfo;//不存在 保存成功
		 }else {
			 return queryResult;//存在
		 }
	 }
	 
	 /**
	  * 依据openId查找个人信息
	  * @param openId
	  * @return
	  */
	 public SysWxUser getSysWxUser(String openId) {
		if(null == openId) {
			return null;
		}
		
		//没有关联号
		SysWxInfo sysWxInfo = sysWxInfoDao.findByOpenId(openId);
		if(null == sysWxInfo) {
			return null;
		}
		String idCard = sysWxInfo.getIdCard();
		//没有身份证号
		if(null == idCard) {
			return null;
		}
		
		SysWxUser sysWxUser = sysWxUserDao.findByIdCard(idCard);
		//没有信息
		if(null == sysWxUser||null == sysWxUser.getIdCard()) {
			return null;
		}
		return sysWxUser;
	}
	 
	@Transactional(readOnly = false)
	public void saveSysWxUserInfo(SysWxUser sysWxUser) {
		sysWxUserDao.insert(sysWxUser);
	}
	
	@Transactional(readOnly = false)
	public void saveSysWxInfoInfo(SysWxInfo sysWxInfo) {
		sysWxInfoDao.insert(sysWxInfo);
	}
	
	@Transactional(readOnly = false)
	public void updateSysWxInfoInfo(SysWxInfo sysWxInfo) {
		sysWxInfoDao.update(sysWxInfo);
	}
	
	//查询快递
	public SysExpress findExpressByExpressId(String expressId) {
		return sysExpressDao.findByExpressId(expressId);
	}
	
	/**
	   * 获取媒体文件
	   * @param accessToken 接口访问凭证
	   * @param mediaId 媒体文件id
	   * @param savePath 文件在本地服务器上的存储路径
	   * */
	 public  String downloadMedia(String mediaId, String savePath,String openId) {
		 String filePath = null;
	     try {
	    	//获取Token
	    	 String requestUrl = String.format(WxGlobal.getUpImage(mediaId));
	    	 logger.info("格式化Url:" + requestUrl); 
	    	 URL url = new URL(requestUrl);
	    	 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    	 conn.setDoInput(true);
		     conn.setRequestMethod("GET");
		     if (!savePath.endsWith("/")) {
		        savePath += "/";
		     }
		     // 根据内容类型获取扩展名
		     String fileExt = CasUtils .getFileexpandedName(conn.getHeaderField("Content-Type"));
		     // 将mediaId作为文件名
		      filePath = savePath + openId + fileExt;
		      BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
		      FileOutputStream fos = new FileOutputStream(new File(filePath));
		      byte[] buf = new byte[8096];
		      int size = 0;
		      while ((size = bis.read(buf)) != -1)
		      fos.write(buf, 0, size);
		      fos.close();
		      bis.close();
		      conn.disconnect();
		      String info = String.format("下载媒体文件成功，filePath=" + filePath);
	    } catch (IOException e) {
	    	  e.printStackTrace();
	 	      String error = String.format("下载媒体文件失败：%s", e);
	 	      System.out.println(error);
	    }
	    return filePath;
	}
	
	//取货
	@Transactional(readOnly = false)
	public void endExpress(String expressId,String openId) {
		SysExpress sysExpress = findExpressByExpressId(expressId);
		String state = DictUtils.getDictValue("已完结", "expressState", "1");
		sysExpress.setState(state);
		//查询操作人员
		User user = findOperator(openId);
		sysExpress.setUpdateBy(user);;
		sysExpress.setUpdateDate(new Date());
		//默认保存数据
		sysExpress.setMsgState(queryMsgState(sysExpress));
		sysExpressDao.update(sysExpress);
		//记录快递数量
		recordExpressNum(sysExpress,user);
		
		
		//查询快递接收人
		String toUserPhone = sysExpress.getPhone();
		//查询快递电话
		if(null == toUserPhone) {
			logger.info("save send phone is null ");
			return;
		}
		
		//查询关联人
		SysWxUser sysWxUser = findByPhone(toUserPhone);
		if(null == sysWxUser) {
			logger.info("save send sysWxUser is null ");
			return;
		}
		
		//查询身份证号
		String idCard = sysWxUser.getIdCard();
		if(null == idCard) {
			logger.info("save send idCard is null ");
			return;
		}
		
		//查询微信
		SysWxInfo sysWxInfo = this.findWxInfoByIdCard(idCard);
		if(null == sysWxInfo) {
			logger.info("save send sysWxInfo is null ");
			return;
		}
		//查询关联人微信
		String toUserOpenId = sysWxInfo.getOpenId();
		if(null == toUserOpenId) {
			logger.info("save send toUserOpenId is null ");
			return;
		}
		
		/**
		 * 发送模板消息
		 */
		String userName = user.getName();
		sendMessageEndExpress(toUserOpenId,userName,"0");	
	}
	
	//更新个人信息
	@Transactional(readOnly = false)
	public SysWxUser modifyWxUser(SysWxUser sysWxUser,String openId) {
		User user = UserUtils.get(DEFAULT_ID_SYS_MANAGER);
		if(null == user) {
			logger.info("No wxuser");
			return null;
		}
		
		String idCard = sysWxUser.getIdCard();
		//添加操作信息
		sysWxUser.setUpdateBy(user);
		sysWxUser.setUpdateDate(new Date());
		updateWxInfo(idCard,openId,user);//更新微信表
		sysWxUserDao.update(sysWxUser);//更新用户表
		return sysWxUser;
	}
	//保存个人用户信息 需要将微信关联
	@Transactional(readOnly = false)
	public SysWxUser saveWxUserInfo(SysWxUser sysWxUser,String openId) {
		//获取操作人信息 默认为系统管理员
		User user = UserUtils.get(DEFAULT_ID_SYS_MANAGER);
		if(null == user) {
			logger.info("No wxuser");
			return null;
		}
		
		String idCard = sysWxUser.getIdCard();
		if(null != idCard) {
			SysWxUser temp = sysWxUserDao.findByIdCard(idCard);//已经存在了 那么要求手机号和身份证号和之前匹配才可以关联
			
			//添加操作信息
			sysWxUser.setUpdateBy(user);
			sysWxUser.setUpdateDate(new Date());
			
			if(null == temp) {
				//不存在 直接保存
				sysWxUser.setId(IdGen.uuid());
				sysWxUser.setCreateBy(user);
				sysWxUser.setCreateDate(new Date());
				sysWxUserDao.insert(sysWxUser);
				//更新关联
				addOrUpdateWxInfo(idCard,openId,user);
			}else {
				updateNewWxInfo(idCard,openId,user);//更新微信表(保存数据的时候出现了已经存在的用户，说明是用户在关联新微信)
				sysWxUserDao.update(sysWxUser);//更新用户表
			}
			return sysWxUser;
		}
		return null;
	}
	
	//保存的时候关联新微信 删除旧微信
	private void updateNewWxInfo(String idCard,String openId,User user) {
		if(null != idCard) {
			SysWxInfo querySysWxInfo = sysWxInfoDao.findByIdCard(idCard);
			String expressNum = null;
			String oldOpenId = querySysWxInfo.getOpenId();//老微信号
			if(null != querySysWxInfo) {
				logger.info("SysWxInfo is delete");
				expressNum = querySysWxInfo.getExpressNum();
				sysWxInfoDao.deleteByOpenId(oldOpenId);
				logger.info("SysWxInfo is delete");
				SysWxUserCheck sysWxUserCheck = sysWxUserCheckDao.findByOpenId(oldOpenId);
				if(null == sysWxUserCheck) {
					sysWxUserCheckDao.delete(sysWxUserCheck);
				}
			}
			//添加信息
			SysWxInfo newSysWxInfo = sysWxInfoDao.findByOpenId(openId);
			newSysWxInfo.setIdCard(idCard);
			newSysWxInfo.setUpdateBy(user);
			newSysWxInfo.setUpdateDate(new Date());
			if(!StringUtils.isEmpty(expressNum)) {
				newSysWxInfo.setExpressNum(expressNum);
			}
			sysWxInfoDao.update(newSysWxInfo);
		}
	}
	
	//更新微信信息
	private void updateWxInfo(String idCard,String openId,User user) {
		if(null != idCard) {
			SysWxInfo querySysWxInfo = sysWxInfoDao.findByIdCard(idCard);
			
			
			if(null != querySysWxInfo) {
				logger.info("SysWxInfo is update");
				//不等于空才更新，空的话是插入操作
				String oldOpenId = querySysWxInfo.getOpenId();//老微信号
				
				//添加信息
				querySysWxInfo.setOpenId(openId);
				querySysWxInfo.setUpdateBy(user);
				querySysWxInfo.setUpdateDate(new Date());
				
				sysWxInfoDao.update(querySysWxInfo);
				//删除检查表的数据
				SysWxUserCheck sysWxUserCheck = sysWxUserCheckDao.findByOpenId(oldOpenId);
				if(null == sysWxUserCheck) {
					sysWxUserCheckDao.delete(sysWxUserCheck);
				}
			}
		}
	}
	
	/**
	 * 查询操作员（微信登录人员）
	 * @param sysExpress
	 * @return
	 */
	public User findOperator(String openId) {
		if(null == openId) {
			//参数为空
			return null;
		}
		SysWxInfo sysWxInfo = sysWxInfoDao.findByOpenId(openId);
		if(null == sysWxInfo) {
			//没有绑定微信
			return null;
		}
		String idCard = sysWxInfo.getIdCard();
		if(null == idCard) {
			//没有身份信息
			return null;
		}
		SysWxUser sysWxUser = sysWxUserDao.findByIdCard(idCard);
		if(null == sysWxUser) {
			//微信中没有注册个人信息
			return null;
		}
		User user = UserUtils.findByIdCard(idCard);
		if(null == user) {
			//操作人员没有关联
			return null;
		}
		return user;
	}
	
	
	//更新或者插入微信信息
	private void addOrUpdateWxInfo(String idCard,String openId,User user) {
		if(null != idCard) {
			SysWxInfo querySysWxInfo = sysWxInfoDao.findByOpenId(openId);
			if(null == querySysWxInfo) {
				logger.info("SysWxInfo is add");
				SysWxInfo sysWxInfo = new SysWxInfo();
				sysWxInfo.setId(IdGen.uuid());
				sysWxInfo.setIdCard(idCard);
				sysWxInfo.setOpenId(openId);
				sysWxInfo.setCreateBy(user);
				sysWxInfo.setCreateDate(new Date());
				sysWxInfo.setUpdateBy(user);
				sysWxInfo.setUpdateDate(new Date());
				sysWxInfo.setDelFlag(SysWxInfo.DEL_FLAG_NORMAL);
				sysWxInfoDao.insert(sysWxInfo);
			}else {
				logger.info("SysWxInfo is update");
				querySysWxInfo.setIdCard(idCard);
				querySysWxInfo.setUpdateBy(user);
				querySysWxInfo.setUpdateDate(new Date());
				sysWxInfoDao.update(querySysWxInfo);
			}
		}
	}
	
	/**
	 * 激活成功消息提示
	 * @param toUser
	 * @param username
	 * @return
	 */
	public String sendMessageActive(String toUser,String username) {
		logger.info("send msg start");
		/*
		 *	模板ID 为 43WT2ikE7JLLBMcBRPqZyW_HgLiDjpoX6X7h05_Mscw
		 *	内容：
		 *		{{first.DATA}}
				激活时间：{{keyword1.DATA}}
				激活账户：{{keyword2.DATA}}
				{{remark.DATA}}
		 */
		
		//first.DATA
		WxTemplateData first = new WxTemplateData();
		first.setColor(WxGlobal.getTemplateMsgColor_1());
		first.setValue("账户激活成功通知");
		WxTemplateData keyword1 = new WxTemplateData();
		keyword1.setColor(WxGlobal.getTemplateMsgColor_1());
		keyword1.setValue(DateUtils.getDateTime());
		WxTemplateData keyword2 = new WxTemplateData();
		keyword2.setColor(WxGlobal.getTemplateMsgColor_1());
		keyword2.setValue(username);
		WxTemplateData remark = new WxTemplateData();
		String content="感谢您的支持，详情请前往快递中心咨询";
		remark.setColor(WxGlobal.getTemplateMsgColor_1());
		remark.setValue(content);
		
		WxTemplate template = new WxTemplate();
		template.setUrl(null);
		template.setTouser(toUser);
		template.setTopcolor(WxGlobal.getTemplateMsgColor_2());
		template.setTemplate_id(WxGlobal.getTemplateMsg_2());
		Map<String,WxTemplateData> wxTemplateDatas = new HashMap<String,WxTemplateData>();
		wxTemplateDatas.put("keyword1", keyword1);
		wxTemplateDatas.put("keyword2", keyword2);
		wxTemplateDatas.put("remark", remark);
		template.setData(wxTemplateDatas);
		//获取Token
    	WxAccessTokenManager wxAccessTokenManager = WxAccessTokenManager.getInstance();
		String accessToken = wxAccessTokenManager.getAccessToken();
		String url = String.format(WxGlobal.getTemplateMsgUrl(),accessToken);
		String jsonString = JSONObject.fromObject(template).toString();
		JSONObject jsonObject = WxUrlUtils.httpRequest(url,Global.POST_METHOD,jsonString); 
		logger.info("msg is " + jsonObject);
		int result = 0;
        if (null != jsonObject) {  
             if (0 != jsonObject.getInt("errcode")) {  
                 result = jsonObject.getInt("errcode");  
                 logger.error("错误 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));  
             }  
         }
        logger.info("模板消息发送结果："+result);
		logger.info("send msg end");
		return null;
	}
	
	/**
	 * 
	 * @param toUser 接收人
	 * @param username 经办人
	 * @param money 金额
	 * @return
	 */
	public String sendMessageEndExpress(String toUser,String username,String money) {
		logger.info("send msg start");
		/*
		 *	模板ID 为 DQjKDzP4EQqrA6r_abDDYJjyNZ9071tuDls2DeNrJZA
		 *	内容：
		 *		{{first.DATA}}
				状态：{{keyword1.DATA}}
				时间：{{keyword2.DATA}}
				金额：{{keyword3.DATA}}
				经办人：{{keyword4.DATA}}
				{{remark.DATA}}
		 */
		
		//first.DATA
		WxTemplateData first = new WxTemplateData();
		first.setColor(WxGlobal.getTemplateMsgColor_1());
		first.setValue("您收到一个订单");
		WxTemplateData keyword1 = new WxTemplateData();
		keyword1.setColor(WxGlobal.getTemplateMsgColor_1());
		String state = DictUtils.getDictLabel("1","expressState","已完结");
		keyword1.setValue(state);
		WxTemplateData keyword2 = new WxTemplateData();
		keyword2.setColor(WxGlobal.getTemplateMsgColor_1());
		keyword2.setValue(DateUtils.getDateTime());
		WxTemplateData keyword3 = new WxTemplateData();
		keyword3.setColor(WxGlobal.getTemplateMsgColor_1());
		keyword3.setValue(money);
		WxTemplateData keyword4 = new WxTemplateData();
		keyword4.setColor(WxGlobal.getTemplateMsgColor_1());
		keyword4.setValue(username);
		WxTemplateData remark = new WxTemplateData();
		String content="您的快递已取走,谢谢您的合作";
		remark.setColor(WxGlobal.getTemplateMsgColor_1());
		remark.setValue(content);
		
		WxTemplate template = new WxTemplate();
		template.setUrl(null);
		template.setTouser(toUser);
		template.setTopcolor(WxGlobal.getTemplateMsgColor_2());
		template.setTemplate_id(WxGlobal.getTemplateMsg_1());
		Map<String,WxTemplateData> wxTemplateDatas = new HashMap<String,WxTemplateData>();
		wxTemplateDatas.put("keyword1", keyword1);
		wxTemplateDatas.put("keyword2", keyword2);
		wxTemplateDatas.put("keyword3", keyword3);
		wxTemplateDatas.put("keyword4", keyword4);
		wxTemplateDatas.put("remark", remark);
		template.setData(wxTemplateDatas);
		//获取Token
    	WxAccessTokenManager wxAccessTokenManager = WxAccessTokenManager.getInstance();
		String accessToken = wxAccessTokenManager.getAccessToken();
		String url = String.format(WxGlobal.getTemplateMsgUrl(),accessToken);
		String jsonString = JSONObject.fromObject(template).toString();
		JSONObject jsonObject = WxUrlUtils.httpRequest(url,Global.POST_METHOD,jsonString); 
		logger.info("msg is " + jsonObject);
		int result = 0;
        if (null != jsonObject) {  
             if (0 != jsonObject.getInt("errcode")) {  
                 result = jsonObject.getInt("errcode");  
                 logger.error("错误 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));  
             }  
         }
        logger.info("模板消息发送结果："+result);
		logger.info("send msg end");
		return null;
	}
	
	/**
	 * 
	 * @param toUser 接收人
	 * @param username 经办人
	 * @param money 金额
	 * @return
	 */
	public String sendMessageExpress(String toUser,String username,String money) {
		logger.info("send msg start");
		/*
		 *	模板ID 为 DQjKDzP4EQqrA6r_abDDYJjyNZ9071tuDls2DeNrJZA
		 *	内容：
		 *		{{first.DATA}}
				状态：{{keyword1.DATA}}
				时间：{{keyword2.DATA}}
				金额：{{keyword3.DATA}}
				经办人：{{keyword4.DATA}}
				{{remark.DATA}}
		 */
		
		//first.DATA
		WxTemplateData first = new WxTemplateData();
		first.setColor(WxGlobal.getTemplateMsgColor_1());
		first.setValue("您收到一个订单");
		WxTemplateData keyword1 = new WxTemplateData();
		keyword1.setColor(WxGlobal.getTemplateMsgColor_1());
		String state = DictUtils.getDictLabel("0","expressState","已入库");
		keyword1.setValue(state);
		WxTemplateData keyword2 = new WxTemplateData();
		keyword2.setColor(WxGlobal.getTemplateMsgColor_1());
		keyword2.setValue(DateUtils.getDateTime());
		WxTemplateData keyword3 = new WxTemplateData();
		keyword3.setColor(WxGlobal.getTemplateMsgColor_1());
		keyword3.setValue(money);
		WxTemplateData keyword4 = new WxTemplateData();
		keyword4.setColor(WxGlobal.getTemplateMsgColor_1());
		keyword4.setValue(username);
		WxTemplateData remark = new WxTemplateData();
		String content="你的快递已到，请携带身份证前往易度空间领取";
		remark.setColor(WxGlobal.getTemplateMsgColor_1());
		remark.setValue(content);
		
		WxTemplate template = new WxTemplate();
		template.setUrl(null);
		template.setTouser(toUser);
		template.setTopcolor(WxGlobal.getTemplateMsgColor_2());
		template.setTemplate_id(WxGlobal.getTemplateMsg_1());
		Map<String,WxTemplateData> wxTemplateDatas = new HashMap<String,WxTemplateData>();
		wxTemplateDatas.put("keyword1", keyword1);
		wxTemplateDatas.put("keyword2", keyword2);
		wxTemplateDatas.put("keyword3", keyword3);
		wxTemplateDatas.put("keyword4", keyword4);
		wxTemplateDatas.put("remark", remark);
		template.setData(wxTemplateDatas);
		//获取Token
    	WxAccessTokenManager wxAccessTokenManager = WxAccessTokenManager.getInstance();
		String accessToken = wxAccessTokenManager.getAccessToken();
		String url = String.format(WxGlobal.getTemplateMsgUrl(),accessToken);
		String jsonString = JSONObject.fromObject(template).toString();
		JSONObject jsonObject = WxUrlUtils.httpRequest(url,Global.POST_METHOD,jsonString); 
		logger.info("msg is " + jsonObject);
		int result = 0;
        if (null != jsonObject) {  
             if (0 != jsonObject.getInt("errcode")) {  
                 result = jsonObject.getInt("errcode");  
                 logger.error("错误 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));  
             }  
         }
        logger.info("模板消息发送结果："+result);
		logger.info("send msg end");
		return null;
	}
	
	//处理微信消息
	public String WxPostMsgProcess(HttpServletRequest request) throws Exception{
		
		String respMessage = null;
		
		//xml解析
		Map<String, String> requestMap = xmlToMap(request); 
		
		//解析公共消息部分
		// 发送方帐号（open_id）
        String fromUserName = requestMap.get("FromUserName");
        // 公众帐号
        String toUserName = requestMap.get("ToUserName");
        // 消息类型
        String msgType = requestMap.get("MsgType");
        // 消息内容
        String content = requestMap.get("Content");
        
        logger.info("FromUserName is:" + fromUserName + ", ToUserName is:" + toUserName + ", MsgType is:" + msgType);
		
        //文本消息
        if (msgType.equals(Global.WX_REQ_MESSAGE_TYPE_TEXT)) {
        	WechatTextMsg wechatMsg = new WechatTextMsg();
        	wechatMsg.setContent("欢迎关注锡职快递系统，请<a href=\""+WxGlobal.getUserClick(WxGlobal.getOauthRedirectUrlIndex(),false)+"\">绑定个人信息</a>，正确绑定之后，快递到达，您将第一时间收到通知");
        	wechatMsg.setToUserName(fromUserName);
        	wechatMsg.setFromUserName(toUserName);
        	wechatMsg.setCreateTime(new Date().getTime() + "");
        	wechatMsg.setMsgType(msgType);
        	respMessage = messageToXml(wechatMsg);
        	logger.info(respMessage);
        } 
        // 事件推送
        else if (msgType.equals(Global.WX_REQ_MESSAGE_TYPE_EVENT)) {
        	String eventType = requestMap.get("Event");// 事件类型
        	// 订阅
            if (eventType.equals(Global.WX_EVENT_TYPE_SUBSCRIBE)) {
            	WechatTextMsg wechatMsg = new WechatTextMsg();
            	wechatMsg.setContent("欢迎关注锡职快递系统，请<a href=\""+WxGlobal.getUserClick(WxGlobal.getOauthRedirectUrlIndex(),true)+"\">绑定个人信息</a>，正确绑定之后，快递到达，您将第一时间收到通知");
            	wechatMsg.setToUserName(fromUserName);
            	wechatMsg.setFromUserName(toUserName);
            	wechatMsg.setCreateTime(new Date().getTime() + "");
            	wechatMsg.setMsgType(Global.WX_RESP_MESSAGE_TYPE_TEXT);
                respMessage = messageToXml(wechatMsg);
            } 
            //取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
            else if (eventType.equals(Global.WX_EVENT_TYPE_UNSUBSCRIBE)) {
            	// 取消订阅
                
            } 
            // 自定义菜单点击事件
            else if (eventType.equals(Global.WX_EVENT_TYPE_CLICK)) {
                String eventKey = requestMap.get("EventKey");// 事件KEY值，与创建自定义菜单时指定的KEY值对应
                if (eventKey.equals("customer_telephone")) {
                	WechatTextMsg wechatMsg = new WechatTextMsg();
                	wechatMsg.setContent("0755-86671980");
                	wechatMsg.setToUserName(fromUserName);
                	wechatMsg.setFromUserName(toUserName);
                	wechatMsg.setCreateTime(new Date().getTime() + "");
                	wechatMsg.setMsgType(Global.WX_RESP_MESSAGE_TYPE_TEXT);
                    
                    respMessage = messageToXml(wechatMsg);
                }
            }
        }
        return respMessage;
	} 
	
	//排序
	public String sort(String timestamp, String nonce) {
		 String[] strArray = { WxGlobal.getCertificationToken(), timestamp, nonce };
		 Arrays.sort(strArray);
		 StringBuilder sbuilder = new StringBuilder();
		    for (String str : strArray) {
		        sbuilder.append(str);
		    }
		 return sbuilder.toString();
	}
	
	//sha1加密
	public String sha1(String decript) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			messageDigest.update(decript.getBytes());
			byte messageDigests[] = messageDigest.digest();
			// Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigests.length; i++) {
                String shaHex = Integer.toHexString(messageDigests[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	
	
	/** 
     * 文本消息对象转换成xml 
     *  
     * @param textMessage 文本消息对象 
     * @return xml 
     */ 
    public static String messageToXml(WechatTextMsg wechatMsg){
        XStream xstream = new XStream();
        xstream.alias("xml", wechatMsg.getClass());
        return xstream.toXML(wechatMsg);
    }
	
	 /**
	  * 解析微信发来的请求(XML)
	  * @param request
	  * @return
	  * @throws IOException
	  * @throws DocumentException
	  */
    public Map<String, String> xmlToMap(HttpServletRequest request) throws IOException, DocumentException{
         
    	// 将解析结果存储在HashMap中  
        Map<String, String> map = new HashMap<String, String>();  
        // 从request中取得输入流    
        InputStream inputStream = request.getInputStream();  
        // 读取输入流   
        SAXReader reader = new SAXReader();   
        Document document = reader.read(inputStream);    
        String requestXml = document.asXML();  
        String subXml = requestXml.split(">")[0] + ">";  
        requestXml = requestXml.substring(subXml.length());  
        // 得到xml根元素  
        Element root = document.getRootElement();  
        // 得到根元素的全部子节点  
        List<Element> elementList = root.elements();  
        // 遍历全部子节点  
        for (Element e : elementList) {  
            map.put(e.getName(), e.getText());  
            }  
        map.put("requestXml", requestXml);    
        // 释放资源    
        inputStream.close();    
        inputStream = null;    
        return map;  
    }

    
    
    
}
