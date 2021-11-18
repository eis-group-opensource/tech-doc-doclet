/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.techdoc.doclet.filter;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Type;

/*
 * Filter is used to find all jobs which are applicable for confluence page generation.
 * Only classes which have annotation @Job are accepted.
 * 
 */
public class JobFilter extends Filter {
	private static final String SCHEDULERJOB_QUALIFIED_NAME = "com.exigen.scheduler.SchedulerJob";
	private static final String JOB_ANNOTATION_NAME = "Job";
	
	public boolean accept(ClassDoc claz) {
		if (hasClassAnnotation(claz, JOB_ANNOTATION_NAME)) {
			return true;
		}
		
		//return false;
		//code bellow could be removed when all job classes will have @Job annotation
		
		if (claz.isAbstract()) {
		    return false;
		}
		boolean accepted = acceptParent(claz);
		if (accepted) {
		    System.out.println("[WARNING] class " + claz.qualifiedName() + " should have @Job annotation because it extends com.exigen.scheduler.SchedulerJob.");
		}
		return accepted;
	}
	
	
	private boolean acceptParent(ClassDoc claz) {
	    
       for (Type type : claz.interfaceTypes()) {
            if (SCHEDULERJOB_QUALIFIED_NAME.equals(type.qualifiedTypeName())) {
                return true;
            }
        }
    
        
        if (claz.superclass() == null) {
            return false;
        }
        
        return acceptParent(claz.superclass());
	}
}	