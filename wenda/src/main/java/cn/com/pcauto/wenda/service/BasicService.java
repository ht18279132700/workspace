/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.pcauto.wenda.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.gelivable.dao.GeliDao;
import org.gelivable.dao.GeliOrm;
import org.gelivable.dao.Mid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import cn.com.pcauto.wenda.util.Pager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.danga.MemCached.MemCachedClient;

/**
 *
 * @author xulin
 */
public class BasicService<T> {
    
    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    protected GeliDao geliDao;
    @Autowired
    protected GeliOrm geliOrm;
    
    @Autowired
    protected MemCachedClient mcc;
    
    private Class<T> type;
    
    public BasicService(Class<T> type) {
        this.type = type;
    }

    public BasicService() {
    }
    
    public  String getTableName(){
        return geliOrm.getTableName(type);
    }
    public String getTableName(long id){
        return geliOrm.getTableName(type,id);
    }
    T find(Object id){
        try{
            return geliDao.find(type,id);
        }catch(EmptyResultDataAccessException ex){
            return null;
        }
    }
    public  T findById(long id){
        if(id<=0){
            return null;
        }
        return find(id);
    }
    protected T find(long x,Object id){
        try{
            return geliDao.find(type,x,id);
        }catch(EmptyResultDataAccessException  ex){
            return null;
        }
    }
     public T findFirst(String sql,Object... params){
         try{
             return geliDao.findFirst(type, sql, params);
         }catch (DataAccessException ex){
             return null;
         }
    }
    /**
     * 创建实体
     * @param obj
     * @return 
     */
    public long create(Object obj){
        return geliDao.create(obj);
    }
    /**
     * 更新实体
     * @param obj
     * @return 
     */
    public  int update(T obj){
        return geliDao.update(obj);
    }
    /**
     * 更新实体指定字段
     * @param obj
     * @param fieldName
     * @return 
     */
    public  int update(T obj,String fieldName){
        return geliDao.update(obj,fieldName);
    }
    
    /**
     * 指定字段自增1
     * @param obj
     * @param fieldName
     * @return
     */
    public int incr(Object obj, String... fieldName){
    	return addNum(obj, fieldName, 1);
    }
    
    /**
     * 指定字段自减1
     * @param obj
     * @param fieldName
     * @return
     */
    public int decr(Object obj, String... fieldName){
    	return addNum(obj, fieldName, -1);
    }
    
    /**
     * 指定字段增加一个指定的数
     * @param obj
     * @param fieldName
     * @param num
     * @return
     */
    public int addNum(Object obj, String[] fieldName, int num){
    	String tableName = null;
    	Object value = geliOrm.getSplitFiledValue(obj);
    	if(value == null){
    		tableName = geliOrm.getTableName(obj.getClass());
    	}else{
    		long splitValue = 0;
    		if(value instanceof Date){
    			splitValue = ((Date)value).getTime();
    		}else{
    			try {
    				splitValue = Long.parseLong(value.toString());
				} catch (Exception e) {
				}
    		}
    		tableName = geliOrm.getTableName(obj.getClass(), splitValue);
    	}
    	
    	String keyColumn = geliOrm.getKeyColumn(obj.getClass());
    	if(tableName == null || keyColumn == null){
    		return 0;
    	}
    	String[] keyColumns = keyColumn.split(",");
    	List<Object> params = new ArrayList<Object>();
    	Object objectId = geliOrm.getObjectId(obj);
    	if(objectId instanceof Mid){
    		Mid mid = (Mid)objectId;
			try {
				Field f = mid.getClass().getDeclaredField("ids");
				f.setAccessible(true);
				Object[] ids = (Object[])f.get(mid);
				for (Object object : ids) {
					String idStr = object.toString();
					long id = 0;
					try {
						id = Long.valueOf(idStr);
					} catch (Exception e) {
					}
					if(id > 0){
						if(idStr.length() > 10){
							params.add(new Date(id));
						}else{
							params.add(id);
						}
					}else{
						params.add(idStr);
					}
				}
			} catch (Exception e) {
			}
    	}else{
    		params.add(objectId);
    	}
    	if(keyColumns.length != params.size()){
    		return 0;
    	}
    	StringBuilder sb = new StringBuilder("UPDATE ").append(tableName);
    	sb.append(" SET");
    	for (String f : fieldName) {
    		String column = geliOrm.getColumnByField(obj.getClass(), f);
    		sb.append(" ").append(column).append("=").append(column).append("+?,");
    		params.add(0, num);
		}
    	sb.delete(sb.length()-1, sb.length());
    	sb.append(" WHERE");
    	for (String kc : keyColumns) {
    		sb.append(" ").append(kc).append("=? AND");
		}
    	String sql = sb.substring(0, sb.length()-3);
    	int affectedRows = geliDao.getJdbcTemplate().update(sql, params.toArray());
    	if(affectedRows > 0){
    		geliDao.deleteCache(obj.getClass(), objectId);
    	}
    	return affectedRows;
    }
    
    /**
     * 物理删除
     * @param obj
     * @param id
     * @return 
     */
    public  int delete(long id){
        try {
            return geliDao.delete(type.newInstance(), id);
        } catch (Exception ex) {
            log.error("delete error :",ex);
            return 0;
        }
    }

    /**
     * 分表查询集合 
     * @param x
     * @param sql
     * @param params
     * @return 
     */
    protected List<T> list(long x,String sql,Object... params){
           return list(type,x,sql,params);
    }
    /**
     * 查询对象集合
     * @param sql
     * @param params
     * @return 
     */
    protected List<T> list(String sql,Object... params){
            return list(type,sql,params);
    }
    /**
     * 查询对象集合
     * @param sql
     * @param params
     * @return 
     */
    protected List<T> list(Class<T> type,String sql,Object... params){
        try{
            return geliDao.list(type,sql,params);
        }catch(EmptyResultDataAccessException ex){
            return Collections.emptyList();
        }
    }
     /**
     * 查询对象集合
     * @param sql
     * @param params
     * @return 
     */
    protected List<T> list(Class<T> type,long x,String sql,Object... params){
        try{
            return geliDao.list(type,x,sql,params);
        }catch(EmptyResultDataAccessException ex){
            return Collections.emptyList();
        }
    }
    /**
     * 分表 分页 查询
     * @param x
     * @param pageNo
     * @param pageSize
     * @param sql
     * @param params
     * @return 
     */
    protected List<T> list(long x,int pageNo,int pageSize ,String sql,Object... params){
        try{
            return geliDao.page(type,x,sql, pageNo, pageSize,params);
        }catch(EmptyResultDataAccessException ex){
            return Collections.emptyList();
        }
    }
    /**
     * 分页查询List
     * @param pageNo
     * @param pageSize
     * @param sql
     * @param params
     * @return 
     */
    protected List<T> list(int pageNo,int pageSize ,String sql,Object... params){
        try{
            return geliDao.page(type,sql, pageNo, pageSize,params);
        }catch(EmptyResultDataAccessException ex){
            return Collections.emptyList();
        }
    }
    public  List<T> list(Object[] ids){
        try{
            return geliDao.list(type, ids);
        }catch(EmptyResultDataAccessException ex){
            return Collections.emptyList();
        }
    }
    
    public  List<T> list(Long[] ids){
        try{
            return geliDao.list(type, ids);
        }catch(EmptyResultDataAccessException ex){
            return Collections.emptyList();
        }
    }
    public List<T> list(long x,Object[] ids){
        try{
            return geliDao.list(type,x,ids);
        }catch(EmptyResultDataAccessException ex){
            return Collections.emptyList();
        }
    }
    /**
     * 分页 分表 查询Pager
     * @param x
     * @param pageNo
     * @param pageSize
     * @param sql
     * @param params
     * @return 
     */
    protected Pager<T> pager(long x,int pageNo,int pageSize ,String sql,Object... params){
         Pager<T> pager = count(sql, params);
         pager.setPageNo(pageNo);
         pager.setPageSize(pageSize);
        try{
            if(pager.getTotal() > 0){
                pager.setResultList(list(x,pageNo,pageSize,sql, params));
            }else{
                pager.setResultList(Collections.<T> emptyList());
            }
        }catch(Exception ex){
            log.error("data not found: {}, value: {}",sql,params);
        }
        return pager;
    }
    /**
     * 分页查询pager
     * @param pageNo
     * @param pageSize
     * @param sql
     * @param params
     * @return 
     */
    protected Pager<T> pager(int pageNo,int pageSize ,String sql,Object... params){
        try{
            Pager<T> pager = count(sql, params);
            pager.setPageNo(pageNo);
            pager.setPageSize(pageSize);
            if(pager.getTotal() > 0){
                pager.setResultList(list(pageNo,pageSize,sql, params));
            }else{
                pager.setResultList(Collections.<T> emptyList());
            }
            return pager;
        }catch(Exception ex){
        	log.error("data not found: {}, value: {}",sql,params);
            return new Pager<T>();
        }
    }
    /**
     * 查询结果数量
     * @param sql
     * @param params
     * @return 
     */
    protected Pager<T> count(String sql,Object... params){
        Pager<T> pager = new Pager<T>();
        String countSql = sql.replace(sql.subSequence(0,sql.toLowerCase().indexOf("from")),"select count(1) ");
        int orderIndex = countSql.toLowerCase().lastIndexOf("order by");
        if(orderIndex>0){
            countSql = countSql.substring(0, orderIndex);
        }
        int count = geliDao.count(countSql, params);
        pager.setTotal(count);
        return pager;
    }
    /**
     * jsonArray 转 list
     * @param array
     * @return 
     */
    public List<T> arrayToList(JSONArray array){
        if(array==null){
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<T>(array.size());
        for(int i = 0 ; i < array.size();i++){
            JSONObject obj = array.getJSONObject(i);
            list.add(GeliDao.string2Object(type, obj.toJSONString()));
        }
        return list;
    }
    /**
     * LIST转JSONARRAY
     * @param list
     * @return 
     */
    public JSONArray listToarray(List<T> list){
        JSONArray array  = new JSONArray(list.size());
        for (T t : list) {
            array.add(geliDao.object2JSON(t));
        }
        return array;
    }
    
    public T findDb(Object id){
    	try {
    		return geliDao.findDb(type, id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
    }
    
    public String getCacheKey(Object id){
    	return GeliDao.getCacheKey(type, id);
    }
    
    public void removeFromCache(Object id){
    	geliDao.deleteCache(type, id);
    }
}
