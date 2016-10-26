package wx.util;


import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import wx.common.MessageType;


/**
 * 消息处理工具类
 * @author Wangzs
 *
 */
public class MessageHandlerUtil {
	
    /**
     * 解析微信发来的请求（XML）
     * @param request
     * @return map
     * @throws Exception
     */
    public static Map<String,String> parseXml(HttpServletRequest request) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String,String> map = new HashMap();
        // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        System.out.println("获取输入流");
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();

        // 遍历所有子节点
        for (Element e : elementList) {
            System.out.println(e.getName() + "|" + e.getText());
            map.put(e.getName(), e.getText());
        }

        // 释放资源
        inputStream.close();
        inputStream = null;
        return map;
    }

/**
 * 
 * @param map
 * @return
 */
    public static String buildResponseMessage(Map map){
    	//响应消息
    	String responseMessage = "";
    	//得到消息类型
    	String msgType = map.get("MsgType").toString();
    	System.out.println("MsgType:" + msgType);
    	//消息类型
    	MessageType messageEnumType = MessageType.valueOf(MessageType.class, msgType.toUpperCase());
    	switch (messageEnumType) {
    	case TEXT:
    		//处理文本消息
    		responseMessage = handleTextMessage(map);
    		break;
    	case IMAGE:
    		//处理图片消息
            responseMessage = handleImageMessage(map);
    		break;

    	case VOICE:
    		//处理语音消息
            responseMessage = handleVoiceMessage(map);
    		break;

    	case VIDEO:
    		//处理视频消息
            responseMessage = handleVideoMessage(map);
    		break;

    	case SHORTVIDEO:
    		//处理小视频消息
            responseMessage = handleSmallVideoMessage(map);
    		break;

    	case LOCATION:
    		//处理位置消息
            responseMessage = handleLocationMessage(map);
    		break;

    	case LINK:
    		//处理链接消息
            responseMessage = handleLinkMessage(map);
    		break;

    	case EVENT:
    		//处理事件消息,用户在关注与取消关注公众号时，微信会向我们的公众号服务器发送事件消息,开发者接收到事件消息后就可以给用户下发欢迎消息
    		responseMessage = handleEventMessage(map);
    	default:
    		responseMessage = buildWelcomeTextMessage(map);
    		break;
    	}
    	//返回响应消息
    	return responseMessage;
    }
    
    /**
     * 生成消息创建时间 （整型）
     * @return 消息创建时间
     */
    private static String getMessageCreateTime() {
        Date dt = new Date();// 如果不需要格式,可直接用dt,dt就是当前系统时间
        DateFormat df = new SimpleDateFormat("yyyyMMddhhmm");// 设置显示格式
        String nowTime = df.format(dt);
        long dd = (long) 0;
        try {
            dd = df.parse(nowTime).getTime();
        } catch (Exception e) {

        }
        return String.valueOf(dd);
    }
    
    /**
     * 构建提示消息
     * @param map 封装了解析结果的Map
     * @return responseMessageXml
     */
    private static String buildWelcomeTextMessage(Map<String, String> map) {
        String responseMessageXml;
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        responseMessageXml = String
                .format(
                        "<xml>" +
                                "<ToUserName><![CDATA[%s]]></ToUserName>" +
                                "<FromUserName><![CDATA[%s]]></FromUserName>" +
                                "<CreateTime>%s</CreateTime>" +
                                "<MsgType><![CDATA[text]]></MsgType>" +
                                "<Content><![CDATA[%s]]></Content>" +
                                "</xml>",
                        fromUserName, toUserName, getMessageCreateTime(),
                        "感谢您关注我的个人公众号，请回复如下关键词来使用公众号提供的服务：\n文本\n图片\n语音\n视频\n音乐\n图文");
        return responseMessageXml;
    }
    
    /**
     * 接收到文本消息后处理
     * @param map 封装了解析结果的Map
     * @return
     */
    public static String handleTextMessage(Map<String, String> map){
    	//响应消息
        String responseMessage;
        //消息的内容
    	String content = map.get("Content");
    	switch (content) {
		case "文本":
			String msgText = "哈哈哈,感兴趣点github\n" +
					"<a href=\"https://github.com/Wzhenshuai\">Freer的github</a>";
			responseMessage = buildTextMessage(map, msgText);
			break;

		case "图片":
			//通过素材管理接口上传图片时得到的media_id
			String imgMediaId = "V1b0e5oZLRPFbXAbvji3krHvkHBAiL5g1c6ryoK1wEL1m0c9xoggPkNR0GWlxRyh";
			responseMessage = buildImageMessage(map, imgMediaId);
			break;
		case "语音":
			//通过素材管理接口上传语音文件时得到的media_id
			String voiceMediaId = "Mfa-_f_OhCdkUvYBkvjj36RrfzZyqn-st2vZvXPm3OmfN8H2DPBCIa3_NNeMtX7k";
			responseMessage = buildVoiceMessage(map,voiceMediaId);
			break;
		case "图文":
			responseMessage = buildNewsMessage(map);
			break;
			
		case "音乐":
            Music music = new Music();
            music.title = "张信哲 - 信仰";
            music.description = "情歌精选";
            music.musicUrl = "http://music.163.com/#/song?id=25643328";
            music.hqMusicUrl = "http://music.163.com/#/song?id=25643328";
            responseMessage = buildMusicMessage(map, music);
            break;
        case "视频":
            Video video = new Video();
            video.mediaId = "WQDHMj0kkC6SDbQjYLebOIWWbehywTMktNU6HfupppkdObAE8VW83Gi5wrTfjLiI";
            video.title = "小苹果";
            video.description = "小苹果搞笑视频";
            responseMessage = buildVideoMessage(map, video);
            break;
		default:
			responseMessage = buildWelcomeTextMessage(map);
			break;
		}
    	return responseMessage;
    }
    
    /**
     * 构造文本消息
     * @param map 封装了解析结果的Map
     * @param content 文本消息内容
     * @return 文本消息XML字符串
     */
    public static String buildTextMessage(Map<String, String> map, String content){
    	//发送方帐号
    	String fromUserName = map.get("FromUserName");
    	// 开发者微信号
    	String toUserName = map.get("ToUserName");
    	
    	/*
    	 * 文本消息XML数据格式
    	 * <xml>
    	<ToUserName><![CDATA[toUser]]></ToUserName>
    	<FromUserName><![CDATA[fromUser]]></FromUserName>
    	<CreateTime>12345678</CreateTime>
    	<MsgType><![CDATA[text]]></MsgType>
    	<Content><![CDATA[你好]]></Content>
    	</xml>
    	*/
    	return String.format(
    			"<xml>" + 
    					"<ToUserName><![CDATA[%s]]></ToUserName>" + 
    					"<FromUserName><![CDATA[%s]]></FromUserName>" + 
    					"<CreateTime>%s</CreateTime>" +
    					"<MsgType><![CDATA[text]]></MsgType>" + 
    					"<Content><![CDATA[%s]]></Content>" + 
    					"</xml>",
    			fromUserName, toUserName, getMessageCreateTime(), content);
    	
    }
    

    /**
     * 构造图片消息
     * @param map 封装了解析结果的Map
     * @param mediaId 通过素材管理接口上传多媒体文件得到的id
     * @return 图片消息XML字符串
     */
    public static String buildImageMessage(Map<String, String> map, String mediaId){
        //发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        
        /*
         * 
         * <xml>
        <ToUserName><![CDATA[toUser]]></ToUserName>
        <FromUserName><![CDATA[fromUser]]></FromUserName>
        <CreateTime>12345678</CreateTime>
        <MsgType><![CDATA[image]]></MsgType>
        <Image>
        <MediaId><![CDATA[media_id]]></MediaId>
        </Image>
        </xml>*/
    	return String.format(
    			"<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[image]]></MsgType>" +
                        "<Image>" +
                        "   <MediaId><![CDATA[%s]]></MediaId>" +
                        "</Image>" +
                        "</xml>",
                fromUserName, toUserName, getMessageCreateTime(), mediaId);
    }
    
    /**
     * 构造语音消息
     * @param map 封装了解析结果的Map
     * @param mediaId 通过素材管理接口上传多媒体文件得到的id
     * @return 语音消息XML字符串
     */
    public static String buildVoiceMessage(Map<String, String> map, String mediaId){
        //发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        
        /*
         * 回复语音消息XML数据格式
         * <xml>
        <ToUserName><![CDATA[toUser]]></ToUserName>
        <FromUserName><![CDATA[fromUser]]></FromUserName>
        <CreateTime>12345678</CreateTime>
        <MsgType><![CDATA[voice]]></MsgType>
        <Voice>
        <MediaId><![CDATA[media_id]]></MediaId>
        </Voice>
        </xml>*/
        
    	return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[voice]]></MsgType>" +
                        "<Voice>" +
                        "   <MediaId><![CDATA[%s]]></MediaId>" +
                        "</Voice>" +
                        "</xml>",
                fromUserName, toUserName, getMessageCreateTime(), mediaId);
    }

    /**
     * 构造视频消息
     * @param map 封装了解析结果的Map
     * @param video 封装好的视频消息内容
     * @return 视频消息XML字符串
     */
    
    private static String buildVideoMessage(Map<String, String> map, Video video) {
        //发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        
        /*
         * 回复视频消息XML数据格式
         * <xml>
        <ToUserName><![CDATA[toUser]]></ToUserName>
        <FromUserName><![CDATA[fromUser]]></FromUserName>
        <CreateTime>12345678</CreateTime>
        <MsgType><![CDATA[video]]></MsgType>
        <Video>
        <MediaId><![CDATA[media_id]]></MediaId>
        <Title><![CDATA[title]]></Title>
        <Description><![CDATA[description]]></Description>
        </Video> 
        </xml>*/
        
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[video]]></MsgType>" +
                        "<Video>" +
                        "   <MediaId><![CDATA[%s]]></MediaId>" +
                        "   <Title><![CDATA[%s]]></Title>" +
                        "   <Description><![CDATA[%s]]></Description>" +
                        "</Video>" +
                        "</xml>",
                fromUserName, toUserName, getMessageCreateTime(), video.mediaId, video.title, video.description);
    }
    
    /**
     * 构造图文消息
     * @param map 封装了解析结果的Map
     * @return 图文消息XML字符串
     */
    public static String buildNewsMessage(Map<String, String> map){
    	//发送方帐号
    	String fromUserName = map.get("FromUserName");
    	// 开发者微信号
        String toUserName = map.get("ToUserName");
        NewsItem item = new NewsItem();
        item.Title = "微信开发环境搭建";
        item.Description = "工欲善其事，必先利其器。要做微信公众号开发，那么要先准备好两样必不可少的东西：\n" +
                "\n" +
                "　　1、要有一个用来测试的公众号。\n" +
                "\n" +
                "　　2、用来调式代码的开发环境";
        item.PicUrl = "http://bpic.588ku.com/element_origin_min_pic/16/08/17/2257b474759f144.jpg";
        item.Url = "https://github.com/Wzhenshuai";
        String itemContent1 = buildSingleItem(item);
        
        NewsItem item2 = new NewsItem();
        item2.Title = "微信开发学习总结（二）——微信开发入门";
        item2.Description = "微信服务器就相当于一个转发服务器，终端（手机、Pad等）发起请求至微信服务器，微信服务器然后将请求转发给我们的应用服务器。应用服务器处理完毕后，将响应数据回发给微信服务器，微信服务器再将具体响应信息回复到微信App终端。";
        item2.PicUrl = "";
        item2.Url = "https://github.com/Wzhenshuai";
        String itemContent2 = buildSingleItem(item2);
        /*
         * 
         * <xml>
        <ToUserName><![CDATA[toUser]]></ToUserName>
        <FromUserName><![CDATA[fromUser]]></FromUserName>
        <CreateTime>12345678</CreateTime>
        <MsgType><![CDATA[news]]></MsgType>
        <ArticleCount>2</ArticleCount>	图文消息个数
        <Articles>多条图文消息信息，默认第一个item为大图,注意
        <item>
        <Title><![CDATA[title1]]></Title> 图文消息标题
        <Description><![CDATA[description1]]></Description>图文消息描述
        <PicUrl><![CDATA[picurl]]></PicUrl>图片链接
        <Url><![CDATA[url]]></Url>点击图文消息跳转链接
        </item>
        <item>
        <Title><![CDATA[title]]></Title>	
        <Description><![CDATA[description]]></Description>	
        <PicUrl><![CDATA[picurl]]></PicUrl>	
        <Url><![CDATA[url]]></Url>	
        </item>
        </Articles>
        </xml> */
        String content = String.format(
        		"<xml>\n" +
                "<ToUserName><![CDATA[%s]]></ToUserName>\n" +
                "<FromUserName><![CDATA[%s]]></FromUserName>\n" +
                "<CreateTime>%s</CreateTime>\n" +
                "<MsgType><![CDATA[news]]></MsgType>\n" +
                "<ArticleCount>%s</ArticleCount>\n" +
                "<Articles>\n" + "%s" +
                "</Articles>\n" +
                "</xml> ",
                fromUserName, toUserName, getMessageCreateTime(), 2, itemContent1 + itemContent2);
        return content;
    }
    /**
     * 生成图文消息的一条记录
     *
     * @param item
     * @return
     */
    private static String buildSingleItem(NewsItem item) {
        String itemContent = String.format("<item>\n" +
                "<Title><![CDATA[%s]]></Title> \n" +
                "<Description><![CDATA[%s]]></Description>\n" +
                "<PicUrl><![CDATA[%s]]></PicUrl>\n" +
                "<Url><![CDATA[%s]]></Url>\n" +
                "</item>", 
                item.Title, item.Description, item.PicUrl, item.Url);
        return itemContent;
    }

    /**
     * 构造音乐消息
     * @param map 封装了解析结果的Map
     * @param music 封装好的音乐消息内容
     * @return 音乐消息XML字符串
     */
    public static String buildMusicMessage(Map<String, String> map, Music music){
        //发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
    	
    	/*
    	 * 音乐消息XML数据格式
    	 * <xml>
    	<ToUserName><![CDATA[toUser]]></ToUserName>
    	<FromUserName><![CDATA[fromUser]]></FromUserName>
    	<CreateTime>12345678</CreateTime>
    	<MsgType><![CDATA[music]]></MsgType>
    	<Music>
    	<Title><![CDATA[TITLE]]></Title>
    	<Description><![CDATA[DESCRIPTION]]></Description>
    	<MusicUrl><![CDATA[MUSIC_Url]]></MusicUrl>
    	<HQMusicUrl><![CDATA[HQ_MUSIC_Url]]></HQMusicUrl>高质量音乐链接
    	<ThumbMediaId><![CDATA[media_id]]></ThumbMediaId>缩略图的媒体id，
    	</Music>
    	</xml>*/
        
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[music]]></MsgType>" +
                        "<Music>" +
                        "   <Title><![CDATA[%s]]></Title>" +
                        "   <Description><![CDATA[%s]]></Description>" +
                        "   <MusicUrl><![CDATA[%s]]></MusicUrl>" +
                        "   <HQMusicUrl><![CDATA[%s]]></HQMusicUrl>" +
                        "</Music>" +
                        "</xml>",
                fromUserName, toUserName, getMessageCreateTime(), music.title, music.description, music.musicUrl, music.hqMusicUrl);
    }

    /**
     * 处理接收到图片消息
     *
     * @param map
     * @return
     */
    private static String handleImageMessage(Map<String, String> map) {
        String picUrl = map.get("PicUrl");
        String mediaId = map.get("MediaId");
        System.out.print("picUrl:" + picUrl);
        System.out.print("mediaId:" + mediaId);
        String result = String.format("已收到您发来的图片，图片Url为：%s\n图片素材Id为：%s", picUrl, mediaId);
        return buildTextMessage(map, result);
    }
    
    /**
     * 处理接收到语音消息
     * @param map
     * @return
     */
    private static String handleVoiceMessage(Map<String, String> map) {
        String format = map.get("Format");
        String mediaId = map.get("MediaId");
        System.out.print("format:" + format);
        System.out.print("mediaId:" + mediaId);
        String result = String.format("已收到您发来的语音，语音格式为：%s\n语音素材Id为：%s", format, mediaId);
        return buildTextMessage(map, result);
    }
    
    /**
     * 处理接收到的视频消息
     * @param map
     * @return
     */
    private static String handleVideoMessage(Map<String, String> map) {
        String thumbMediaId = map.get("ThumbMediaId");
        String mediaId = map.get("MediaId");
        System.out.print("thumbMediaId:" + thumbMediaId);
        System.out.print("mediaId:" + mediaId);
        String result = String.format("已收到您发来的视频，视频中的素材ID为：%s\n视频Id为：%s", thumbMediaId, mediaId);
        return buildTextMessage(map, result);
    }
    
    /**
     * 处理接收到的小视频消息
     * @param map
     * @return
     */
    private static String handleSmallVideoMessage(Map<String, String> map) {
        String thumbMediaId = map.get("ThumbMediaId");
        String mediaId = map.get("MediaId");
        System.out.print("thumbMediaId:" + thumbMediaId);
        System.out.print("mediaId:" + mediaId);
        String result = String.format("已收到您发来的小视频，小视频中素材ID为：%s,\n小视频Id为：%s", thumbMediaId, mediaId);
        return buildTextMessage(map, result);
    }
    
    /**
     * 处理接收到的地理位置消息
     * @param map
     * @return
     */
    private static String handleLocationMessage(Map<String, String> map) {
        String latitude = map.get("Location_X");  //纬度
        String longitude = map.get("Location_Y");  //经度
        String label = map.get("Label");  //地理位置精度
        String result = String.format("纬度：%s\n经度：%s\n地理位置：%s", latitude, longitude, label);
        return buildTextMessage(map, result);
    }
    
    /**
     * 处理接收到的链接消息
     * @param map
     * @return
     */
    private static String handleLinkMessage(Map<String, String> map) {
        String title = map.get("Title");
        String description = map.get("Description");
        String url = map.get("Url");
        String result = String.format("已收到您发来的链接，链接标题为：%s,\n描述为：%s\n,链接地址为：%s", title, description, url);
        return buildTextMessage(map, result);
    }
    
    /**
     * 处理消息Message
     * @param map 封装了解析结果的Map
     * @return
     */
    private static String handleEventMessage(Map<String, String> map) {
        String responseMessage = buildWelcomeTextMessage(map);
        return responseMessage;
    }

}
/**
 * 图文消息
 */
class NewsItem {
    public String Title;

    public String Description;

    public String PicUrl;

    public String Url;
}

/**
 * 音乐消息
 */
class Music {
    public String title;
    public String description;
    public String musicUrl;
    public String hqMusicUrl;
}

/**
 * 视频消息
 */
class Video {
    public String title;
    public String description;
    public String mediaId;
}
