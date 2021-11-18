/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.publicapi.doclet;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.umlgraph.doclet.UmlGraphDoc;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.RootDoc;
import com.sun.tools.javadoc.Main;

//import jdk.javadoc.doclet.StandardDoclet;

/**
 * @author szacharov
 * 
 * Exigen UmlGraphDoc Starter program-doclet
 * 
 * Starter Doclet collects parameters for project and writes them to file {output}\Parameters.txt;
 * Also doclet generates {output}\ClassPathsList.txt which is later used by Starter program to generate Javadoc only for particular classes
 * 
 * Starter Program Starts UmlGraphDoc doclet using {output}\Parameters.txt where changes -sourcepath to particular classes defined in ClassPathsList.txt
 * 
 * public static boolean start() - doclet generates parameters for UmlGraphDoc doclet
 * 
 * main() method executes UmlGraphDoc with generated parameters
 * 
 * parameters for Starter DOCLET:
 * -includeannotated Includes only classes annotated with defined annotations (Annotations separated by colon (":"), e.g. -includeannotated CanExtend:CanInvoke)
 * -fileslistpath - output for generated params, e.g. D:\Javadoc\Starter-output\
 * 
 * tested on EIS-4.10
 * 
 * more info: \\szacharov\Starter\
 */

public class Start extends Doclet {

	private static final String INCLUDE_OPTION = "-includeannotated";
	
	private static final String JAVADOC_OPTION = "-javadocdir";

	private static final String BOTTOM  = "Copyright &#169; 2008-2015 <a href=\"http://www.eisgroup.com/\">EIS Group</a> Properties, Ltd. All Rights Reserved.";
	
	private static String[] annotations = null;
	
	private static String javadocdir = null;
	
	private static String target = null;
	
	private static Logger LOG = LoggerFactory.getLogger("com.exigen.publicapi.doclet");
	
	/**
	 * Entry point for Starter Doclet
	 */
	public static boolean start(RootDoc root) {
		
		LOG.error("Starting com.exigen.publicapi.doclet logger");
		
		// List of path to every file
		Set<String> classpaths = new HashSet<String>();
		if (null != annotations) {
			ClassDoc[] classes = root.classes();
			for (ClassDoc clasz : classes) {
				if (!findAnnotation(clasz.annotations())) {
					root.printNotice("Excluding " + clasz.qualifiedName());
					continue;
				}
				classpaths.add(clasz.position().file().getPath());
			}
		}

		String options = getOptionsAsString(root.options());
		
		
		String[] filesArray = classpaths.toArray(new String[classpaths.size()]);
		String[] params = parseParams(options);
		
		String[] bottomParam = new String[] {"-bottom", BOTTOM};
		
		String[] finalParameters = (String[]) ArrayUtils.addAll(params, bottomParam);
		
		finalParameters = (String[]) ArrayUtils.addAll(finalParameters, filesArray);
		
		Main.execute(UmlGraphDoc.class.getName(), UmlGraphDoc.class.getName(), finalParameters);
		
		//copy stylesheet and resources
		if (javadocdir != null && target != null) {
			try {
				FileUtils.copyDirectory(new File(javadocdir) , new File(target));
			} catch (IOException e) {

			}
		}
		return true;
	}

	/**
	 * Let every option be valid.
	 * 
	 * @param options
	 *            the options from the command line
	 * @param reporter
	 *            the error reporter
	 */
	public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
		for (int i = 0; i < options.length; i++) {
			if (options[i][0].equalsIgnoreCase(INCLUDE_OPTION)) {
				readAnnotations(options[i][1]);
				continue;
			}
			if (options[i][0].equalsIgnoreCase(JAVADOC_OPTION)) {
				javadocdir = (options[i][1]);
				continue;
			}
			if (options[i][0].equalsIgnoreCase("-d")) {
				target = (options[i][1]);
				continue;
			}
		}
		return true;
	}

	/**
	 * Method required to validate the length of the given option. This is a bit
	 * ugly but the options must be hard coded here. Otherwise, Javadoc will
	 * throw errors when parsing options. We could delegate to the Standard
	 * doclet when computing option lengths, but then this doclet would be
	 * dependent on the version of J2SE used. Prefer to hard code options here
	 * so that this doclet can be used with 1.4.x or 1.5.x .
	 * 
	 * @param option
	 *            the option to compute the length for
	 */
	public static int optionLength(String option) {
		if (option.equalsIgnoreCase(INCLUDE_OPTION) || option.equalsIgnoreCase(JAVADOC_OPTION)) {
			return 2;
		}
		/* 1.4 Options Begin Here */

		/* 1.5 Options Begin Here */

		// General options
		if (option.equals("-author") || option.equals("-docfilessubdirs") || option.equals("-keywords")
				|| option.equals("-linksource") || option.equals("-nocomment")
				|| option.equals("-nodeprecated") || option.equals("-nosince")
				|| option.equals("-notimestamp") || option.equals("-quiet") || option.equals("-xnodate")
				|| option.equals("-version")) {
			return 1;
		} else if (option.equals("-d") || option.equals("-docencoding") || option.equals("-encoding")
				|| option.equals("-excludedocfilessubdir") || option.equals("-link")
				|| option.equals("-sourcetab") || option.equals("-noqualifier") || option.equals("-output")
				|| option.equals("-sourcepath") || option.equals("-tag") || option.equals("-taglet")
				|| option.equals("-tagletpath")) {
			return 2;
		} else if (option.equals("-group") || option.equals("-linkoffline")) {
			return 3;
		}

		// Standard doclet options
		option = option.toLowerCase();
		if (option.equals("-nodeprecatedlist") || option.equals("-noindex") || option.equals("-notree")
				|| option.equals("-nohelp") || option.equals("-splitindex") || option.equals("-serialwarn")
				|| option.equals("-use") || option.equals("-nonavbar") || option.equals("-nooverview") 
				// TODO: UmlGraphDoc-specific option
				/*
				 * http://www.umlgraph.org/doc/indexw.html
				 * 
				 * add additional options if needed
				 */
				|| option.equals("-compact")  || option.equals("-xdoclint:none")) {
			return 1;
		} else if (option.equals("-footer") || option.equals("-header") || option.equals("-packagesheader")
				|| option.equals("-doctitle") || option.equals("-windowtitle") || option.equals("-bottom")
				|| option.equals("-helpfile") || option.equals("-stylesheetfile")
				|| option.equals("-charset") || option.equals("-overview")) {
			return 2;
		} else {
			return 0;
		}
	}

	/**
	 * Annotations splitted by colon
	 */
	private static void readAnnotations(String annotationsLine) {
		annotations = annotationsLine.split(":");
	}

	/**
	 * @param options - two-dim array from Starter Doclet
	 * @return plain String of options separated by whitespaces
	 */
	private static String getOptionsAsString(String[][] options) {
		String result = "";
		for (int i = 0; i < options.length; i++) {
			for (int j = 0; j < options[i].length; j++) {
				result += options[i][j] + " ";
			}
		}
		return result;
	}

	private static boolean findAnnotation(AnnotationDesc[] annotations) {
		for (AnnotationDesc annotation : annotations) {
			if (isAnnotated(annotation.annotationType().name())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * class annotations contains string, e.g. for class annotation
	 * 
	 * @SuppressWarnings("unchecked") returns true for Suppresswarnings or
	 * unchecked Strings
	 */
	private static boolean isAnnotated(String classAnnotation) {
		for (String annotation : annotations) {
			if (classAnnotation.contains(annotation)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Takes String of unparsed parameters and removes unneeded
	 * 
	 * @return String array of params (to pass com.sun.tools.javadoc.Main())
	 */
	private static String[] parseParams(String unparsedParams) {
		/*
		 * FIX error org.umlgraph.doclet.UmlGraphDoc: error - Illegal package
		 * name: "Files/Java/jdk1.6.0_37/jre/../jre/lib/plugin.jar; No
		 * whitespaces allowed in -classpath - just replace program files
		 * whitespaces with '%' before splitting all params by whitespaces
		 */
		// TODO: refactor if possible
		unparsedParams = unparsedParams.replace("Program Files", "Program%Files");

		String[] splittedParams = unparsedParams.split(" ");

		/*
		 * remove first 4 params -doclet xxx -docletpath yyy
		 * 
		 * and -sourcepath xxx
		 */
		int deletedOptions = 0;

		for (int i = 0; i < splittedParams.length; i++) {
			if (splittedParams[i].equalsIgnoreCase("-sourcepath")
					|| splittedParams[i].equalsIgnoreCase("-docletpath")
					|| splittedParams[i].equalsIgnoreCase("-doclet")
					|| splittedParams[i].equalsIgnoreCase(INCLUDE_OPTION)
					|| splittedParams[i].equalsIgnoreCase(JAVADOC_OPTION)) {
				// delete tag and its value
				splittedParams[i] = "";
				splittedParams[i + 1] = "";
				deletedOptions += 2;
			}
		}
		String[] result = new String[splittedParams.length - deletedOptions];
		int i = 0;
		for (String param : splittedParams) {
			if (StringUtils.isNotEmpty(param)) {
				result[i++] = param;
			}
		}
		
		return result;
	}
}
