/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.techdoc.doclet;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class PageCreationUtils {
	private static final String TAG_CLASSNAME = "@className";
	private static final String TAG_QUALIFIED_CLASSNAME = "@classQualifiedName";
	private static final String TAG_CLASS_DESCRIPTION = "@classDescription";
	private static final String TAG_CLASS_IS_BUSINESS = "@classExtendsBusinessEvent";
	private static final String TAG_CLASS_IS_ASYNC = "@classAsync";
	private static final String TAG_CLASS_IS_INTERRUPRABLE = "@classInterruptable";
	private static final String TAG_CLASS_EXCEPTION_PROCESSING_LOGIC  = "@classExceptionProcessingLogic";
	private static final String TAG_CLASS_RECOMENDED_SCHEDULING = "@classRecomendedScheduling";
	private static final String TAG_CLASS_SELECTION_CRITERIA_LOGIC = "@classSelectionCriteriaLogic";
	
	public static String createPage(List<ClassDataHolder> classes,
			String pageTitle, Map<String, String> tagColumn) {
		if (classes == null || classes.isEmpty()) {
			
			Document doc = Jsoup.parse("");
			Element bodyElement = doc.getElementsByTag("body").get(0);
			if (!(pageTitle == null || "".equals(pageTitle.trim()))) {
				bodyElement.appendElement("h3").attr("id", "header").text(pageTitle);
			}
			Element tableElement = bodyElement.appendElement("table");
			Collection<String> colName = tagColumn.values();
			Iterator<String> colNameIter = colName.iterator();
			while (colNameIter.hasNext()) {
				tableElement.appendElement("th").appendText(colNameIter.next());
			}
			
			return doc.html();
		}
		Document doc = Jsoup.parse("");
		Element bodyElement = doc.getElementsByTag("body").get(0);
		if (!(pageTitle == null || "".equals(pageTitle.trim()))) {
			bodyElement.appendElement("h3").attr("id", "header").text(pageTitle);
		}
		Element tableElement = bodyElement.appendElement("table");

		Collection<String> col = tagColumn.keySet();
		Collection<String> colName = tagColumn.values();
		Iterator<String> colNameIter = colName.iterator();
		while (colNameIter.hasNext()) {
			tableElement.appendElement("th").appendText(colNameIter.next());
		}

		for (ClassDataHolder claz : classes) {
			Iterator<String> iterator = col.iterator();
			Element row = tableElement.appendElement("tr");

			while (iterator.hasNext()) {
				String key = "@" + iterator.next();
				String columnValue = null;
				Element cellElement = null;
				if (TAG_CLASS_IS_BUSINESS.equals(key)) {
					columnValue = claz.getClassIsBusinessEvent();
				} else if (TAG_CLASS_DESCRIPTION.equals(key)) {
					columnValue = claz.getClassDescription();
				} else if (TAG_CLASSNAME.equals(key)) {
					columnValue = claz.getClassName();
				} else if (TAG_QUALIFIED_CLASSNAME.equals(key)) {
					columnValue = claz.getClassQualifiedName();
				} else if (TAG_CLASS_IS_ASYNC.equals(key)){
					columnValue = claz.getClassAsync();
				} else if (TAG_CLASS_IS_INTERRUPRABLE.equals(key)){
					columnValue = claz.getClassInterruptable();
				} else if (TAG_CLASS_EXCEPTION_PROCESSING_LOGIC.equals(key)){
					columnValue = claz.getClassExceptionProcessingLogic();
				} else if (TAG_CLASS_RECOMENDED_SCHEDULING.equals(key)){
					columnValue = claz.getClassRecomendedScheduling();
				} else if (TAG_CLASS_SELECTION_CRITERIA_LOGIC.equals(key)){
					columnValue = claz.getClassSelectionCriteriaLogic();
				}
				if (columnValue == null) {
					columnValue = claz.getClassTagData().get(key);
				}
				if (columnValue == null) {
					columnValue = "";
				}
				cellElement = row.appendElement("td");
				appendElementToCell(columnValue, cellElement);
			}
		}
		return doc.html();
	}

	private static void appendElementToCell(String columnValue,
			Element cellElement) {
		Elements elements = Jsoup.parse(columnValue).getElementsByTag("body");
/*		for (Element node : elements) {
			System.out.println(node.nodeName());
		}
*/		for (int i = 0; i < elements.size(); i++) {
			cellElement.appendChild(elements.get(i));
		}
	}

}
