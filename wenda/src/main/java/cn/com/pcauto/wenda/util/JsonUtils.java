package cn.com.pcauto.wenda.util;

import com.alibaba.fastjson.JSONObject;

public class JsonUtils {

	private JsonUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param code 状态码
	 * @param message 信息
	 * @return
	 */
	public static JSONObject getJson(int code, String message) {
		JSONObject json = new JSONObject();

		json.put("code", code);
		json.put("message", message);

		return json;
	}
	
	public static JSONObject successJson(String message) {
		return getJson(SystemConstant.SUCCESS, message);
	}
	
	public static JSONObject errorJson(String message) {
		return getJson(SystemConstant.ERROR, message);
	}
	
	/**
	 * 权限问题
	 * @param message
	 * @return
	 */
	public static JSONObject permissionsErrorJson(String message) {
		return getJson(SystemConstant.STATUS_PERMISSIONS_ERROR, message);
	}
	
	/**
	 * 数据错误
	 * @param message
	 * @return
	 */
	public static JSONObject dataErrorJson(String message) {
		return getJson(SystemConstant.STATUS_DATA_ERROR, message);
	}
	
	/**
	 * 未登录
	 * @return
	 */
	public static JSONObject noLoginJson() {
		return getJson(SystemConstant.STATUS_NOLOGIN_ERROR, "用户未登录");
	}
	
	/**
	 * 
	 * @param code
	 * @param message
	 * @return
	 */
	public static String getJsonStr(int code, String message) {
		return getJson(code, message).toJSONString();
	}
	
	/**
	 * 
	 * @param json
	 * @param code
	 * @return
	 */
	public static JSONObject getReturnJson(JSONObject json, int code) {
		if (json == null) {
			json = getJsonCode(code);
		} else {
			json.put("code", code);
		}
		return json;
	}
	
	/**
	 * 
	 * @param code
	 * @return
	 */
	public static JSONObject getJsonCode(int code) {
		JSONObject json = new JSONObject();
		json.put("code", code);
		return json;
	}
	
	/**
	 * 
	 * @param json
	 * @return
	 */
	public static JSONObject successJson(JSONObject json) {
		return getReturnJson(json, SystemConstant.SUCCESS);
	}
}
