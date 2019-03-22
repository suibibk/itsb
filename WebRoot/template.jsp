<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset='utf-8'>
    <title>博客首页</title>
	 <link rel="stylesheet" href="${pageContext.request.contextPath }/highlight/styles/default.css">
	<script src="${pageContext.request.contextPath }/highlight/highlight.pack.js"></script>
	<script type="text/javascript">
	hljs.initHighlightingOnLoad();
	</script>
</head>
<body>
	<!--头部-->
	<pre><code>
package cn.myforever.test;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/**
 * 在Java5之后，任务分两类：一类是实现了Runnable接口的类，一类是实现了Callable接口的类。两者都可以被ExecutorService执行，但是Runnable任务没有返回值，而Callable任务有返回值。并且Callable的call()方法只能通过ExecutorService的(<T> task) 方法来执行，并且返回一个 <T><T>，是表示任务等待完成的 Future。
 * @author forever
 *
 */
public class SyncYBDO {
public static void main(String[] args) throws Throwable, ExecutionException {  
        ExecutorService executor1 = Executors.newFixedThreadPool(3);  
        // Future代表了线程执行完以后的结果，可以通过future获得执行的结果  
        // 但是jdk1.8之前的Future有点鸡肋，并不能实现真正的异步，需要阻塞的获取结果，或者不断的轮询  
        // 通常我们希望当线程执行完一些耗时的任务后，能够自动的通知我们结果，很遗憾这在原生jdk1.8之前  
        // 是不支持的，但是我们可以通过第三方的库实现真正的异步回调  
        Future<Boolean> f1 = executor1.submit(new Callable<Boolean>() {  
  
            @Override  
            public Boolean call() throws Exception {  
                Thread.sleep(3000);
                System.out.println("3秒任务执行结束");
                return true;  
            }  
        });
        Future<String> f2 = executor1.submit(new Callable<String>() {  
          
            @Override  
            public String call() throws Exception {  
                Thread.sleep(1000);  
                System.out.println("1秒任务执行结束");
                return "success";  
            }  
        });
        Future<String> f3 = executor1.submit(new Callable<String>() {  
          
            @Override  
            public String call() throws Exception {  
                Thread.sleep(2000);  
                System.out.println("2秒任务执行结束");
                return "success";  
            }  
        });
        
        
        //此处阻塞main线程  （也可以设置超时时间）
        Boolean f1_result =f1.get();
        String f2_result =f2.get();
     }
}</code></pre>
<!--底部-->
<pre>
<code>
//验证值是否为身份证
function isIdCard(value)
{
    if (value.length == 0)
    {
        return false;
    }
    var reg = /^\d{17}([Xx0-9])$/;
    if (!reg.test(value))
    {
      // alert("身份证号码格式错误");
        return false;
    }else{
        if(!idcard_checksum18(value)){//校验错误
            //alert("身份证号校验有误，请核对填写的身份证号码");
            return false;
        }
    
    }
    return true;
}

//18位身份证校验码有效性检查
function idcard_checksum18(idcard) {
    if (idcard.length != 18) {
        return false;
    }
    var idcard_base = idcard.substr(0, 17);
    if (idcard_verify_number(idcard_base) != idcard.substr(17, 1).toUpperCase()) {
        return false;
    } else {
        return true;
    }
}

// 计算身份证校验码，根据国家标准GB 11643-1999
function idcard_verify_number(idcard_base) {
    if (idcard_base.length != 17) {
        return false;
    }
//加权因子
    var factor = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
//校验码对应值
    var verify_number_list = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2');
    var checksum = 0;
    for (var i = 0; i < idcard_base.length; i++) {
        checksum += idcard_base.substr(i, 1) * factor[i];
    }
    var mod = checksum % 11;
    var verify_number = verify_number_list[mod];
    return verify_number;
}

//---------------------------------end----------------------------------------------------------------------

/****公共的js操作方法20170620 lwh****/
//校验手机号是否有效
//验证手机号码的有效性
function validatePhone(mobile){
	//验证手机号码的有效性
	if(mobile.length==0) 
    { 
       //alertMsg('请输入手机号码！'); 
       return false; 
    }     
    if(mobile.length!=11) 
    { 
        //alertMsg('请输入有效的手机号码！'); 
        return false; 
    } 
     
    var myreg = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/; 
    if(!myreg.test(mobile)) 
    { 
        //alertMsg('请输入有效的手机号码！'); 
        return false; 
    } 
	return true;
}

/**
 * 根据时间格式yyyyMMddHHmmss和flag类型返回预订的时间格式
 * flag为0 返回格式yyyy年MM月dd日 HH时mm分ss秒
 * flag为1 返回格式yyyy-MM-dd HH:mm:ss
 * flag为2 返回格式yyyy年MM月dd日
 */
function getFormatTime(time,flag){
	var time1='';
	var time2='';
	if(flag==0){
		time1 = time.substring(0,4)+'年'+time.substring(4,6)+'月'+time.substring(6,8)+'日 ';
		time2 = time.substring(8,10)+'时'+time.substring(10,12)+'分'+a.substring(12,14)+'秒';
	}
	if(flag==1){
		time1 = time.substring(0,4)+'-'+time.substring(4,6)+'-'+time.substring(6,8)+' ';
		time2 = time.substring(8,10)+':'+time.substring(10,12)+':'+time.substring(12,14);
	}
	if(flag==2){
		time1 = time.substring(0,4)+'年'+time.substring(4,6)+'月'+time.substring(6,8)+'日 ';
		time2 = '';
	}
	return time1+time2;
}

/**
 * 根据字符串日期格式，获取对应的毫秒值，只支持两种
 * yyyyMMdd  和  yyyyMMddHHmmss
 * @param time
 * @returns
 */
function getLong(time){
	var len = time.length;
	var str=time.substring(0,4)+"/"+time.substring(4,6)+"/"+time.substring(6,8);
	var t1 = new Date(Date.parse(str)).getTime();
	if(len==14){
		var hh = parseInt(time.substring(8,10));
		var mm = parseInt(time.substring(10,12));
		var ss = parseInt(time.substring(12,14));
		var t2 = (hh*60*60+mm*60+ss)*1000
		t1=t1+t2;
	}
	return t1;
}
/**
 * 两个数相乘（包括小数）
 * @param arg1
 * @param arg2
 * @returns {Number}
 */
function accMul(arg1,arg2)
{
var m=0,s1=arg1.toString(),s2=arg2.toString();
try{m+=s1.split(".")[1].length}catch(e){}
try{m+=s2.split(".")[1].length}catch(e){}
return Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m)
}
/**
 * 两个数想除 包括小数
 */
function accDiv(arg1,arg2){
	var t1=0,t2=0,r1,r2;
	try{t1=arg1.toString().split(".")[1].length}catch(e){}
	try{t2=arg2.toString().split(".")[1].length}catch(e){}
	with(Math){
	r1=Number(arg1.toString().replace(".",""))
	r2=Number(arg2.toString().replace(".",""))
	return (r1/r2)*pow(10,t2-t1);
	}
}

/**************JS输出文本（不识别JS） lwh 20180110******************/
function htmlEncode(html){
	  //1.首先动态创建一个容器标签元素，如DIV
  var temp = document.createElement ("div");
  //2.然后将要转换的字符串设置为这个元素的innerText(ie支持)或者textContent(火狐，google支持)
  (temp.textContent != undefined ) ? (temp.textContent = html) : (temp.innerText = html);
  //3.最后返回这个元素的innerHTML，即得到经过HTML编码转换的字符串了
  var output = temp.innerHTML;
  temp = null;
  return output;
}
/**************JS过滤特殊字符，有的话返回true lwh 20180110******************/
function  cleanXSS(value) {
  //You'll need to remove the spaces from the html entities below
 var re = new RegExp("['?;\"<>|~]","gm");
 return re.test(value);

}


</code>
</pre>
</body>
</html>