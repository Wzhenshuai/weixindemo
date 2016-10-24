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
import javax.security.cert.X509Certificate;


/**
  * ���������õ��Ĺ�����
  */
public class NetWorkHelper {

	     /**
	      * ����Https����
	      * @param reqUrl �����URL��ַ
	      * @param requestMethod
	      * @return ��Ӧ����ַ���
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
			con.setDoInput(true); // ����������������������

			// ��android�б��뽫��������Ϊfalse
			con.setDoOutput(false); // ������������������ϴ�

			con.setUseCaches(false); // ��ʹ�û���

			if (null != requestMethod && !requestMethod.equals("")) {
				con.setRequestMethod(requestMethod); // ʹ��ָ���ķ�ʽ
			} else {
				con.setRequestMethod("GET"); // ʹ��get����
			}
			is = con.getInputStream(); // ��ȡ����������ʱ��������������
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
}
