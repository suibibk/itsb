<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/blog/blogView/template.jsp"></jsp:include>
<style>
		.topic{
			width:95%;
			margin:0px auto;
			margin-top:10px;
			margin-bottom:20px;
		}
		.topic_title{
			width:100%;
			font-weight:bold;
			font-size:17px;
			height:30px;
			line-height:30px;
			text-align:center;
			overflow:hidden;
			text-overflow:ellipsis;
			white-space:nowrap;
		}
		.topic_info{
			width:100%;
			font-size:14px;
			height:30px;
			text-align:center;
			line-height:30px;
			padding-right:10px;
			overflow:hidden;
			text-overflow:ellipsis;
			white-space:nowrap;
		}
		.zhan{
			display:none;
		}
		.page_view{
			font-size:13px;
		}
		.topic_back{
			width:100%;
			font-size:14px;
			height:30px;
			text-align:center;
			line-height:30px;
			padding-right:10px;
			overflow:hidden;
			text-overflow:ellipsis;
			white-space:nowrap;
			cursor:pointer;
		}
		.topic_content{
			width:100%;
			word-wrap: break-word;
		}
		.topic_content img{
			max-width: 100% !important;
  			height: auto !important;
		}
		/* <!--代码样式--> */
		blockquote{
		    padding-left: 10px;
		     padding-top: 5px;
		     padding-bottom: 5px;
   			 margin-top: 10px;
   			 margin-bottom: 10px;
   			 color: #000;
    		background: #f5f5f5;
    		border: 1px solid #ccc;
    		border-left: 8px solid #ccc;
     	}
		
</style>
<script>
	var id = '${requestScope.pageValue.id}';
	var valueC = '${requestScope.pageValue.valueC}';
	var valueA = '${requestScope.pageValue.valueA}';
	$(function(){
			addPageView();
			//将元素放入center
			$(".center").html($("#topic").html());
			$("#topic").remove();
			if(valueC==''){
				$(".topic_content").html($("#valueB").html());
			}else{
				//var path="${pageContext.request.contextPath}"+valueC;
				$(".topic_content").load(valueC);
			}
			$("title").html(valueA);
		});
	//异步插入一条浏览记录
	function addPageView(){
		$.ajax({ 
				 url: "${pageContext.request.contextPath }/blog/blogViewAction!addPageView.action",
		         type: "POST", 
		         data : {"msgId":id},
		         success: function (data) {
		         },
		         error : function (data) {
		         }
		 });
	}
	
</script>
<div style="display:none;" id="valueB">${requestScope.pageValue.valueB}</div>
<div id="topic" style="display:none;">
			<div class="topic">
						<div class="topic_title">${requestScope.pageValue.valueA}</div>
						<div class="topic_info">作者:${requestScope.pageValue.valueE}&nbsp;&nbsp;时间:${requestScope.pageValue.valueD}&nbsp;&nbsp;浏览量:${requestScope.pageValue.valueG}</div>
						<div class="topic_content">
						
						</div>
					</div>
</div>
