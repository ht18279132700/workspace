package cn.com.pcauto.wenda.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.gelivable.dao.SqlPageBuilder;
import org.gelivable.param.OrderBy;
import org.gelivable.param.QueryParam;
import org.gelivable.param.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.pcauto.wenda.config.SystemConfig;
import cn.com.pcauto.wenda.entity.Tag;
import cn.com.pcauto.wenda.util.Functions;
import cn.com.pcauto.wenda.util.McCacheTime;
import cn.com.pcauto.wenda.util.Pager;
import cn.com.pcauto.wenda.util.TagType;
import cn.com.pcauto.wenda.web.intf.IntfTagController;
import cn.pconline.r.client.RClient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TagService extends BasicService<Tag> {
	
	@Autowired
	private RClient rClient;
	@Autowired
	private SystemConfig systemConfig;
	@Autowired
	private SqlPageBuilder sqlPageBuilder;
	@Autowired
	private TagDailyService tagDailyService;
	
	private static final Logger LOG = LoggerFactory.getLogger(TagService.class);
	
	private static final String TagList = Tag.class.getSimpleName() + "-tagList";
	private static final String TagSortList = Tag.class.getSimpleName() + "-tagSortList";
	private static final String HotTagList = Tag.class.getSimpleName() + "-hotTagList";
	

	public TagService(){
		super(Tag.class);
	}

	public void incrQuestionNum(List<Tag> tagList) {
		addQuestionNum(tagList, 1);
	}
	
	public void decrQuestionNum(List<Tag> tagList) {
		addQuestionNum(tagList, -1);
	}

	public int addQuestionNum(List<Tag> tagList, int addNum) {
		if(tagList == null || tagList.isEmpty()){
			return 0;
		}
		List<Long> tagIdList = Functions.getTagIdList(tagList);
		QueryParam param = new QueryParam();
		param.andIn("id", tagIdList);
		StringBuilder sql = new StringBuilder("UPDATE ").append(getTableName());
		sql.append(" SET question_num = question_num + ? ").append(param.getWhereSql());
		List<Object> params = param.getParams();
		params.add(0, addNum);
		int update = geliDao.getJdbcTemplate().update(sql.toString(), params.toArray());
		if(update > 0){
			for(Long id : tagIdList){
				removeFromCache(id);
			}
		}
		return update;
	}
	
	private String getTagSql(QueryParam param) {
		StringBuilder sb = new StringBuilder("SELECT id FROM ");
		sb.append(getTableName()).append(param.getWhereSql()).append(param.getOrderBy());
		return sb.toString();
	}
	
	public Tag getTag(long tid, int status){
		QueryParam param = new QueryParam();
		param.and("status", status);
		param.and("id", tid);
		String sql = getTagSql(param);
		
		return findFirst(sql, param.getParams().toArray());
	}
	
	/**
	 * 根据车系id获取一个标签
	 * @param sid
	 * @return
	 */
	public Tag getTagBySid(long sid){
		if(sid <= 0){
			return null;
		}
		QueryParam param = new QueryParam();
		param.and("serial_id", sid);
		param.and("status", 0);
		String sql = getTagSql(param);
		
		return findFirst(sql, param.getParams().toArray());
	}
	
	/**
	 * 显示被打标签次数最多的num个话题标签
	 * @param num
	 * @return
	 */
	public List<Tag> listTagForNum(int num){
		QueryParam param = new QueryParam();
		param.and("status", 0);
		param.orderBy("question_num");
		String sql = getTagSql(param);
		return list(1, num, sql, param.getParams().toArray());
	}
	
	/**
	 * 2019-07-26 相关标签规则修改，优先取定时任务匹配出来的相关标签，不够的部分再用旧的规则补够
	 * @param tag
	 * @param limit
	 * @return
	 */
	public List<Tag> listRelateTag2(Tag tag, int limit) {
		List<Long> list = Functions.string2LongList(tag.getCorrelation());
		if(tag.getPid() > 0 && tag.getPid() != Tag.OTHER){
			list.add(0, tag.getPid());
		}
		int sub = limit - list.size();
		if(sub < 0){
			list = list.subList(0, limit);
		}
		List<Tag> tagList = list.size() > 0 ? list(list.toArray()) : new ArrayList<Tag>();
		if(sub > 0){
			tagList.addAll(listRelateTag(tag, sub));
		}
		return tagList;
	}
	
	/**
	 * 显示车系标签对应车系相关的车系对应的车系标签,车系相关的规则为车系首页“看了又看”现有接口
	 * @param tag
	 * @param limit
	 * @return
	 */
	public List<Tag> listRelateTag(Tag tag, int limit) {
		int limit2 = limit*2;
		List<Tag> list = null;
		if(TagType.BRAND.getName().equals(tag.getTagType()) || TagType.LEVEL1.getName().equals(tag.getTagType())){
			list = listRelateTag(tag, limit2, true);
		}else if(TagType.LEVEL2.getName().equals(tag.getTagType())){
			list = listRelateTag(tag, limit2, false);
		}else if(TagType.SERIAL.getName().equals(tag.getTagType())){
			list = getTagFromPrice(tag, limit2);
		}
		return Functions.randomInList(list, limit);
	}
	
	/**
	 * 相关标签
	 * @param tag
	 * @param limit
	 * @param subordinate 是否下级，true为查询下级标签
	 * @return
	 */
	private List<Tag> listRelateTag(Tag tag, int limit, boolean subordinate) {
		QueryParam param = new QueryParam();
		param.orderBy("question_num");
		param.and("question_num", Relation.GT, 0);
		param.and("status", 0);
		if(subordinate){ // 如果是查询下级
			param.and("pid", tag.getId());
		}else{ // 平级
			param.and("id", Relation.NEQ, tag.getId());
			param.and("pid", tag.getPid());
			param.and("tag_type", tag.getTagType());
		}
		String sql = getTagSql(param);
		return list(1, limit, sql, param.getParams().toArray());
	}

	/**
	 * 从车系首页“看了又看”现有接口，获取相关车系的数据
	 * @param serialId
	 * @return
	 */
	public List<Tag> getTagFromPrice(Tag tag, int limit){
		StringBuilder url = new StringBuilder(systemConfig.getPriceRoot());
		url.append("/price/api/v1/serialgroup/relate_serial_group_by_hot");
		url.append("?sgid=").append(tag.getSerialId()).append("&type=1&num=").append(limit);
		
		List<Long> sids = new ArrayList<Long>();
		try {
			String data = rClient.get(url.toString(), null, 1, TimeUnit.SECONDS);
			JSONObject jsonObject = JSONObject.parseObject(data);
			JSONArray jsonArray = jsonObject.getJSONArray("serialGroups");
			for(int i = 0; i < jsonArray.size(); i++){
				JSONObject json = (JSONObject) jsonArray.get(i);
				sids.add(json.getLongValue("id"));
			}
		} catch (Exception e) {
			LOG.warn("getTagFromPrice fail", e);
			return listRelateTag(tag, limit, false);
		}
		
		QueryParam param = new QueryParam();
		param.and("status", 0);
		param.andIn("serial_id", sids);
		param.and("question_num", Relation.GT, 0);
		String sql = getTagSql(param);
		
		return list(sql, param.getParams().toArray());
	}
	/**
	 * 根据标签类型获取标签
	 * @param tagType
	 * @return
	 */
	public List<Tag> listForType(String tagType){
		QueryParam param = new QueryParam();
		param.and("tag_type", tagType);
		param.and("status", 0);
		param.orderBy("letter", OrderBy.ASC);
		String sql = getTagSql(param);
		return list(sql, param.getParams().toArray());
	}
	/**
	 * 根据父标签获取子标签
	 * @param pid
	 * @return
	 */
	public List<Tag> listForPid(long pid){
		QueryParam param = new QueryParam();
		param.and("pid", pid);
		param.and("status", 0);
		param.orderBy("letter", OrderBy.ASC);
		String sql = getTagSql(param);
		return list(sql, param.getParams().toArray());
	}
	/**
	 * 查询所有标签
	 * @return
	 */
	public List<Tag> listTag(){
		QueryParam param = new QueryParam();
		param.and("status", 0);
		String sql = getTagSql(param);
		return list(sql, param.getParams().toArray());
	}
	
	/**
	 * 给问题的标题和描述匹配标签，将标题、描述中含有的标签词提取出来
	 * @param title
	 * @return
	 */
	public List<Tag> matchTagByTitle(String title){
		return list("select id from wd_tag where status=? and locate(name, ?) > 0", Tag.STATUS_PASS, title);
	}
	
	/**
	 * 根据用户输入的关键词联想标签
	 * @param title
	 * @return
	 */
	public List<Tag> matchTagByKeyword(String keyword, int limit){
		QueryParam param = new QueryParam();
		param.and("status", Tag.STATUS_PASS);
		param.andIn("tag_type", Arrays.asList(TagType.LEVEL1.getName(), TagType.LEVEL2.getName()));
		param.and("name", Relation.LIKE, keyword+"%");
		String sql = getTagSql(param);
		return list(1, limit, sql, param.getParams().toArray());
	}

	/**
	 * 获取所有标签的缓存数据
	 * @return
	 */
	public JSONObject getMCTag(){
		JSONObject json = (JSONObject) mcc.get(TagList);
		if (json != null) {
			return json;
		}else {
			json = new JSONObject();
		}
		JSONArray tagArray = new JSONArray();
		List<Tag> listTag = listTag();
		for (Tag tag : listTag) {
			JSONObject object = new JSONObject();
			object.put("id", tag.getId());
			object.put("name", tag.getName());
			object.put("tagType", tag.getTagType());
			tagArray.add(object);
		}
		json.put("data", tagArray);
		if (!json.isEmpty()) {
			mcc.set(TagList, json);
		}
		return json;
	}
	/**
	 * 获取1级和2级标签的缓存数据
	 * @return
	 */
	public JSONObject getRelateTagSort(){
		JSONObject json = (JSONObject)mcc.get(TagSortList);
		if (json != null) {
			return json;
		}else {
			json = new JSONObject();
		}
		JSONArray tagArray = new JSONArray();
		QueryParam param = new QueryParam();
		param.and("status", 0);
		param.andIn("tag_type", Arrays.asList(TagType.LEVEL1.getName(), TagType.LEVEL2.getName()));
		String sql = getTagSql(param);
		List<Tag> listTag = list(sql, param.getParams().toArray());
		for (Tag tag : listTag) {
			JSONObject object = new JSONObject();
			object.put("id", tag.getId());
			object.put("name", tag.getName());
			tagArray.add(object);
		}
		json.put("data", tagArray);
		if (!json.isEmpty()) {
			mcc.set(TagSortList, json);
		}
		return json;
	}

	public Pager<Tag> pager(int pageNo, int pageSize, QueryParam param) {
		String sql = getTagSql(param);
		return pager(pageNo, pageSize, sql, param.getParams().toArray());
	}

	public List<Tag> list(int pageNo, int pageSize, QueryParam param) {
		String sql = getTagSql(param);
		return list(pageNo, pageSize, sql, param.getParams().toArray());
	}
	
	public long createTag(Tag tag) {
		long id = geliDao.generateId(Tag.class);
		tag.setSeq((int) id);
		tag.setId(id);
		return create(tag);
	}

	public String exportBrand(QueryParam param, int pageNo, int pageSize) {
		StringBuilder sb = new StringBuilder("SELECT name,id,status FROM ");
		sb.append(getTableName()).append(param.getWhereSql()).append(param.getOrderBy());
		String buildPageSql = sqlPageBuilder.buildPageSql(sb.toString(), pageNo, pageSize);
		List<Map<String,Object>> list = geliDao.getJdbcTemplate().queryForList(buildPageSql, param.getParams().toArray());
		
		StringBuilder data = new StringBuilder();
		for (Map<String, Object> map : list) {
			data.append(map.get("name")).append(",");
        	data.append(map.get("id")).append(",");
        	data.append(map.get("status")).append(",");
        	data.append("\n");
		}
		return data.toString();
	}

	public List<Tag> listByParam(QueryParam param) {
		String sql = getTagSql(param);
		return list(sql, param.getParams().toArray());
	}
	public List<Long> listTagID(QueryParam param){
		List<Tag> list = listByParam(param);
		if (list == null || list.size() == 0) {
			return null;
		}
		List<Long> ids = new ArrayList<Long>();
		for (Tag tag : list) {
			ids.add(tag.getId());
		}
		return ids;
	}

	public List<Tag> listChildTag(List<Long> listTagID) {
		if (listTagID == null) {
			return null;
		}
		QueryParam param = new QueryParam();
		param.andIn("pid", listTagID);
		String sql = getTagSql(param);
		return list(sql, param.getParams().toArray());
	}

	public String exportSerialOrSecond(QueryParam param, int pageNo, int pageSize) {
		StringBuilder sb = new StringBuilder("SELECT name,id,pid,status FROM ");
		sb.append(getTableName()).append(param.getWhereSql()).append(param.getOrderBy());
		String buildPageSql = sqlPageBuilder.buildPageSql(sb.toString(), pageNo, pageSize);
		List<Map<String,Object>> list = geliDao.getJdbcTemplate().queryForList(buildPageSql, param.getParams().toArray());
		
		StringBuilder data = new StringBuilder();
		for (Map<String, Object> map : list) {
			data.append(map.get("name")).append(",");
        	data.append(map.get("id")).append(",");
        	Tag tag = findById((Long) map.get("pid"));
        	if (tag != null) {
        		data.append(tag.getName()).append(",");
			}else {
				data.append("0,");
			}
        	data.append(map.get("status")).append(",");
        	data.append("\n");
		}
		return data.toString();
	}

	/**
	 * 删除全部标签的缓存
	 * @return
	 */
	public boolean deleteTagListCache() {
		return mcc.delete(TagList);
	}
	/**
	 * 删除一级和二级标签的缓存
	 * @return
	 */
	public boolean deleteTagSortListCache(){
		return mcc.delete(TagSortList);
	}
	/**
	 * 删除品牌标签的缓存
	 * @return
	 */
	public boolean deleteBrandCache(){
		return mcc.delete(IntfTagController.Brand);
	}
	/**
	 * 删除品牌下车系的缓存
	 * @param brandID
	 * @return
	 */
	public boolean deleteSerialCache(long brandID){
		return mcc.delete(IntfTagController.Serial+"-"+brandID);
	}

	/**
	 * 删除标签的所有缓存
	 */
	public void deleteTagCache() {
		deleteTagListCache();
		deleteTagSortListCache();
		deleteBrandCache();
		List<Tag> list = listForType(TagType.BRAND.getName());
		if (list != null) {
			for (Tag tag : list) {
				deleteSerialCache(tag.getId());
			}
		}
	}
	
	/**
	 * 获取被打标签次数最多的num个话题标签ID
	 * @param num
	 * @return
	 */
	public List<Long> listTagIDForNum(int num){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT id FROM ").append(getTableName()).append(" ORDER BY question_num DESC LIMIT ? ");
		return geliDao.getJdbcTemplate().queryForList(sql.toString(), Long.class, num);
	}
	/**
	 * 获取标签索引页的热门标签
	 * @param start
	 * @param end
	 * @param num
	 * @return
	 */
	public List<Tag> listHotTag(Date start, Date end, int num){
		String hotTags = (String) mcc.get(HotTagList);
		if (StringUtils.isBlank(hotTags)) {
			List<Long> tagDailyID = tagDailyService.getWeekTopTag(start,end,num);
			if (tagDailyID.size() < num) {
				List<Long> tagID = listTagIDForNum(num);
				Set<Long> ids = new HashSet<Long>(tagDailyID);
				for (Long id : tagID) {
					ids.add(id);
					if (ids.size() >= num) {
						break;
					}
				}
				mcc.set(HotTagList, ids.toString(), McCacheTime.getMc1HTimes());
				return list(ids.toArray());
			}else {
				mcc.set(HotTagList, tagDailyID.toString(), McCacheTime.getMc1HTimes());
				return list(tagDailyID.toArray());
			}
		}else {
			hotTags = hotTags.substring(1, hotTags.length() -1);
			String[] hotTagList = hotTags.split(",");
			return list(hotTagList);
		}
	}
	
	/**
	 * 获取标签分类页的标签列表
	 * @param letter
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Pager<Tag> sortTag(String letter, int pageNo, int pageSize) {
		QueryParam param = new QueryParam();
		param.and("letter", letter);
		param.and("status", 0);
		param.and("question_num",Relation.GT, 0);
		param.orderBy("question_num", OrderBy.DESC);
		String sql = getTagSql(param);
		return pager(pageNo, pageSize, sql, param.getParams().toArray());
	}
	
	public List<Tag> listHotTagDaily(){
		List<Long> listHotTagID = tagDailyService.listHotTagID();
		if (listHotTagID.size() > 0) {
			return list(listHotTagID.toArray());
		}else{
			return null;
		}
	}
	
}
