package marc.java_utilities.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import marc.java_utilities.collection.list.ListUtils;
import marc.java_utilities.file.FileUtils;
import marc.java_utilities.strings.StringUtils;

public class Csv {
	
	private String csvPath;
	private Map<String, List<String>> content; // Key --> column | Value --> value of row
	
	public Csv(String csvPath) {
		this.setPath(csvPath);
	}
	
	public String getPath() {
		return this.csvPath;
	}
	
	public void setPath(String csvPath) {
		this.csvPath = csvPath;
		this.readCsv();
	}
	
	public Map<String, List<String>> getContent() {
		return this.content;
	}
	
	private void readCsv() {
		this.content = FileUtils.getMapOfCsv(this.csvPath);
	}
	
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
	
	public String getColumnsLine() {
		return StringUtils.concatListOfString(this.getColumns(), ", ");
	}
	
	public List<String> getColumns() {
		List<String> columns = new ArrayList<String>();
		
		for (String key : this.content.keySet()) {
			columns.add(key);
		}
		
		return columns;
	}
	
	/**
	 * First line starts in 0 index
	 * @param index - int (positive)
	 * @return String - row line
	 */
	public String getRowLine(int index) {
		return StringUtils.concatListOfString(this.getRow(index), ", ");
	}
	
	/**
	 * First line starts in 0 index
	 * @param index - int (positive)
	 * @return List&lt;String&gt; row values in list of String
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
	 * @return Map&lt;String, String&gt; row values in Map of String, where key of map is column value
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
