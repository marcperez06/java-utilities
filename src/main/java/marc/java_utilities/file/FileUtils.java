/**
 * Classe Statica usada para leer y escribir los Txt.
 * @author Marc P�rez - 173287
 *
 */

package marc.java_utilities.file;

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

import marc.java_utilities.strings.StringUtils;

public class FileUtils {

	// --- CREACION DEL FICHERO ---
	
	private static final String DIRECTORY = "directory";
	private static final String FILE = "file";
	
	private FileUtils() {}
	
	public static boolean existFile(String path) {
		boolean existFile = false;
		
		try {
			File file = new File(path);
			existFile = file.exists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return existFile;
	}
	
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
			if (file.exists() == false) {
				
				if (typeOfFile.equals(DIRECTORY) == true) {
					file.mkdir();
				} else if (typeOfFile.equals(FILE) == true){
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
	
	// --- ESCRITURA DE LOS RESULTADOS ---
	
	public static boolean writeTxt(String txt, String path) {
		boolean write = false;
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
			File file = createFileIfNotExist(path);
			if (file != null) {
				fw = new FileWriter(file);
				bw = new BufferedWriter(fw);
				
				bw.write(txt);
			
				write = true;
			}
	
		} catch (IOException e) {
			e.printStackTrace();
			write = false;
		} finally {
			try {
				if (bw != null) { bw.close(); }
				if (fw != null) { fw.close(); }
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return write;
	}
	
	public static boolean writeTxtWithEncoding(String txt, String path, String encoding) {
		boolean write = false;
		BufferedWriter bw = null;
		FileOutputStream fileOutputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		
		try {
			File file = createFileIfNotExist(path);
			if (file != null) {
				fileOutputStream = new FileOutputStream(path);
				outputStreamWriter = new OutputStreamWriter(fileOutputStream, encoding);
				bw = new BufferedWriter(outputStreamWriter);
				
				bw.write(txt);
			
				write = true;
			}
	
		} catch (IOException e) {
			e.printStackTrace();
			write = false;
		} finally {
			try {
				if (bw != null) { bw.close(); }
				if (outputStreamWriter != null) { outputStreamWriter.close(); }
				if (fileOutputStream != null) { fileOutputStream.close(); }
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return write;
		
	}
	
	// ---- ELIMINAR FICHEROS I / O DIRECTORIOS ----
	
	public static boolean deleteFile(String path) {
		boolean fileDeleted = false;
		
		if (path != null && path.isEmpty()) {
			
			File file = new File(path);
			fileDeleted = deleteFile(file);
			
		}

		return fileDeleted;
	}
	
	public static boolean deleteFile(File file) {
		boolean fileDeleted = false;
		if (file != null) {
			fileDeleted = file.delete();
			
			if (fileDeleted == true) {
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
						file.delete();
					}
					
				}
				
			}
			
			directoryDeleted = directory.delete();
			
		}
		
		return directoryDeleted;
	}
	
}
