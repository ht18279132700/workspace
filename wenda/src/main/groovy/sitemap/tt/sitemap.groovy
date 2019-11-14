import groovy.xml.MarkupBuilder
import groovy.sql.Sql
import java.text.SimpleDateFormat

def db;
int pageSize;
def dir1 = 'd1';
def dir2 = 'd2';
def defaultDir;
def nextDir;
def xmlFilePrefix='question_';
int xmlFileIndex=0;

class Question{
	def qid;
	def title;
	def descpt;
	def modDate;
}


println formatDate(new Date(), 'yyyy-MM-dd HH:mm:ss,SSS') + ' program start ...';

def fileDir1 = new File(dir1);
def fileDir2 = new File(dir2);

if(!fileDir1.exists()){
	fileDir1.mkdir();
}
if(!fileDir2.exists()){
	fileDir2.mkdir();
}

def flagFile = new File('flag');
if(flagFile.exists()){
    flagFile.eachLine{defaultDir = it}
}

if(defaultDir == dir1){
	nextDir = dir2;
}else if(defaultDir == dir2){
	nextDir = dir1;
}else{
	defaultDir = dir1;
	nextDir = dir2;
}

deleteFileList(new File(defaultDir).listFiles());


try{
	init();
	def pageCount = getPageCount();
	for(int i=1; i<=pageCount; i++){
		def arr = getQuestionList(i);
		xmlFileIndex++;
		writeXml(arr, defaultDir + '/' + xmlFilePrefix + xmlFileIndex + '.xml');
	}
	def tmpFile = new File('tmp.xml');
	writeIndexXml(1, xmlFileIndex, defaultDir, xmlFilePrefix, tmpFile);
	def f = new File('index.xml');
	if(f.exists()){
		f.delete();
	}
	tmpFile.renameTo(f);
}catch(Exception e){
	println 'error error error...';
	e.printStackTrace();
}finally{
	closeDB();
}

def writer = new PrintWriter(new FileWriter(flagFile));
writer.print(nextDir);
writer.close();

println formatDate(new Date(), 'yyyy-MM-dd HH:mm:ss,SSS') + ' program end';
println '----------------------------------------------------------------';


def init(){
	def prop = new Properties();
	prop.load(new FileInputStream('../../conf.properties'));
	
	def dbUrl = prop.getProperty('db.url');
	def username = prop.getProperty('db.username');
	def password = prop.getProperty('db.password');
	def driver = prop.getProperty('db.driver');
	
	db = Sql.newInstance(dbUrl, username, password, driver);
	pageSize = 3000;
}

def getPageCount(){
	def sql= 'select count(1) as total from wd_question where status=1';
	long total = db.firstRow(sql).total;
	println 'total = ' + total;
	long pageCount = total / pageSize + (total % pageSize == 0 ? 0 : 1);
	println 'pageCount = ' + pageCount;
	return pageCount;
}

def getQuestionList(int pageNo){
	def arr = [];
	def sql = 'select id, create_at, title, has_content from wd_question where status=1 limit ?,?';
	def start = (pageNo-1)*pageSize;
	db.eachRow(sql, [start, pageSize]){
		def q = new Question();
		q.qid = it.id;
		q.modDate = it.create_at;
		q.title = it.title;
		if(it.has_content == true){
			q.descpt = getContent(it.id);
		}else{
			q.descpt = '';
		}
		arr << q;
	}
	return arr;
}

def getContent(def qid){
	def res = '';
	if(qid <= 0){
		return res;
	}
	def index = qid % 20;
	def sql = 'select content from wd_question_content_'+index+' where qid=? order by seq';
	db.eachRow(sql, [qid]){
		res += it.content;
	}
	return res;
}

def writeXml(def arr, def xmlFile){
    if(arr.size()==0)return;
    
    def strXml = new StringWriter();
    MarkupBuilder mb  = new groovy.xml.MarkupBuilder(strXml);
 
    mb.DOCUMENT{
    	arr.each{ q -> 
	        mb.item{
	        	key('')
	        	display{
	        		title{
	        			mb.getMkp().yieldUnescaped('<![CDATA['+q.title+']]>')
	        		}
	        		description{
	        			mb.getMkp().yieldUnescaped('<![CDATA['+q.descpt+']]>')
	        		}
	        		create_time(formatDate(q.modDate, 'yyyy-MM-dd HH:mm:ss'))
	        		from(new String('太平洋汽车网'.getBytes(), "UTF-8"))
	        		url('https://www.pcauto.com.cn/wd/'+q.qid+'.html')
	        	}
	        }
    	}
    }
    
    RandomAccessFile raf = new RandomAccessFile(xmlFile, "rw");
    if(raf.length()>70){
		raf.getChannel().truncate(raf.length()-'</DOCUMENT>'.length());
		raf.seek(raf.length());
		writeBigString(raf, strXml.toString().replace('<DOCUMENT>',''));
	}else{
		raf.getChannel().truncate(0);
		raf.write('<?xml version="1.0" encoding="utf-8"?>'.getBytes());
		writeBigString(raf, strXml.toString());
	}
	raf.close();
}

def writeIndexXml(int minIndex, int maxIndex, def defaultDir, def prefix, def xmlFile){
    if(maxIndex < minIndex)return;
    
    def now = new Date();
    def strXml = new StringWriter();
    MarkupBuilder mb  = new groovy.xml.MarkupBuilder(strXml);

    mb.sitemapindex{
    	for(int i=minIndex; i<=maxIndex; i++){
	        mb.sitemap{
	            loc('https://wenda.pcauto.com.cn/sitemap/tt/'+defaultDir+'/'+prefix+i+'.xml')
	            lastmod(formatDate(now, 'yyyy-MM-dd'))
	        }
    	}
    }
    
    RandomAccessFile raf = new RandomAccessFile(xmlFile, "rw");
    if(raf.length()>70){
		raf.getChannel().truncate(raf.length()-'</sitemapindex>'.length());
		raf.seek(raf.length());
		writeBigString(raf, strXml.toString().replace('<sitemapindex>',''));
	}else{
		raf.getChannel().truncate(0);
		raf.write('<?xml version="1.0" encoding="utf-8"?>'.getBytes());
		writeBigString(raf, strXml.toString());
	}
	raf.close();
}

def writeBigString(def raf,def str){
	def len = str.length();
	def size = 10240;
	if(len<=size){
		raf.write(str.getBytes("UTF-8"));
		return;
	}
	def f = len%size!=0;
	def i=0;
	for(;i<(int)(len/size);i++){
		raf.write(str.substring(i*size, i*size+size).getBytes("UTF-8"));
	}
	if(f){
		raf.write(str.substring(i*size, i*size+len%size).getBytes("UTF-8"));
	}
}

def formatDate(def date, def pattern){
	return new SimpleDateFormat(pattern).format(date);
}

def deleteFileList(def fileArr){
	if(fileArr == null || fileArr.length == 0){
		return;
	}
	for(File file : fileArr){
		if(file.isFile()){
			file.delete();
		}
	}
}

def closeDB(){
	if(db != null){
		db.close();
	}
}