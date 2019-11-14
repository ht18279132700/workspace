package cn.com.pcauto.wenda.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import cn.com.pcauto.wenda.entity.ReplyContent;

public class ReplyContentService extends BasicService<ReplyContent> {

	private final static String MC_KEY = "reply-content-";
	
	public ReplyContentService(){
		super(ReplyContent.class);
	}
	
	public void create(long rid, String content){
		if(rid <= 0 || StringUtils.isBlank(content)){
			return;
		}
		StringBuilder sql = new StringBuilder("INSERT INTO ").append(getTableName(rid));
		sql.append("(rid,seq,content) values(?,?,?)");
		List<Object[]> params = new ArrayList<Object[]>();
		for (int i = 0,s=1; i < content.length(); i+=128,s++) {
			int end = Math.min(i+128, content.length());
			params.add(new Object[]{rid,s,content.substring(i, end)});
		}
		geliDao.getJdbcTemplate().batchUpdate(sql.toString(), params);
	}
	
	public String getContent(long rid){
		if(rid <= 0){
			return "";
		}
		
		String content = (String) mcc.get(MC_KEY + rid);
		if(StringUtils.isNotBlank(content)){
			return content;
		}
		
		StringBuilder sql = new StringBuilder("SELECT content FROM ").append(getTableName(rid));
		sql.append(" WHERE rid=? ORDER BY seq ASC");
		content = geliDao.getJdbcTemplate().query(sql.toString(), new Object[]{rid}, new ResultSetExtractor<String>(){
			@Override
			public String extractData(ResultSet rs) throws SQLException,DataAccessException {
				StringBuilder sb = new StringBuilder();
				while(rs.next()){
					sb.append(rs.getString(1));
				}
				return sb.toString();
			}
		});
		mcc.set(MC_KEY + rid, content);
		return content;
	}
	
}
