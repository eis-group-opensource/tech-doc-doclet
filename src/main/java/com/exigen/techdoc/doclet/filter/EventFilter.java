/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.techdoc.doclet.filter;

import com.sun.javadoc.ClassDoc;

/*
 * Filter is used to find all events which are applicable for confluence page generation.
 * Only classes which have annotation @Event are accepted.
 * 
 */
public class EventFilter extends Filter {
	private static final String EVENT_SUPERCLASS_QUALIFIED_NAME = "org.springframework.context.ApplicationEvent";
	private static final String EVENT_ANNOTATION_NAME = "Event";
	private static final String CANINVOKE_ANNOTATION_NAME = "CanInvoke";
	
	public boolean accept(ClassDoc claz) {
		
		if (hasClassAnnotation(claz, EVENT_ANNOTATION_NAME)) {
			return true;
		}
		//return false;
		//code bellow could be removed when all event classes will have @Event annotation
		
		if (!hasClassAnnotation(claz, CANINVOKE_ANNOTATION_NAME)) {
		    return false;
		}
		
        boolean accepted = acceptParent(claz);
        if (accepted) {
            System.out.println("[WARNING] class " + claz.qualifiedName() + " should have @Event annotation because it extends org.springframework.context.ApplicationEvent.");
        }
		return accepted;
	}
	
	private boolean acceptParent(ClassDoc claz) {
	    if (claz.superclass() == null) {
            return false;
        }
        if (EVENT_SUPERCLASS_QUALIFIED_NAME.equals(claz.superclass().qualifiedName())) {
            return true;
        }
        return acceptParent(claz.superclass());
	}
}
