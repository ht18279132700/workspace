<script type="text/javascript">
    function pagerGo(o,e){
        o.value=o.value.replace(/[^\d]/g,'');
        var pNum = o.value;
        if(pNum == null || pNum == '' || pNum == undefined || pNum == 0) {
            pNum = 1;
        }
        pNum = pNum > parseInt('${pager.pageCount}') ? parseInt('${pager.pageCount}') : pNum;
        if((window.event && e.keyCode == 13) || (e.which && e.which == 13)) {
            var url = $(o).parent().parent().find("a").eq(0).attr("href");
            url = url.replace(/(^|&)pageNo=\d+/ig, '');
            url = url + (url.indexOf("?") > 0 ?  '&' : '?') + 'pageNo=' + pNum;
            window.location.href = url;
        }
    }
</script>