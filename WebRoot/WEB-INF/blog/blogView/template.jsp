<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
 	<meta charset='utf-8'>
    <meta name="Keywords" content="IT,JAVA,服务器,个人博客,笔记,人工智能,深入学习,神经网络,前端,HTML,JS,CSS,MYSQL,LINUX,微信开发,音乐,视频,电影"/> 
    <title>IT随笔</title>
	<script src="${pageContext.request.contextPath }/blog/js/jquery-1.10.1.min.js" type="text/javascript"></script>
	<style>
		*{margin:0px;padding:0px;font-size:15px;font-family: 微软雅黑}
		.top{
			height:30px;
			line-height:30px;
			border-bottom:1px solid #ddd;
			position: fixed;
			width:90%;
			left:5%;
			right:5%;
			top:0px;
			background-color:#fff;
		}
		.top_left{
			float:left;
			width:50%;
			text-align:center;
			height:100%;
		}
		.top_right{
			float:right;
			width:50%;
			text-align:center;
			height:100%;
		}
		.homePage{
			width:90%;
			margin:0px auto;
			margin-bottom:50px;
			margin-top:30px;
		}
		#pageHome{
			cursor: pointer;
		}
		.content{
			width:100%;
			/*有这个下面的float就不会脱离文档流*/
			overflow:hidden; 
		}
		.left{
			width:15%;
			background: #eee;
			float: left;
		}		
		.right{
			width:15%;
			background: #eee;
			float: right;
		}
		
		.model{
			width:95%;
			margin:0px auto;
			margin-top:10px;
		}
		.model_title{
			height:30px;
			line-height:30px;
			width:100%;
			text-align:center;
			cursor:pointer;
			overflow:hidden;
			text-overflow:ellipsis;
			white-space:nowrap;
		}
		.model_title:hover{ color:#CD6600;font-size:20px;}
		.model_title_first{
			font-weight:bold;
			height:30px;
			line-height:30px;
			width:100%;
			text-align:center;
		}	
		.search{
			width:95%;
			margin:0px auto;
			margin-top:10px;
		}
		.search input{
			width:100%;
			height:25px;
			box-sizing: border-box;
		}
		#date{
			width:95%;
			margin:0px auto;
			margin-top:10px;
		}
		#nowTime{
			width:100%;
			text-align: center;
			font-weight:bold;
			
		}
		.center{
			width:70%;
			float: left;
		}
		.zhan{
			display:none;
		}
		.bottom{
			width:90%;
			position: fixed;
			text-align:center;
			height:30px;
			line-height:30px;
			bottom:0px;
			left:5%;
			right:5%;
			font-size:13px;
			background-color:#fff;
		}		
		.cd6600_color{
			 color:#CD6600;font-size:20px;
		}
	    .msg{
	    	height:30px;
	    	width:100%;
	    	color:#CD6600;
	    	display:none;
	    	text-align: center;
	    }
	    /********MOBILE*********/
	    #mobile{
	    		width:100%;
	    		overflow:hidden; 
	    		text-align: center;
	    		border-bottom:1px solid #ddd;
	    		display:none;
	    }
	    #mobile div{
	    	width:33.33%;
	    	float:left;
	    	text-align:center;
	    }
	    #mobile .model_title_first{
	    	display:none;
	    }
	    #mobile_search{
	        width: 100%;
   			 height: 30px;
   			 line-height: 30px;
   			 text-align: center;
   			 display:none;
   			 margin-top: 5px;
	    }
	</style>
	<script>
	 $(window).resize(function () {          //当浏览器大小变化时
	   var width=$(window).width();          //浏览器时下窗口可视区域高度
	   if(width<=900){
		   mobile_style();
	   }else{
		   pc_style();
	   }
	});
	var sUserAgent = navigator.userAgent.toLowerCase(); 
	var wechat = sUserAgent.match(/wechat/i) == "wechat";   
	var bIsIpad = sUserAgent.match(/ipad/i) == "ipad";    
	var bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";  
	var bIsMidp = sUserAgent.match(/midp/i) == "midp";  
	var bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4";  
	var bIsUc = sUserAgent.match(/ucweb/i) == "ucweb";  
	var bIsAndroid = sUserAgent.match(/android/i) == "android";  
	var bIsCE = sUserAgent.match(/windows ce/i) == "windows ce";  
	var bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile"; 
	if(wechat||bIsIpad||bIsIphoneOs||bIsMidp||bIsUc7||bIsUc||bIsAndroid||bIsCE||bIsWM){
		var oMeta1 = document.createElement('meta');
		oMeta1.content ="width=device-width,initial-scale=1, maximum-scale=1,user-scalable=no";
		oMeta1.name = "viewport";
		var oMeta2 = document.createElement('meta');
		oMeta2.content ="black";
		oMeta2.name = "apple-mobile-web-app-capable";
		var oMeta3 = document.createElement('meta');
		oMeta3.content ="yes";
		oMeta3.name = "apple-mobile-web-app-capable";
		var oMeta4 = document.createElement('meta');
		oMeta4.content ="email=no";
		oMeta4.name = "format-detection";
		var oMeta5 = document.createElement('meta');
		oMeta5.content ="telephone=no";
		oMeta5.name = "format-detection";
		document.getElementsByTagName('head')[0].appendChild(oMeta1);
		document.getElementsByTagName('head')[0].appendChild(oMeta2);
		document.getElementsByTagName('head')[0].appendChild(oMeta3);
		document.getElementsByTagName('head')[0].appendChild(oMeta4);
		document.getElementsByTagName('head')[0].appendChild(oMeta5);
		mobile_style();
	}else{
		pc_style();
	} 
	
	/**
	*移动端以及PC端屏幕缩小的样式
	*/
	function mobile_style(){
		$(".left").hide();
		$(".right").hide();
		$(".center").css("width","100%");
		$("#mobile").show();
		$("#mobile_search").show();
	}
	/**
	*PC端的样式
	*/
	function pc_style(){
		$(".left").show();
		$(".right").show();
		$(".center").css("width","70%");
		$("#mobile").hide();
		$("#mobile_search").hide();
	}
	
	var type_1 = '${requestScope.type}';
	var value_1 = '${requestScope.value}';
	$(function(){
		var width=$(window).width();          //浏览器时下窗口可视区域高度
		   if(width<=900){
			   mobile_style();
		   }else{
			   pc_style();
		   }
		
			//回显搜索框的值
		   if(type_1=="2"){
				$("#search_text").val(value_1);
				$("#mobile_search_text").val(value_1);
			}
			
			getAllMenu();
			getHotTopic();
			getQuickWebMemory();
			addVisit();
		});
		/**
		 * 获取所有的菜单
		 */
		function getAllMenu(){
			 $.ajax({ 
				     url: "${pageContext.request.contextPath }/blog/blogViewAction!getAllMenu.action",
			         type: "POST", 
			         dataType:'json',
			         //data : {"type" : '0',"menuId":'-1'},获取所有的菜单
			         success: function (data) {
			        	 if(data.state!="error"){
			        		str='<div class="model_title_first">目录</div>';
			        		//这里加入模块(资源文件)
			        		for(var i=0;i<data.length;i++){
				        		str+='<div class="model_title';
				        		if(type_1=="1"&&data[i].id==value_1){
				        			str+=' cd6600_color';
				        		}
				        		str+='" id="menuId_'+data[i].id+'" onclick="toTopics('+data[i].id+',1)">'+data[i].name+'('+data[i].value+')</div>';
			        		}
			        		$(".catalog").html(str);
			        		$("#mobile").html(str);
		 	 	        }
			         },
			         error : function (data) {
			           	//alert("网络异常");
			         }
			 	});
		 }
		
		
		/**
		 *异步获取个热门主题10个
	     */
	     function getHotTopic(){
	 		$.ajax({ 
	 			 url: "${pageContext.request.contextPath }/blog/blogViewAction!getHotTopic.action",
	 	         type: "POST", 
	 	         //async: false,
	 	         dataType:'json',
	 	         data : {"num" : 10},
	 	         success: function (data) {
	 	        	 if(data.state!="error"){
	 	        		str='';
	 	        		str='<div class="model_title_first">热门主题</div>';
	 	        		for(var i=0;i<data.length;i++){
	 	        			str+='<div class="model_title" id="hot_topic_title_'+data[i].id+'" onclick="toTopic('+data[i].id+')">'+(i+1)+'.'+data[i].valueA+'</div>';
		 	 	        }
	 	        		$(".hot").html(str);
	 	        	 }
	 	         },
	 	         error : function (data) {
	 	           	//alert("网络异常");
	 	         }
	 	 	});
	 	}
	     /**
			 *获取快速通道
		     */
		     function getQuickWebMemory(){
		 		$.ajax({ 
		 			 url: "${pageContext.request.contextPath }/blog/blogViewAction!getQuickWebMemory.action",
		 	         type: "POST", 
		 	         //async: false,
		 	         dataType:'json',
		 	         data : {},
		 	         success: function (data) {
		 	        	 if(data.state!="error"){
		 	        		str='';
		 	        		str='<div class="model_title_first">快速通道</div>';
		 	        		for(var i=0;i<data.length;i++){
		 	        			str+='<div class="model_title" onclick="toQuickWeb(\''+data[i].valueB+'\')">'+data[i].valueA+'</div>';
			 	 	        }
		 	        		$(".quick").html(str);
		 	        	 }
		 	         },
		 	         error : function (data) {
		 	           	//alert("网络异常");
		 	         }
		 	 	});
		 	}
		
		/**
		*去到某一主题
		*/
		function toTopic(id){
			if(id==""){
				return;
			}
			window.location.href='${pageContext.request.contextPath }/blog/blogViewAction!topic.action?topicId='+id;
		}
		/**
		*去到某一主题
		*/
		function toHome(){
			window.location.href='${pageContext.request.contextPath }/blog/blogViewAction!home.action';
		}
		/**
		*去到管理
		*/
		function toManage(){
			window.location.href='/itmgr/blog/blogManageAction!blogManage.action';
		}
		/**
		*去到蛋黄盘
		*/
		function toDHP(){
			window.location.href='/itmgr/dhp/dhpAction!dhp.action';
		}
		/**
		*去胡聊
		*/
		function toChat(){
			window.location.href='/huChat/huChat/huChatAction!huChat.action';
		}
		/**
		*去到某一列表
		*/
		function toTopics(value,type){
			if(value==""){
				return;
			}
			window.location.href='${pageContext.request.contextPath }/blog/blogViewAction!topics.action?value='+value+'&type='+type;
		}
		function toQuickWeb(url){
			window.location.href=url;			
		}	
		/**
		*去到外部
		*/
		//异步插入一条浏览记录
		function addVisit(){
			$.ajax({ 
					 url: "${pageContext.request.contextPath }/blog/blogViewAction!addVisit.action",
			         type: "POST", 
			         data : {"url":location.href},
			         success: function (data) {
			         },
			         error : function (data) {
			         }
			 });
		}
	</script>
</head>

<body>
<!--头部-->
	<input type="hidden" id="type_now">
	<input type="hidden" id="value_now">
	<div class="top">
			<div class="top_left">林蛋黄的IT随笔博客</div>
			<div class="top_right"><span id="pageHome" onclick="toHome()">首页</span>&nbsp;|&nbsp;<span onclick="toManage()">管理</span></div>
	</div>
	<div  class="homePage">
		<!-- <table class="content">
			<tr>
				<td class="left"> -->
			<div class="content">
			<div id="mobile_search">
							<input  id="mobile_search_text" type="text"/>
							<input type="button" onclick="toTopics($('#mobile_search_text').val(),2)" value="搜索"/>
						</div>
			<div id="mobile">
				
			</div>
				<div class="left">
					<div class="model catalog">
						<!-- <div class="model_title_first">目录</div>
						<div class="model_title">Linux(23)</div>
						<div class="model_title">MySQL(23)</div>
						<div class="model_title">Spring(23)</div>
						<div class="model_title">人工智能(23)</div>
						<div class="model_title">Hibernate(23)</div>
						<div class="model_title">设计模式(23)</div>
						<div class="model_title">高并发(23)</div>
						<div class="model_title">Linux(23)</div>
						<div class="model_title">MySQL(23)</div>
						<div class="model_title">SpringMVC(23)</div> -->
					</div>
				</div>
				<div class="center">
				
				</div>
				<div class="right">
				
						<div class="search">
							<input  id="search_text" type="text"/>
							<input type="button" onclick="toTopics($('#search_text').val(),2)" value="搜索"/>
						</div>
						<div id="date">
							<p id="nowTime"><fmt:formatDate value="<%=new Date()%>" pattern="HH:mm:ss" /><p>
							<jsp:include page="/WEB-INF/blog/blogView/date.jsp"></jsp:include>
						</div>
						<div class="model hot">
							<!-- <div class="model_title_first">热门文章</div>
							<div class="model_title">1.JAVA的设计模式...</div>
							<div class="model_title">1.JAVA的设计模式...</div>
							<div class="model_title">1.JAVA的设计模式...</div>
							<div class="model_title">1.JAVA的设计模式...</div>
							<div class="model_title">1.JAVA的设计模式...</div>
							<div class="model_title">1.JAVA的设计模式...</div>
							<div class="model_title">1.JAVA的设计模式...</div> -->
						</div>
						<div class="model quick">
							<!-- <div class="model_title_first">快速通道</div>
							<div class="model_title">百度一下</div>
							<div class="model_title">阿里云</div>
							<div class="model_title">狗带TV</div>
							<div class="model_title">微信公众号</div>
							<div class="model_title">SCALA入门教程</div>
							<div class="model_title">bilibili</div>
							<div class="model_title">起点阅读</div> -->
						</div>
					</div>
					</div>
				<!-- </td>
			</tr>
		</table> -->
		<!--底部-->
	</div>
	<div class="bottom">Copyright&nbsp;: &nbsp;林蛋黄 &nbsp;&nbsp;&nbsp;&nbsp;备案号：粤ICP备18099399号</div>
	
</body>
</html>