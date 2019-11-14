package cn.com.pcauto.wenda.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import cn.com.pcauto.wenda.entity.TagDaily;
import cn.com.pcauto.wenda.util.DateUtils;

public class TagDailyService extends BasicService<TagDaily>{
	
	private static final String HotTagDailyList = TagDaily.class.getSimpleName() + "-hotTagDailyList";

	public TagDailyService(){
		super(TagDaily.class);
	}
	

	/**
	 * 获取某段时间内，发布问题标签总数的标签ID排行。
	 * @param start
	 * @param end
	 * @return
	 */
	public List<Long> getWeekTopTag(Date start, Date end, int num) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT id FROM ").append(getTableName())
		.append(" WHERE create_at < ?  AND create_at >= ? GROUP BY id ORDER BY SUM(daily_num) DESC limit ?");
		return geliDao.getJdbcTemplate().queryForList(sql.toString(), Long.class, end, start, num);
	}
	/**
	 * 每日定时任务，将排行前num个标签id保存到缓存
	 * @param num
	 */
	public void listHotTag(int num){
		Date beforeDay = DateUtils.getBefore1Day();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT id FROM ").append(getTableName()).append(" WHERE create_at = ? ")
		.append(" ORDER BY daily_num DESC,question_num DESC LIMIT " + num);
		List<Long> list = geliDao.getJdbcTemplate().queryForList(sql.toString(), Long.class, beforeDay);
		if (list.size() < num ) {
			String hotTagDaily = (String) mcc.get(HotTagDailyList);
			if (StringUtils.isNotBlank(hotTagDaily)) {
				hotTagDaily = hotTagDaily.substring(1, hotTagDaily.length() -1);
				String[] tagDailyArr = hotTagDaily.split(",");
				Set<Long> set = new HashSet<Long>(list);
				for (String tagDaily : tagDailyArr) {
					set.add(Long.parseLong(tagDaily.trim()));
					if (set.size() >= num) {
						break;
					}
				}
				mcc.set(HotTagDailyList, set.toString());
			}
		}else {
			mcc.set(HotTagDailyList, list.toString());
		}
	}
	/**
	 * 从缓存中获取每日热门标签的ID
	 * @return
	 */
	public List<Long> listHotTagID(){
		List<Long> list = new ArrayList<Long>();
		String hotTagDaily = (String) mcc.get(HotTagDailyList);
		if (StringUtils.isNotBlank(hotTagDaily)) {
			hotTagDaily = hotTagDaily.substring(1, hotTagDaily.length() -1);
			String[] tagDailyArr = hotTagDaily.split(",");
			for (String tagDaily : tagDailyArr) {
				list.add(Long.parseLong(tagDaily.trim()));
			}
		}
		return list;
	}
}
