package cn.forever.utils;
/**
 * 常量Memory中valueA是功能锁的类型 valueB是是否开始0未开始1开始
 * 下面的常量都是valueA,key_name:
 * FUNCTION_LOCK:是功能锁
 * @author forever
 *
 */
public class Const {
	public static final String REDIS_STATUS = "00";//REDIS的开启状态 01开启
	public static final String PAGE_NUM = "10";//获取的主题数目
	public static final String HOT_NUM = "10";//默认获取的热门主题数
	public static final String MENUS_KEY="MENUS";//menus在redis中的key
	public static final String QUICK_WEB="QUICK_WEB";//跨速通道在redis中的key
}
