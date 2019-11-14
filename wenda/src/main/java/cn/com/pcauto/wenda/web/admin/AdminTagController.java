package cn.com.pcauto.wenda.web.admin;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.gelivable.auth.entity.GeliSession;
import org.gelivable.param.OrderBy;
import org.gelivable.param.QueryParam;
import org.gelivable.param.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.pcauto.wenda.config.SystemConfig;
import cn.com.pcauto.wenda.entity.Tag;
import cn.com.pcauto.wenda.service.TagService;
import cn.com.pcauto.wenda.util.Pager;
import cn.com.pcauto.wenda.util.TagType;
import cn.com.pcauto.wenda.util.WebPrintUtils;
import cn.com.pcauto.wenda.util.WebUtils;
import cn.com.pcauto.wenda.util.excel.ExcelHelper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value = "/admin/tag")
public class AdminTagController {

	@Autowired
	private TagService tagService;
	@Autowired
	private SystemConfig systemConfig;
	
	private final static Logger LOG = LoggerFactory.getLogger(AdminTagController.class);
	private static final String Brand = "Brand";
	private static final String Keyword = "Keyword";
	
	@RequestMapping(value = "/brandList")
	public String BrandList(HttpServletRequest request, HttpServletResponse response){
		int pageNo = WebUtils.paramInt(request, "pageNo", 1);
		int pageSize = WebUtils.paramInt(request, "pageSize", 20);
				
		String name = WebUtils.param(request, "name", "");
		long tid = WebUtils.paramLong(request, "tid", 0);
		int status = WebUtils.paramInt(request, "status", 99);
		
		QueryParam param = new QueryParam();
		param.and("tag_type",TagType.BRAND.getName());
		
		if (StringUtils.isNotBlank(name)) {
			param.and("name", name);
			request.setAttribute("name", name);
		}
		if (tid > 0) {
			param.and("id", tid);
			request.setAttribute("tid", tid);
		}
		if (status != 99) {
			param.and("status", status);
			request.setAttribute("status", status);
		}
		param.orderBy("letter", OrderBy.ASC);
		Pager<Tag> pager = tagService.pager(pageNo, pageSize, param);
		
		
		request.setAttribute("pager", pager);
		return "/admin/tag/brandList";
	}
	
	@RequestMapping(value = "/createBrandTag")
	public void CreateBrandTag(HttpServletRequest request, HttpServletResponse response){
		long brandID = WebUtils.paramLong(request, "brandID", 0);
		if (brandID <= 0) {
			WebPrintUtils.errorMsg(request, response, "品牌ID错误");
			return;
		}
		String name = WebUtils.param(request, "name", "");
		if (StringUtils.isBlank(name)) {
			WebPrintUtils.errorMsg(request, response, "车系名称为空");
			return;
		}
		String letter = WebUtils.param(request, "letter", "");
		if (StringUtils.isBlank(letter)) {
			WebPrintUtils.errorMsg(request, response, "首字母为空");
			return;
		}
		int status = WebUtils.paramInt(request, "status", 99);
		if(status == 99){
			WebPrintUtils.errorMsg(request, response, "车系状态错误");
			return;
		}
		long userId = GeliSession.getCurrentUser().getUserId();
		Tag tag = new Tag();
		tag.setTagType(TagType.BRAND.getName());
		tag.setName(name);
		tag.setPid(0);
		tag.setLetter(letter);
		tag.setBrandId(brandID);
		tag.setSerialId(0);
		tag.setStatus(status);
		tag.setCreateAt(new Date());
		tag.setCreateBy(userId);
		long flag = tagService.createTag(tag);
		if(flag > 0){
			WebPrintUtils.successMsg(request, response);
		}else {
			WebPrintUtils.errorMsg(request, response, "创建品牌失败");
		}
		
	}
	
	@RequestMapping(value = "/updateTag")
	public void UpdateBrandTag(HttpServletRequest request, HttpServletResponse response){
		long tagID = WebUtils.paramLong(request, "tagID", 0);
		Tag tag = tagService.findById(tagID);
		if (tag == null) {
			WebPrintUtils.errorMsg(request, response, "品牌ID错误");
			return;
		}
		String name = WebUtils.param(request, "name", "");
		if (StringUtils.isBlank(name)) {
			WebPrintUtils.errorMsg(request, response, "品牌名称为空");
			return;
		}
		int status = WebUtils.paramInt(request, "status", 99);
		if(status == 99){
			WebPrintUtils.errorMsg(request, response, "品牌状态错误");
			return;
		}
		long userId = GeliSession.getCurrentUser().getUserId();
		tag.setName(name);
		tag.setStatus(status);
		tag.setUpdateAt(new Date());
		tag.setUpdateBy(userId);
		int flag = tagService.update(tag, "name,status,updateAt,updateBy");
		if (flag > 0) {
			WebPrintUtils.successMsg(request, response);
		}else {
			WebPrintUtils.errorMsg(request, response, "标签更新失败");
		}
	}
	
	@RequestMapping(value = "/brandOrFirstExport")
	public void BrandExport(HttpServletRequest request, HttpServletResponse response){
		String type = WebUtils.param(request, "type", "");
		if (StringUtils.isBlank(type)) {
			WebPrintUtils.errorMsg(request, response, "标签类型为空");
			return;
		}
		String name = WebUtils.param(request, "name", "");
		long id = WebUtils.paramLong(request, "id", 0);
		int status = WebUtils.paramInt(request, "status", 99);
		QueryParam param = new QueryParam();
		if (TagType.BRAND.getName().equals(type)) {
			param.and("tag_type", TagType.BRAND.getName());
			param.orderBy("letter", OrderBy.ASC);
		}else {
			param.and("tag_type", TagType.LEVEL1.getName());
		}
		if (StringUtils.isNotBlank(name)) {
			param.and("name", name);
		}
		if (id > 0) {
			param.and("id",id);
		}
		if (status != 99) {
			param.and("status",status);
		}
		//获取标签列表
		List<Tag> list = tagService.listByParam(param);
		List<Object[]> tagData = new ArrayList<Object[]>();
		if (TagType.BRAND.getName().equals(type)) {
			Object[] heads = {"品牌名称", "ID", "状态"};
			tagData.add(heads);
		}else {
			Object[] heads = {"一级分类名称", "ID", "状态"};
			tagData.add(heads);
		}
		for (Tag tag : list) {
			Object[] tagArr = {tag.getName(), tag.getId(), tag.getStatus()};
			tagData.add(tagArr);
		}
		//获取标签下的子标签列表
		List<Long> listTagID = tagService.listTagID(param);
		List<Tag> listChildTag = tagService.listChildTag(listTagID);
		List<Object[]> tagChileData = new ArrayList<Object[]>();
		if (TagType.BRAND.getName().equals(type)) {
			Object[] headsChild = {"车系名称", "ID", "所属品牌", "状态"};
			tagChileData.add(headsChild);
		}else {
			Object[] headsChild = {"二级分类名称", "ID", "所属一级分类", "状态"};
			tagChileData.add(headsChild);
		}
		if (listChildTag != null) {
			for (Tag tag : listChildTag) {
				Tag pidTag = tagService.findById(tag.getPid());
				if (pidTag == null) {
					continue;
				}
				Object[] tagChildArr = {tag.getName(), tag.getId(),pidTag.getName(), tag.getStatus()};
				tagChileData.add(tagChildArr);
			}
		}
		
		ExcelHelper excelHelper = new ExcelHelper("标签列表");
		if (TagType.BRAND.getName().equals(type)) {
			excelHelper.addSheetData("品牌标签", tagData);
			excelHelper.addSheetData("车系标签", tagChileData);
		}else {
			excelHelper.addSheetData("一级标签分类", tagData);
			excelHelper.addSheetData("二级标签分类", tagChileData);
		}
		try {
			excelHelper.exportToExcel(response);
		} catch (Exception e) {
			LOG.error("导出品牌或一级标签错误："+e);
		}
	}

	@RequestMapping(value = "/serialList")
	public String SerialList(HttpServletRequest request, HttpServletResponse response){
		long brandID = WebUtils.paramLong(request, "brandID", 0);
		Tag tag = tagService.findById(brandID);
		if (tag == null) {
			WebPrintUtils.errorMsg(request, response, "品牌标签ID错误");
			return null;
		}
		int pageNo = WebUtils.paramInt(request, "pageNo", 1);
		int pageSize = WebUtils.paramInt(request, "pageSize", 20);
				
		String name = WebUtils.param(request, "name", "");
		long tid = WebUtils.paramLong(request, "tid", 0);
		int status = WebUtils.paramInt(request, "status", 99);
		
		QueryParam param = new QueryParam();
		param.and("tag_type",TagType.SERIAL.getName());
		param.and("pid", brandID);
		
		if (StringUtils.isNotBlank(name)) {
			param.and("name", name);
			request.setAttribute("name", name);
		}
		if (tid > 0) {
			param.and("id", tid);
			request.setAttribute("tid", tid);
		}
		if (status != 99) {
			param.and("status", status);
			request.setAttribute("status", status);
		}
		param.orderBy("letter", OrderBy.ASC);
		Pager<Tag> pager = tagService.pager(pageNo, pageSize, param);
		
		
		request.setAttribute("pager", pager);
		request.setAttribute("tag", tag);
		return "/admin/tag/serialList";
	}
	
	@RequestMapping(value = "/createSerialTag")
	public void CreateSerialTag(HttpServletRequest request, HttpServletResponse response){
		long brandID = WebUtils.paramLong(request, "brandID", 0);
		if (brandID <= 0) {
			WebPrintUtils.errorMsg(request, response, "品牌ID错误");
			return;
		}
		String name = WebUtils.param(request, "name", "");
		if (StringUtils.isBlank(name)) {
			WebPrintUtils.errorMsg(request, response, "车系名称为空");
			return;
		}
		String letter = WebUtils.param(request, "letter", "");
		if (StringUtils.isBlank(letter)) {
			WebPrintUtils.errorMsg(request, response, "首字母为空");
			return;
		}
		long serialID = WebUtils.paramLong(request, "serialID", 0);
		if (serialID <= 0) {
			WebPrintUtils.errorMsg(request, response, "车系ID错误");
			return;
		}
		int status = WebUtils.paramInt(request, "status", 99);
		if(status == 99){
			WebPrintUtils.errorMsg(request, response, "车系状态错误");
			return;
		}
		long userId = GeliSession.getCurrentUser().getUserId();
		Tag tag = new Tag();
		tag.setTagType(TagType.SERIAL.getName());
		tag.setName(name);
		tag.setPid(brandID);
		tag.setLetter(letter);
		tag.setSerialId(serialID);
		tag.setStatus(status);
		tag.setCreateAt(new Date());
		tag.setCreateBy(userId);
		long flag = tagService.createTag(tag);
		if(flag > 0){
			WebPrintUtils.successMsg(request, response);
		}else {
			WebPrintUtils.errorMsg(request, response, "创建车系失败");
		}
	}
	
	@RequestMapping(value = "/exportSerialOrSecond")
	public void ExportSerial(HttpServletRequest request, HttpServletResponse response){
		long pid = WebUtils.paramLong(request, "tagID", 0);
		Tag tag = tagService.findById(pid);
		if (tag == null) {
			WebPrintUtils.errorMsg(request, response, "标签ID错误");
			return;
		}
		String type = WebUtils.param(request, "type", "");
		if (StringUtils.isBlank(type)) {
			WebPrintUtils.errorMsg(request, response, "标签类型为空");
			return;
		}
		String name = WebUtils.param(request, "name", "");
		long id = WebUtils.paramLong(request, "id", 0);
		int status = WebUtils.paramInt(request, "status", 99);
		QueryParam param = new QueryParam();
		param.and("pid", pid);
		if (StringUtils.isNotBlank(name)) {
			param.and("name",name);
		}
		if (id > 0) {
			param.and("id", id);
		}
		if (status != 99) {
			param.and("status", status);
		}
		param.orderBy("letter", OrderBy.ASC);
		PrintWriter out = null;
		try {
			ArrayList<String> arrayList = null;
			response.setContentType("application/octet-stream");
			if (TagType.SERIAL.getName().equals(type)) {
				response.setHeader("Content-Disposition", "attachment;" + "filename=serialTag.csv");
				arrayList = new ArrayList<String>(Arrays.asList("车系名称","ID","所属品牌","状态"));
			}else {
				response.setHeader("Content-Disposition", "attachment;" + "filename=secondTag.csv");
				arrayList = new ArrayList<String>(Arrays.asList("二级分类名称","ID","所属一级分类","状态"));
			}
			out = new PrintWriter(response.getOutputStream());
			StringBuilder header = new StringBuilder();
			for (String word : arrayList) {
				header.append(word).append(",");
			}
			out.println(header);
			int pageNo = 1;
			int pageSize = 1000000;
			while(true){
    			String data = tagService.exportSerialOrSecond(param, pageNo, pageSize);
    			if(StringUtils.isBlank(data)) {
    				break;
    			}
    			pageNo++;
    			out.println(data);
    		}
		} catch (Exception e) {
			LOG.error("导出车系标签错误："+e);
		}finally{
			out.flush();
			out.close();
		}
	}
	
	@RequestMapping(value = "/hotTag")
	public String hotTag(HttpServletRequest request, HttpServletResponse response){
		int pageNo = WebUtils.paramInt(request, "pageNo", 1);
		int pageSize = WebUtils.paramInt(request, "pageSize", 20);
				
		String name = WebUtils.param(request, "name", "");
		long tid = WebUtils.paramLong(request, "tid", 0);
		int status = WebUtils.paramInt(request, "status", 99);
		
		QueryParam param = new QueryParam();

		if (StringUtils.isNotBlank(name)) {
			param.and(String.format("locate(',%s,', keywords)", name), Relation.GT, 0);
			request.setAttribute("name", name);
		}
		if (tid > 0) {
			param.and("id", tid);
			request.setAttribute("tid", tid);
		}
		if (status != 99) {
			param.and("status", status);
			request.setAttribute("status", status);
		}
		param.orderBy("question_num");
		Pager<Tag> pager = tagService.pager(pageNo, pageSize, param);
		
		
		request.setAttribute("pager", pager);
		return "/admin/tag/hotTag";
	}
	
	@RequestMapping(value = "/exportHotTag")
	public void exportHotTag(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		String name = WebUtils.param(request, "name", "");
		long tid = WebUtils.paramLong(request, "tid", 0);
		int status = WebUtils.paramInt(request, "status", 99);
		
		QueryParam param = new QueryParam();
		param.and("question_num", Relation.GT, 0);

		if (StringUtils.isNotBlank(name)) {
			param.and(String.format("locate(',%s,', keywords)", name), Relation.GT, 0);
			request.setAttribute("name", name);
		}
		if (tid > 0) {
			param.and("id", tid);
			request.setAttribute("tid", tid);
		}
		if (status != 99) {
			param.and("status", status);
			request.setAttribute("status", status);
		}
		param.orderBy("question_num");
		List<Tag> list = tagService.listByParam(param);
		
		List<Object[]> data = new ArrayList<Object[]>();
		data.add(new String[]{"标签名称", "ID", "keywords", "热度值"});
		
		for (Tag tag : list) {
			if(tag == null) continue;
			data.add(new Object[]{tag.getName(), tag.getId(), tag.getKeywords(), tag.getQuestionNum()});
		}		
		
		ExcelHelper helper = new ExcelHelper("热词");
		helper.setColumnWidth(30);
		helper.addSheetData("Sheet1", data);
		helper.exportToExcel(response);
	}
	
	@RequestMapping(value = "/firstList")
	public String keywordList(HttpServletRequest request, HttpServletResponse response){
		int pageNo = WebUtils.paramInt(request, "pageNo", 1);
		int pageSize = WebUtils.paramInt(request, "pageSize", 20);
				
		String name = WebUtils.param(request, "name", "");
		long tid = WebUtils.paramLong(request, "tid", 0);
		int status = WebUtils.paramInt(request, "status", 99);
		
		QueryParam param = new QueryParam();
		param.and("tag_type",TagType.LEVEL1.getName());

		if (StringUtils.isNotBlank(name)) {
			param.and("name", name);
			request.setAttribute("name", name);
		}
		if (tid > 0) {
			param.and("id", tid);
			request.setAttribute("tid", tid);
		}
		if (status != 99) {
			param.and("status", status);
			request.setAttribute("status", status);
		}
		param.orderBy("letter", OrderBy.ASC);
		Pager<Tag> pager = tagService.pager(pageNo, pageSize, param);
		
		
		request.setAttribute("pager", pager);
		return "/admin/tag/firstList";
	}
	
	@RequestMapping(value = "/createFirstTag")
	public void CreateFirstTag(HttpServletRequest request, HttpServletResponse response){
		String name = WebUtils.param(request, "name", "");
		if (StringUtils.isBlank(name)) {
			WebPrintUtils.errorMsg(request, response, "一级标签名字错误");
			return;
		}
		int status = WebUtils.paramInt(request, "status", 99);
		if (status == 99) {
			WebPrintUtils.errorMsg(request, response, "一级标签状态错误");
			return;
		}
		long userId = GeliSession.getCurrentUser().getUserId();
		Tag tag = new Tag();
		tag.setTagType(TagType.LEVEL1.getName());
		tag.setName(name);
		tag.setPid(0);
		tag.setBrandId(0);
		tag.setSerialId(0);
		tag.setStatus(status);
		tag.setCreateAt(new Date());
		tag.setCreateBy(userId);
		long flag = tagService.createTag(tag);
		if (flag > 0) {
			WebPrintUtils.successMsg(request, response);
		}else {
			WebPrintUtils.errorMsg(request, response, "创建一级标签失败");
		}
	}
	
	@RequestMapping(value = "/createSecondTag")
	public void CreateSecondTag(HttpServletRequest request,HttpServletResponse response){
		long pid = WebUtils.paramLong(request, "pid", 0);
		if (pid <= 0) {
			WebPrintUtils.errorMsg(request, response, "父标签ID错误");
			return;
		}
		String name = WebUtils.param(request, "name", "");
		if (StringUtils.isBlank(name)) {
			WebPrintUtils.errorMsg(request, response, "一级标签名字错误");
			return;
		}
		int status = WebUtils.paramInt(request, "status", 99);
		if (status == 99) {
			WebPrintUtils.errorMsg(request, response, "一级标签状态错误");
			return;
		}
		long userId = GeliSession.getCurrentUser().getUserId();
		Tag tag = new Tag();
		tag.setTagType(TagType.LEVEL2.getName());
		tag.setName(name);
		tag.setPid(pid);
		tag.setBrandId(0);
		tag.setSerialId(0);
		tag.setStatus(status);
		tag.setCreateAt(new Date());
		tag.setCreateBy(userId);
		long flag = tagService.createTag(tag);
		if (flag > 0) {
			WebPrintUtils.successMsg(request, response);
		}else {
			WebPrintUtils.errorMsg(request, response, "创建一级标签失败");
		}
	}
	
	@RequestMapping(value = "/secondList")
	public String SecondList(HttpServletRequest request, HttpServletResponse response){
		long firstID = WebUtils.paramLong(request, "firstID", 0);
		Tag tag = tagService.findById(firstID);
		if (tag == null) {
			WebPrintUtils.errorMsg(request, response, "一级标签ID错误");
			return null;
		}
		int pageNo = WebUtils.paramInt(request, "pageNo", 1);
		int pageSize = WebUtils.paramInt(request, "pageSize", 20);
				
		String name = WebUtils.param(request, "name", "");
		long tid = WebUtils.paramLong(request, "tid", 0);
		int status = WebUtils.paramInt(request, "status", 99);
		
		QueryParam param = new QueryParam();
		param.and("tag_type",TagType.LEVEL2.getName());
		param.and("pid", firstID);
		
		if (StringUtils.isNotBlank(name)) {
			param.and("name", name);
			request.setAttribute("name", name);
		}
		if (tid > 0) {
			param.and("id", tid);
			request.setAttribute("tid", tid);
		}
		if (status != 99) {
			param.and("status", status);
			request.setAttribute("status", status);
		}
		param.orderBy("letter", OrderBy.ASC);
		Pager<Tag> pager = tagService.pager(pageNo, pageSize, param);
		
		
		request.setAttribute("pager", pager);
		request.setAttribute("tag", tag);
		return "/admin/tag/secondList";
	}
	
	@RequestMapping(value = "/getTags")
	public void GetTags(HttpServletRequest request, HttpServletResponse response){
		String tagStatus = WebUtils.param(request, "tagStatus", "");
		long tid = WebUtils.paramLong(request, "id", 0);
		if (StringUtils.isBlank(tagStatus)) {
			WebUtils.send404(response, "标签页的标识错误");
			return;
		}
		List<Tag> listTag = new ArrayList<Tag>();
		if (tid > 0) {
			listTag = tagService.listForPid(tid);
		}else {
			if (Brand.equals(tagStatus)) {
				listTag = tagService.listForType(TagType.BRAND.getName());
			}else {
				listTag = tagService.listForType(TagType.LEVEL1.getName());
			}
		}
		JSONArray tagArray = new JSONArray();
		if (listTag != null) {
			for (Tag tag : listTag) {
				JSONObject tagObject = new JSONObject();
				tagObject.put("name", tag.getName());
				tagObject.put("id", tag.getId());
				if (Brand.equals(tagStatus)) {
					tagObject.put("target", "brandQuestion");
				}else {
					tagObject.put("target", "keywordQuestion");
				}
				tagObject.put("url", "/admin/question/question.do?tid="+tag.getId());
				if (tid == 0) {
					tagObject.put("isParent", true);
				}
				tagArray.add(tagObject);
			}
		}
		WebPrintUtils.cbMsg(request, response, tagArray.toString());
	}
	
	@RequestMapping(value = "/deleteTagCache")
	public void DeleteTagCache(HttpServletRequest request, HttpServletResponse response){
		tagService.deleteTagCache();
		WebPrintUtils.successMsg(request, response);
	}
}
