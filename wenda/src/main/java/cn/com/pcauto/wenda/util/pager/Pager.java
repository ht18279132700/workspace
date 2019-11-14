package cn.com.pcauto.wenda.util.pager;

import java.util.List;
/**
 * 分页对象
 */
public class Pager<T> {

    private int pageNo;		//当前页
    private int totalSize;	//所有记录数
    private int pageSize;	//每页记录数
    private int totalPage;	//总页数
    private List<T> resultList;	//结果集

    public Pager() {
    }

    public Pager(int pageNo, int pageSize, int totalSize) {
        this.pageSize = pageSize;
        this.totalSize = totalSize;
        if (totalSize <= pageSize) {
            this.pageNo = 1;
            this.totalPage = 1;
        } else {
            this.pageNo = pageNo;
            this.totalPage = totalSize / pageSize + (totalSize % pageSize == 0 ? 0 : 1);
        }
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getResultList() {
        return resultList;
    }

    public void setResultList(List<T> resultList) {
        this.resultList = resultList;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public int getPageCount() {
        return totalSize / pageSize + (totalSize % pageSize == 0 ? 0 : 1);
    }
}
