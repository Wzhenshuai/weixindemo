package wx.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.annotation.WebInitParam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import wx.common.AccessTokenInfo;
import wx.entry.AccessToken;
import wx.util.NetWorkHelper;


/**
  * 用于获取accessToken的Servlet
  * Created by xdp on 2016/1/25.
  */
 @WebServlet(
         name = "AccessTokenServlet",
         urlPatterns = {"/AccessTokenServlet"},
         loadOnStartup = 1,
         initParams = {
                 @WebInitParam(name = "appId", value = "wx5eee6eb1eb15c3c2"),
                 @WebInitParam(name = "appSecret", value = "a75d2132cb3ac9796dba42dde7d1834a")
         })
public class AccessTokenServlet extends HttpServlet{

	 /**
	 * 
	 */
	private static final long serialVersionUID = -1559187297937355099L;

	@Override
	public void init() throws ServletException {
		 
		System.out.println("启动WebServlet");
		super.init();
		
		final String appId = getInitParameter("appId");
		final String appSecret = getInitParameter("appSecret");
		
		//开启一个新的线程
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						//获取accessToken
						AccessTokenInfo.accessToken = getAccessToken(appId, appSecret);
						//获取成功
						if (AccessTokenInfo.accessToken != null) {
							//获取到access_token 休眠7000秒,大约2个小时左右
							Thread.sleep(7000 * 1000);
							//Thread.sleep(10 * 1000);//10秒钟获取一次
						} else {
							//获取失败
							Thread.sleep(1000 * 3); //获取的access_token为空 休眠3秒
						}
					}catch(Exception e){
						System.out.println("发生异常：" + e.getMessage());
						e.printStackTrace();
						try {
							Thread.sleep(1000 * 10); //发生异常休眠1秒
						} catch (Exception e1) {
							// TODO: handle exception
						}
					}
				}
				
			}
		}).start();
	}
	 
	/**
	 * 
	 * 获取access_token
	 * @return AccessToken
	 */
	 private AccessToken getAccessToken(String appId, String appSecret) {
		 NetWorkHelper netHelper = new NetWorkHelper();
		 
		 /**
		  * 接口地址为https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET，其中grant_type固定写为client_credential即可。
		  */
		 String Url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s", appId, appSecret);
		 
		//此请求为https的get请求，返回的数据格式为{"access_token":"ACCESS_TOKEN","expires_in":7200}
		 
		 String result = netHelper.getHttpsResponse(Url, "");
		 System.out.println("获取到的access_token="+result);
		 
		//使用FastJson将Json字符串解析成Json对象
		 JSONObject json = JSON.parseObject(result);
		 AccessToken token = new AccessToken();
		 token.setAccessToken(json.getString("access_token"));
		 token.setExpiresin(json.getInteger("expires_in"));
		 return token;
	 }

}
