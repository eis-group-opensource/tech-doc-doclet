/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.techdoc.doclet.filter;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;

/**
 *	Abstract class which must be extended in order to write custom filters
 */
public abstract class Filter {
	public abstract boolean accept(ClassDoc claz);
	
	/*
	 * Checks if class has class annotation
	 * NOTE: if some conditions are met, ClassCastException is thrown.
	 * This is a java bug: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6442982
	 */
	protected boolean hasClassAnnotation(ClassDoc claz, String annotationName) {
		try {
			AnnotationDesc[] annotations = claz.annotations();
			if (annotations == null || annotations.length == 0 || annotationName == null) {
				return false;
			}
			for (AnnotationDesc annotation : annotations) {
				if (annotationName.equals(annotation.annotationType().name())) {
					return true;
				}
			}
		} catch (Exception e) {
			
		}
		return false;
	}
}
