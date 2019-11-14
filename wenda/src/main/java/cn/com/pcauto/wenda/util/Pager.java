package cn.com.pcauto.wenda.util;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 分页工具类
 *
 * @author guoqiang.
 * @Time 2014-1-8
 * @Version 1.0
 * @param <T>
 */
@SuppressWarnings({"serial"})
public class Pager<T> implements Serializable {

    /**
     * 页号
     */
    private int pageNo;
    /**
     * 每页数量
     */
    private int pageSize;
    /**
     * 总数量
     */
    private int total;
    /**
     * 是否为所有列表
     */
    private boolean isAllList = false;
    /**
     * 查询后的结果集
     */
    private List<T> resultList;

    /**
     *
     * @param resultList
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Pager(List resultList) {
        // 这个是给查出全部结果集的list用的构建方法
        this.resultList = resultList;
        isAllList = true;
        if (resultList != null && !resultList.isEmpty()) {
            total = resultList.size();
        }
    }

    /**
     * empty constructor
     */
    public Pager() {
    }

    /**
     * 总页数
     *
     * @return
     */
    public int getPageCount() {
        return (total == 0 || pageSize <= 0) ? 1 : total / pageSize + (total % pageSize == 0 ? 0 : 1);
    }

    /**
     * 页号
     *
     * @return
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * 每页数量
     *
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @return
     */
    public List<T> getResultList() {
        if (isAllList && resultList != null && !resultList.isEmpty()) {
            int fromIndex = (pageNo - 1) * pageSize;
            int toIndex = pageNo * pageSize;
            if (fromIndex >= total) {
                return null;
            } else if (toIndex > total) {
                toIndex = total;
            }
            return resultList.subList(fromIndex, toIndex);
        }
        return resultList;
    }

    /**
     * @return
     */
    public int getTotal() {
        return total;
    }

    /**
     * @param pageNo
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @param total
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * @param resultList
     */
    public void setResultList(List<T> resultList) {
        this.resultList = resultList;
    }

    public static <T> Pager<T> buildEmptyPager() {
        return new Pager<T>();
    }

    public JSONObject toJSONObject(JSONArray array) {
        JSONObject obj = new JSONObject();
        obj.put("pageNo", pageNo);
        obj.put("pageSize", pageSize);
        obj.put("total", total);
        obj.put("resultList", array);
        obj.put("isAllList", isAllList);
        return obj;
    }
    
    
    /**
     * 对list进行分页
     * @param list
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static <T> Pager<T> pagerList(List<T> list, int pageNo, int pageSize) {
    	if (list == null || list.isEmpty()) {
    		return new Pager<T>();
    	}
    	
    	Pager<T> pager = new Pager<T>(list);
    	pager.setPageNo(pageNo);
    	pager.setPageSize(pageSize);
    	pager.setTotal(list.size());
		
		return pager;
    }
}
