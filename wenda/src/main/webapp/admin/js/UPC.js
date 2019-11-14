var retCodeDict = new Array();
retCodeDict[0] = "成功";
retCodeDict[1] = "事务编码未指定";
retCodeDict[2] = "事务编码不存在";
retCodeDict[3] = "创建事务失败";
retCodeDict[4] = "非法的参数";
retCodeDict[5] = "上传失败";
retCodeDict[6] = "处理失败";
retCodeDict[7] = "事务已关闭";
retCodeDict[8] = "删除事务失败";
retCodeDict[9] = "资源删除失败";
retCodeDict[10] = "未指定资源";
retCodeDict[11] = "资源更新失败";
retCodeDict[12] = "用户未登录";
retCodeDict[13] = "非法的状态";
retCodeDict[14] = "文件大小超出规定";
retCodeDict[15] = "未指定应用编码";
retCodeDict[100] = "未知错误";
retCodeDict[702] = "参数不正确";
var UPC = {
    upcPath: null,
    application: null,
    windowname: null,
    command: null,
    sessionCookieName: null,
    sessionCookieValue: null,
    retCodeJson: function(c) {
        eval("var errResult={retCode:" + c + ",desc:'" + this.retCodeResult[c] + "'}");
        return errResult
    },
    init: function(a) {
        this.upcPath = a.upcPath || "";
        this.application = a.application || null;
        this.windowname = a.windowname || 1;
        this.command = a.command || null;
        this.sessionCookieName = a.sessionCookieName || "common_session_id";
        this.sessionCookieValue = this.Util.getCookie(this.sessionCookieName)
    },
    overInit: function(b, a) {
        if (a == "") {
            return true
        }
        if (b == "application") {
            this.application = a;
            return true
        } else {
            if (b == "windowname") {
                this.windowname = a;
                return true
            } else {
                if (b == "command") {
                    this.command = a;
                    return true
                } else {
                    if (b == "sessionCookieName") {
                        this.sessionCookieName = a;
                        this.sessionCookieValue = this.Util.getCookie(this.sessionCookieName);
                        return true
                    }
                }
            }
        }
        return false
    },
    retCodeResult: function(a) {
        return retCodeDict[a]
    },
    applicationPara: function() {
        return this.application == null ? "": "&application=" + this.application
    },
    commandPara: function() {
        if (this.command == null) {
            return ""
        }
        var a = "";
        if (typeof(this.command) == "object") {
            for (var b = 0; b < this.command.length; b++) {
                a += ("&command=" + this.command[b])
            }
        } else {
            a = this.command
        }
        return a
    },
    sessionCookiePara: function() {
        return this.sessionCookieName == null ? "": "&" + this.sessionCookieName + "=" + this.sessionCookieValue
    },
    windownamePara: function() {
        return this.windowname == null ? "": "&windowname=" + this.windowname
    },
    post: function(c) {
        var f = c.url || null;
        var a = c.data || null;
        if (f == null || a == null) {
            return this.retCodeJson(702)
        }
        var e = this.Util.praseParam(a);
        if (e == null) {
            return this.retCodeJson(702)
        }
        f = this.upcPath + f + "?" + this.applicationPara() + this.windownamePara() + this.commandPara() + this.sessionCookiePara() + e;
        var g = null;
        if (c.complete && typeof(c.complete) == "function") {
            g = c.complete
        }
        if (c.before && typeof(c.before) == "function") {
            c.before()
        }
        var b = c.file || null;
        var d = c.fileData || null;
        UPC.WindownameUtil.send(f, b, g, d)
    },
    WindownameUtil: {
        iframeLoadStep: null,
        userCallback: null,
        send: function(d, a, h, g) {
            this.iframeLoadStep = 1;
            this.userCallback = h;
            var f = (a != null && (typeof(a) == "object" || typeof(a) == "string")) ? "encType='multipart/form-data'": "";
            var c = jQuery('<form action="" id="windownameFormId" ' + f + ' name="windownameFormId" method="POST" target="windownameFrame" style="display:none"><span id="windownameSpan"></span></form>');
            jQuery(c).appendTo("body");
            if (a != null && typeof(a) == "object") {
                for (var e = 0; e < a.length; e++) {
                    var b = jQuery("#" + a[e]);
                    jQuery(b).attr("id", "windownameFile");
                    jQuery(b).attr("name", "windownameFile");
                    jQuery(b).appendTo(c)
                }
            }
            if (g != null) {
                jQuery("<input type='hidden' name='fileData'/>").val(g).appendTo(c)
            }
            jQuery("#windownameFormId").attr("action", d);
            jQuery("#windownameSpan").append('<iframe name="windownameFrame" id="windownameFrame" style="display:none"></iframe>');
            jQuery("#windownameFrame").bind("load",
            function(i) {
                UPC.WindownameUtil.getData()
            });
            jQuery("#windownameFormId").submit()
        },
        getData: function() {
            switch (this.iframeLoadStep) {
            case 1:
                this.iframeLoadStep++;
                document.getElementById("windownameFrame").contentWindow.location.href = "/admin/proxy.txt";
                break;
            case 2:
                var jret = document.getElementById("windownameFrame").contentWindow.name;
                this.clearup();
                eval("var result = " + jret);
                if (this.userCallback && typeof(this.userCallback) == "function") {
                    this.userCallback(result)
                }
                break
            }
        },
        clearup: function() {
            document.getElementById("windownameFrame").contentWindow.close();
            jQuery("#windownameFrame").unbind("load");
            jQuery("#windownameSpan").empty();
            jQuery("#windownameFile").remove();
            jQuery("#windownameFormId").remove()
        }
    },
    Util: {
        praseParam: function(b) {
            if (typeof(b) == "number" || typeof(b) == "boolean" || typeof(b) == "function" || typeof(b) == "undefined") {
                return null
            }
            if (typeof(b) == "string" && b.length == 0) {
                return null
            }
            if (typeof(b) == "string") {
                return b
            }
            if (b && typeof(b) == "object") {
                var c = "";
                for (na in b) {
                    var a = b[na];
                    if (UPC.overInit(na, a)) {
                        continue
                    }
                    if (typeof(a) == "object") {
                        for (cm in a) {
                            c += ("&" + na + "=" + cm)
                        }
                    } else {
                        c += ("&" + na + "=" + a)
                    }
                }
                return c
            }
            return null
        },
        getCookie: function(b) {
            var a = document.cookie.match(new RegExp("(^| )" + b + "=([^;]*)(;|$)"));
            if (a != null) {
                return unescape(a[2])
            }
            return null
        }
    }
};