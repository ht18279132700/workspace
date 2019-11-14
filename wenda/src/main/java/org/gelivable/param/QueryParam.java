package org.gelivable.param;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class QueryParam {

	private LinkedHashMap<String, OrderBy> orderbyMap = new LinkedHashMap<String, OrderBy>();
	private StringBuilder condition = new StringBuilder();
	private List<Object> params = new ArrayList<Object>();
	private String whereSql;
	private String orderBy;
	
	private static final String TYPE_AND = " AND ";
	private static final String TYPE_OR = " OR ";
	private static final String TYPE_IN = " IN ";
	private static final String TYPE_NOT_IN = " NOT IN ";
	
	public void and(String name, Object value){
		and(name, Relation.EQ, value);
	}
	
	public void and(String name, Relation relation){
		and(name, relation, null);
	}
	
	public void and(String name, Relation relation, Object value){
		addParam(TYPE_AND, name, relation, value);
	}
	
	public void and(OrParam... orParams){
		addOrParam(TYPE_AND, orParams);
	}
	
	public void or(String name, Object value){
		or(name, Relation.EQ, value);
	}
	
	public void or(String name, Relation relation){
		or(name, relation, null);
	}
	
	public void or(String name, Relation relation, Object value){
		addParam(TYPE_OR, name, relation, value);
	}
	
	public void or(OrParam... orParams){
		addOrParam(TYPE_OR, orParams);
	}
	
	public void andIn(String name, Collection<? extends Object> collection){
		addInParam(TYPE_AND, TYPE_IN, name, collection);
	}
	
	public void andNotIn(String name, Collection<? extends Object> collection){
		addInParam(TYPE_AND, TYPE_NOT_IN, name, collection);
	}
	
	public void orIn(String name, Collection<? extends Object> collection){
		addInParam(TYPE_OR, TYPE_IN, name, collection);
	}
	
	public void orNotIn(String name, Collection<? extends Object> collection){
		addInParam(TYPE_OR, TYPE_NOT_IN, name, collection);
	}
	
	public void orderBy(String orderbyField){
		orderBy(orderbyField, OrderBy.DESC);
	}
	
	public void orderBy(String orderbyField, OrderBy ascOrDesc){
		orderbyMap.put(orderbyField, ascOrDesc);
	}
	
	
	public String getWhereSql(){
		if(whereSql == null){
			if(condition.length() == 0){
				whereSql = "";
			}else{
				whereSql = concatWhereSql();
			}
		}
		return whereSql;
	}
	
	public String getOrderBy(){
		if(orderBy == null){
			if(orderbyMap.isEmpty()){
				orderBy = "";
			}else{
				orderBy = concatOrderBy();
			}
		}
		return orderBy;
	}

	public List<Object> getParams(){
		return params;
	}
	
	
	private void addParam(String type, String name, Relation relation, Object value){
		if(Relation.IS_NULL == relation || Relation.IS_NOT_NULL == relation){
			condition.append(type).append(name).append(relation.getValue());
		}else if(value != null){
			condition.append(type).append(name).append(relation.getValue());
			params.add(value);
		}
	}
	
	private void addInParam(String type1, String type2, String name, Collection<? extends Object> collection){
		if(collection == null || collection.isEmpty()){
			return;
		}
		condition.append(type1).append(name).append(type2).append("(");
		for (Object value : collection) {
			if(value instanceof Relation){
				throw new IllegalArgumentException();
			}
			condition.append("?,");
			params.add(value);
		}
		condition.setCharAt(condition.length()-1, ')');
	}
	
	private void addOrParam(String type, OrParam... orParams){
		if(orParams == null || orParams.length == 0){
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (OrParam orParam : orParams) {
			StringBuilder sql = orParam.getSql();
			if(sql.length() == 0){
				continue;
			}
			if(orParam.getValue() != null){
				params.add(orParam.getValue());
			}
			sb.append(TYPE_OR).append(sql);
		}
		if(sb.length() > 0){
			sb.append(")");
			condition.append(sb.toString().replaceFirst(TYPE_OR, type + "("));
		}
	}

	private String concatWhereSql() {
		return condition.toString().replaceFirst("AND|OR", "WHERE");
	}
	
	private String concatOrderBy() {
		StringBuilder sb = new StringBuilder(" ORDER BY ");
		Set<String> keySet = orderbyMap.keySet();
		for (String key : keySet) {
			sb.append(key).append(" ").append(orderbyMap.get(key)).append(",");
		}
		return sb.substring(0,sb.length()-1);
	}
	
}
