package wx;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wx.util.MessageHandlerUtil;


/**  * Created by xdp on 2016/1/25.
  * ʹ��@WebServletע������WxServlet,urlPatterns����ָ����WxServlet�ķ���·��
  */
@WebServlet(urlPatterns="/WxServlet")
public class WxServlet extends HttpServlet{

	/**
	       * Token���ɿ����߿���������д����������ǩ������Token��ͽӿ�URL�а�����Token���бȶԣ��Ӷ���֤��ȫ�ԣ�
	       * ���������ҽ�Token����Ϊgacl
	       */
	private final String TOKEN = "wangfree";
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO ���ա�������Ӧ��΢�ŷ�����ת�����û����͸������ʺŵ���Ϣ
        // ��������Ӧ�ı��������ΪUTF-8����ֹ�������룩
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        System.out.println("�������");
        String result = "";
        try {
            Map<String,String> map = MessageHandlerUtil.parseXml(request);
            System.out.println("��ʼ������Ϣ");
            result = MessageHandlerUtil.buildXml(map);
            System.out.println(result);
            if(result.equals("")){
                result = "δ��ȷ��Ӧ";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("�����쳣��"+ e.getMessage());
        }
        response.getWriter().println(result);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("��ʼУ��ǩ��");
		
		/**
		 * ����΢�ŷ�������������ʱ���ݹ�����4������
		 */
		
		String signature = request.getParameter("signature");//΢�ż���ǩ��signature����˿�������д��token�����������е�timestamp������nonce������
		String timestamp = request.getParameter("timestamp");//ʱ���
		String nonce = request.getParameter("nonce");//�����
		String echostr = request.getParameter("echostr");//����ַ���
		
		//����
		String sortString = sort(TOKEN, timestamp, nonce);
		//����
		String mySignature = sha1(sortString);
		//У��ǩ��
		if (mySignature != null && mySignature != "" && mySignature.equals(signature)) {
			System.out.println("ǩ��У��ͨ����");
			//�������ɹ����echostr��΢�ŷ��������յ���������Ż�ȷ�ϼ�����ɡ�
			//response.getWriter().println(echostr);
			response.getWriter().write(echostr);
		}else {
			System.out.println("ǩ��У��ʧ��.");
		}
	}
	
	     /**
	       * ���򷽷�
	       *
	       * @param token
	       * @param timestamp
	       * @param nonce
	       * @return
	       */
	
	public String sort(String token, String timestamp, String nonce) {
		String[] strArray = {token, timestamp, nonce};
		Arrays.sort(strArray);
		StringBuilder sb = new StringBuilder();
		 for (String str : strArray) {
			 sb.append(str);
		 }
		 
		 return sb.toString();
	}
	
	      /**
	       * ���ַ�������sha1����
	       *
	       * @param str ��Ҫ���ܵ��ַ���
	       * @return ���ܺ������
	       */
	public String sha1(String str) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(str.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// �ֽ�����ת��Ϊ ʮ������ ��
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
	  
}
