package wx.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
  * 访问网络用到的工具类
  */
public class NetWorkHelper {

	     /**
	      * 发起Https请求
	      * @param reqUrl 请求的URL地址
	      * @param requestMethod
	      * @return 响应后的字符串
	      */
	public String getHttpsResponse(String reqUrl, String requestMethod) {
		URL url;
		InputStream is;
		String resultData = "";

		try {
			url = new URL(reqUrl);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			TrustManager[] tm = { xtm };

			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, tm, null);

			con.setSSLSocketFactory(ctx.getSocketFactory());
			con.setHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			});
			con.setDoInput(true); // 允许输入流，即允许下载

			// 在android中必须将此项设置为false
			con.setDoOutput(false); // 允许输出流，即允许上传

			con.setUseCaches(false); // 不使用缓冲

			if (null != requestMethod && !requestMethod.equals("")) {
				con.setRequestMethod(requestMethod); // 使用指定的方式
			} else {
				con.setRequestMethod("GET"); // 使用get请求
			}
			is = con.getInputStream(); // 获取输入流，此时才真正建立链接
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader bufferReader = new BufferedReader(isr);
			String inputLine;
			while ((inputLine = bufferReader.readLine()) != null) {
				resultData += inputLine + "\n";
			}
			System.out.println(resultData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultData;
	}
	X509TrustManager xtm = new X509TrustManager() {
		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		@Override
		public void checkClientTrusted(
				java.security.cert.X509Certificate[] chain, String authType)
				throws CertificateException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void checkServerTrusted(
				java.security.cert.X509Certificate[] chain, String authType)
				throws CertificateException {
			// TODO Auto-generated method stub
			
		}
		
	};
	public static void main(String[] args) {
		System.out.println("heheh----");
	}
}
