package io.github.marcperez06.java_utilities.gherkin;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import io.github.marcperez06.java_utilities.file.FileUtils;
import io.github.marcperez06.java_utilities.gherkin.objects.GherkinCriteria;
import io.github.marcperez06.java_utilities.gherkin.objects.GherkinObject;
import io.github.marcperez06.java_utilities.strings.StringUtils;

public class GherkinParser {
	
	private String gherkinFilePath;
	private List<String> lines;
	private List<Integer> startOfScenarios;
	private List<List<String>> scenarios;
	private List<GherkinObject> gherkinObjects;
	
	public GherkinParser(String gherkinFilePath) {
		this.setGherkinFilePath(gherkinFilePath);
		this.parseGherkin();
	}
	
	private void initProperties() {
		this.lines = null;
		this.startOfScenarios = null;
		this.scenarios = null;
		this.gherkinObjects = new ArrayList<GherkinObject>();
	}
	
	public String getGherkinFilePath() {
		return this.gherkinFilePath;
	}

	public void setGherkinFilePath(String gherkinFilePath) {
		this.gherkinFilePath = gherkinFilePath;
		this.initProperties();
	}

	public List<String> getLines() {
		return this.lines;
	}

	public List<Integer> getStartOfScenarios() {
		return this.startOfScenarios;
	}

	public List<List<String>> getScenarios() {
		return this.scenarios;
	}

	public List<GherkinObject> getGherkinObjects() {
		return this.gherkinObjects;
	}

	public void parseGherkin() {
		if (!this.gherkinFilePath.isEmpty()) {
			
			this.lines = FileUtils.getStringListOfFile(this.gherkinFilePath);
			
			GherkinCriteria feature = this.getFeature();
			
			this.startOfScenarios = this.createIndexStartOfScenarios();
			this.scenarios = this.createScenarios();
			
			for (List<String> scenario : this.scenarios) {
				GherkinObject gherkin = new GherkinObject(feature, scenario);
				this.gherkinObjects.add(gherkin);
			}

		}
	}
	
	private GherkinCriteria getFeature() {
		boolean found = false;
		GherkinCriteria feature = new GherkinCriteria(GherkinCriteria.FEATURE, "");
		
		for (int i = 0; i < this.lines.size() && !found; i++) {
			String line = this.clearLine(this.lines.get(i));
			boolean isFeature = line.startsWith(GherkinCriteria.FEATURE);
			if (isFeature) {
				String[] featureSplitLine = line.split(" ");
				featureSplitLine[0] = "";
				String featureLine = StringUtils.concatArrayOfString(featureSplitLine, " ");
				featureLine = featureLine.trim();
				
				feature.setLine(featureLine);
				
				if (i > 0) {
					String previousLine = this.lines.get(i - 1);
					this.addFeaturesTag(feature, previousLine);
				}
				
				found = true;
			}
		}
		
		return feature;
	}
	
	private String clearLine(String line) {
		String clearedLine = "";
		if (line != null && !line.isEmpty()) {
			clearedLine = line.trim();
			clearedLine = clearedLine.replaceAll("\n", "");
			clearedLine = clearedLine.replaceAll("\t", "");
			clearedLine = Normalizer.normalize(clearedLine, Normalizer.Form.NFD);
			clearedLine = clearedLine.replaceAll("[^\\p{ASCII}]", "");
		}
		return clearedLine;
	}
	
	private void addFeaturesTag(GherkinCriteria feature, String previousLine) {
		boolean haveTags = (previousLine != null && !previousLine.isEmpty() && previousLine.startsWith("@"));
		if (haveTags) {
			String[] tags = previousLine.split(" ");
			for (String tag : tags) {
				feature.addTag(tag.trim());
			}
		}
	}

	private List<Integer> createIndexStartOfScenarios() {
		List<Integer> startOfScenarios = new ArrayList<Integer>();
		
		for (int i = 0; i < this.lines.size(); i++) {
			String line = this.clearLine(this.lines.get(i));
			boolean isScenario = line.startsWith(GherkinCriteria.SCENARIO);
			boolean notAddedPreviosIndex = true;
			
			if (isScenario) {
				
				if (i > 0) {
					String previousLine = this.clearLine(this.lines.get(i - 1));
					if (previousLine.startsWith("@")) {
						startOfScenarios.add(i - 1);
						notAddedPreviosIndex = false;
					}
				}
				
				if (notAddedPreviosIndex) {
					startOfScenarios.add(i);
				}

			}
		}
		
		return startOfScenarios;
	}
	
	private List<List<String>> createScenarios() {
		List<List<String>> scenarios = new ArrayList<List<String>>();
		
		for (int i = 0; i < this.startOfScenarios.size(); i++) {
			
			int start = this.startOfScenarios.get(i);
			int endIndex = i + 1;
			int end = (endIndex >= this.startOfScenarios.size()) ? this.lines.size() : this.startOfScenarios.get(endIndex);
			
			List<String> scenario = new ArrayList<String>();

			for (int j = start; j < end; j++) {
				scenario.add(lines.get(j));
			}
			
			scenarios.add(scenario);
		}
		
		return scenarios;
	}
	
	/**
	 * Return the first Gherkin Object parsed
	 * @param gherkinFilePath - String path of file wrote in gherkin
	 * @return GherkinObject - Object that contains all the info about feature, scenario, background, given, when and then
	 */
	public static GherkinObject getFirstGherkinObject(String gherkinFilePath) {
		GherkinObject gherkinObject = null;
		List<GherkinObject> gherkinObjects = getGherkinObjects(gherkinFilePath);
		if (!gherkinObjects.isEmpty()) {
			gherkinObject = gherkinObjects.get(0);
		}
		return gherkinObject;
	}
	
	/**
	 * Return a List of Gherkin Objects parsed
	 * @param gherkinFilePath - String path of file wrote in gherkin
	 * @return List&lt;GherkinObject&gt; - List of Objects that contains all the info about feature, scenario, background, given, when and then
	 */
	public static List<GherkinObject> getGherkinObjects(String gherkinFilePath) {
		GherkinParser parser = new GherkinParser(gherkinFilePath);
		return parser.getGherkinObjects();
	}

}