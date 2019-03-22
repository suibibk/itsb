<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/blog/blogView/template.jsp"></jsp:include>
<style>
		.topicList{
			width:95%;
			margin:0px auto;
			margin-top:10px;
		}
		.lists{
			width:100%;
			margin-bottom:10px;
		}
		.list{
			width:100%;
			height:180px;
			border-bottom:1px solid  #ccc;
			margin-bottom:10px;
		}
		.list_title{
			width:100%;
			font-weight:bold;
			font-size:17px;
			height:30px;
			line-height:30px;
			overflow:hidden;
			text-overflow:ellipsis;
			white-space:nowrap;
			cursor:pointer;
		}
		.list_content{
			width:100%;
			font-size:15px;
			height:120px;
			overflow:hidden;
			word-wrap: break-word;
			text-overflow:ellipsis;
			line-height:25px;
		}
		.list_info{
			font-size:13px;
			height:30px;
			line-height:30px;
			text-align:right;
			padding-right:10px;
			overflow:hidden;
			text-overflow:ellipsis;
			white-space:nowrap;
			
		}
		.list_topic{
			cursor: pointer;
			color:#336699;
			font-size:14px;
		}
		.page{
			width:100%;
			text-align:center;
			display:none;
		}
		#page_num{
			width:50px;
		}
		/**********************/
		.other_web{
			width:100%;
			text-align: center;
			font-size: 0px;
			margin-bottom: 15px;
			margin-top: 20px;
		}
		.webs{
			width:21%;
			display: inline-block;
			height:50px;
			background: #e4e4e4;
			line-height: 50px;
			margin: 2%;
		}
		.webs:hover{
		   cursor: pointer;
		   background: #bbb;
		}
</style>
<script>
	$(function(){
			//将元素放入center
			$(".center").html($("#home").html());
			$("#home").remove();
			getTopics("0",0,0);
			$("#pageHome").addClass("cd6600_color");
		});
		
		/**
		*value:参数
		*type:0-首页，value为'',1-列表，value为menu的ID，2-搜索，value为title中的内容，这里只支持标题搜索
		*/	
		function getTopics(value,type,page){
			if(value==''){
				return;
			}
			$(".page").hide();
	  	    $(".lists").html("");
	  	    $(".msg").show();
	  		$.ajax({ 
	  			 url: "${pageContext.request.contextPath }/blog/blogViewAction!getTopicList.action",
	  	         type: "POST", 
	  	         dataType:'json',
	  	         data : {"page" : page,"value":value,"type":type},
	  	         success: function (data) {
	  	        	//$(".msg").hide();
	  	        	 if(data!=''){
	  	        		 //获取data中的内容
	  	        		 var all_page =data.all_page;//总页数
	  	        		 var type_now =data.type_now;//总页数
	  	        		 var value_now =data.value_now;//值
	  	        		 var all_count =data.all_count;//总数
	  	        		 var now_page =data.page;//当前显示页数，也是下一页要加载的数目
	  	        		 $("#now_page").html(now_page);
	  	        		 $("#all_page").html(all_page);
	  	        		 $("#all_count").html(all_count);
	  	        		 $("#type_now").val(type_now);
	  	        		 $("#value_now").val(value_now);
	  	        		 var pageValues =data.pageValues;
	  	        		 if(pageValues==undefined){
	  	        			$(".msg").html("未查询到记录");
	  	        			 return;
	  	        		 }
	  	        		 var str='';
	  	        		 for(var i=0;i<pageValues.length;i++){
	  	        			str+='<div class="list"  onclick="toTopic('+pageValues[i].id+')">';
							str+='<div class="list_title">'+pageValues[i].valueA+'</div>';
							str+='<div class="list_content">'+pageValues[i].valueB+'<span class="list_topic" onclick="toTopic('+pageValues[i].id+')">[查看详情]</span></div>';
							str+='<div class="list_info">作者:'+pageValues[i].valueE+'&nbsp;&nbsp;时间:'+pageValues[i].valueD+'&nbsp;&nbsp;浏览量:<span class="page_view">'+pageValues[i].valueG+'</span>&nbsp;&nbsp;<span class="zhan">赞:'+pageValues[i].valueF+'</span></div>';
							str+='</div>';
	  	        		 }
	  	        		 $(".lists").html(str);
	  	        		 $(".page").show();
	  	        		$(".msg").hide();
	  	        	 }else{
	  	        		$(".msg").html("未查询到记录");
	  	        	 }
	  	         },
	  	         error : function (data) {
	  	        	$(".msg").html("未查询到记录");
	  	         }
	  	 	});
	  	}
		
		
		
		/**
		*页面的跳转
		*/
		function pageUp(){
			var page = parseInt($("#now_page").html());
			if(page==1){
				return;
			}else{
				var value=$("#value_now").val();
				var type=$("#type_now").val();
				getTopics(value,type,page-2);
			}
			
		}
		function pageDown(){
			var page = parseInt($("#now_page").html());
			var all_page=parseInt($("#all_page").html());
			if(page==all_page){
				return;
			}else{
				var value=$("#value_now").val();
				var type=$("#type_now").val();
				getTopics(value,type,page);
			}
		}
		function toPage(){
			var page = parseInt($("#now_page").html());
			if($("#page_num").val()==""){
				return;
			}
			var page_num = parseInt($("#page_num").val());
			var all_page=parseInt($("#all_page").html());
			if(page_num<1||page_num==page||page_num>all_page){
				return;
			}else{
				var value=$("#value_now").val();
				var type=$("#type_now").val();
				getTopics(value,type,page_num-1);
			}
		}
		
</script>
<input type="hidden" id="type_now">
<input type="hidden" id="value_now">
<div id="home" style="display:none;">
			<div class="topicList">
				<!-- 放置其他功能按钮 -->
				<div class="other_web">
					<div class="webs" onclick="toChat()">胡聊</div>
					<div class="webs" onclick="toDHP()">蛋黄盘</div>
				</div>
				<div class="msg">正在查询中...</div>
							<div class="lists">
								<!-- <div class="list">
									<div class="list_title">Java异步调用原理以及代码示例</div>
									<div class="list_content">在java5以后，一个可以调度执行的线程单元可以有三种方式定义：Thread、Runnable、Callable，其中Runnable实现的是void run()方法，Callable实现的是 V call()方法，并且可以返回执行结果，其中Runnable可以提交给Thread来包装下，直接启动一个线程来执行，而Callable则一般都是提交给ExecuteService来执行。</div>
									<div class="list_info">作者:林文&nbsp;&nbsp;时间:2018-08-09&nbsp;&nbsp;浏览量:18&nbsp;&nbsp;赞:19</div>
								</div> -->
							</div>
							<div class="page">
								<span>总数:</span><span id="all_count">0</span>
								<span id="now_page">0</span>/<span id="all_page">0</span><span>页</span>
								<span>
									<input type="button" value="上一页" onclick="pageUp();"/>
									<input type="button" value="下一页" onclick="pageDown();"/>
						    		<input type="text" value="" id="page_num"/>
						    		<input type="button" value="跳转" onclick="toPage();"/>
					    		</span>
							</div>						
			</div>
</div>
