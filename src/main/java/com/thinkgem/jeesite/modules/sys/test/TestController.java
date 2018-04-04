package com.thinkgem.jeesite.modules.sys.test;

import java.io.File;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.BasePathUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.sys.service.WxService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;

/**
* @author wzy
* @version 创建时间：2018年3月16日 下午9:28:52
* @ClassName 类名称
* @Description 类描述
*/

@Controller
@RequestMapping(value = "test")
public class TestController extends BaseController {
	
	@Autowired
	private WxService wxService;
	
	
	@RequestMapping(value="/pickUpCodeRepeat",method=RequestMethod.GET)
	public String pickUpCodeRepeat(HttpServletRequest request, HttpServletResponse response,Model model) {
		try {
			PickUpCodeThread1 thread1 = new PickUpCodeThread1(wxService);
			PickUpCodeThread2 thread2 = new PickUpCodeThread2(wxService);
			Thread t1 = new Thread(thread1);
			Thread t2 = new Thread(thread2);
			t1.start();
			//t2.start();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "modules/wxp/test";
	}
	
	/**
	 * 测试使用(开发可删除)
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/getUpImage",method=RequestMethod.GET)
	public String getUpImage(HttpServletRequest request, HttpServletResponse response,Model model) {
		return "modules/wxp/test";
	}
	
	/**
	 * 个人身份图片
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/upIdCard",method=RequestMethod.POST)
	public String upIdCard(HttpServletRequest request, HttpServletResponse response,Model model) {
		try {
			request.setCharacterEncoding("UTF-8");
			//获取用户的身份证ID
			String idCard = request.getParameter("wmwId"); 
			String dirParam = headPhotoPath();
			 //上传
      	    File fileName = new File(dirParam,idCard + ".jpeg");
      	    System.out.println(fileName.getAbsolutePath());
      	    logger.info("fileName.getAbsolutePath():"+fileName.getAbsolutePath());
      	    logger.info("fileName.getAbsolutePath():"+fileName.getPath());
      	    String filePath = fileName.getPath();
      	    CommonsMultipartResolver multipartResolver=new CommonsMultipartResolver(request.getSession().getServletContext());
			if(multipartResolver.isMultipart(request)){
				  //将request变成多部分request
                MultipartHttpServletRequest multiRequest=(MultipartHttpServletRequest)request;
                //获取multiRequest 中所有的文件名
                Iterator<String> iter=multiRequest.getFileNames();
                while(iter.hasNext()){
               	 MultipartFile file=multiRequest.getFile(iter.next().toString());
                 if(file!=null){
                   	   //上传
                       file.transferTo(fileName);
                 }
               }
			}
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
		    String reqUrl = url + request.getContextPath()  + Global.USER_ID_CARD + "/" + paths[paths.length-1];
		    logger.info(reqUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
        return ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath(savePath);
    }
	

}
