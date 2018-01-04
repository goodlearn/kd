package com.thinkgem.jeesite.modules.sys.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.config.WxGlobal;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.utils.TimeUtils;
import com.thinkgem.jeesite.common.utils.WxUrlUtils;
import com.thinkgem.jeesite.modules.sys.dao.SysWxInfoDao;
import com.thinkgem.jeesite.modules.sys.dao.SysWxUserDao;
import com.thinkgem.jeesite.modules.sys.dao.UserDao;
import com.thinkgem.jeesite.modules.sys.entity.SysWxInfo;
import com.thinkgem.jeesite.modules.sys.entity.SysWxUser;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.entity.wx.WechatMsg;
import com.thinkgem.jeesite.modules.sys.entity.wx.WechatTextMsg;
import com.thinkgem.jeesite.modules.sys.entity.wx.WxTemplate;
import com.thinkgem.jeesite.modules.sys.entity.wx.WxTemplateData;
import com.thinkgem.jeesite.modules.sys.manager.WxAccessTokenManager;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thoughtworks.xstream.XStream;

import net.sf.json.JSONObject;

/**
 * 微信信息处理
 * @author wzy
 *
 */
@Service
public class WxService extends BaseService implements InitializingBean {
	
	private static final String defaultLoginNameForQuery = "wxuser";
	
	@Autowired
	private SysWxUserDao sysWxUserDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SysWxInfoDao sysWxInfoDao;

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
	//获取access_token和openId
	private Map<String,String> getOpenIdInfo(String code) {
		Map<String,String> ret = new HashMap<String,String>();
		String url = String.format(WxGlobal.USERINFO_TOKEN_URL,WxGlobal.APPID,WxGlobal.APPSECREST,code);
		logger.info("request accessToken from url: {}", url);
		JSONObject jsonObject = WxUrlUtils.httpRequest(url, Global.GET_METHOD, null);
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
	
	 /**
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
			 User paramUser = new User();
				paramUser.setLoginName(defaultLoginNameForQuery);
				User user = userDao.getByLoginName(paramUser);
				if(null == user) {
					logger.info("No wxuser");
					return null;//没有操作用户
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
	
	//保存个人用户信息 需要将微信关联
	@Transactional(readOnly = false)
	public void saveWxUserInfo(SysWxUser sysWxUser,String openId) {
		//获取操作人信息 默认为微信用户
		User paramUser = new User();
		paramUser.setLoginName(defaultLoginNameForQuery);
		User user = userDao.getByLoginName(paramUser);
		if(null == user) {
			logger.info("No wxuser");
			return;
		}
		
		String idCard = sysWxUser.getIdCard();
		String phone = sysWxUser.getPhone();
		if(null != idCard) {
			SysWxUser temp = sysWxUserDao.findByIdCard(idCard);//已经存在了 那么要求手机号和身份证号和之前匹配才可以关联
			if(null == temp) {
				//不存在 直接保存
				sysWxUser.setId(IdGen.uuid());
				sysWxUser.setCreateBy(user);
				sysWxUser.setCreateDate(new Date());
				sysWxUser.setUpdateBy(user);
				sysWxUser.setUpdateDate(new Date());
				sysWxUserDao.insert(sysWxUser);
				//更新关联
				addOrUpdateWxInfo(idCard,openId,user);
			}else {
				//已经存在了 需要进行关联判断 验证手机号
				String tempPhone = temp.getPhone();
				if(null == phone) {
					//手机号为空
					logger.info("idCard exist phone is null");
					return;
				}else {
					if(!phone.equals(tempPhone)) {
						//手机号不同
						logger.info("idCard exist and phone is diff");
						return;
					}else {
						logger.info("idCard exist and phone is same");
						//更新关联
						addOrUpdateWxInfo(idCard,openId,user);
						return;
					}
				}
			}
		}
	}
	
	//修改个人信息，需要将微信的数据一同更新
	@Transactional(readOnly = false)
	public void modifyWxUserInfo(SysWxUser sysWxUser,String openId) {
		//获取操作人信息 默认为微信用户
		User paramUser = new User();
		paramUser.setLoginName(defaultLoginNameForQuery);
		User user = userDao.getByLoginName(paramUser);
		if(null == user) {
			logger.info("No wxuser");
			return;
		}
		
		SysWxUser queryResult = sysWxUserDao.get(sysWxUser);
		if(null == queryResult) {
			return;
		}
		
		String idCard = sysWxUser.getIdCard();
		if(null != idCard) {
			queryResult.setIdCard(idCard);
		}
		
		String phone = sysWxUser.getPhone();
		if(null != phone) {
			queryResult.setPhone(phone);
		}
		
		String name = sysWxUser.getName();
		if(null != name) {
			queryResult.setName(name);
		}
		queryResult.setCreateBy(user);
		queryResult.setCreateDate(new Date());
		queryResult.setUpdateBy(user);
		queryResult.setUpdateDate(new Date());
		sysWxUserDao.update(queryResult);
		
		addOrUpdateWxInfo(idCard,openId,user);//更新微信信息
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
				sysWxInfoDao.update(querySysWxInfo);
			}
		}
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
		first.setColor(WxGlobal.TEMPLATE_Msg_COLOR_1);
		first.setValue("您收到一个订单");
		WxTemplateData keyword1 = new WxTemplateData();
		keyword1.setColor(WxGlobal.TEMPLATE_Msg_COLOR_1);
		String state = DictUtils.getDictLabel("0","expressState","已入库");
		keyword1.setValue(state);
		WxTemplateData keyword2 = new WxTemplateData();
		keyword2.setColor(WxGlobal.TEMPLATE_Msg_COLOR_1);
		keyword2.setValue(DateUtils.getDateTime());
		WxTemplateData keyword3 = new WxTemplateData();
		keyword3.setColor(WxGlobal.TEMPLATE_Msg_COLOR_1);
		keyword3.setValue(money);
		WxTemplateData keyword4 = new WxTemplateData();
		keyword4.setColor(WxGlobal.TEMPLATE_Msg_COLOR_1);
		keyword4.setValue(username);
		WxTemplateData remark = new WxTemplateData();
		String content="你的快递已到，请携带身份证前往易度空间领取";
		remark.setColor(WxGlobal.TEMPLATE_Msg_COLOR_1);
		remark.setValue(content);
		
		WxTemplate template = new WxTemplate();
		template.setUrl("https://www.toutiao.com/i6505228910123287054/");
		template.setTouser(toUser);
		template.setTopcolor(WxGlobal.TOP_Msg_COLOR_1);
		template.setTemplate_id(WxGlobal.TEMPLATE_Msg_1);
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
		String url = String.format(WxGlobal.TMPLATE_MSG_URL,accessToken);
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
        	String url = String.format(WxGlobal.OAUTHREQUESTURL,WxGlobal.APPID,WxGlobal.OAUTHREDIRECTURL);
    		logger.info("request code from url: {}", url);
        	wechatMsg.setContent(WxGlobal.getUserClick());
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
            	wechatMsg.setContent("欢迎关注，xxx");
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
		 String[] strArray = { WxGlobal.WX_TOKEN, timestamp, nonce };
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
