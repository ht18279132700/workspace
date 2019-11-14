package cn.com.pcauto.wenda.util;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author wanganning
 */
public class JsonResult {

    public final static int STATUS_ERROR = -1;		//处理错误RTE
    public final static int STATUS_SUCCESS = 0;		//成功
    public final static int STATUS_AUTH_FAILED = 1;	//没有权限 没有登录
    public final static int STATUS_ILLEGAL_ARGUMENT = 2; //参数错误
    public final static int STATUS_ILLEGAL_STATE = 3;	//不符合处理条件
    private int status;
    private String desc;
    private String result;
    private String callback;
    private boolean windowName;

    public JsonResult() {
    }

    public JsonResult(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setWindowName(boolean windowName) {
        this.windowName = windowName;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDesc() {
        return desc;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public void setWindowName(String windowName) {
        if ("true".equals(windowName) || "1".equals(windowName)) {
            setWindowName(true);
        }
    }

    public boolean isSuccess() {
        return this.status == JsonResult.STATUS_SUCCESS;
    }

    public String toString2() {
        StringBuilder output = new StringBuilder();
        JSONObject json = new JSONObject();
        json.put("status", status);
        json.put("desc", desc);
        json.put("result", result);
        output.append(json.toJSONString());
        if (!isBlank(callback)) {
            output.insert(0, "(").insert(0, callback).append(");");
        }
        if (windowName) {
            output.insert(0, "<script>window.name='").append("';</script>");
        }
        return output.toString();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        if (!isBlank(result)) {
            output.append(result);
        } else {
            JSONObject json = new JSONObject();
            json.put("status", status);
            if (!isBlank(desc)) {
                json.put("desc", desc);
            }
            output.append(json.toString());
        }

        if (!isBlank(callback)) {
            output.insert(0, "(").insert(0, callback).append(");");
        }

        if (windowName) {
            output.insert(0, "<script>window.name='").append("';</script>");
        }
        return output.toString();
    }

    private static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }
}
