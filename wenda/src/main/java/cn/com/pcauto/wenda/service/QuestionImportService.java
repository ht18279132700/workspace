package cn.com.pcauto.wenda.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.gelivable.dao.SqlBuilder;
import org.gelivable.param.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.pcauto.wenda.entity.AnswerImport;
import cn.com.pcauto.wenda.entity.QuestionImport;
import cn.com.pcauto.wenda.util.Pager;
import cn.com.pcauto.wenda.util.excel.AbsObj;

public class QuestionImportService extends BasicService<QuestionImport>{
	
	@Autowired
	private AnswerImportService answerImportService;
	
	private static final Logger log = LoggerFactory.getLogger(QuestionImportService.class);
	
	public QuestionImportService(){
		super(QuestionImport.class);
	}

	public String questionImport(List<AbsObj> sources, long userId) {
		long beginTime = System.currentTimeMillis();
		int count = 0;
		int row = 1;
		int exist = 0;
		int ignore = 0;
    	
		for (AbsObj source : sources) {
			row++;
			log.debug("正在处理第{}行导入问答数据...", row);

			if (isLegal(source)) {   
				ignore++;
				continue;
			}

			if (!isNewData(source.getString("URL"), userId)) {
				exist++;
				continue;
			}
			
			try {
				QuestionImport questionImport = new QuestionImport();
				questionImport.setTags(source.getString("标签ID"));
				questionImport.setUrl(source.getString("URL"));
				questionImport.setTitle(source.getString("标题"));
				questionImport.setContent(source.getString("主楼内容"));
				questionImport.setQuestionTime(source.getDate("主楼时间"));
				questionImport.setNickname(source.getString("主楼用户"));
				questionImport.setPhotos(source.getString("图片"));
				if (userId > 0) {
					questionImport.setCreateBy(userId);
				}else {
					questionImport.setCreateBy(0);
				}
				questionImport.setCreateAt(new Date());
				long id = geliDao.create(questionImport);
				
				for (int i = 1; i <= 20; i++) {
					String content = source.getString(i + "楼回复");
					if (isIgnore(content) || content.length() > 3000) {
						continue;
					}
					AnswerImport answerImport = new AnswerImport();
					answerImport.setQiid(id);
					answerImport.setContent(content);
					answerImport.setAnswerTime(source.getDate(i + "楼时间"));
					answerImport.setNickname(source.getString(i + "楼用户"));
					if (userId > 0) {
						answerImport.setCreateBy(userId);
					}else {
						answerImport.setCreateBy(0);
					}
					answerImport.setCreateAt(new Date());
					
					geliDao.create(answerImport); 
				}
			} catch (Exception e) {
				log.error("导入错误，定位行号: {}", row);
				continue;
			}
			count++;
		}

		double finalTime = (System.currentTimeMillis() - beginTime) / 1000;

		return String.format(
				"导入成功，新增了%s行导入问答数据，其中有%s行重复，%s行忽略，耗时%s秒！", count, exist, ignore, finalTime);
	}
	
	public boolean isNewData(String url, long userId) {
		SqlBuilder sql = new SqlBuilder();
		sql.appendSql("select * from ").appendSql(getTableName());
		sql.appendSql(" where url = ").appendValue(url);

		QuestionImport questionImport = geliDao.findFirst(QuestionImport.class, sql.getSql(), sql.getValues());
		if (questionImport == null) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isLegal(AbsObj source) {
		String url = source.getString("URL");
		String title = source.getString("标题");
		String content = source.getString("主楼内容");
		String photos = source.getString("图片");
		if (isIgnore(title) || title.length() > 255 || (StringUtils.isNotBlank(content) && content.length() > 3000)
				|| url == null || (StringUtils.isNotBlank(photos) && photos.length() > 1000) ){
			return true;
		}
		return false;
	}
	
	private boolean isIgnore(String reply) {
		if (StringUtils.isBlank(reply)) {
			return true;
		}
		if (reply.matches("^.*?((?=&#[0-9]+;)|(?=<img)|(?=#发表#)|(?=#引用#)|(?=http://)).*$")) {
			return true;
		}
		return false;
	}
	
	public String exportQuestion(Date start, Date end, int pageNo, int pageSize){
		if (start == null || end == null) {
			return null;
		}
		SqlBuilder sql = new SqlBuilder();
		sql.appendSql("select id, qid, title, url from ").appendSql(getTableName());
		sql.appendSql(" where create_at >= ").appendValue(start);
		sql.appendSql(" and create_at <= ").appendValue(end);
		sql.appendSql(" limit ").appendValue((pageNo - 1) * pageSize);
		sql.appendSql(", ").appendValue(pageSize);
		List<Map<String, Object>> questionList = geliDao.getJdbcTemplate().queryForList(sql.getSql(), sql.getValues());
		
		StringBuilder data = new StringBuilder();
		for (Map<String, Object> map : questionList) {
			data.append(map.get("id")).append(",");
        	data.append(map.get("qid")).append(",");
        	data.append(map.get("title")).append(",");
        	data.append(map.get("url")).append(",");
        	data.append("\n");
		}
		return data.toString();
	}

	public Pager<QuestionImport> pager(int pageNo, int pageSize,QueryParam param){
		StringBuilder sb = new StringBuilder("SELECT id FROM ");
		sb.append(getTableName()).append(param.getWhereSql()).append(param.getOrderBy());
		return pager(pageNo, pageSize, sb.toString(), param.getParams().toArray());
	}
}
