/**
 * Classe Statica usada para leer y escribir los Txt.
 * @author Marc P�rez - 173287
 *
 */

package io.github.marcperez06.java_utilities.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.marcperez06.java_utilities.strings.StringUtils;

public class FileUtils {

	// --- CREACION DEL FICHERO ---
	
	private static final String DIRECTORY = "directory";
	private static final String FILE = "file";
	
	private FileUtils() {}
	
	/**
	 * Returns true if exist file, false otherwise
	 * @param path - String
	 * @return boolean - true if exist file, false otherwise
	 */
	public static boolean existFile(String path) {
		boolean existFile = false;
		
		try {
			File file = new File(path);
			existFile = file.exists();
		} catch (Exception e) {
			existFile = false;
			e.printStackTrace();
		}
		
		return existFile;
	}
	
	/**
	 * Creates a file if not exist, otherwise returns the existent file, if have an exception return null
	 * @param path - String
	 * @return File - the created or existent file, If have an exception return null
	 */
	public static File createFileIfNotExist(String path) {
		return createFileIfNotExist(path, FILE);
	}
	
	private static File createFileIfNotExist(String path, String typeOfFile) {
		File file = null;

		try {
			
			file = new File(path);
			createParentFileIfNotExist(file);
			createFileOrDirectory(file, typeOfFile);
			
		} catch (Exception e) {
			e.printStackTrace();
			file = null;
		}

		return file;
	}
	
	private static void createParentFileIfNotExist(File child) {
		File parent = child.getParentFile();
		if (parent != null && parent.exists() == false) {
			createFileIfNotExist(parent.getAbsolutePath(), DIRECTORY);
		}
	}
	
	private static void createFileOrDirectory(File file, String typeOfFile) {
		try {
			if (!file.exists()) {
				
				if (typeOfFile.equals(DIRECTORY)) {
					file.mkdir();
				} else if (typeOfFile.equals(FILE)) {
					file.createNewFile();
				}
	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static BufferedReader createReader(String path) throws IOException {
		return new BufferedReader(new FileReader(path));
	}
	
	private static BufferedWriter createWriter(String path) throws IOException {
		BufferedWriter bufferedWriter = null;
		File file = new File(path);
		
		if (file != null) {
			file.setWritable(true);
			FileWriter fileWriter = new FileWriter(file);
			bufferedWriter = new BufferedWriter(fileWriter);
		}
		
		return bufferedWriter;
	}
	
	private static BufferedWriter createWriterWithEncoding(String path, String encoding) throws IOException {
		BufferedWriter bufferedWriter = null;
		FileOutputStream fileOutputStream = new FileOutputStream(path);
		
		if (fileOutputStream != null) {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, encoding);
			bufferedWriter = new BufferedWriter(outputStreamWriter);
		}
		
		return bufferedWriter;
	}
	
	// --- LECTURAS SIMPLES ---
	
	public static String getStringOfFile(String path) {
		String lineText = "";
		String text = "";

		try (BufferedReader reader = createReader(path)) {

			while ((lineText = reader.readLine()) != null) {
				text = text + lineText + "\n";
			}
			
			text = StringUtils.cutStringWithOtherString(text, "\n", 0);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return text;
	}
	
	public static List<String> getStringListOfFile(String path) {
		String lineText;
		List<String> lines = new ArrayList<String>();
		
		try (BufferedReader reader = createReader(path)) {
			
			while ((lineText = reader.readLine()) != null) {
				lines.add(lineText);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
			lines.clear();
		}

		return lines;
	}
	
	public static List<String> getStringListOfFileIgnoringLinesThatContainsWords(String path, String...wordsToIgnore) {
		String lineText = "";
		List<String> lines = new ArrayList<String>();
		boolean ignore = false;
		
		try (BufferedReader reader = createReader(path)) {

			while ((lineText = reader.readLine()) != null) {
				
				ignore = false;
				
				for (int i = 0; i < wordsToIgnore.length && ignore == false; i++) {
					String word = wordsToIgnore[i];
					if (lineText.contains(word) == true) {
						ignore = true;
					}
				}
				
				if (ignore == false && lineText.isEmpty() == false) {
					lines.add(lineText);	
				}

			}

		} catch (IOException ex) {
			ex.printStackTrace();
			lines.clear();
		}

		return lines;
	}
	
	public static Map<String, List<String>> getMapOfCsv(String path) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		String lineText = "";
		
		try (BufferedReader reader = createReader(path)) {

			// put keys in map
			lineText = reader.readLine();
			String[] keys = lineText.split(",");
			
			for (int i = 0; i < keys.length; i++) {
				map.put(keys[i], new ArrayList<String>());
			}
			
			// put the values in map
			while ((lineText = reader.readLine()) != null) {
				
				String[] values = lineText.split(",");
				
				for (int j = 0; j < keys.length; j++) {
					
					if (j < values.length) {
						map.get(keys[j]).add(values[j]);
					} else {
						map.get(keys[j]).add(null);
					}
				}

			}

		} catch (IOException ex) {
			ex.printStackTrace();
			map.clear();
		}

		return map;
	}
	
	// --- LECTURAS MULTIPLES ---
	
	public static String getStringOfAllFilesInOneDirectory(String path) {
		/* directory to process */
		File inDir = new File(path);
		File[] flist = inDir.listFiles();
		String floc = "";
		String text = "";
		String finaltext = "";
		
		for (int f = 0; f < flist.length; f++) {
			floc = flist[f].getAbsolutePath();
			text = getStringOfFile(floc);
			finaltext = finaltext + text;
		}

		return finaltext;
	}
	
	public static List<String> getStringListOfAllFilesInOneDirectory(String path) {
		/* directory to process */
		File inDir = new File(path);
		File[] flist = inDir.listFiles();
		String floc = "";
		String text = "";
		List<String> listOfFileText = new ArrayList<String>();

		if (flist != null) {
			for (int f = 0; f < flist.length; f++) {
				floc = flist[f].getAbsolutePath();
				text = getStringOfFile(floc);
				if (text.isEmpty() == false) {
					listOfFileText.add(text);
				}
			}
		}

		return listOfFileText;
	}
	
	public static List<String> getListOfAbsolutePathsInDirectory(String directory) {
		File inDir = new File(directory);
		File[] flist = inDir.listFiles();
		String floc = "";
		List<String> listOfPaths = new ArrayList<String>();
		
		if (flist != null) {
			for (int f = 0; f < flist.length; f++) {
				floc = flist[f].getAbsolutePath();
				listOfPaths.add(floc);
			}
		}
		
		return listOfPaths;
	}
	
	// --- ESCRITURA EN FICHERO ---
	
	/**
	 * Add a line in the file, if not exists, creates it
	 * @param line - String
	 * @param path - String
	 * @return boolean - true if add a line in the file, false otherwise
	 */
	public static boolean addLineInFile(String line, String path) {
		boolean addLine = false;
		
		if (!existFile(path)) {
			writeTxt(line, path);
		} else {
			
			try (BufferedWriter bufferedWritter = createWriter(path)){
				
				bufferedWritter.append(line);
				addLine = true;

			} catch (Exception e) {
				e.printStackTrace();
				addLine = false;
			}
		}
		
		return addLine;
	}
	
	/**
	 * Add a line in the file with encoding specified, if not exists, creates it
	 * @param line - String
	 * @param path - String
	 * @param encoding - String (Example: UTF-8)
	 * @return boolean - true if add a line in the file, false otherwise
	 */
	public static boolean addLineInFileWithEncoding(String line, String path, String encoding) {
		boolean addLine = false;

		if (!existFile(path)) {
			writeTxtWithEncoding(line, path, encoding);
		} else {
			
			try (BufferedWriter bufferedWriter = createWriterWithEncoding(path, encoding)) {
				
				bufferedWriter.append(line);
				addLine = true;
				
			} catch (Exception e) {
				e.printStackTrace();
				addLine = false;
			}
		}
		
		return addLine;
	}
	
	/**
	 * Write a file, if not exists, creates it
	 * @param txt - String
	 * @param path - String
	 * @return boolean - true if write the file, false otherwise
	 */
	public static boolean writeTxt(String txt, String path) {
		boolean write = false;

		File file = createFileIfNotExist(path);
		
		if (file != null) {
			
			try (BufferedWriter bufferedWritter = createWriter(path)){
				
				bufferedWritter.write(txt);;
				write = true;

			} catch (Exception e) {
				e.printStackTrace();
				write = false;
			}
			
		}

		return write;
	}
	
	/**
	 * Write a file with encoding specified, if not exists, creates it
	 * @param txt - String
	 * @param path - String
	 * @param encoding - String (Example: UTF-8)
	 * @return boolean - true if write the file, false otherwise
	 */
	public static boolean writeTxtWithEncoding(String txt, String path, String encoding) {
		boolean write = false;
		
		File file = createFileIfNotExist(path);
		
		if (file != null) {
			
			try (BufferedWriter bufferedWritter = createWriterWithEncoding(path, encoding)) {
				
				bufferedWritter.write(txt);
				write = true;
		
			} catch (IOException e) {
				e.printStackTrace();
				write = false;
			}
			
		}

		return write;
	}

	// ---- ELIMINAR FICHEROS I / O DIRECTORIOS ----
	
	/**
	 * Delete a file 
	 * @param path - String
	 * @return boolean - true if delete file, false otherwise
	 */
	public static boolean deleteFile(String path) {
		boolean fileDeleted = false;
		if (path != null && !path.isEmpty()) {
			File file = new File(path);
			fileDeleted = deleteFile(file);	
		}
		return fileDeleted;
	}
	
	/**
	 * Delete a file 
	 * @param file - File
	 * @return boolean - true if delete file, false otherwise
	 */
	public static boolean deleteFile(File file) {
		boolean fileDeleted = false;
		if (file != null && file.exists()) {
			fileDeleted = file.delete();
			
			if (fileDeleted) {
				System.out.println("The file was deleted");
			} else {
				System.out.println("The file can not be deleted");
			}
		}
		return fileDeleted;
	}
	
	/**
	 * Delete the directory and all files inside it
	 * @param path - String path of directory
	 * @return boolean - true if delete directory, false otherwise
	 */
	public static boolean deleteDirectory(String path) {
		boolean directoryDeleted = false;
		if (path != null && path.isEmpty()) {
			File directory = new File(path);
			directoryDeleted = deleteDirectory(directory);
		}
		return directoryDeleted;
	}
	
	/**
	 * Delete the directory and all files inside it
	 * @param directory - File
	 * @return boolean - true if delete directory, false otherwise
	 */
	public static boolean deleteDirectory(File directory) {
		boolean directoryDeleted = false;
		
		if (directory != null && directory.exists()) {
			
			File[] files = directory.listFiles();
			if (files != null) {
				
				for (int i = 0; i < files.length; i++) {
					
					File file = files[i];
					
					if (file.isDirectory()) {
						deleteDirectory(file);
					} else {
						deleteFile(file);
					}
					
				}
				
			}
			
			directoryDeleted = directory.delete();
			
		}
		
		return directoryDeleted;
	}
	
	/**
	 * Delete a file or directory with all the files inside it
	 * @param path - String
	 * @return boolean - true if delete file or directory, false otherwise
	 */
	public static boolean delete(String path) {
		boolean delete = false;
		if (path != null && path.isEmpty()) {
			File fileOrDirectory = new File(path);
			delete = delete(fileOrDirectory);
		}
		return delete;
	}
	
	/**
	 * Delete a file or directory with all the files inside it
	 * @param fileOrDirectory - File file or directory
	 * @return boolean - true if delete file or directory, false otherwise
	 */
	public static boolean delete(File fileOrDirectory) {
		boolean delete = false;
		if (fileOrDirectory.isFile()) {
			delete = deleteFile(fileOrDirectory);
		} else {
			delete = deleteDirectory(fileOrDirectory);
		}
		return delete;
	}
	
}