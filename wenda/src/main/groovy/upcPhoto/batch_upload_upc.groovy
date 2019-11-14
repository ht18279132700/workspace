import groovy.sql.Sql;
import groovy.json.JsonSlurper;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;

def df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

println df.format(new Date()) + ' program start ...';

def proxyHost;
def proxyPort;
def uploadUrl;
def db;
int pageSize;
int retryCount;

File file = new File("lockUPC");
if(!file.exists()){
	file.createNewFile();
}
FileChannel channel = new FileOutputStream(file).getChannel();
FileLock fileLock = channel.tryLock();

if(fileLock != null){
	try{
		init();
		def pageCount = getPhotoImportPageCount();
		for(int i=1; i<= pageCount; i++){
			println "current page is ${i}";
			publishPhotoImport();
		}
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		colseDB();
		fileLock.release();
	}
}else{
	println 'get lockUPC fail';
}

channel.close();

println df.format(new Date()) + ' program end';

def init(){
	def prop = new Properties();
	prop.load(new FileInputStream('conf.properties'));
	
	proxyHost = prop.getProperty('http.proxyHost');
	proxyPort = prop.getProperty('http.proxyPort');
	uploadUrl = prop.getProperty('upload.url');
	
	def dbUrl = prop.getProperty('db.url');
	def username = prop.getProperty('db.username');
	def password = prop.getProperty('db.password');
	def driver = prop.getProperty('db.driver');
	
	db = Sql.newInstance(dbUrl, username, password, driver);
	pageSize = 50;
	retryCount = 0;
}

def colseDB(){
	if(db != null){
		db.close();
	}
}

def getPhotoImportPageCount(){
	def sql= 'select count(1) as total from wd_photo_import where status=0';
	long total = db.firstRow(sql).total;
	println 'total = ' + total;
	long pageCount = total / pageSize + (total % pageSize == 0 ? 0 : 1);
	println 'pageCount = ' + pageCount;
	return pageCount;
}

def publishPhotoImport(){
	def queryPhoto= 'select * from wd_photo_import where status=0 limit ?';
	db.eachRow(queryPhoto,[pageSize]){ rows->
		def url = rows.url;
		def id = rows.id;
		
		def param = "application=pcautowenda&keepSrc=yes&command=2079001&command=2079002&command=2079003&srcURL=${url}";
		param = encodeParam(param);
		def result = post(param);
		println "result = " + result;
		def jsonSlurper = new JsonSlurper();
		def json = null;
		try{
	        json = jsonSlurper.parseText(result);
		}catch(Exception e){
			println "json parse Exception --- " + e;
			println "result = " + result;
		}
		
		if(json != null && json.retCode == 0){
			def photo = json.files.find{ it.isorg == 1};
        	println "photo = " + photo;
        	def height = photo.height;
        	def width = photo.width;
        	def fileSize = photo.fileSize;
        	def photoUrl = photo.url;
        	
        	def updatePhoto = 'update wd_photo_import set wd_url=?, width=?, height=?, size=?,update_at = ?, status=1 where id=?';
			db.execute(updatePhoto, [photoUrl, width, height, fileSize, new Date(), id]);
        	
        }
	}
}


def post(params) {
	def fail = false;
    def output
    def input
    def result =""
    try {
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        
        def realUrl = new URL(uploadUrl)
        def conn = realUrl.openConnection() // 打开和URL之间的连接

        // 设置通用的请求属性
        conn.setRequestProperty("accept", "*/*")
        conn.setRequestProperty("referer", "http://wenda.pcauto.com.cn/admin/login.jsp")
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
            result += line
        }
    } catch (Exception e) {
        println "result from upc --- " + e
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