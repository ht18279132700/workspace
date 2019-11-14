package cn.com.pcauto.wenda.service;

import java.util.List;

import org.gelivable.param.QueryParam;

import cn.com.pcauto.wenda.entity.AnswerImport;

public class AnswerImportService extends BasicService<AnswerImport> {
	
	public AnswerImportService(){
		super(AnswerImport.class);
	}
	
	public int[] createAnswerImports(List<Object[]> list){
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ").append(getTableName())
		.append(" (qiid,content,answer_time,nickname,create_by,create_at) VALUES(?,?,?,?,?,?)");
		return geliDao.getJdbcTemplate().batchUpdate(sql.toString(), list);
	}
	/**
	 * 根据导入问题的ID,获取导入的答案
	 * @param qiid
	 * @return
	 */
	public List<AnswerImport> listAnswerImport(long qiid){
		QueryParam param = new QueryParam();
		param.and("qiid", qiid);
		StringBuilder sb = new StringBuilder("SELECT id FROM ");
		sb.append(getTableName()).append(param.getWhereSql()).append(param.getOrderBy());
		return list(sb.toString(), param.getParams().toArray());
	}

}
