/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.techdoc.doclet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.exigen.techdoc.doclet.filter.EventFilter;
import com.exigen.techdoc.doclet.filter.Filter;
import com.exigen.techdoc.doclet.filter.JobFilter;
import com.exigen.techdoc.doclet.filter.SPIFilter;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.RootDoc;

public class Start extends Doclet {
	// variables which hold information taken from command line
	private static Filter filter = null;
	private static String superTagName = null;
	// LinkedHashMap is used to preserve order of elements in map
	private static Map<String, String> tagColumn = new LinkedHashMap<String, String>();
	private static String destinationFilePath = null;
	private static String pageTitle = null;
	private static String filterType = null;
	
	// classes applicable for confluence file generation
	private static List<ClassDataHolder> classes = new LinkedList<ClassDataHolder>();
	
	// allClasses hold all classes which have any superclasses
	private static List<ClassDoc> allClasses = new LinkedList<ClassDoc>();
	// classesFound all classes which are accepted by a filter and all subclasses of them
	private static List<ClassDoc> classesFound = new LinkedList<ClassDoc>();

    public static void main(String[] args) {  
     // 	String[] docArgs = new String[]{"-doclet", Start.class.getName(), "-filter", "job", "-destinationFile", "C:/output.html", "C:/Users/TestClass.java"};
     //   com.sun.tools.javadoc.Main.execute(docArgs);  
    }  
	
	public static boolean start(RootDoc root) {
		// if no mapping file specified, then use simple description format:
		// class name and class description (comment in class)
		if (tagColumn == null || tagColumn.isEmpty()) {
			tagColumn = new LinkedHashMap<String, String>();
			String type = "Class";
			if (filter instanceof JobFilter) {
				type = "Job";
			} else if (filter instanceof EventFilter) {
				type = "Event";
			} else if (filter instanceof SPIFilter) {
				type = "SPI Class";
			} else {
				type = "Class";
			}
			if (type.equals("Event")){
				tagColumn.put("className", type + " Name");
				tagColumn.put("classDescription", type + " Description");
				tagColumn.put("classExtendsBusinessEvent", " Business Event");	
			} else if (type.equals("Job")){
				tagColumn.put("className", type + " Name");
				tagColumn.put("classDescription", type + " Description");
				tagColumn.put("classAsync", "Async?");
				tagColumn.put("classInterruptable", "Interruptable?");
				tagColumn.put("classRecomendedScheduling", "Recommended Scheduling");
				tagColumn.put("classExceptionProcessingLogic", "Exception Processing Logic");
				tagColumn.put("classSelectionCriteriaLogic", "Job Selection Criteria/Logic");
			} else {
				tagColumn.put("className", type + " Name");
				tagColumn.put("classDescription", type + " Description");
			}
		}
		
		// collect all classes which extend some other class
		for (ClassDoc claz : root.classes()) {
			allClasses.add(claz);
		}
		// recursively find all the classes applicable -> subclasses of classes, which are accepted by filter
		filterClasses(filter, allClasses);
		
		// convert all found classes in previous step into ClassDataHolder objects
		for (ClassDoc claz : classesFound) {
			classes.add(new ClassDataHolder(claz, superTagName, tagColumn));
		}
		
		Collections.sort(classes,new Comparator<ClassDataHolder>() {
            public int compare(ClassDataHolder classDataHolder, ClassDataHolder otherClassDataHolder) {
                return classDataHolder.getClassName().compareTo(otherClassDataHolder.getClassName());
            }
        });
		
		// create content of a confluence page as String and write it to the file
		// which path was specified in -destinationFile option
		String pageContents = PageCreationUtils.createPage(classes, pageTitle, tagColumn);
		File file = new File(destinationFilePath);
		try {
			File parentFile = file.getParentFile();
			parentFile.mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(pageContents);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/*
	 * Methods below are used for enabling passing custom options via command
	 * line arguments.
	 * Some additional info on this: http://docs.oracle.com/javase/1.4.2/docs/tooldocs/javadoc/overview.html#options
	 */
	public static boolean validOptions(String options[][],
			DocErrorReporter reporter) {
		for (int i = 0; i < options.length; i++) {
			String[] opts = options[i];
			String opt = opts[0];

			if ("-supertag".equalsIgnoreCase(opt)) {
				if (opts[1] == null || "".equals(opts[1].trim())) {
					String errorMessage = "-superTag needs exactly one value.";
					reporter.printError(errorMessage);
					return false;
				}
				superTagName = opts[1];
			} else if ("-mappingFile".equalsIgnoreCase(opt)) {
				if (opts[1] == null || "".equals(opts[1].trim())) {
					String errorMessage = "-mappingFile needs exactly one value.";
					reporter.printError(errorMessage);
					return false;
				}
				File file = new File(opts[1]);
				try {
					Scanner s = new Scanner(file);
					while (s.hasNextLine()) {
						String fileLine = s.nextLine();
						String[] lineSplit = fileLine.split(":");
						if (lineSplit == null || lineSplit.length != 2) {
							String errorMessage = "Not valid pair of \"tagName:Column value\" found in mapping file "
									+ file.getAbsolutePath()
									+ ". Invalid pair: " + fileLine + ".";
							reporter.printError(errorMessage);
							return false;
						}
						System.out.println(lineSplit[0].replaceAll("\\s", "")
								+ "\t" + lineSplit[1]);
						tagColumn.put(lineSplit[0].replaceAll("\\s", ""),
								lineSplit[1]);
					}
					s.close();
				} catch (FileNotFoundException e) {
					String errorMessage = "File : "
							+ file.getAbsolutePath()
							+ " was not found. Please check -mappingFile value.";
					reporter.printError(errorMessage);
					return false;
				} catch (Exception e) {
					String errorMessage = e.getMessage();
					reporter.printError(errorMessage);
					return false;
				}
			} else if ("-destinationFile".equalsIgnoreCase(opt)) {
				if (opts[1] == null || "".equals(opts[1].trim())) {
					String errorMessage = "-destinationFile needs exactly one value.";
					reporter.printError(errorMessage);
					return false;
				}
				destinationFilePath = opts[1];
			} else if ("-pageTitle".equalsIgnoreCase(opt)) {
				if (opts[1] == null || "".equals(opts[1].trim())) {
					String errorMessage = "-pageTitle needs exactly one value.";
					reporter.printError(errorMessage);
					return false;
				}
				pageTitle = opts[1].replace('_', ' ');
			} else if ("-filter".equalsIgnoreCase(opt)) {
				if (opts[1] == null || "".equals(opts[1].trim())) {
					String errorMessage = "-filter needs exactly one value.";
					reporter.printError(errorMessage);
					return false;
				}
				filterType = opts[1];
				if ("job".equalsIgnoreCase(filterType)) {
					filter = new JobFilter();
					//String message = "Job filter specified.";
					//reporter.printNotice(message);
				} else if ("event".equalsIgnoreCase(filterType)) {
					filter = new EventFilter();
					//String message = "Event filter specified.";
					//reporter.printNotice(message);
				} else if ("spi".equalsIgnoreCase(filterType)) {
					filter = new SPIFilter();
					//String message = "SPI filter specified.";
					//reporter.printNotice(message);
				} else {
					String message = "Parsing all source files. No filter specified.";
					reporter.printError(message);
				}
			}
		}
		// check if all required options are set
		if (filterType == null || (!"job".equalsIgnoreCase(filterType) && !"event".equalsIgnoreCase(filterType) && !"spi".equalsIgnoreCase(filterType))) {
			String errorMessage = "No filter or invalid filter specified. Check if -filter option is used, and if it " +
					"contains a valid value of 'job' or 'event'";
			reporter.printError(errorMessage);
			return false;
		}
		if (destinationFilePath == null) {
			reporter.printError("Check if -destinationFile is specified.");
			return false;
		}
		// check if set, supertag is set with --columns values
		if (superTagName != null && (tagColumn == null || tagColumn.isEmpty())) {
			reporter.printError("-supertag is specified, but there are no mappings to columns specified (in -mappingFile). "
					+ "Fix that.");
			return false;
		}

		if (tagColumn != null && !tagColumn.isEmpty() && superTagName == null) {
			reporter.printError("Mappings to columns (in -mappingFile) are specified, but -supertag option is not specified or invalid. "
					+ "Fix that.");
			return false;
		}
		return true;
	}
	
	/*
	 * Used for defining length of option. If option length is 2, then first element is a flag, second is a value
	 * Example: -filter job // job is one argument
	 * Example: -filter job event // WRONG, 2 values are specified, flag name, value, value, total 3
	 * more on this: http://docs.oracle.com/javase/1.4.2/docs/tooldocs/javadoc/overview.html#options
	 */
	public static int optionLength(String option) {
		if (option.equalsIgnoreCase("-filter")
				|| option.equalsIgnoreCase("-supertag")
				|| option.equalsIgnoreCase("-mappingFile")
				|| option.equalsIgnoreCase("-destinationFile")
				|| option.equalsIgnoreCase("-pageTitle")) {
			return 2;
		}
		return 0;
	}
	
	/*
	 * Finds all class hierarchy by class extension. Found classes are put into classesFound list
	 * filter - root class in hierarchy
	 */
	private static void filterClasses(Filter filter, List<ClassDoc> classesToSearchIn) {		
		for (ClassDoc claz : classesToSearchIn) {
			if (filter.accept(claz)) {
				classesFound.add(claz);
			}
		}
	}
}
