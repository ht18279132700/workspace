import groovy.sql.Sql;
import java.text.SimpleDateFormat;

def df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

println df.format(new Date()) + ' program start ...';

def db;
def timeFile;
def timeFileFormat;

try{
	init();
	Date lastEndTime = getLastEndTime();
	Date todayTime = getTodayTime();
	println 'lastEndTime='+lastEndTime;
	println 'todayTime='+todayTime;
	for(;lastEndTime.before(todayTime);lastEndTime=incDate(lastEndTime)){
		Date i=incDate(lastEndTime);
		def seoQuestionNum = getSeoQuestionNum(lastEndTime,i);
		def seoAnswerNum = getSeoAnswerNum(lastEndTime,i);
		def userQuestionNum = getUserQuestionNum(lastEndTime,i);
		def userAnswerNum = getUserAnswerNum(lastEndTime,i);
		def userReplyNum = getUserReplyNum(lastEndTime,i);
		def day = dateToIntDay(lastEndTime);
		insertDB(day,seoQuestionNum,seoAnswerNum,userQuestionNum,userAnswerNum,userReplyNum);
	}
	writeLastEndTime(todayTime);
}catch(Exception e){
	e.printStackTrace();
}finally{
	colseDB();
}

println df.format(new Date()) + ' program end';
println();



def init(){
	def prop = new Properties();
	prop.load(new FileInputStream('conf.properties'));
	
	def dbUrl = prop.getProperty('db.url');
	def username = prop.getProperty('db.username');
	def password = prop.getProperty('db.password');
	def driver = prop.getProperty('db.driver');
	
	db = Sql.newInstance(dbUrl, username, password, driver);
	timeFile="lastEndTime.count_wd_daily";
	timeFileFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}

def insertDB(def day, def seoQuestionNum, def seoAnswerNum,
			def userQuestionNum, def userAnswerNum, def userReplyNum){
	db.execute("insert into wd_daily_stat(day,seo_question_num,seo_answer_num,user_question_num,user_answer_num,user_reply_num,create_at) values(?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE seo_question_num=?,seo_answer_num=?,user_question_num=?,user_answer_num=?,user_reply_num=?,create_at=?",
	[day,seoQuestionNum,seoAnswerNum,userQuestionNum,userAnswerNum,userReplyNum,new Date(),
	seoQuestionNum,seoAnswerNum,userQuestionNum,userAnswerNum,userReplyNum,new Date()]);
}

def dateToIntDay(def date){
	return Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(date));
}

def getSeoQuestionNum(def beginDate, def endDate){
	def r = db.firstRow("select count(1) total from wd_question where agent=-1 and create_at>=? and create_at<?",[beginDate,endDate]);
	return r.total;
}

def getSeoAnswerNum(def beginDate, def endDate){
	int total = 0;
	for(int i=0;i<50;i++){
		def r=db.firstRow("select count(1) total from wd_answer_"+i+" where agent=-1 and create_at>=? and create_at<?",[beginDate,endDate]);
		total += r.total;
	}
	return total;
}

def getUserQuestionNum(def beginDate, def endDate){
	def r = db.firstRow("select count(1) total from wd_question where agent<>-1 and create_at>=? and create_at<?",[beginDate,endDate]);
	return r.total;
}

def getUserAnswerNum(def beginDate, def endDate){
	int total = 0;
	for(int i=0;i<50;i++){
		def r=db.firstRow("select count(1) total from wd_answer_"+i+" where agent<>-1 and create_at>=? and create_at<?",[beginDate,endDate]);
		total += r.total;
	}
	return total;
}

def getUserReplyNum(def beginDate, def endDate){
	int total = 0;
	for(int i=0;i<50;i++){
		def r=db.firstRow("select count(1) total from wd_reply_"+i+" where create_at>=? and create_at<?",[beginDate,endDate]);
		total += r.total;
	}
	return total;
}

def colseDB(){
	if(db != null){
		db.close();
	}
}

def incDate(Date date){
	Calendar c = Calendar.getInstance();
	c.setTime(date);
	c.add(Calendar.DATE, 1);
	return c.getTime();
}

def getTodayTime(){
	Calendar c = Calendar.getInstance();
	c.set(Calendar.HOUR_OF_DAY, 0);
	c.set(Calendar.MINUTE, 0);
	c.set(Calendar.SECOND, 0);
	c.set(Calendar.MILLISECOND, 0);
	return c.getTime();
}

def getLastEndTime(){
    def timeStr = '';
	File file = new File(timeFile);
    if(file.exists()){
    	timeStr = file.text;
    	if(timeStr != null) timeStr = timeStr.replaceAll("\r|\n", "");
    }
    if(isBlank(timeStr) || !isDate(timeStr)){
    	timeStr = '2018-12-21 00:00:00';
    }
    return timeFileFormat.parse(timeStr);
}

def writeLastEndTime(def date){
	new File(timeFile).withWriter('utf-8') {
		writer -> writer.writeLine timeFileFormat.format(date);
	}
}

def isBlank(def str){
	if(str == null || str.trim().length() == 0){
		return true;
	}
	return false;
}

def isDate(def str){
	boolean f = str.matches("2[0-9]{3}-[0-9]{2}-[0-9]{2} 00:00:00");
	if(f){
		return Integer.parseInt(str.replace("-","").substring(0,8)) >= 20181221;
	}
	return false;
}