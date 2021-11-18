/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.techdoc.doclet;

import java.util.ArrayList;
import java.util.List;

/*
 * Used for creating table in confluence format.
 * Table consists of table header and rows
 */
public class ConfluenceTable {
	private int numOfColumns = 0;
	private List<List<String>> rows = new ArrayList<List<String>>();
	
	private List<String> header = null;
	
	public ConfluenceTable(int numOfColumns) {
		this.numOfColumns = numOfColumns;
	}
	
	public void appendRow(List<String> rowToAppend) {
		if (rowToAppend.size() == numOfColumns) {
			rows.add(rowToAppend);
		}
	}
	
	public String getTableInConfluenceFormat() {
		StringBuilder builder = new StringBuilder();
		if (header != null) {
			builder.append("|| ");
			for (String headerCell : header) {
				builder.append(headerCell + " || ");
			}
			builder.append("\n");
		}
				
		if (rows != null && rows.isEmpty()) {
			return builder.toString();
		}
		
		for (List<String> row : rows) {
			builder.append("| ");
			for (String rowCell : row) {
				builder.append(rowCell + " | ");
			}
			builder.append("\n");
		}
		
		return builder.toString();
	}

	public List<String> getHeader() {
		return header;
	}

	public void setHeader(List<String> header) {
		this.header = header;
	}
}
