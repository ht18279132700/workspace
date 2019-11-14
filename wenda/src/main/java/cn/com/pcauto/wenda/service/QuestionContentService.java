package cn.com.pcauto.wenda.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.apache.commons.lang.StringUtils;
import org.gelivable.dao.SqlBuilder;

import cn.com.pcauto.wenda.entity.QuestionContent;

public class QuestionContentService extends BasicService<QuestionContent> {

	private final static String MC_KEY = "question-content-";
	
	public QuestionContentService(){
		super(QuestionContent.class);
	}
	
	public void create(long qid, String content){
		if(qid <= 0 || StringUtils.isBlank(content)){
			return;
		}
		StringBuilder sql = new StringBuilder("INSERT INTO ").append(getTableName(qid));
		sql.append("(qid,seq,content) values(?,?,?)");
		List<Object[]> params = new ArrayList<Object[]>();
		for (int i = 0,s=1; i < content.length(); i+=128,s++) {
			int end = Math.min(i+128, content.length());
			params.add(new Object[]{qid,s,content.substring(i, end)});
		}
		geliDao.getJdbcTemplate().batchUpdate(sql.toString(), params);
	}
	
	public String getContent(long qid){
		if(qid <= 0){
			return "";
		}
		
		String content = (String) mcc.get(MC_KEY + qid);
		if(StringUtils.isNotBlank(content)){
			return content;
		}
		
		StringBuilder sql = new StringBuilder("SELECT content FROM ").append(getTableName(qid));
		sql.append(" WHERE qid=? ORDER BY seq ASC");
		content = geliDao.getJdbcTemplate().query(sql.toString(), new Object[]{qid}, new ResultSetExtractor<String>(){
			@Override
			public String extractData(ResultSet rs) throws SQLException,DataAccessException {
				StringBuilder sb = new StringBuilder();
				while(rs.next()){
					sb.append(rs.getString(1));
				}
				return sb.toString();
			}
		});
		mcc.set(MC_KEY + qid, content);
		return content;
	}

	public void updateContent(long qid, String content) {
		deleteContent(qid);
		create(qid, content);
	}
	public boolean deleteContent(long qid){
		SqlBuilder sql = new SqlBuilder();
		sql.appendSql("DELETE FROM ").appendSql(getTableName(qid));
		sql.appendSql(" WHERE qid = ").appendValue(qid);
		int flag = geliDao.getJdbcTemplate().update(sql.getSql(), sql.getValues());
		if (flag > 0) {
			return mcc.delete(MC_KEY + qid);
		}
		return false;
	}
	
}
