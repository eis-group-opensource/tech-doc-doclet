/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.techdoc.doclet.filter;

import com.sun.javadoc.ClassDoc;

/*
 * Filter is used to find all events which are applicable for confluence page generation.
 * Only classes which have annotation @SPI are accepted.
 * 
 */
public class SPIFilter extends Filter {
	private static final String SPI_ANNOTATION_NAME = "SPI";
	
	public boolean accept(ClassDoc claz) {
		if (hasClassAnnotation(claz, SPI_ANNOTATION_NAME)) {
			return true;
		}
		return false;
	}
}
