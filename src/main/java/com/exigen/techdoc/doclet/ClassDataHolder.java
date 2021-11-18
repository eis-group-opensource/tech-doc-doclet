/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.techdoc.doclet;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
/*
 * Used for holding necessary information of a ClassDoc object.
 * This class simplifies class data extraction, because all necessary data is extracted
 * during object creation.
 */
public class ClassDataHolder {
	private String className = null;
	private String classQualifiedName = null;
	private String classDescription = null;
	private String classIsBusinessEvent = null;
	private String classAsync = null;
	private String classInterruptable = null;
	private String classExceptionProcessingLogic  = null;
	private String classRecomendedScheduling = null;
	private String classSelectionCriteriaLogic =  null;
	
	private Map<String, String> classTagData = new LinkedHashMap<String, String>();
	private static final String BUSINESSEVENT_QUALIFIED_NAME = "com.exigen.eis.work.dto.BusinessEvent";
	
	public ClassDataHolder(ClassDoc claz, String superTagName, Map<String, String> customDocFields) {
		if (claz == null || customDocFields == null || customDocFields.isEmpty()) {
			return;
		}
		
		superTagName = "@" + superTagName;
		this.className = claz.name();
		this.classQualifiedName = claz.qualifiedName();
		this.classIsBusinessEvent = "No";
		StringBuilder descriptionBuilder = new StringBuilder();	
		Set<String> keys = customDocFields.keySet();
		for (Tag superTag : claz.inlineTags()) {
			if (superTagName.equals(superTag.name())) {
				Iterator<String> iter = keys.iterator();
				while (iter.hasNext()) {
					String tagName = "@" + iter.next();
					for (Tag subTag : superTag.inlineTags()) {
						if (subTag.name().trim().equals(tagName)) {
							classTagData.put(tagName, subTag.text());
						}
					}
				}
			} else if ("Text".equals(superTag.name())) {
				descriptionBuilder.append(superTag.text().trim() + "\n");
				this.classDescription = descriptionBuilder.toString();
			}
			for (Type type : claz.interfaceTypes()) {
				if (BUSINESSEVENT_QUALIFIED_NAME.equals(type.qualifiedTypeName())) {
					this.classIsBusinessEvent = "Yes";
					}
			}
		} //end of for
		for (Tag commentTagsText : claz.tags()){
			if (commentTagsText.name().equals("@jobAsync")){
				this.classAsync = commentTagsText.text();
			} else if (commentTagsText.name().equals("@jobInerruptable")){
				this.classInterruptable = commentTagsText.text();
			} else if (commentTagsText.name().equals("@jobExceptionProcessingLogic")){
				this.classExceptionProcessingLogic = commentTagsText.text();
			} else if (commentTagsText.name().equals("@jobRecomendedScheduling")){
				this.classRecomendedScheduling = commentTagsText.text();
			} else if (commentTagsText.name().equals("@jobSelectionCriteriaLogic")){
				this.classSelectionCriteriaLogic = commentTagsText.text();
			}
		}
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassQualifiedName() {
		return classQualifiedName;
	}

	public void setClassQualifiedName(String classQualifiedName) {
		this.classQualifiedName = classQualifiedName;
	}
	
	public String getClassIsBusinessEvent() {
		return classIsBusinessEvent;
	}

	public void setClassIsBusinessEvent(String classIsBusinessEvent) {
		this.classIsBusinessEvent = classIsBusinessEvent;
	}

	public String getClassDescription() {
		return classDescription;
	}

	public void setClassDescription(String classDescription) {
		this.classDescription = classDescription;
	}

	public Map<String, String> getClassTagData() {
		return this.classTagData;
	}

	public void setClassTagData(Map<String, String> classTagData) {
		this.classTagData = classTagData;
	}
	
	public String getClassAsync() {
		return classAsync;
	}

	public void setClassAsync(String classAsync) {
		this.classAsync = classAsync;
	}
	
	public String getClassInterruptable() {
		return classInterruptable;
	}

	public void setClassInterruptable(String classInterruptable) {
		this.classInterruptable = classInterruptable;
	}
	
	public String getClassExceptionProcessingLogic () {
		return classExceptionProcessingLogic;
	}

	public void setClassExceptionProcessingLogic (String classExceptionProcessingLogic) {
		this.classExceptionProcessingLogic = classExceptionProcessingLogic;
	}
	public String getClassRecomendedScheduling() {
		return classRecomendedScheduling;
	}

	public void setClassRecomendedScheduling(String classRecomendedScheduling) {
		this.classRecomendedScheduling = classRecomendedScheduling;
	}
	
	public String getClassSelectionCriteriaLogic() {
		return classSelectionCriteriaLogic;
	}

	public void setClassSelectionCriteriaLogic(String classSelectionCriteriaLogic) {
		this.classSelectionCriteriaLogic = classSelectionCriteriaLogic;
	}
	
}
