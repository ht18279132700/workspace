package cn.com.pcauto.wenda.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.gelivable.dao.SqlBuilder;
import org.gelivable.param.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.pcauto.wenda.entity.PhotoImport;
import cn.com.pcauto.wenda.entity.QuestionPhoto;
import cn.com.pcauto.wenda.util.Const;
import cn.com.pcauto.wenda.util.Pager;
import cn.com.pcauto.wenda.util.excel.AbsObj;

public class PhotoImportService extends BasicService<PhotoImport>{

	private static final Logger log = LoggerFactory.getLogger(PhotoImportService.class);
	
	public PhotoImportService(){
		super(PhotoImport.class);
	}
	
	public String photoImport(List<AbsObj> sources, long userId) {
		long beginTime = System.currentTimeMillis();
		int count = 0;
		int row = 1;
		int ignore = 0;
    	
		for (AbsObj source : sources) {
			row++;
			log.debug("正在处理第{}行导入图片数据...", row);

			String url = source.getString("原图url");
			if (StringUtils.isBlank(url)) {   
				ignore++;
				continue;
			}
			
			try {
				PhotoImport photoImport = new PhotoImport();
				photoImport.setUrl(url);
				photoImport.setCreateAt(new Date());
				photoImport.setCreateBy(userId);
				geliDao.create(photoImport);
			} catch (Exception e) {
				log.error("导入错误，定位行号: {}", row);
				continue;
			}
			count++;
		}

		double finalTime = (System.currentTimeMillis() - beginTime) / 1000;

		return String.format(
				"导入成功，新增了%s行图片数据，%s行忽略，耗时%s秒！", count, ignore, finalTime);
	}
	
	public Pager<PhotoImport> pager(int pageNo, int pageSize, QueryParam param){
		StringBuilder sb = new StringBuilder("SELECT id FROM ");
		sb.append(getTableName()).append(param.getWhereSql()).append(param.getOrderBy());
		return pager(pageNo, pageSize, sb.toString(), param.getParams().toArray());
	}

	public String exportPhoto(Date begin, Date end, int pageNo, int pageSize) {
		if (begin == null || end == null) {
			return null;
		}
		SqlBuilder sql = new SqlBuilder();
		sql.appendSql("select id, url from ").appendSql(getTableName());
		sql.appendSql(" where status = ").appendValue(Const.STATUS_PASS);
		sql.appendSql(" and create_at >= ").appendValue(begin);
		sql.appendSql(" and create_at <= ").appendValue(end);
		sql.appendSql(" limit ").appendValue((pageNo - 1) * pageSize);
		sql.appendSql(", ").appendValue(pageSize);
		List<Map<String, Object>> questionList = geliDao.getJdbcTemplate().queryForList(sql.getSql(), sql.getValues());
		
		StringBuilder data = new StringBuilder();
		for (Map<String, Object> map : questionList) {
			data.append(map.get("id")).append(",");
			data.append(map.get("url")).append(",");
        	data.append("\n");
		}
		return data.toString();
	}
	
	public List<QuestionPhoto> getQuestionPhotoByIds(List<Long> photoIds){
		List<QuestionPhoto> list = new ArrayList<QuestionPhoto>();
		for (long id : photoIds) {
			PhotoImport pi = findById(id);
			if(pi == null || pi.getStatus() == 0){
				continue;
			}
			QuestionPhoto qp = new QuestionPhoto();
			qp.setUrl(pi.getWdUrl());
			qp.setWidth(pi.getWidth());
			qp.setHeight(pi.getHeight());
			qp.setSize(pi.getSize());
			list.add(qp);
		}
		return list;
	}
}
