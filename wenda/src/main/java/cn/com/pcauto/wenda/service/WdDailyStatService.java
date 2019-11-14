package cn.com.pcauto.wenda.service;

import java.util.List;

import org.gelivable.dao.SqlBuilder;

import cn.com.pcauto.wenda.entity.WdDailyStat;
import cn.com.pcauto.wenda.util.Pager;


public class WdDailyStatService  extends BasicService<WdDailyStat> {
	
	public WdDailyStatService(){
		super(WdDailyStat.class);
	}
	
	public List<WdDailyStat> listByDay(int beginDay, int endDay){
		SqlBuilder sb = buildSql(beginDay, endDay);
		return list(sb.getSql(), sb.getValues());
	}
	
	public Pager<WdDailyStat> pagerByDay(int beginDay, int endDay, int pageNo, int pageSize){
		SqlBuilder sb = buildSql(beginDay, endDay);
		return pager(pageNo, pageSize, sb.getSql(), sb.getValues());
	}
	
	private SqlBuilder buildSql(int beginDay, int endDay) {
		SqlBuilder sb = new SqlBuilder();
		sb.appendSql("select day from ").appendSql(getTableName()).appendSql(" where 1=1");
		if(beginDay > 0){
			sb.appendSql(" and day>=").appendValue(beginDay);
		}
		if(endDay > 0){
			sb.appendSql(" and day<=").appendValue(endDay);
		}
		sb.appendSql(" order by day desc");
		return sb;
	}
}
