package cn.com.pcauto.wenda.util;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.danga.MemCached.MemCachedClient;

/**
 *
 * @email chensy0203@163.com
 * @author chensy
 */
public class McUtils {
    
    
    private MemCachedClient mcc;
    private String key;

    public McUtils(MemCachedClient mcc, String keyFmt, Object... args) {
        this.mcc = mcc;
        this.key = String.format(keyFmt, args);
    }

    public JSONArray get() {
        String jsonStr = (String) mcc.get(key);
        if (StringUtils.isNotEmpty(jsonStr)) {
            return JSONArray.parseArray(jsonStr);
        }
        return null;
    }
       
    /**
     * 适用于转换 List<T>放入JSONArray进行序列化后，再反序列化的操作
     * 
     * @param clazz
     * @return
     */
    public <T> List<T> getList(Class<T> clazz) {
    	String jsonStr = (String) mcc.get(key);
    	if (StringUtils.isNotBlank(jsonStr)) {
    		// 直接把List放入JSONArray中进行序列化后，会多了一层"[]"
    		// 把前后的“[]”去掉
    		jsonStr = jsonStr.substring(1, jsonStr.length() - 1);
    		return JSONArray.parseArray(jsonStr, clazz);
    	}
    	
    	return null;
    }
    
    public JSONObject getJSONObject(){
        String jsonStr =(String) mcc.get(key);
        if(StringUtils.isNotBlank(jsonStr)){
            return JSONObject.parseObject(jsonStr);
        }
        return null;
    }
    
    public boolean set(JSONArray arr) {
        if(arr.isEmpty()){
            return false;
        }
        return mcc.set(key, arr.toJSONString());
    }
    
    public boolean set(Object obj, Date expirt) {
    	return mcc.set(key, obj, expirt);
    }
    
    /**
     * 设置缓存
     * @param arr
     * @param expirt 有效时间
     * @return
     */
    public boolean set(JSONArray arr, Date expirt) {
        if(arr.isEmpty()){
            return false;
        }
        return mcc.set(key, arr.toJSONString(), expirt);
    }
    
    public boolean set(JSONObject obj){
        if(obj.isEmpty()){
            return false;
        }
        return mcc.set(key, obj.toJSONString());
    }
    
    /**
     * 将list列表放入MC
     * 
     * <p> 使用JSONArray进行序列化
     * 
     * @param list
     * @return
     */
    public boolean set(List<?> list) {
    	if (list == null || list.isEmpty()) {
    		return false;
    	}
    	JSONArray array = new JSONArray();
    	array.add(list);
    	return this.set(array);
    }
    
    /**
     * 将列表设置到MC缓存
     * @param list
     * @param expirt 有效时间
     * @return
     */
    public boolean set(List<?> list, Date expirt) {
    	if (list == null || list.isEmpty()) {
    		return false;
    	}
    	JSONArray array = new JSONArray();
    	array.add(list);
    	return this.set(array, expirt);
    }
    
    /**
     * 判断key是否存在
     * 
     * @param key
     * @return
     */
    public boolean keyExists() {
    	return mcc.keyExists(key);
    }
    
    /**
	 * 删除
	 * @param key
	 * @return
	 */
	public boolean delete() {
		return mcc.delete(key);
	}
}
