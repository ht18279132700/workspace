package cn.com.pcauto.wenda.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cn.com.pcauto.wenda.entity.AnswerRelation;

public class AnswerRelationService extends BasicService<AnswerRelation> {

	public AnswerRelationService() {
		super(AnswerRelation.class);
	}
	
	public List<AnswerRelation> listRelations(){
		String sql = "SELECT * FROM wd_answer_relation ORDER BY LENGTH(keywords) DESC ";
		return geliDao.getJdbcTemplate().query(sql, resultSet);
	}
	
	ParameterizedRowMapper<AnswerRelation> resultSet = new ParameterizedRowMapper<AnswerRelation>() {

		@Override
		public AnswerRelation mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			AnswerRelation answerRelation = new AnswerRelation();
			answerRelation.setId(rs.getLong("id"));
			answerRelation.setKeywords(rs.getString("keywords"));
			answerRelation.setContent(rs.getString("content"));
			return answerRelation;
		}
	};
	
	
	
}
