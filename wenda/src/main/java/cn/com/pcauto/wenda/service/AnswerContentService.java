package cn.com.pcauto.wenda.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.gelivable.dao.SqlBuilder;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import cn.com.pcauto.wenda.entity.AnswerContent;

public class AnswerContentService extends BasicService<AnswerContent> {

	private final static String MC_KEY = "answer-content-";
	
	public AnswerContentService(){
		super(AnswerContent.class);
	}
	
	public void create(long aid, String content){
		if(aid <= 0 || StringUtils.isBlank(content)){
			return;
		}
		StringBuilder sql = new StringBuilder("INSERT INTO ").append(getTableName(aid));
		sql.append("(aid,seq,content) values(?,?,?)");
		List<Object[]> params = new ArrayList<Object[]>();
		for (int i = 0,s=1; i < content.length(); i+=128,s++) {
			int end = Math.min(i+128, content.length());
			params.add(new Object[]{aid,s,content.substring(i, end)});
		}
		geliDao.getJdbcTemplate().batchUpdate(sql.toString(), params);
	}
	
	public String getContent(long aid){
		if(aid <= 0){
			return "";
		}
		
		String content = (String) mcc.get(MC_KEY + aid);
		if(StringUtils.isNotBlank(content)){
			return content;
		}
		
		StringBuilder sql = new StringBuilder("SELECT content FROM ").append(getTableName(aid));
		sql.append(" WHERE aid=? ORDER BY seq ASC");
		content = geliDao.getJdbcTemplate().query(sql.toString(), new Object[]{aid}, new ResultSetExtractor<String>(){
			@Override
			public String extractData(ResultSet rs) throws SQLException,DataAccessException {
				StringBuilder sb = new StringBuilder();
				while(rs.next()){
					sb.append(rs.getString(1));
				}
				return sb.toString();
			}
		});
		mcc.set(MC_KEY + aid, content);
		return content;
	}
	
	public boolean deleteContent(long aid){
		SqlBuilder sql = new SqlBuilder();
		sql.appendSql("DELETE FROM ").appendSql(getTableName(aid));
		sql.appendSql(" WHERE aid = ").appendValue(aid);
		int flag = geliDao.getJdbcTemplate().update(sql.getSql(), sql.getValues());
		if (flag > 0) {
			return mcc.delete(MC_KEY + aid);
		}
		return false;
	}
	
}
