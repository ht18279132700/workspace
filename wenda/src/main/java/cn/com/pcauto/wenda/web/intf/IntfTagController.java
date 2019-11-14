package cn.com.pcauto.wenda.web.intf;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.danga.MemCached.MemCachedClient;

import cn.com.pcauto.wenda.entity.Question;
import cn.com.pcauto.wenda.entity.Tag;
import cn.com.pcauto.wenda.service.TagService;
import cn.com.pcauto.wenda.util.Functions;
import cn.com.pcauto.wenda.util.Pager;
import cn.com.pcauto.wenda.util.TagType;
import cn.com.pcauto.wenda.util.WebPrintUtils;
import cn.com.pcauto.wenda.util.WebUtils;

@Controller
@RequestMapping(value = "intf/tag")
public class IntfTagController {

	@Autowired
	private TagService tagService;
	@Autowired
	private MemCachedClient mcc; 
	
	public static final String Brand = Tag.class.getSimpleName() + "-brand";
	public static final String Serial = Tag.class.getSimpleName() + "-serial";
	
	/**
	 * 发表问题页，品牌的列表数据接口
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/brand", method = RequestMethod.GET)
	public void Brand(HttpServletRequest request, HttpServletResponse response){
		Functions.setAllowCredentialHeader(request, response);
		JSONObject mcJson = (JSONObject) mcc.get(Brand);
		if (mcJson != null && mcJson.size() > 0) {
			WebPrintUtils.successMsg(request, response, mcJson);
			return;
		}
		JSONObject json = new JSONObject();
		List<Tag> listBrand = tagService.listForType("B");
		JSONObject jsonObject = new JSONObject();
		JSONArray brandArray = new JSONArray();
		String letter = listBrand.get(0).getLetter();
		for (Tag tag : listBrand) {
			JSONObject brandObject = new JSONObject();
			brandObject.put("id", tag.getId());
			brandObject.put("letter", tag.getLetter());
			brandObject.put("name", tag.getName());
			if (letter.equals(tag.getLetter())) {
				brandArray.add(brandObject);
			}else {
				jsonObject.put(letter, brandArray);
				brandArray = new JSONArray();
				brandArray.add(brandObject);
			}
			letter = tag.getLetter();
		}
		jsonObject.put(letter, brandArray);

		json.put("data", jsonObject);
		if (!jsonObject.isEmpty()) {
			mcc.set(Brand, json);
			WebPrintUtils.successMsg(request, response, json);
		}else {
			WebPrintUtils.errorMsg(request, response, "获取品牌失败");
		}
	}
	/**
	 *发布问题页，车系列表的数据
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/serial", method = RequestMethod.GET)
	public void Serial(HttpServletRequest request, HttpServletResponse response){
		Functions.setAllowCredentialHeader(request, response);
		long Bid = WebUtils.paramLong(request, "Bid", 0);
		if (Bid < 1) {
			WebPrintUtils.errorMsg(request, response, "品牌ID错误");
			return;
		}
		JSONObject mcJson = (JSONObject) mcc.get(Serial+"-"+Bid);
		if (mcJson != null && mcJson.size() > 0) {
			WebPrintUtils.successMsg(request, response, mcJson);
			return;
		}
		JSONObject json = new JSONObject();
		List<Tag> listSerial = tagService.listForPid(Bid);
		JSONArray serialArray = new JSONArray();
		for (Tag tagS : listSerial) {
			JSONObject serailObject = new JSONObject();
			serailObject.put("id", tagS.getId());
			serailObject.put("letter", tagS.getLetter());
			serailObject.put("name", tagS.getName());
			serialArray.add(serailObject);
		}
		json.put("data", serialArray);
		if (!serialArray.isEmpty()) {
			mcc.set(Serial+"-"+Bid, json);
			WebPrintUtils.successMsg(request, response, json);
		}else {
			WebPrintUtils.errorMsg(request, response, "获取车系失败");
		}
	}
	
	/**
	 * 发表问题页，标题和内容相关联的标签接口
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/relateTag", method = RequestMethod.GET)
	public void getRelateTag(HttpServletRequest request, HttpServletResponse response)throws IOException{
		Functions.setCacheHeader(response, 24*60*60);
		Functions.setAllowCredentialHeader(request, response);
		
		String content = request.getParameter("content");
		if (StringUtils.isBlank(content)) {
			WebPrintUtils.errorMsg(request, response, "内容不能为空");
			return;
		}
		content = URLDecoder.decode(content, "UTF-8");
		/*
		JSONObject json = tagService.getMCTag();
		if (json == null) {
			WebPrintUtils.errorMsg(request, response, "获取标签失败");
			return;
		}
		JSONObject returnObject = new JSONObject();
		
		returnObject.put("data", inftRelateTagList(json, content.toUpperCase()));
		WebPrintUtils.successMsg(request, response, returnObject);
		*/
		
		// 2019-07-18 新增了16万标签，直接在数据库模糊匹配速度更快
		JSONObject returnObject = new JSONObject();
		List<Tag> list = tagService.matchTagByTitle(content);
		returnObject.put("data", classifyTag(list));
		WebPrintUtils.successMsg(request, response, returnObject);
	}
	
	// 把标签按照品牌、车系、关键词标签分类一下
	public JSONObject classifyTag(List<Tag> list){
		JSONArray brandArray = new JSONArray();
		JSONArray serialArray = new JSONArray();
		JSONArray sortArray = new JSONArray();
		for (Tag tag : list) {
			if(tag == null){
				continue;
			}
			JSONObject json = new JSONObject();
			json.put("id", tag.getId());
			json.put("name", tag.getName());
			
			if(TagType.BRAND.getName().equals(tag.getTagType())){
				brandArray.add(json);
			}else if(TagType.SERIAL.getName().equals(tag.getTagType())){
				serialArray.add(json);
			}else if(TagType.LEVEL1.getName().equals(tag.getTagType())
					|| TagType.LEVEL2.getName().equals(tag.getTagType())){
				sortArray.add(json);
			}
		}
		JSONObject resultObject = new JSONObject();
		resultObject.put("brand", brandArray);
		resultObject.put("serial", serialArray);
		resultObject.put("sort", sortArray);
		return resultObject;
	}
	
	public JSONObject inftRelateTagList(JSONObject json,String content){
		JSONArray tagList = json.getJSONArray("data");
		JSONObject resultObject = new JSONObject();
		JSONArray brandArray = new JSONArray();
		JSONArray serialArray = new JSONArray();
		JSONArray sortArray = new JSONArray();
		for (Object object : tagList) {
			JSONObject tagObject = (JSONObject) object;
			int indexOf = content.indexOf(tagObject.getString("name").toUpperCase());
			if (indexOf >= 0) {
				JSONObject indexObject = new JSONObject();
				indexObject.put("id", tagObject.getLongValue("id"));
				indexObject.put("name", tagObject.getString("name"));
				if ("B".equals(tagObject.getString("tagType"))) {
					brandArray.add(indexObject);
				}
				if ("S".equals(tagObject.getString("tagType"))) {
					serialArray.add(indexObject);
				}
				if ("1".equals(tagObject.getString("tagType")) || "2".equals(tagObject.getString("tagType"))) {
					sortArray.add(indexObject);
				}
			}
		}
		resultObject.put("brand", brandArray);
		resultObject.put("serial", serialArray);
		resultObject.put("sort", sortArray);
		return resultObject;
	}

	/**
	 * 发表问题页，问题分类相关联的标签接口
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/relateTagSort")
	public void getRelateTagSort(HttpServletRequest request, HttpServletResponse response)throws IOException{
		Functions.setCacheHeader(response, 24*60*60);
		Functions.setAllowCredentialHeader(request, response);
		
		String content = request.getParameter("content");
		if (StringUtils.isBlank(content)) {
			WebPrintUtils.errorMsg(request, response, "内容不能为空");
			return;
		}
		content = URLDecoder.decode(content, "UTF-8").toUpperCase();
		/*
		JSONObject tagSort = tagService.getRelateTagSort();
		if (tagSort == null) {
			WebPrintUtils.errorMsg(request, response, "获取分类标签失败");
			return;
		}
		JSONObject returnObject = new JSONObject();
		JSONArray resultArray = new JSONArray();
		JSONArray tagList = tagSort.getJSONArray("data");
		for (Object object : tagList) {
			JSONObject tagObject = (JSONObject) object;
			String name = tagObject.getString("name").toUpperCase();
			int indexOf = name.indexOf(content);
			if (indexOf >= 0) {
				resultArray.add(tagObject);
			}
		}
		returnObject.put("data", resultArray);
		WebPrintUtils.successMsg(request, response, returnObject);	
		*/
		
		// 2019-07-18 新增了16万标签，直接在数据库模糊匹配速度更快
		// 新增标签后，随便输入一个字都能匹配到几千个，此处需要控制一下数量，暂定50个
		List<Tag> list = tagService.matchTagByKeyword(content, 50);
		JSONArray array = new JSONArray();
		for (Tag tag : list) {
			if(tag == null) continue;
			JSONObject jo = new JSONObject();
			jo.put("id", tag.getId());
			jo.put("name", tag.getName());
			array.add(jo);
		}
		JSONObject json = new JSONObject();
		json.put("data", array);
		WebPrintUtils.successMsg(request, response, json);
	}
	
	/**
	 * 热门话题
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/hotTag")
	public void hotTag(HttpServletRequest request, HttpServletResponse response){
		Functions.setAllowCredentialHeader(request, response);
		//热门话题
		List<Tag> tags = tagService.listHotTagDaily();
		if (tags == null) {
			tags = tagService.listTagForNum(10);
		}
		if (tags !=null && tags.size() >0) {
			JSONArray array = new JSONArray();
			for (Tag tag : tags) {
				JSONObject object = new JSONObject();
				object.put("id", tag.getId());
				object.put("name", tag.getName());
				array.add(object);
			}
			JSONObject json = new JSONObject();
			json.put("data", array);
			WebPrintUtils.successMsg(request, response, json);
		}else {
			WebPrintUtils.errorMsg(request, response,"热门话题为空");
		}
	}
	
	@RequestMapping(value = "/relateTags")
	public void relateTags(HttpServletRequest request, HttpServletResponse response){
		Functions.setAllowCredentialHeader(request, response);
		long tid = WebUtils.paramTagId(request);
		//话题标签
		Tag tag = tagService.findById(tid);
		if (tag == null) {
			WebPrintUtils.errorMsg(request, response, "标签错误");
			return;
		}
		//相关话题标签的规则
		List<Tag> relateTags = tagService.listRelateTag2(tag, 12);
		if (relateTags !=null && relateTags.size() >0) {
			JSONArray array = new JSONArray();
			for (Tag t : relateTags) {
				JSONObject object = new JSONObject();
				object.put("id", t.getId());
				object.put("name", t.getName());
				array.add(object);
			}
			JSONObject json = new JSONObject();
			json.put("data", array);
			WebPrintUtils.successMsg(request, response, json);
		}else {
			WebPrintUtils.errorMsg(request, response,"相关话题标签为空");
		}
	}

}
