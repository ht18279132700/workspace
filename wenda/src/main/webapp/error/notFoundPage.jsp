<%@ page contentType="text/html;charset=GBK" pageEncoding="GBK" session="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
        <meta content="always" name="referrer">
        <title>网站群404</title>
        <style>
            body,h1,h2,h3,h4,blockquote,p,dl,dt,dd,ul,ol,li,th,td,form,fieldset,legend,button,input,textarea{margin:0;padding:0}body,button,input,select,textarea{font:12px/1.75 \5b8b\4f53,arial,sans-serif}button,input,select,textarea,h1,h2,h3,h4{font-size:100%}em,i{font-style:normal}ol,ul{list-style:none}table{border-collapse:collapse;border-spacing:0}img,fieldset{border:0;vertical-align:middle}a{color:#333;text-decoration:none;zoom:1;outline:0}a:hover{color:#f60;text-decoration:underline}.fl,.mark{float:left}.fr,.subMark{float:right}.spanclass,.contentdiv{display:none}.f14{font-size:14px}.mb10{margin-bottom:10px}img{vertical-align:top}body{background:#fff;text-align:left;color:#333}.content{width:1000px;margin:auto;background:#fff;text-align:left}.clearfix:after{content:"\20";clear:both;height:0;display:block;overflow:hidden}.clearfix,.ivy230{*zoom:1}
            body #navibar{border-bottom: none;}
            .header{background: #F3F3F3;height: 29px;border-bottom: 1px solid #E3E3E3;}
            .banner{height: 366px; background: url(//www1.pcauto.com.cn/2013/404/banner.png) no-repeat center top;}
            .ttext{width: 795px; margin: 0 auto; padding-top: 108px;}
            .ttext h1{display: block; padding-left: 297px; height: 46px;font: bold 26px/46px "微软雅黑";}
            .ttext p{padding-left: 320px;font: bold 14px/24px "微软雅黑"; height: 24px;}
            .wrap{width: 795px; margin: 0 auto;}
            .green,.green a,a.green{color:#33b168}
            .blue,.blue a,a.blue{color: #05a}
            .orange,.orange a,a.orange{color: #f60}
            .red,.red a,a.red{color: #c00}
            .green a:hover,a.green:hover,.blue a:hover,a.blue:hover,.orange a:hover,a.orange:hover,.red a:hover,a.red:hover{color: #f60}


            .th{height: 39px; line-height: 39px;}
            .th .mark{font:bold 14px/39px "微软雅黑"}
            .tb{padding-bottom: 10px; border-bottom: 1px solid #dedede;width: 795px; overflow: hidden;}

            .ulPic{width:1025px;}
            .ulPic li{float:left;display:inline;width:180px;line-height:24px;border-bottom: none;margin:0 25px 0 0;overflow:hidden;}
            .ulPic i{display: block; overflow: hidden;}
            .ulPic .tit{text-align: center; height: 28px; line-height: 30px; font-size: 14px; font-weight: bold;}
            .ulPic .pri{text-align: center;height: 28px; line-height: 22px; border-bottom: 1px dotted #d7d7d7;}
            .ulPic .info{height: 56px; line-height: 28px;}
            .ulPic .info span{float: left; height: 28px;}
            .ulPic .info .blue{overflow:hidden; width: 180px;display: block;height:28px;}
            .ulPic .info .d{color: #999; text-decoration: line-through; margin-right: 5px;}
            .ulPic .info em{float: right;height: 28px; }
            .ulPic .info em a{color: #008000}
            .ulPic .info em a:hover{color: #f60;}
            body #footer .copyRight table .trTop a{margin: 0 10px;}
        </style>
    </head>

    <body class="auto">
        <script src="//www.pconline.com.cn/_hux_/auto/default/index.js"></script>
        <script>
            document.write("<img style=display:none src=//count.pcauto.com.cn/count.php?channel=1426&screen="+screen.width+"*"+screen.height+"&refer="+encodeURIComponent(document.referrer)+"&anticache="+new Date().getTime()+"&url="+encodeURIComponent(location.href)+" >");
        </script>
        <div class="header"><script class=" defer" src="//www.pcauto.com.cn/global/navibar/index.html"></script></div>
        <div class="banner">		
            <div class="ttext">
                <h1>抱歉，您访问的页面暂时没能找到...TOT</h1>
                <p><a href="//www.pcauto.com.cn" target="_self" id="newUrl" class="newUrl green">太平洋汽车网首页>></a></p>			
            </div>
        </div>
        <div class="wrap">

            <div class="th clearfix">
                <span class="mark">为表歉意，我们准备了<em class="green">给力的购车优惠！</em></span>
                <span class="subMark">想了解更多的优惠详情，<a href="//price.pcauto.com.cn/market/" class="blue">马上去看看>></a></span>
            </div>
            <div class="tb" id="modelList" style="display:none;">
            </div>
            <div style="text-align:center; margin-top:10px;"><script class=" defer" src="//jwz.3conline.com/adpuba/auto_default_show?id=auto.bcy.dbtl.&media=js&channel=inline&trace=1"></script></div>
        </div>	
        <div class="footer">
            <script src="//www.pcauto.com.cn/global/s_footer/index.html"></script>
        </div>
        <script type="text/javascript" src="//js.3conline.com/js/jquery-1.4.2.min.js"></script>
        <script src="//js.3conline.com/pcauto/price2010/js/auto_dealers_area.js"></script>
        <script src="//price.pcauto.com.cn/js/fav_car.js"></script>
        <script>
            var newUrl = document.getElementById("newUrl");
            var curUrl = window.location.hostname;
            var linkName='太平洋汽车网首页';
            if(curUrl=='www.pcauto.com.cn'){
                linkName='太平洋汽车网首页';
            };
            if(curUrl=='price.pcauto.com.cn'){
                linkName='报价库首页';
            };
            if(curUrl=='arch.pcauto.com.cn'){
                curUrl='www.pcauto.com.cn';
                linkName='太平洋汽车网首页';
            };
            if(curUrl=='bbs.pcauto.com.cn'){
                linkName='论坛首页';
            };
            if(curUrl=='v.pcauto.com.cn'){
                linkName='视频频道首页';
            };
            newUrl.href="//"+curUrl;
            newUrl.innerHTML="返回"+linkName;
            function display(data){
                if(data.length>0){
                    var html = '<ul class="ulPic clearfix">';
                    for(var i=0;i<data.length;i++){
                        var item = data[i];
                        var marketUrl = '//price.pcauto.com.cn/market/r1/sg'+item.serialGroupId+'/#ad=4210';
                        var newsUrl = '//price.pcauto.com.cn/market/'+item.dealerId+'-'+item.dealerNewsId+'.html#ad=4210';
                        html += '<li><i class="pic"><a href="'+ marketUrl +'" target="_blank"><img width="180" height="135" src="'+item.logo+'"></a></i>'
                            +'<i class="tit blue"><a target="_self" href="'+ marketUrl +'">'+item.serialGroupName+'</a></i><i class="info"><a class="blue" target="_self" href="'+ newsUrl +'">'
                            + item.modelName +'</a><span class="d">'+ item.price +'万</span><span class="red"><a target="_self" href="'+ newsUrl +'">'
                            + item.discountPrice +'万</a></span><em><a target="_self" href="'+ newsUrl +'">↓降'+item.maxDiscount*1.0/10000
                            +'万</a></em></i></li>';
                    }
                    html += '</ul>';
                    $('#modelList').html(html).show();
                }
            }
	
            $(function(){
                var rs = getPCautoArea();
                var _areaId = area[0];
                _areaId = _areaId != "" ? _areaId : 0;
                getCarsFromCookie();
                var serialGroupIds = getCarSerialGroupIds();
                $.getScript("//price.pcauto.com.cn/interface/serial_json_for_404.jsp?serialGroupIds="+serialGroupIds+"&regionId="+_areaId);
            });
        </script>
        <script>_submitIvyID();</script>
    </body>
</html>