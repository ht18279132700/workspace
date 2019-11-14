package cn.com.pcauto.wenda.service;

import java.util.Date;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.pcauto.wenda.entity.Counter;

public class CounterService extends BasicService<Counter>{
	
    @Resource(name="pvThreadPool")
    private ExecutorService executorService;
	
	private static final Logger LOG = LoggerFactory.getLogger(CounterService.class);
	
	public CounterService(){
		super(Counter.class);
	}
	/**
	 * 获取问题的浏览数量
	 * @param qid
	 * @return
	 */
	public int getPv(long qid){
		String key = getCacheKey(qid);
		Object object = mcc.get(key);
		if (object != null) {
			return NumberUtils.toInt(object.toString());
		}
		Counter counter = findDb(qid);
		if (counter != null) {
			int pv = counter.getPv();
			mcc.set(key, pv);
			return pv;
		}
		return 0;
	}
	
	/**
	 * 增加问题的浏览数量
	 * @param qid
	 * @return
	 */
	public void incrQuestionPv(final long qid){
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				int mcCount = 0;
				try {
					String key = getCacheKey(qid);
					Object object = mcc.get(key);
					if (object != null) {
						mcCount = NumberUtils.toInt(object.toString());
					}else {
						Counter counter = findDb(qid);
						if (counter != null) {
							mcCount= counter.getPv();
						}else {
							create(new Counter(qid, 0));
						}
					}
					addNum(qid, 1);
					mcc.set(key, ++mcCount);
				} catch (Exception e) {
					LOG.error("incr question pv fail", e);
				}
			}
		});
	}
	
	private int addNum(long qid, int num){
		StringBuilder sb = new StringBuilder("UPDATE ").append(getTableName());
		sb.append(" SET pv = pv + ? , update_at = ?");
		sb.append(" WHERE qid=?");
		return geliDao.getJdbcTemplate().update(sb.toString(), num ,new Date(), qid);
		
	}
}
