import groovy.sql.Sql;
import groovy.json.JsonSlurper;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;

def df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

println df.format(new Date()) + ' program start ...';

def db;
def publishUrl;
int pageSize;
int retryCount;
long timeInterval;

File file = new File("lock");
if(!file.exists()){
	file.createNewFile();
}
FileChannel channel = new FileOutputStream(file).getChannel();
FileLock fileLock = channel.tryLock();

if(fileLock != null){
	try{
		init();
		def pageCount = getQuestionPageCount();
		for(int i=1; i<= pageCount; i++){
			println "current page is ${i}";
			publishQuestion();
		}
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		closeDb();
		fileLock.release();
	}
}else{
	println 'get lock fail';
}

channel.close();

println df.format(new Date()) + ' program end';



def init(){
	def prop = new Properties();
	prop.load(new FileInputStream('conf.properties'));
	
	def dbUrl = prop.getProperty('db.url');
	def username = prop.getProperty('db.username');
	def password = prop.getProperty('db.password');
	def driver = prop.getProperty('db.driver');
	
	db = Sql.newInstance(dbUrl, username, password, driver);
	publishUrl = prop.getProperty('wd.publish.url');
	timeInterval = Long.parseLong(prop.getProperty('wd.time.interval'));
	pageSize = 50;
	retryCount = 0;
}

def getUserList(def limit){
	def userList = [];
	def sql = 'select distinct uid from wd_sockpuppet where status=0 order by last_publish_at asc limit ?';
	db.eachRow(sql, [limit]){
		userList << it.uid;
	}
	return userList;
}

def getQuestionPageCount(){
	def sql= 'select count(1) as total from wd_question_import where status=0';
	long total = db.firstRow(sql).total;
	println 'total = ' + total;
	long pageCount = total / pageSize + (total % pageSize == 0 ? 0 : 1);
	println 'pageCount = ' + pageCount;
	return pageCount;
}

def publishQuestion(){
	def userList = getUserList(pageSize + pageSize*20);
	Map<String,Long> map = new HashMap<String,Long>();
	def sql= 'select * from wd_question_import where status=0 limit ?';
	int i=0;
	db.eachRow(sql,[pageSize]){ rows->
		long qiid = rows.id;
		Long uid = map.get(rows.nickname);
		if(uid == null){
			if(i < userList.size()){
				uid = userList.get(i);
				i++;
			}else{
				uid = userList.get((int)(Math.random() * userList.size()));
			}
			map.put(rows.nickname, uid);
		}
		
		def qTags = rows.tags;
		if(qTags == null){
			qTags = "";
		}
		
		def qContent = rows.content;
		if(qContent == null){
			qContent = "";
		}
		
		def qPhotos = rows.photos;
		if(qPhotos == null){
			qPhotos = "";
		}
		
		def data = "type=question&uid=${uid}"
            .concat("&title=${rows.title}&content=${qContent}&photos=${qPhotos}")
            .concat("&tags=${qTags}&createAt=${rows.question_time.time}");
            
		def params  = encodeParam(data);
		String results = post(params);
		
		def jsonSlurper = new JsonSlurper();
		def json = null;
		try{
	        json = jsonSlurper.parseText(results);
		}catch(Exception e){
			println "json parse Exception --- " + e;
			println "results = " + results;
		}
		
        if(json != null && json.code == 0 && json.qid > 0){
        	def qid = json.qid;
        	updateQuestion(qid, uid, qiid);
        	updateSockpuppet(uid, rows.question_time);
        	Thread.sleep(timeInterval);
        	
	        //发回答
	        def aSql= 'select * from wd_answer_import where qiid=? order by answer_time asc';
	        db.eachRow(aSql, [qiid]){
	        	Long aUid = map.get(it.nickname);
				if(aUid == null){
					if(i < userList.size()){
						aUid = userList.get(i);
						i++;
					}else{
						aUid = userList.get((int)(Math.random() * userList.size()));
					}
					map.put(it.nickname, aUid);
				}
				def aData = "type=answer&uid=${aUid}&qid=${qid}"
		            .concat("&content=${it.content}&createAt=${it.answer_time.time}")
		            
				def aParams  = encodeParam(aData);
				String aResults = post(aParams);
				
				def aJson = null;
				try{
			        aJson = jsonSlurper.parseText(aResults);
				}catch(Exception e){
					println "json parse Exception --- " + e;
					println "results = " + aResults;
				}
				
				if(aJson != null && aJson.code == 0 && aJson.aid > 0){
					updateAnswer(aJson.aid, aUid, it.id);
					updateSockpuppet(aUid, it.answer_time);
					Thread.sleep(timeInterval);
				}
	        }
        }
	}
}

def updateQuestion(def qid, def uid, def id){
	def sql= 'update wd_question_import set qid=?, uid=?, status=1 where id=?';
	db.execute(sql, [qid, uid, id]);
}

def updateAnswer(def aid, def uid, def id){
	def sql= 'update wd_answer_import set aid=?, uid=?, status=1 where id=?';
	db.execute(sql, [aid, uid, id]);
}

def updateSockpuppet(def uid, def time){
	def sql= 'update wd_sockpuppet set last_publish_at = ? where uid=?';
	db.execute(sql, [time, uid]);
}

def post(params) {
	def fail = false;
    def output
    def input
    def result =""
    try {
        
        def realUrl = new URL(publishUrl)
        def conn = realUrl.openConnection() // 打开和URL之间的连接

        // 设置通用的请求属性
        conn.setRequestProperty("accept", "*/*")
        conn.setRequestProperty("connection", "Keep-Alive")
        conn.setRequestProperty("user-agent",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")

        // 发送POST请求必须设置如下两行
        conn.setDoOutput(true)
        conn.setDoInput(true)


        // 获取URLConnection对象对应的输出流
        output = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));

        // 发送请求参数
        output.print(params);
	
        // flush输出流的缓冲
        output.flush()


        // 定义BufferedReader输入流来读取URL的响应
        input = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))
        def line
        while ((line = input.readLine()) != null) {
            println "line from wenda --- " + line
            result += line
        }
    } catch (Exception e) {
        println "result from wenda --- " + e
        e.printStackTrace()
        fail = true
    } finally {
        try {
            if (output != null) {
                output.close()
            } 
            if (input != null) {
                input.close()
            }
        } catch (IOException ex) {
            ex.printStackTrace()
        }
    }
    if(fail && retryCount < 6){ //失败重试，最多可重试6次
    	retryCount++;
    	println "http request fail, retry " + retryCount;
    	Thread.sleep(retryCount*5000);
    	result = post(params); //递归调用
	    //退出递归后，重置retryCount=0，下一次请求仍有6次重试机会
	    retryCount = 0;
    }
    return result;
}

def encodeParam(data){
	def encodeData = data.replaceAll("%", "%25");
	return encodeData;
}

def closeDb(){
	if(db != null){
		db.close();
	}
}