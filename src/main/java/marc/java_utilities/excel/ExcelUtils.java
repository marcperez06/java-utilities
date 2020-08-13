package marc.java_utilities.excel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ExcelUtils {
	
	private ExcelUtils() {}

	/**
	 * Iterates through the first row of the excel table to get the names in that
	 * row.
	 * 
	 * @param sheet
	 *            The sheet you want to extract the headers from.
	 * @return A list of the headers in the excel sheet.
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

	public List<String> getSheetColumn(Sheet sheet, int index) {
		List<String> column = new ArrayList<String>();
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			column.add(getCellValue(row.getCell(index)));
		}
		return column;
	}

	public Cell getCellInRowByColumnName(Sheet sheet, Row row, String columnName) {
		int column = getSheetHeaders(sheet).indexOf(columnName);
		return row.getCell(column);
	}

	public Cell getCell(Sheet sheet, int row, int column) {
		return sheet.getRow(row).getCell(column);
	}

	/**
	 * Parses the content of a cell in an XLS file to a String.
	 * 
	 * @param cell
	 *            Cell to parse from.
	 * @return String containing the value of the cell (it can be parsed back to
	 *         whatever the original type was).
	 */
	public String getCellValue(Cell cell) {
		switch (cell.getCellTypeEnum()) {
			case STRING:
				return cell.getRichStringCellValue().getString();
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					return cell.getDateCellValue().toString();
				} else {
					return Long.toString((long) cell.getNumericCellValue());
				}
			case BOOLEAN:
				return Boolean.toString(cell.getBooleanCellValue());
			case FORMULA:
				try {
					return cell.getStringCellValue();
				} catch (Exception e) {
					return String.valueOf(cell.getNumericCellValue());
				}
			default:
				return "";
		}
	}

	/**
	 * Given a test case name, this method gets the row in a sheet corresponding to
	 * that test case.
	 * 
	 * @param sheet
	 *            Sheet in which to look for the row.
	 * @param name
	 *            Name of the test case to look for.
	 * @return Row containing the data of the parameter test case's name
	 */
	public Row getRowByTestCaseName(Sheet sheet, String name) {
		if (sheet != null) {
			for (int i = 0; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (getCellValue(row.getCell(0)).toLowerCase().trim().equals(name.toLowerCase().trim())) {
					return row;
				}
			}
		}
		return null;
	}

	public boolean isCellHidden(Cell cell) {
		if (cell != null) {
			Row row = cell.getRow();
			return row.getZeroHeight();
		}
		return true;
	}

	public boolean isRowHidden(Row row) {
		if (row != null) {
			return row.getZeroHeight();
		}
		return true;
	}

	public int getRowNumbers(Sheet sheet) {
		return sheet.getPhysicalNumberOfRows();
	}
}
