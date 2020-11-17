/**
 * This class allows to read a Csv content, specifying the splitter used.
 * 
 * @author marcperez06
 */
package io.github.marcperez06.java_utilities.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.github.marcperez06.java_utilities.collection.list.ListUtils;
import io.github.marcperez06.java_utilities.file.FileUtils;
import io.github.marcperez06.java_utilities.strings.StringUtils;

public class Csv {
	
	private String splitter;
	private String csvPath;
	private List<String> lines;
	private Map<String, List<String>> content; // Key --> column | Value --> value of row
	
	public Csv(String csvPath) {
		this(csvPath, ",");
	}
	
	public Csv(String csvPath, String splitter) {
		this.lines = new ArrayList<String>();
		this.content = new HashMap<String, List<String>>();
		this.splitter = splitter;
		this.setPath(csvPath);
	}
	
	public String getPath() {
		return this.csvPath;
	}
	
	public void setPath(String csvPath) {
		this.csvPath = csvPath;
		this.readCsv();
	}
	
	public List<String> getLines() {
		return this.lines;
	}
	
	public Map<String, List<String>> getContent() {
		return this.content;
	}
	
	private void readCsv() {
		this.lines = FileUtils.getStringListOfFile(this.csvPath);
		this.content = FileUtils.getMapOfCsv(this.csvPath);
	}
	
	/**
	 * Return all rows that contains the value in the column specified
	 * @param columnName - String column name
	 * @param value - String value for finding rows that contains this value.
	 * @return Map&lt;String, String&gt; - Rows in Map of List of String, where the rows contains the value in the column specified
	 */
	public Map<String, List<String>> getContentByColumWithValue(String columnName, String value) {
		Map<String, List<String>> partialContent = new HashMap<String, List<String>>();
		List<String> columnValues = this.content.get(columnName);
		if (columnValues != null && columnValues.isEmpty() == false) {
			List<Integer> indexList = ListUtils.getIndexListOfObjectInList(columnValues, value);
			
			for (Entry<String, List<String>> entry : this.content.entrySet()) {
				List<String> csvValues = this.getValuesByIndex(this.content.get(entry.getKey()), indexList);
				partialContent.put(entry.getKey(), csvValues);
			}

		}
		return partialContent;
	}
	
	private List<String> getValuesByIndex(List<String> values, List<Integer> indexes) {
		List<String> valuesByIndex = new ArrayList<String>();
		
		if (values.isEmpty() == false && indexes.isEmpty() == false) {
			for (Integer index : indexes) {
				valuesByIndex.add(values.get(index));
			}
		}
		
		return valuesByIndex;
	}
	
	public String getKeysLine() {
		return StringUtils.concatListOfString(this.getKeys(), this.splitter + " ");
	}
	
	/**
	 * Return a list with the values of the first line of csv.
	 * @return List&lt;String&gt; - Return a list of string with the values of the first line of csv
	 */
	public List<String> getKeys() {
		List<String> columns = new ArrayList<String>();
		
		for (String key : this.content.keySet()) {
			columns.add(key);
		}
		
		return columns;
	}
	
	/**
	 * First line starts in 0 index
	 * @param index - int (positive)
	 * @return String - Row line
	 */
	public String getRowLine(int index) {
		return StringUtils.concatListOfString(this.getRow(index), this.splitter+ " ");
	}
	
	/**
	 * First line starts in 0 index
	 * @param index - int (positive)
	 * @return List&lt;String&gt; - Row values in list of String
	 */
	public List<String> getRow(int index) {
		List<String> row = new ArrayList<String>();
		
		if (index >= 0) {
			for (Entry<String, List<String>> entry : this.content.entrySet()) {
				String value = "";
				if (index < entry.getValue().size()) {
					value = entry.getValue().get(index);
				}
				
				row.add(value);
			}
		}

		return row;
	}
	
	/**
	 * First line starts in 0 index
	 * @param index - int (positive)
	 * @return Map&lt;String, String&gt; - Row values in Map of String, where key of map is column value
	 */
	public Map<String, String> getRowContent(int index) {
		Map<String, String> rowContent = new HashMap<String, String>();
		
		if (index >= 0) {
			for (Entry<String, List<String>> entry : this.content.entrySet()) {
				
				String value = "";
				if (index < entry.getValue().size()) {
					value = entry.getValue().get(index);
				}
				
				rowContent.put(entry.getKey(), value);
			}
		}
		
		return rowContent;
	}

}