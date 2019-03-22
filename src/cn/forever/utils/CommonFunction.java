package cn.forever.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import cn.forever.blog.model.Memory;
import cn.forever.blog.model.Menu;
import cn.forever.dao.ObjectDao;
import cn.forever.redis.CacheUtil;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
/**
 * 20170421 lwh
 * 公共方法 获取access_token 获取openId等等的
 */
public class CommonFunction {
	private static ServletContext application;
	public static void setApplication(ServletContext application){
		CommonFunction.application=application;
	}
	public static ServletContext getApplication(){
		return application;
	}
	/**
	 * 刷入内存，刷Memory表和菜单
	 * @param objectDao
	 */
	/*public static void flushApplication(ObjectDao objectDao){
		loadingMemoryToApplication(objectDao);
	}*/
	
	/**
	 * 将Memory中的记录装载进application中
	 */
/*	private static void loadingMemoryToApplication(ObjectDao objectDao){
		//获取memory表中所有有效的记录
		String hql = "select key_name from Memory m where m.visible='1' group by key_name";
		@SuppressWarnings("unchecked")
		List<Memory> list = objectDao.findByHqlAndArgs(hql, null);
		if(list!=null){
			for (int i = 0; i <list.size(); i++) {
				Object object = list.get(i);
				//更加key_name去或去记录按序号排序
				String key_name=(String)object;
				String hql_memory = "from Memory m where m.visible='1' and m.key_name=? order by m.sort";
				Object[] args_memory = new Object[]{key_name};
				@SuppressWarnings("unchecked")
				List<Memory> memorys = objectDao.findByHqlAndArgs(hql_memory, args_memory);
				if(memorys!=null){
					//将这个Key对应的值刷入内存
					application.setAttribute(key_name, memorys);
					System.out.println("key_name:"+key_name+"的值："+memorys.toString()+"刷入内存成功");
				}
			}
		}
	}*/
	public static Object openConnection(String link){
		if(link==null||"".equals(link)){
			System.out.println("链接不存在");
			return null;
		}
		HttpURLConnection conn=null;
		InputStream is=null;
		BufferedInputStream bis=null;
		try {
			URL url=new URL(link);
			conn=(HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
            conn.setRequestMethod("GET");
			is=conn.getInputStream();
			bis = new BufferedInputStream(is);
			byte[] bytes = new byte[1024];
			int len = -1;
			StringBuilder sb = new StringBuilder();
			while((len=bis.read(bytes))!=-1){
				sb.append(new String(bytes,0,len));
			}
			String json = sb.toString();
			JsonParser parser = new JsonParser();
			JsonObject object=(JsonObject)parser.parse(json);
            bis.close();
            is.close();
            conn.disconnect();
			System.out.println("链接返回的值："+object);
			return object;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据key_name去获取缓存中的值
	 * @param key_name
	 * @return
	 */
	public static List<Memory> getMemorys(String key_name){
		CacheUtil cacheUtil=(CacheUtil) SpringBeanManager.getBean("cacheUtil");
		List<Memory> memorys =cacheUtil.getList(key_name, Memory.class);
		if(memorys==null){
			String hql_memory = "from Memory m where m.visible='1' and m.key_name=? order by m.sort+0";
			Object[] args_memory = new Object[]{key_name};
			ObjectDao objectDao=(ObjectDao) SpringBeanManager.getBean("objectDao");
			memorys = objectDao.findByHqlAndArgs(hql_memory, args_memory);
			if(memorys!=null){
				cacheUtil.put(key_name, memorys);
			}
		}
		return memorys;
	}
	
	/**
	 * 根据valueA的类型来获取需要的信息，比如功能锁
	 * @param key_name
	 * @param constType
	 * @return
	 */
	public static Memory getMemory(String key_name,String constType){
		List<Memory> memorys=CommonFunction.getMemorys(key_name);
		if(memorys==null){
			System.out.println(key_name+"在 memory无值");
			return null;
		}
		for (Memory memory : memorys) {
			if(memory.getValueA().equals(constType)){
				return memory;
			}
		}
		return null;
	}
	
	public static String getUrl(){
		HttpServletRequest request=StrutsParamUtils.getRequest();
		String q = request.getQueryString();
		String url = request.getScheme()+"://"+ request.getServerName()+request.getRequestURI();
		if(q!=null){
			url=url+"?"+q;
		}
		return url;
	}
	
	public static  String getIpAddr() {
		HttpServletRequest request =StrutsParamUtils.getRequest();
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
           ip = request.getRemoteAddr();  
       }
       if (ip != null && ip.indexOf(",") != -1) {  
            ip = (ip.split(","))[0];  
       }
       System.out.println("用户的访问ip是："+ip);
       return ip;  
    }
}





