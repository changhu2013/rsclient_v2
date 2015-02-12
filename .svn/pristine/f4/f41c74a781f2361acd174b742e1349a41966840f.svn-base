package com.riambsoft.rsclient.ext;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.riambsoft.rsclient.web.parse.ParserException;
import com.riambsoft.rsclient.web.parse.SuperParser;

public class StoreDataTest {

	@Test
	public void a() throws ParserException {

		String json = "{'metaData':{'paramNames':{'start':'start','limit':'limit','sort':'sort','dir':'dir'},'idProperty':'kanban_id','root':'data','totalProperty':'total','successProperty':'success','messageProperty':'message','limit':20},'xaction':'read'}";

		Map<String, Object> params = (Map) SuperParser.marshal(json, HashMap.class);

		StoreData data = new StoreData(params);

		System.out.println(data.getGroupBy());
		System.out.println(data.getGroupDir());
		System.out.println(data.getMetaData().getSuccessProperty());
		System.out.println(data.getStart());
		System.out.println(data.getLimit());
		System.out.println(data.getSortDir());
		System.out.println(data.getSortField());
	}

}
