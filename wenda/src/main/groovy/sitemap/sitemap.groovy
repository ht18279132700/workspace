import groovy.xml.MarkupBuilder
import groovy.sql.Sql
import java.text.SimpleDateFormat

def db;
int pageSize;
def dir1 = 'dir1';
def dir2 = 'dir2';
def defaultDir = dir1;
def xmlFilePrefix='question_';
int xmlFileIndex=1;

class Question{
	def qid;
	def modDate;
}


def fileDir1 = new File(dir1);
def fileDir2 = new File(dir2);
if(!fileDir1.exists()){
	fileDir1.mkdir();
}
if(!fileDir2.exists()){
	fileDir2.mkdir();
}

if(fileDir1.list().length == 0){
	defaultDir = dir1;
}else if(fileDir2.list().length == 0){
	defaultDir = dir2;
}


println formatDate(new Date(), 'yyyy-MM-dd HH:mm:ss,SSS') + ' program start ...';

try{
	init();
	def pageCount = getPageCount();
	for(int i=1; i<=pageCount; i++){
		def arr = getQuestionList(i);
		if(i%100 == 0){
			xmlFileIndex++;
		}
		writeXml(arr, defaultDir + '/' + xmlFilePrefix + xmlFileIndex + '.xml');
	}
	def tmpFile = new File('tmp.xml');
	writeIndexXml(1, xmlFileIndex, defaultDir, xmlFilePrefix, tmpFile);
	def f = new File('index.xml');
	if(f.exists()){
		f.delete();
	}
	tmpFile.renameTo(f);
	if(defaultDir == dir1){
		deleteFileList(fileDir2.listFiles());
	}else{
		deleteFileList(fileDir1.listFiles());
	}
}catch(Exception e){
	println 'error error error';
	e.printStackTrace();
}finally{
	if(db != null){
		db.close();
	}
}

println formatDate(new Date(), 'yyyy-MM-dd HH:mm:ss,SSS') + ' program end';
println '----------------------------------------------------------------';


def init(){
	def prop = new Properties();
	prop.load(new FileInputStream('../conf.properties'));
	
	def dbUrl = prop.getProperty('db.url');
	def username = prop.getProperty('db.username');
	def password = prop.getProperty('db.password');
	def driver = prop.getProperty('db.driver');
	
	db = Sql.newInstance(dbUrl, username, password, driver);
	pageSize = 100;
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
	def sql = 'select id, create_at, update_at, last_answer_at from wd_question where status=1 limit ?,?';
	def start = (pageNo-1)*pageSize;
	db.eachRow(sql, [start, pageSize]){
		def qid = it.id;
		def createAt = it.create_at;
		def updateAt = it.update_at;
		def lastAnswerAt = it.last_answer_at;
		def modDate = null;
		if(updateAt != null && lastAnswerAt != null){
			if(updateAt.after(lastAnswerAt)){
				modDate = updateAt;
			}else{
				modDate = lastAnswerAt;
			}
		}else if(updateAt != null){
			modDate = updateAt;
		}else if(lastAnswerAt != null){
			modDate = lastAnswerAt;
		}else if(createAt != null){
			modDate = createAt;
		}else{
			modDate = new Date();
		}
		def q = new Question();
		q.qid = qid;
		q.modDate = modDate;
		arr << q;
	}
	return arr;
}

def writeXml(def arr, def xmlFile){
    if(arr.size()==0)return;
    
    def strXml = new StringWriter();
    MarkupBuilder mb  = new groovy.xml.MarkupBuilder(strXml);
 
    mb.urlset{
    	arr.each{ q -> 
	        mb.url{
	            loc('https://www.pcauto.com.cn/wd/'+q.qid+'.html')
	            lastmod(formatDate(q.modDate, 'yyyy-MM-dd'))
	            changefreq('daily')
	            priority('0.8')
	        }
    	}
    }
    
    RandomAccessFile raf = new RandomAccessFile(xmlFile, "rw");
    if(raf.length()>56){
		raf.getChannel().truncate(raf.length()-'</urlset>'.length());
		raf.seek(raf.length());
		writeBigString(raf, strXml.toString().replace('<urlset>',''));
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
	            loc('https://wenda.pcauto.com.cn/sitemap/'+defaultDir+'/'+prefix+i+'.xml')
	            lastmod(formatDate(now, 'yyyy-MM-dd'))
	        }
    	}
    }
    
    RandomAccessFile raf = new RandomAccessFile(xmlFile, "rw");
    if(raf.length()>56){
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
		raf.write(str.getBytes());
		return;
	}
	def f = len%size!=0;
	def i=0;
	for(;i<(int)(len/size);i++){
		raf.write(str.substring(i*size, i*size+size).getBytes());
	}
	if(f){
		raf.write(str.substring(i*size, i*size+len%size).getBytes());
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