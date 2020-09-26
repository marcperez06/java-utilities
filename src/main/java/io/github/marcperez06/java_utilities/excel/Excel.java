/**
 * Clase que permite leer un fichero excel utilizando la libreria de Apache POI
 * @author Marc Pérez
 */
package io.github.marcperez06.java_utilities.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import io.github.marcperez06.java_utilities.collection.list.ListUtils;
import io.github.marcperez06.java_utilities.logger.Logger;

public class Excel {
	
	private String excelPath;
	private Workbook workbook;
	private List<Sheet> sheets;
	
	public Excel(String excelPath) {
		this.setExcelPath(excelPath);
		this.readAllExcel();
		this.sheets = this.getSheets();
	}

	public String getExcelPath() {
		return this.excelPath;
	}
	
	public void setExcelPath(String excelPath) {
		this.excelPath = excelPath;
	}
	
	/**
	 * Gets the sheets of a XLS file
	 * @return A list of the sheets in the file - List&lt;Sheet&gt;
	 */
	public List<Sheet> getSheets() {
		List<Sheet> sheetsList = new ArrayList<Sheet>();
		for (int i = 0; i < this.workbook.getNumberOfSheets(); i++) {
			sheetsList.add(this.workbook.getSheetAt(i));
		}
		return sheetsList;
	}
	
	private void readAllExcel() {
        try {
        	
        	if (this.excelPath.isEmpty() == false) {
        		File excelFile = new File(this.excelPath);
        		this.workbook = WorkbookFactory.create(excelFile);
        	}
        	
		} catch (EncryptedDocumentException e) {
			this.workbook = null;
			e.printStackTrace();
			Logger.error(e.getMessage());
		} catch (InvalidFormatException e) {
			this.workbook = null;
			e.printStackTrace();
			Logger.error(e.getMessage());
		} catch (IOException e) {
			this.workbook = null;
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
	}
	
	/**
	 * Read excel sheet by the name of sheet
	 * @param <T> - Generic object returned
	 * @param sheetName Name of the sheet will be readed
	 * @return Map with all the content of excel (key: column) - Map&lt;String, List&lt;T&gt;&gt;
	 */
	public <T> Map<String, List<T>> readSheet(String sheetName) {
		int sheetIndex = this.getSheetIndexByName(sheetName);
		return this.readSheet(sheetIndex);
	}
	
	/**
	 * This method returns the sheet index of the given name in this class's list of sheets
	 * @param name - Name of the sheet to look for
	 * @return If found, index of sheet with name equal to the name parameter, -1 otherwise
	 */
	public int getSheetIndexByName(String name) {
		int index = -1;
		boolean found = false;
		Sheet auxSheet = null;
		
		for (int i = 0; i < this.sheets.size() && !found; i++) {
			auxSheet = this.sheets.get(i);
			if (auxSheet.getSheetName().equalsIgnoreCase(name)) {
				found = true;
				index = i;
			}
		}
		
		return index;
	}
	
	/**
	 * Read excel sheet by the index of sheet
	 * @param <T> - Generic object returned
	 * @param sheetIndex - Number of the sheet will be readed
	 * @return Map with all the content of excel (key: column) - Map&lt;String, List&lt;T&gt;&gt;
	 */
	public <T> Map<String, List<T>> readSheet(int sheetIndex) {
        Map<String, List<T>> map = new HashMap<String, List<T>>();
        
        if (sheetIndex > -1) {

        	Sheet sheet = this.sheets.get(sheetIndex);

            if (!excelPath.isEmpty() && sheet != null) {
            	map = this.readRows(sheet);
            }

        }

        return map;
	}
	
	private <T> Map<String, List<T>> readRows(Sheet sheet) {
		Map<String, List<T>> map = new HashMap<String, List<T>>();
        List<String> mapKeys = new ArrayList<String>();
		int rowCount = 0;
        int cellSize = 0;
        
		for (Row row : sheet) {

    		if (rowCount == 0) {
    			cellSize = (int) row.getLastCellNum();
    		}
    		
    		this.readCells(mapKeys, map, row, rowCount, cellSize);

            rowCount++;
        }
		
		return map;
	}
	
	private <T> void readCells(List<String> mapKeys, Map<String, List<T>> map, Row row, int rowCount, int cellSize) {
		int cellCount = 0;

		while(cellCount < cellSize) {
			
			Cell cell = row.getCell(cellCount);
			
			if (rowCount == 0) {
        		String key = this.getCellValue(cell);
        		map.put(key, new ArrayList<T>());
        		mapKeys.add(key);
        	} else {
        		String key = mapKeys.get(cellCount);
        		T value = this.getCellValue(cell);
        		map.get(key).add(value);
        	}

            cellCount++;
			
		}
	}
	
	/**
	 * Return the value of cell
	 * @param <T> - Generic object returned
	 * @param cell - Cell that read value
	 * @return T - Value of cell
	 */
	@SuppressWarnings("unchecked")
	public <T> T getCellValue(Cell cell) {
		
		T value = null;
		
		if (cell != null) {
		
		    switch (cell.getCellTypeEnum()) {
		        case BOOLEAN:
		            value = (T) Boolean.valueOf(cell.getBooleanCellValue());
		            break;
		        case STRING:
		            value = (T) cell.getRichStringCellValue().getString();
		            break;
		        case NUMERIC:
		            if (DateUtil.isCellDateFormatted(cell)) {
		                value = (T) cell.getDateCellValue();
		            } else {
		                //value = (T) new Double(cell.getNumericCellValue());
		            	cell.setCellType(CellType.STRING);
		                value = (T) cell.getRichStringCellValue().getString();
		            }
		            break;
		        case FORMULA:
		            value = (T) cell.getCellFormula();
		            break;
		        case BLANK:
		            value = null;
		            break;
		        default:
		            value = null;
		    }
	    
		}

	    return value;
	}

	/**
	 * Gets the sheet names of a XLS file
	 * @return A list of the sheet names in the file - List&lt;String&gt;
	 */
	public List<String> getSheetNames() {
		List<String> sheetNames = new ArrayList<String>();
		for (int i = 0; i < this.workbook.getNumberOfSheets(); i++) {
			sheetNames.add(this.workbook.getSheetAt(i).getSheetName());
		}
		return sheetNames;
	}
	
	/**
	 * This method returns the sheet with the given name in this class's list of sheets
	 * @param name - Name of the sheet to look for
	 * @return If found, the sheet with name equal to the name parameter, null otherwise - List&lt;String&gt;
	 */
	public Sheet getSheetByName(String name) {
		Sheet sheet = null;
		Sheet auxSheet = null;
		boolean found = false;
		
		for (int i = 0; i < this.sheets.size() && !found; i++) {
			auxSheet = this.sheets.get(i);
			if (auxSheet.getSheetName().equalsIgnoreCase(name)) {
				found = true;
				sheet = auxSheet;
			}
		}

		return sheet;
	}

	/**
	 * Iterates through the first row of the excel table to get the names in that
	 * row
	 * @param sheet The sheet you want to extract the headers from
	 * @return A list of the headers in the excel sheet - List&lt;String&gt;
	 */
	public List<String> getSheetHeaders(Sheet sheet) {
		List<String> result = new ArrayList<String>();
		Iterator<Row> rows = sheet.rowIterator();
		Row row = rows.next();
		Iterator<Cell> cells = row.cellIterator();
		Cell cell;
		while (cells.hasNext()) {
			cell = cells.next();
			result.add(cell.getStringCellValue());
		}
		return result;
	}

	/**
	 * Return all the values in the column specified by index
	 * @param sheet - Sheet
	 * @param index - int index of column
	 * @return List with all the values in column - List&lt;String&gt;
	 */
	public List<String> getSheetColumnByIndex(Sheet sheet, int index) {
		List<String> column = new ArrayList<String>();
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			Cell cell = row.getCell(index);
			column.add((String) this.getCellValue(cell));
		}
		return column;
	}

	/**
	 * Return the cell in excel specifing the Sheet, Row and column name
	 * @param sheet - Sheet
	 * @param row - Row
	 * @param columnName - String column of name
	 * @return cell - Cell
	 */
	public Cell getCellInRowByColumnName(Sheet sheet, Row row, String columnName) {
		int column = this.getSheetHeaders(sheet).indexOf(columnName);
		return row.getCell(column);
	}

	/**
	 * Returns a map with excel values that have the value specified in the colum specified by name
	 * @param sheetIndex - int index of sheet
	 * @param columnName - String name of column
	 * @param value - String value of cell
	 * @return Map with all the excels rows values that are equals to value specified - Map&lt;String, List&lt;String&gt;&gt;
	 */
	public Map<String, List<String>> getTestDataOfColumnWithValue(int sheetIndex, String columnName, String value) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
        if (!this.sheets.isEmpty() && sheetIndex < this.sheets.size()) {
        	Sheet sheet = this.sheets.get(sheetIndex);
        	map = this.getTestDataOfColumnWithValue(sheet, columnName, value);
        }
		return map;
	}
	
	/**
	 * Returns a map with excel values that have the value specified in the colum specified by name
	 * @param sheetName - String name of sheet
	 * @param columnName - String name of column
	 * @param value - String value of cell
	 * @return Map with all the excels rows values that are equals to value specified - Map&lt;String, List&lt;String&gt;&gt;
	 */
	public Map<String, List<String>> getTestDataOfColumnWithValue(String sheetName, String columnName, String value) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		Sheet sheet = this.getSheetByName(sheetName);
        if (sheet != null) {
        	map = this.getTestDataOfColumnWithValue(sheet, columnName, value);
        }
		return map;
	}
	
	private Map<String, List<String>> getTestDataOfColumnWithValue(Sheet sheet, String columnName, String value) {
		List<String> mapKeys = this.getSheetHeaders(sheet);
		Map<String, List<String>> map = this.initializeExcelMap(mapKeys);
        boolean found = false;
   
        if (ListUtils.existObjectInList(mapKeys, columnName)) {
        	int columIndex = mapKeys.indexOf(columnName);
        	
        	for (int i = 1; i < sheet.getLastRowNum() && !found; i++) {

    			Row row = sheet.getRow(i);
    			
    			Cell cell = row.getCell(columIndex);
    			
    			String cellValue = this.getCellValue(cell);
    			
    			if (value.equals(cellValue)) {
    				found = true;
    				this.readCells(mapKeys, map, row, i, row.getLastCellNum());
    			}
            }
        }

		return map;
	}
	
	private Map<String, List<String>> initializeExcelMap(List<String> mapKeys) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for (String key : mapKeys) {
        	map.put(key, new ArrayList<String>());
        }
		return map;
	}
	
	/**
	 * Override Desturctor, for close the excel workbook
	 */
	@Override
	protected void finalize() throws Throwable {
		try {
			this.close();
		} finally {
			super.finalize();
		}
	}
	
	/**
	 * Close the excel workbook if not is null.
	 */
	public void close() {
		if (this.workbook != null) {
			try {
				this.workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}