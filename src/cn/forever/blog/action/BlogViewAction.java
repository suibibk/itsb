package cn.forever.blog.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import cn.forever.blog.model.Memory;
import cn.forever.blog.model.Menu;
import cn.forever.blog.model.PageValue;
import cn.forever.blog.model.RTMsg;
import cn.forever.blog.model.Reply;
import cn.forever.blog.model.Topic;
import cn.forever.blog.model.User;
import cn.forever.blog.model.Visit;
import cn.forever.blog.service.BlogViewService;
import cn.forever.dao.ObjectDao;
import cn.forever.redis.CacheUtil;
import cn.forever.utils.CommonFunction;
import cn.forever.utils.Const;
import cn.forever.utils.Encrypt;
import cn.forever.utils.StrutsParamUtils;
/**
 * 20170508
 * @author lwh
 * 用于博客展示
 */
@ParentPackage(value = "struts-default") 
@Namespace(value = "/blog")
@Action(value = "blogViewAction",results = {
		@Result(name = "home",location = "/WEB-INF/blog/blogView/home.jsp"),
		@Result(name = "topics",location = "/WEB-INF/blog/blogView/topics.jsp"),
		@Result(name = "topic",location = "/WEB-INF/blog/blogView/topic.jsp"),
		@Result(name = "fail",location = "/index.jsp")
})
public class BlogViewAction {
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Gson gson =new Gson();
	private static final Logger log = Logger.getLogger(BlogViewAction.class);
	private final static int NUM = 6;
	@Resource(name="objectDao")
	private ObjectDao objectDao;
	//加入缓存
	@Resource(name="cacheUtil")
	private CacheUtil cacheUtil;
	
	
	@Resource(name="blogViewService")
	private BlogViewService blogViewService;

	/**
	 * 去到博客首页
	 * @return
	 */
	public String home(){
		log.info("去到首页");
		return "home";
	}
	/**
	 * 去到博客首页
	 * @return
	 */
	public String topics(){
		log.info("去到列表");
		HttpServletRequest request = StrutsParamUtils.getRequest();
		String type = StrutsParamUtils.getPraramValue("type", "0");//所在列表
		String value = StrutsParamUtils.getPraramValue("value", "0");//值
		request.setAttribute("type", type);
		try {
			value=new String(value .getBytes("iso8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println("value:"+value);
		request.setAttribute("value", value);
		return "topics";
	}
	/**
	 * 去到博客首页
	 * @return
	 */
	public String topic(){
		log.info("去到帖子页");
		String topicId = StrutsParamUtils.getPraramValue("topicId", "");//页面
		if(!"".equals(topicId)){
			String hql_topic = "from Topic t where t.visible='1' and t.id=?";
			Object[] args_topic = new Object[]{Long.parseLong(topicId)};
			List<Topic> topics = objectDao.findByHqlAndArgs(hql_topic, args_topic);
			//获取该帖子的一些信息
			List<PageValue> pageValues=getTopicListPageValue(topics);
			if(pageValues==null){
				return "fail";
			}
			HttpServletRequest request = StrutsParamUtils.getRequest();
			request.setAttribute("pageValue", pageValues.get(0));
			//这里也可以定位到是哪一个列的,用于页面显示列
			request.setAttribute("value", topics.get(0).getMenuId()+"");
			request.setAttribute("type", "1");
			//用来返回下一页
			return "topic";
		}
		return "fail";
	}
	
	/**
	 * 获取所有菜单，这个菜单只有一级
	 */
	
	@SuppressWarnings("unchecked")
	public void getAllMenu(){
		String str = "{\"state\":\"error\"}";
		String type="1";
		String menuId="0";
		//菜单也加入
		List<Menu> menus=cacheUtil.getList(Const.MENUS_KEY,Menu.class);
		System.out.println("从redis中获取menus:"+menus);
		if(menus==null){
			//获取菜单列表 菜单有效 且按sort 排序
			String hql = "from Menu m where m.type=? and m.menuId=? and m.visible='1' order by m.sort+0,m.create_datetime";
			Object[] args = new Object[]{type,Long.parseLong(menuId)};
			menus = objectDao.findByHqlAndArgs(hql, args);
			if(menus!=null){
				cacheUtil.put(Const.MENUS_KEY, menus);
			}
		}
		if(menus!=null){
			//过滤敏感信息
			List menu_list = new ArrayList<Menu>();
			//这里要判断有没有子菜单，没有的话value就为0
			for (Menu menu : menus) {
				Menu m = new Menu();
				m.setId(menu.getId());
				m.setName(menu.getName());
				//这里要去获取这个目录下面的主题数目，流程是先从redis获取，获取不到再取读取数据库获取数目，然后再放入redis
				//并且后台在维护topic的时候也会在topic中添加1，这样子就保证数据的正确性以及速度
				//KEY是：menu_topic_count:id
				String key = "menu_topic_count:"+m.getId();
				String menu_topic_count=cacheUtil.get(key);
				if(menu_topic_count==null){
					log.info("REDIS没有开，或者内存中的值丢失，从表中去获取");
					String hql_hot = "select count(*) from Topic t where t.visible='1' and t.menuId=?";
					Object[] args_hot =  new Object[]{menu.getId()};
					int count = objectDao.getCount(hql_hot, args_hot);
					menu_topic_count=count+"";
					//这里才需要重新放入redis
					cacheUtil.put(key, menu_topic_count);
				}
				log.info("key:"+key+",的主题个数是："+menu_topic_count);
				//主题个数放入菜单的预留字段
				m.setValue(menu_topic_count);
				menu_list.add(m);
			}
			//这里应该把二级菜单也给查询出来
			str = gson.toJson(menu_list);
		}
		log.info("菜单："+str);
		StrutsParamUtils.writeStr(str);
		return;
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public void getTopicList(){
		Map<String,Object> result =new HashMap<String,Object>();
		int page = Integer.parseInt(StrutsParamUtils.getPraramValue("page", "0"));
		int num = Integer.parseInt(StrutsParamUtils.getPraramValue("num",Const.PAGE_NUM));
		String type = StrutsParamUtils.getPraramValue("type", "0");
		//首页传0
		String value = StrutsParamUtils.getPraramValue("value", "0");
		if(page>=0&&num>0&&!"".equals(value)){
			String hql="";
			String key = "";//主题的总数目
			Map<String,Object> map = new HashMap<String, Object>();
			if("0".equals(type)){
				//首页
				hql = "from Topic t where t.visible='1' order by t.create_datetime desc";
				map=null;
				key="menu_topic_count:"+value;
			}
			if("1".equals(type)){
				//列表
				hql = "from Topic t where t.visible='1' and t.menuId=:menuId order by t.create_datetime desc";
				map.put("menuId", Long.parseLong(value));
				key="menu_topic_count:"+value;
			}
			if("2".equals(type)){
				//列表，搜索
				hql = "from Topic t where t.visible='1' and t.title like :title order by t.create_datetime desc";
				map.put("title", "%"+value+"%");
				key="menu_topic_count:"+Encrypt.md5(value);
			}
			//查询主题的数目
			String menu_topic_count = cacheUtil.get(key);
			if(menu_topic_count==null){
				//此时要去数据库中查找
				int count = objectDao.getCount("select count(*) "+hql, map);
				menu_topic_count=count+"";
				//这里才需要重新放入redis
				cacheUtil.put(key, menu_topic_count);
			}
			log.info("key:"+key+",的主题个数是："+menu_topic_count);
			//这里就是真正的去获取主题
			int topic_num = Integer.parseInt(menu_topic_count);
			int all_page=topic_num/num+1;
			if(topic_num%num==0){
				all_page--;//相当于刚刚够页数 不用加下一页
			}
			if(topic_num>0){
				//起始位置
				int start_page = page*num;
				List<Topic> topics = objectDao.findPageByHqlAndMap(hql, map,start_page, num);
				if(topics!=null){
					//对帖子进行处理;委会帖子的时候，就已经在redis中维护好帖子的作者，时间，浏览量
					List<PageValue> pageValues=getTopicListPageValue(topics);
					if(pageValues!=null){
						result.put("pageValues", pageValues);
						//有页数
						page=page+1;
					}
				}
				
			}
			result.put("all_page", all_page+"");//总页数
			result.put("type_now", type);//类型
			result.put("value_now", value);//总页数
			result.put("all_count", menu_topic_count);//总数
			//页面现在显示的页数
			result.put("page", page);
			Gson gson = new Gson();
			StrutsParamUtils.writeStr(gson.toJson(result));
			//获取最新的帖子
			return;
		}
	}
	/**
	 * 获取10条热门主题
	 */
	@SuppressWarnings("unchecked")
	public void getHotTopic(){
		String str = "{\"state\":\"error\"}";
		//获取页面传过来的条数 默认是9条
		String num = Const.HOT_NUM;
		log.info("num="+num);
		//根据热度降序排行 若是热度相同 那就根据时间 最新发表的排在前面
		String hql = "from Topic t where t.visible='1' order by t.hot desc,t.create_datetime desc";
		if(Integer.parseInt(num)>0){
			List<Topic> topics = objectDao.findPageByHqlAndArgs(hql, new Object[]{}, 0, Integer.parseInt(num));
			List<PageValue> pageValues=getTopicListPageValue(topics);
			if(pageValues!=null){
				Gson gosn = new Gson();
				str = gosn.toJson(pageValues);
			}
		}
		//log.info("十条热门帖子："+str);
		StrutsParamUtils.writeStr(str);
		return;
	}
	
	public void getQuickWebMemory(){
		String str = "{\"state\":\"error\"}";
		//从redis里面取
		
		List<Memory> menorys = CommonFunction.getMemorys(Const.QUICK_WEB);
		if(menorys!=null){
			Gson gosn = new Gson();
			str = gosn.toJson(menorys);
		}
		//log.info("十条热门帖子："+str);
		StrutsParamUtils.writeStr(str);
		return;
	}
	/**
	 * 根据主题列表 去封装主题的热度 和 浏览量
	 * @param topics
	 * @return
	 */
	private List<PageValue> getTopicListPageValue(List<Topic> topics){
		if(topics==null){
			return null;
		}
		List<PageValue> pageValues = new ArrayList<PageValue>();
		String topic_key = "";
		for (Topic topic : topics) {
			PageValue pageValue = new PageValue();
			//获取帖子的信息:topic_info:ID
			topic_key="topic_info:"+topic.getId();
			//获取用户的作者
			String name = cacheUtil.getHash(topic_key, "name");//主题
			String page_view = cacheUtil.getHash(topic_key, "page_view");//浏览量
			String hot = cacheUtil.getHash(topic_key, "hot");//热度
			if(name==null){
				//去获取姓名
				String hql_user =  "from User u where u.userId=?";
				Object[] args_user = new Object[]{topic.getUserId()};
				String nickname="匿名";
				User user = (User) objectDao.findObjectByHqlAndArgs(hql_user, args_user);
				if(user!=null){
					nickname=user.getNickname();
				}
				name=nickname;
				cacheUtil.putHash(topic_key, "name", name);
			}
			if(page_view==null){
				//去获取页面浏览量
				//获取该帖子的浏览量
				String hql_page_view = "select count(*) from RTMsg r where r.type1='2' and r.type2='1' and r.msgId=?";
				Object[] args_page_view =  new Object[]{topic.getId()};
				page_view = objectDao.getCount(hql_page_view, args_page_view)+"";
				cacheUtil.putHash(topic_key, "page_view", page_view);
			}
			if(hot==null){
				//去获取点赞
				//获取该帖子的热度
				String hql_hot = "select count(*) from RTMsg r where r.type1='1' and r.type2='1' and r.msgId=?";
				Object[] args_hot =  new Object[]{topic.getId()};
				hot=objectDao.getCount(hql_hot, args_hot)+"";
				cacheUtil.putHash(topic_key, "hot", hot);
			}
			pageValue.setId(topic.getId());//主题的ID
			pageValue.setValueA(topic.getTitle());//主题的标题
			pageValue.setValueB(topic.getContent());//主题的内容
			// /uploadFiles/topics/menuId/UUID.txt
			pageValue.setValueC(topic.getValue());//主题的文件名路径称UUID.html
			pageValue.setValueD(topic.getCreate_datetime());//主题的创建时间
			pageValue.setValueE(name);//主题的作者
			pageValue.setValueF(hot);//主题的热度
			pageValue.setValueG(page_view);//主题的浏览量
			pageValues.add(pageValue);
		}
		return pageValues;
	}
	
	/**
	 * 用于小程序显示
	 * @return
	 */
	public void getTopic(){
		log.info("去到帖子页");
		String topicId = StrutsParamUtils.getPraramValue("topicId", "");//页面
		if(!"".equals(topicId)){
			String hql_topic = "from Topic t where t.visible='1' and t.id=?";
			Object[] args_topic = new Object[]{Long.parseLong(topicId)};
			List<Topic> topics = objectDao.findByHqlAndArgs(hql_topic, args_topic);
			//获取该帖子的一些信息
			List<PageValue> pageValues=getTopicListPageValue(topics);
			if(pageValues!=null){
				Gson gosn = new Gson();
				StrutsParamUtils.writeStr(gosn.toJson(pageValues.get(0)));
			}
		}
	}
	
	
	
	
	
	
	
	//------------------------------------------下面的暂时不用--------------------------//
	
	/**
	 * 获取回复列表
	 */
	public void getReplyList(){
		String str = "{\"state\":\"error\"}";
		//获取页面传的参数
		String topicId = StrutsParamUtils.getPraramValue("topicId", "");
		int page = Integer.parseInt(StrutsParamUtils.getPraramValue("page", "0"));
		int num = Integer.parseInt(StrutsParamUtils.getPraramValue("num", NUM+""));
		//根据主题ID获取主题下面的所有回复
		if(page>=0&&num>0&&!"".equals(topicId)){
			//定位初始位置:start_page 为0,15,30
			int start_page = page*num;
			//按时间排序获取最新的9条或者多少条记录
			String hql = "from Reply r where r.visible='1' and r.topicId=? order by r.create_datetime";
			List<Reply> replys = objectDao.findPageByHqlAndArgs(hql, new Object[]{Long.parseLong(topicId)},start_page, num);
			List<PageValue> pageValues = getReplyListPageValue(replys);
			Gson gosn = new Gson();
			if(pageValues!=null){
				str = gosn.toJson(pageValues);
			}
		}
		//log.info("主题的回复列表："+str);
		StrutsParamUtils.writeStr(str);
		return;
	}
	
	private List<PageValue> getReplyListPageValue(List<Reply> replys) {
		if(replys==null){
			return null;
		}
		List<PageValue> pageValues = new ArrayList<PageValue>();
		for (Reply reply : replys) {
			PageValue pageValue = new PageValue(); 
			Long id = reply.getId();
			String hql_user =  "from User u where u.userId=?";
			Object[] args_user = new Object[]{reply.getUserId()};
			String nickname="匿名";
			String default_url= "/blog/images/user.jpg";//默认头像
			User user = (User) objectDao.findObjectByHqlAndArgs(hql_user, args_user);
			if(user!=null){
				String img_url = user.getImgUrl();
				nickname=user.getNickname();
				if(img_url!=null&&!"".equals(img_url)){
					default_url =img_url;
				}
			}
			//查看点赞量
			//获取该回复的热度
			String hql_hot = "select count(*) from RTMsg r where r.type1='1' and r.type2='2' and r.msgId=?";
			Object[] args_hot =  new Object[]{id};
			int hot = objectDao.getCount(hql_hot, args_hot);
			log.info("回复："+id+"的热度是："+hot);
			pageValue.setId(id);
			pageValue.setValueA(reply.getContent());//内容
			pageValue.setValueB(reply.getCreate_datetime());//发布日期
			pageValue.setValueC(hot+"");//点赞数
			pageValue.setValueD(nickname);//昵称
			pageValue.setValueE(default_url);//用户头像
			pageValues.add(pageValue);
		}
		return pageValues;
	}

	/**
	 * 获取主题或回复的赞和浏览记录
	 */
	public void getRTMsg(){
		String str = "{\"state\":\"error\"}";
		//是获取热度 还是浏览量还是两个都获取
		String type1 = StrutsParamUtils.getPraramValue("type1", "0");
		//是主题的还是回复的，1是主题，2是回复
		String type2 = StrutsParamUtils.getPraramValue("type2", "1");
		//主题还是回复的ID
		String msgId = StrutsParamUtils.getPraramValue("msgId", "");
		log.info("type1="+type1+"|type2="+type2+"|msgId="+msgId);
		Map<String,Integer> map = new HashMap<String, Integer>();
		if(!"".equals(msgId)){
			int hot=0;
			int page_view=0;
			Long id = Long.parseLong(msgId);
			//获取热度
			if("1".equals(type1)){
				String hql_hot = "select count(*) from RTMsg r where r.type1='1' and r.type2=? and r.msgId=?";
				Object[] args_hot =  new Object[]{type2,id};
				hot = objectDao.getCount(hql_hot, args_hot);
			}
			//获取浏览量
			if("2".equals(type1)){
				String hql_page_view = "select count(*) from RTMsg r where r.type1='2' and r.type2=? and r.msgId=?";
				Object[] args_page_view =  new Object[]{type2,id};
				page_view = objectDao.getCount(hql_page_view, args_page_view);
			}
			//两个都获取
			if("0".equals(type1)){
				String hql_hot = "select count(*) from RTMsg r where r.type1='1' and r.type2=? and r.msgId=?";
				Object[] args_hot =  new Object[]{type2,id};
				hot = objectDao.getCount(hql_hot, args_hot);
				String hql_page_view = "select count(*) from RTMsg r where r.type1='2' and r.type2=? and r.msgId=?";
				Object[] args_page_view =  new Object[]{type2,id};
				page_view = objectDao.getCount(hql_page_view, args_page_view);
			}
			log.info("id"+id+"-----热度："+hot+"---------浏览量："+page_view);
			map.put("hot", hot);
			map.put("page_view", page_view);
			Gson gosn = new Gson();
			str=gosn.toJson(map);
			log.info("热度和浏览量："+str);
			StrutsParamUtils.writeStr(str);
			return;
		}
	}
	
	/**
	 * 主题的浏览量（这个不是来访记录）
	 */
	public void addPageView(){
		String msgId = StrutsParamUtils.getPraramValue("msgId", "0");
		log.info("浏览的主题："+msgId);
		if(!"".equals(msgId)){
			RTMsg rtmsg = new RTMsg();
			rtmsg.setMsgId(Long.parseLong(msgId));//主题的ID
			rtmsg.setType1("2");//浏览量
			rtmsg.setType2("1");//主题
			rtmsg.setCreate_datetime(format.format(new Date()));
			objectDao.saveOrUpdate(rtmsg);
			log.info("保存主题"+msgId+"的浏览记录一条成功");
		}
		//修改redis的值
		String topic_key="topic_info:"+msgId;
		String page_view = cacheUtil.getHash(topic_key, "page_view");//浏览量
		if(page_view!=null){
			int now = Integer.parseInt(page_view)+1;
			cacheUtil.putHash(topic_key, "page_view", now+"");
		}
	}
	
	/**
	 * 页面的访问记录
	 */
	public void addVisit(){
		HttpServletRequest request = StrutsParamUtils.getRequest();
		//访问的时间
		String create_datetime=format.format(new Date());
		//访问的链接
		//3:获取链接和参数
		//String requestURI=request.getRequestURI(); 
        //String queryString = request.getQueryString();
        //4：拼接 链接
        //String url = requestURI+"?"+queryString;
		String url = StrutsParamUtils.getPraramValue("url", "");
        //5:IP地址
        String ip=CommonFunction.getIpAddr();
        //6：访问者
        Object obj = request.getSession().getAttribute("user");
        String userId = "";
        if(obj!=null){
        	userId=((User) obj).getUserId();
        	log.info("用户已经登录");
        }
        Visit visit = new Visit();
        visit.setCreate_datetime(create_datetime);
        visit.setIp(ip);
        visit.setUserId(userId);
        visit.setUrl(url);
        objectDao.saveOrUpdate(visit);
        //log.info("访问记录："+visit.toString());
	}
	/************************************************************/
	/**
	 * 判断用户是否登录
	 * 这里从session中取的是博客用户，而非管理员用户
	 */
	public void isHaveLogin(){
		User user = (User) StrutsParamUtils.getRequest().getSession().getAttribute("user");
		if(user!=null){
			log.info("用户已经登录");
			StrutsParamUtils.writeStr("login");
			return;
		}else{
			log.info("用户没有登录");
			StrutsParamUtils.writeStr("logon");
			return;
		}
	}
	/**
	 * 用户去登录
	 */
	public void login(){
		//获取页面传过来的参数
		String username = StrutsParamUtils.getPraramValue("username", "");
		String password = StrutsParamUtils.getPraramValue("password", "");
		log.info("username="+username+"|password="+password);
		if("".equals(username)||"".equals(password)){
			StrutsParamUtils.writeStr("error");
			log.info("用户名或密码为空");
			return;
		}
		//获取加密后的密码
		String encrypt_password = Encrypt.encrypt(password);
		//根据用户名和密码去数据库中查找是否有记录（这里不需要限定用户的类型）
		String hql = "from User u where u.username=? and u.password=? and u.visible='1'";
		Object[] args = new Object[]{username,encrypt_password};
		User user = (User) objectDao.findObjectByHqlAndArgs(hql, args);
		if(user!=null){
			//将用户放到session中
			StrutsParamUtils.getRequest().getSession().setAttribute("user", user);
			StrutsParamUtils.writeStr("success");
			log.info("用户登录成功");
			return;
		}else{
			StrutsParamUtils.writeStr("error");
			log.info("用户名或密码错误");
			return;
		}
	}
	/**
	 * 注销
	 */
	public void logon(){
		HttpServletRequest request = StrutsParamUtils.getRequest();
		HttpSession session = request.getSession();
		Object obj = session.getAttribute("user");
		if(obj!=null){
			session.removeAttribute("user");
		}
		StrutsParamUtils.writeStr("success");
		return;
	}
	/**
	 * 用户去登录
	 */
	public void register(){
		//获取页面传过来的参数
		String username = StrutsParamUtils.getPraramValue("username", "");
		String password = StrutsParamUtils.getPraramValue("password", "");
		String password2 = StrutsParamUtils.getPraramValue("password2", "");
		log.info("username="+username+"|password="+password+"|password2="+password2);
		if("".equals(username)||"".equals(password)||"".equals(password2)){
			StrutsParamUtils.writeStr("error");
			log.info("用户名或密码为空");
			return;
		}
		if(!password.equals(password2)){
			StrutsParamUtils.writeStr("no_same");
			log.info("两次输入的密码不同");
			return;
		}
		//根据用户名和密码去数据库中查找是否有记录（这里不需要限定用户的类型）
		String hql = "from User u where u.username=?";
		Object[] args = new Object[]{username};
		User blog_user = (User) objectDao.findObjectByHqlAndArgs(hql, args);
		if(blog_user!=null){
			//将用户放到session中
			StrutsParamUtils.writeStr("isHave");
			log.info("用户名已经存在");
			return;
		}else{
			String encrypt_password = Encrypt.encrypt(password);
			User user= new User();
			user.setCreate_datetime(format.format(new Date()));
			user.setAge("");
			user.setNickname(username);
			user.setUsername(username);
			user.setPassword(encrypt_password);
			user.setRemark("正常登录用户");
			user.setType("1");
			user.setVisible("1");
			objectDao.saveOrUpdate(user);
			log.info("用户注册成功");
			StrutsParamUtils.writeStr("success");
			return;
		}
	}
	/**
	 * 回复，前面已经确认用户已经登录，这里就不需要再次去确认啦，不过确认一下也好
	 */
	public void replyTopic(){
		User blog_user = (User) StrutsParamUtils.getRequest().getSession().getAttribute("user");
		if(blog_user!=null){
			log.info("用户已经登录");
			//获取页面传过来的参数
			String topicId = StrutsParamUtils.getPraramValue("topicId", "");
			String topicUserId = StrutsParamUtils.getPraramValue("topicUserId", "");
			String reply_content = StrutsParamUtils.getPraramValue("reply_content", "");
			if("".equals(topicId)||"".equals(topicUserId)||"".equals(reply_content)){
				log.info("参数错误");
				StrutsParamUtils.writeStr("param_error");
				return;
			}else{
				Reply reply = new Reply();
				reply.setContent(reply_content);
				reply.setCreate_datetime(format.format(new Date()));
				reply.setHot(0l);
				reply.setTopicId(Long.parseLong(topicId));
				reply.setToUserId(Long.parseLong(topicUserId));
				reply.setUserId(blog_user.getUserId());
				reply.setVisible("1");
				objectDao.saveOrUpdate(reply);
				log.info("回复成功");
				StrutsParamUtils.writeStr("success");
				return;
			}
		}else{
			log.info("页面已过期");
			StrutsParamUtils.writeStr("page_lose");
			return;
		}
	}
	
	public void addHot(){
		String id = StrutsParamUtils.getPraramValue("id", "");
		String type = StrutsParamUtils.getPraramValue("type", "");
		if("".equals(id)||"".equals(type)){
			log.info("参数错误");
			StrutsParamUtils.writeStr("param_error");
			return;
		}
		User blog_user = (User) StrutsParamUtils.getRequest().getSession().getAttribute("blog_user");
		if(blog_user!=null){
			//看看用户是否已经点过赞
			Long msgId = Long.parseLong(id);
			String userId = blog_user.getUserId();
			String hql = "from RTMsg r where r.userId=? and r.msgId=? and r.type1='1' and r.type2=?";
			Object[] args= new Object[]{userId,msgId,type};
			if(objectDao.findObjectByHqlAndArgs(hql, args)!=null){
				log.info("用户已经点过赞");
				StrutsParamUtils.writeStr("isHave");
				return;
			}else{
				RTMsg rtmsg = new RTMsg();
				rtmsg.setMsgId(msgId);//主题或者回复的ID
				rtmsg.setType1("1");//热度
				rtmsg.setType2(type);//主题
				rtmsg.setCreate_datetime(format.format(new Date()));
				rtmsg.setUserId(userId);
				objectDao.saveOrUpdate(rtmsg);
				log.info("保存一条热度成功");
				//更新一条热度
				if("1".equals(type)){
					//主题
					String hql_topic = "from Topic t where t.id=?";
					Object[] args_topic = new Object[]{msgId};
					Topic topic = (Topic) objectDao.findObjectByHqlAndArgs(hql_topic, args_topic);
					if(topic!=null){
						topic.setHot(topic.getHot()+1);
						objectDao.saveOrUpdate(topic);
					}
				}
				if("2".equals(type)){
					//回复
					String hql_reply = "from Reply t where t.id=?";
					Object[] args_reply = new Object[]{msgId};
					Reply reply = (Reply) objectDao.findObjectByHqlAndArgs(hql_reply, args_reply);
					if(reply!=null){
						reply.setHot(reply.getHot()+1);
						objectDao.saveOrUpdate(reply);
					}
				}
				
				StrutsParamUtils.writeStr("success");
				return;
			}
		}else{
			log.info("页面已过期");
			StrutsParamUtils.writeStr("page_lose");
			return;
		}
	}
}
