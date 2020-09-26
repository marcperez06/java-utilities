package io.github.marcperez06.java_utilities.gherkin.objects;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.marcperez06.java_utilities.strings.StringUtils;

public class GherkinObject {
	
	private static final String BACKGROUND = "Background";
	private static final String AND = "And";
	private static final String EXAMPLES = "Examples";
	
	private String scenarioName;
	private GherkinCriteria feature;
	private GherkinCriteria scenario;
	private List<String> content;
	private List<String> backgroundContent;
	private Map<String, List<GherkinCriteria>> background;
	private List<GherkinCriteria> givens;
	private List<GherkinCriteria> whens;
	private List<GherkinCriteria> thens;
	private List<String> examplesContent;
	private Map<String, List<GherkinVariable>> examplesValues;
	
	public GherkinObject(GherkinCriteria feature) {
		this.feature = feature;
		this.backgroundContent = new ArrayList<String>();
		this.background = new HashMap<String, List<GherkinCriteria>>();
		this.examplesValues = new HashMap<String, List<GherkinVariable>>();
	}
	
	public GherkinObject(GherkinCriteria feature, List<String> scenarioContent) {
		this(feature);
		this.fillInfo(scenarioContent);
	}

	public GherkinCriteria getFeature() {
		return this.feature;
	}
	
	public List<String> getBackgroundContet() {
		return this.backgroundContent;
	}
	
	public Map<String, List<GherkinCriteria>> getBackground() {
		return this.background;
	}

	public GherkinCriteria getScenario() {
		return this.scenario;
	}
	
	public String getScenarioName() {
		return this.scenarioName;
	}

	public List<String> getContent() {
		return this.content;
	}

	public List<GherkinCriteria> getGivens() {
		return this.givens;
	}

	public List<GherkinCriteria> getWhens() {
		return this.whens;
	}

	public List<GherkinCriteria> getThens() {
		return this.thens;
	}

	public Map<String, List<GherkinVariable>> getExamplesValues() {
		return this.examplesValues;
	}
	
	public void fillInfo(List<String> scenarioContent) {
		this.content = scenarioContent;
		this.fillBackgroundInfo(scenarioContent);
		this.fillScenario(scenarioContent);
		this.givens = this.getCriteriaLines(scenarioContent, GherkinCriteria.GIVEN);
		this.whens = this.getCriteriaLines(scenarioContent, GherkinCriteria.WHEN);
		this.thens = this.getCriteriaLines(scenarioContent, GherkinCriteria.THEN);
		this.fillExamplesMap(scenarioContent);
	}
	
	private void fillBackgroundInfo(List<String> lines) {
		this.fillBackgroundContent(lines);
		if (!this.backgroundContent.isEmpty()) {
			this.fillBackground();	
		}
	}
	
	private void fillBackgroundContent(List<String> lines) {
		boolean isScenario = false;
		boolean takeBackgroundContent = false;

		for (int i = 0; i < lines.size() && !isScenario; i++) {
			String line = this.clearLineForAnnotation(lines.get(0));
			
			if (!line.isEmpty()) {
				boolean isBackground = line.startsWith(BACKGROUND);
				
				if (takeBackgroundContent) {
					this.backgroundContent.add(line);
				}
				
				if (isBackground) {
					takeBackgroundContent = true;
				} else {
					isScenario = line.startsWith(GherkinCriteria.SCENARIO);
					takeBackgroundContent = false;
				}
			}

		}
	}
	
	private void fillBackground() {
		List<GherkinCriteria> givens = this.getCriteriaLines(this.backgroundContent, GherkinCriteria.GIVEN);
		List<GherkinCriteria> whens = this.getCriteriaLines(this.backgroundContent, GherkinCriteria.WHEN);
		List<GherkinCriteria> thens = this.getCriteriaLines(this.backgroundContent, GherkinCriteria.THEN);
		
		if (!givens.isEmpty()) {
			this.background.put(GherkinCriteria.GIVEN, givens);	
		}
		
		if (!whens.isEmpty()) {
			this.background.put(GherkinCriteria.WHEN, whens);	
		}
		
		if (!whens.isEmpty()) {
			this.background.put(GherkinCriteria.THEN, thens);
		}
	}
	
	private void fillScenario(List<String> lines) {
		boolean found = false;
		this.scenarioName = "";
		
		for (int i = 0; i < lines.size() && !found; i++) {
			String line = this.clearLine(lines.get(i));
			
			if (!line.isEmpty()) {
				boolean isScenario = line.startsWith(GherkinCriteria.SCENARIO);
				if (isScenario) {
					String[] scenarioLine = line.split(" ");
					scenarioLine[0] = "";
					String scenario = StringUtils.concatArrayOfString(scenarioLine, " ").trim();
					this.scenarioName = this.getScenarioName(scenario);
					
					if (i > 0) {
						String previousLine = this.clearLine(lines.get(i - 1));
						this.scenario = this.createGherkinCriteria(GherkinCriteria.SCENARIO, previousLine, scenario);
					}
					
					found = true;
				}
			}
		}
	}
	
	private String getScenarioName(String scenario) {
		String scenarioName = "";
		String[] scenarioLine = scenario.split(" ");
		StringBuilder stringBuilder = new StringBuilder();
		
		String[] specialCharacters = {"\\\\", "\\/", "\\[", "\\]", "\\{", "\\}", "\\(", "\\)", "\\&", "\\:", "@", "\"", "'"};
		
		for (String token : scenarioLine) {
			
			if (!token.isEmpty()) {
				token = token.trim();
				
				for (String specialChar : specialCharacters) {
					token = token.replaceAll(specialChar, "");
				}
				
				token = StringUtils.capitalizeWord(token);
				stringBuilder.append(token);
			}

		}
		
		stringBuilder.append("Steps");
		
		scenarioName = stringBuilder.toString();
		return scenarioName;
	}
	
	private List<GherkinCriteria> getCriteriaLines(List<String> lines, String criteriaType) {
		List<GherkinCriteria> criteriaLines = new ArrayList<GherkinCriteria>();
		List<String> previousLines = new ArrayList<String>();
		
		for (String line : lines) {
			String clearedLine = this.clearLine(line);
			String annotationLine = this.clearLineForAnnotation(line);
			List<String> copyOfPreviosLines = new ArrayList<String>();
			
			if (!clearedLine.isEmpty()) {
				
				copyOfPreviosLines.addAll(previousLines);
				
				boolean isCriteriaLine = false;
				
				if (clearedLine.startsWith(AND)) {
					isCriteriaLine = this.lineStartsWithCriteriaType(copyOfPreviosLines, criteriaType);
				} else {
					isCriteriaLine = clearedLine.startsWith(criteriaType);
				}
				
				if (isCriteriaLine) {
					
					String previousLine = previousLines.get(previousLines.size() - 1);
					
					if (clearedLine.startsWith(AND)) {
						clearedLine = clearedLine.replaceFirst(AND, criteriaType);
						annotationLine = annotationLine.replaceFirst(AND, criteriaType);
					}
					
					GherkinCriteria criteria = this.createGherkinCriteria(criteriaType, previousLine, line, annotationLine);
					
					criteriaLines.add(criteria);
				}
			}
			
			previousLines.add(clearedLine);
		}
		
		return criteriaLines;
	}
	
	private boolean lineStartsWithCriteriaType(List<String> lines, String criteriaType) {
		boolean lineStartsWithCriteriaType = false;
		
		if (lines != null && !lines.isEmpty()) {
			int lastLineIndex = lines.size() - 1;
			String previousLine = lines.get(lastLineIndex);
			
			if (previousLine.startsWith(AND)) {
				lines.remove(lastLineIndex);
				lineStartsWithCriteriaType = this.lineStartsWithCriteriaType(lines, criteriaType);
			} else {
				lineStartsWithCriteriaType = previousLine.startsWith(criteriaType);
			}
		}

		return lineStartsWithCriteriaType;
	}
	
	private GherkinCriteria createGherkinCriteria(String criteriaType, String previousLine, String line) {
		return createGherkinCriteria(criteriaType, previousLine, line, line);
	}
	
	private GherkinCriteria createGherkinCriteria(String criteriaType, String previousLine, 
														String line, String annotationLine) {
		
		GherkinCriteria criteria = new GherkinCriteria(criteriaType, line, annotationLine);
		boolean haveTags = (previousLine != null && !previousLine.isEmpty() && previousLine.startsWith("@"));
		if (haveTags) {
			String[] tags = previousLine.split(" ");
			for (String tag : tags) {
				criteria.addTag(tag.trim());
			}
		}
		return criteria;
	}
	
	private void fillExamplesMap(List<String> lines) {
		this.examplesContent = this.getExamplesContent(lines);
		
		if (this.examplesContent.size() > 1) {
			
			List<String> realKeys = this.putAndGetExamplesMapKeys(this.examplesValues);
			
			for (int i = 1; i < this.examplesContent.size(); i++) {

				String examplesLine = this.examplesContent.get(i);
				String[] values = examplesLine.split("\\|");
				
				this.putExamplesMapValues(this.examplesValues, realKeys, values);
			}
			
			
		}
	}
	
	private List<String> putAndGetExamplesMapKeys(Map<String, List<GherkinVariable>> gherkinVariables) {
		List<String> realKeys = new ArrayList<String>();
		
		String header = this.examplesContent.get(0);
		String[] keys = header.split("\\|");
		
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i].trim();
			if (!key.isEmpty()) {
				gherkinVariables.put("<" + key + ">", new ArrayList<GherkinVariable>());
				realKeys.add(key);
			}

		}
		
		return realKeys;
	}
	
	private void putExamplesMapValues(Map<String, List<GherkinVariable>> gherkinVariables, List<String> realKeys,
										String[] values) {
		
		int countKey = 0;
		for (int j = 1; j < values.length; j++) {
			
			String value = values[j].trim();
			
			if (!value.isEmpty()) {
				String key = realKeys.get(countKey);
				GherkinVariable variable = new GherkinVariable(key, value);
				gherkinVariables.get("<" + key + ">").add(variable);
				countKey++;
			}

		}
	}
	
	private List<String> getExamplesContent(List<String> lines) {
		List<String> examplesContent = new ArrayList<String>();
		boolean takeExamplesContent = false;
		
		for (String line : lines) {
			line = this.clearLine(line);
			
			if (!line.isEmpty()) {
				boolean isCriteriaLine = line.startsWith(EXAMPLES);
					
				if (takeExamplesContent) {
					examplesContent.add(line);
				}
				
				if (isCriteriaLine) {
					takeExamplesContent = true;
				} else {
					takeExamplesContent = line.startsWith(GherkinCriteria.SCENARIO);
					takeExamplesContent |= line.startsWith(AND);
					takeExamplesContent |= line.startsWith(GherkinCriteria.GIVEN);
					takeExamplesContent |= line.startsWith(GherkinCriteria.WHEN);
					takeExamplesContent |= line.startsWith(GherkinCriteria.THEN);
					takeExamplesContent = !takeExamplesContent;
				}
			}

		}
		
		return examplesContent;
	}
	
	private String clearLine(String line) {
		
		String clearedLine = "";
		if (line != null && !line.isEmpty()) {
			clearedLine = this.clearLineForAnnotation(line);
			clearedLine = Normalizer.normalize(clearedLine, Normalizer.Form.NFD);
			clearedLine = clearedLine.replaceAll("[^\\p{ASCII}]", "");
		}
		return clearedLine;
	}
	
	private String clearLineForAnnotation(String line) {
		
		String clearedLine = "";
		if (line != null && !line.isEmpty()) {
			clearedLine = line.trim();
			clearedLine = clearedLine.replaceAll("\"", "\\\"");
			clearedLine = clearedLine.replaceAll("\'", "\\\'");
			clearedLine = clearedLine.replaceAll("\n", "");
			clearedLine = clearedLine.replaceAll("\t", "");
		}
		return clearedLine;
	}
	
	public List<GherkinVariable> getVariablesInCriteria(String line) {
		
		List<GherkinVariable> listOfVariables = new ArrayList<GherkinVariable>();
		
		if (!line.isEmpty()) {
			String[] tokens = line.split(" ");
			
			for (String token : tokens) {
				
				GherkinVariable variable = this.getFirstGherkinVariable(token);
				
				if (variable != null) {
					listOfVariables.add(variable);
				}

			}
			
		}
		
		return listOfVariables;
	}

	private GherkinVariable getFirstGherkinVariable(String key) {
		GherkinVariable variable = null;
		
		key = key.trim();

		if (key.contains("<") && key.contains(">")) {
			
			List<GherkinVariable> list = this.examplesValues.get(key);
			
			if (list != null && list.isEmpty() == false) {
				variable = list.get(0);
			}
			
		}
		
		return variable;
	}
	
	public static String getMethodName(String criteria) {
		String methodName = "";
		
		if (!criteria.isEmpty()) {
			criteria = StringUtils.clearSpecialCharacters(criteria);
			
			String[] tokens = criteria.split(" ");
			tokens[0] = tokens[0].toLowerCase();
			
			if (tokens.length > 1) {
				for (int i = 1; i < tokens.length; i++) {
					tokens[i] = StringUtils.capitalizeWord(tokens[i]);
				}
			}
			
			methodName = StringUtils.concatArrayOfString(tokens, "");
		}
		
		return methodName;
	}
	
	public static String getAnnotationCriteria(String criteria) {
		return getAnnotationCriteria(criteria, null);
	}
	
	public static String getAnnotationCriteria(String criteria, Map<String, List<GherkinVariable>> gherkinVariables) {
		String annotationCriteria = "";
		String splitter = "x01102332x";
		
		String clearedCriteria = clearCriteria(criteria, splitter);
		String finalCriteria = constructAnnotationCriteria(clearedCriteria, gherkinVariables, splitter);

		if (!finalCriteria.isEmpty()) {
			annotationCriteria = "\"^" + finalCriteria + "$\"";
		}
		
		return annotationCriteria;
	}
	
	private static String clearCriteria(String criteria, String splitter) {
		String clearedCriteria = "";
		
		clearedCriteria = criteria.replaceAll("<", splitter);
		clearedCriteria = clearedCriteria.replaceAll(">", splitter);
		
		//clearedCriteria = StringUtils.clearSpecialCharacters(clearedCriteria);
		//clearedCriteria = clearedCriteria.toLowerCase();
		
		return clearedCriteria;
	}
	
	private static String constructAnnotationCriteria(String clearedCriteria, 
														Map<String, List<GherkinVariable>> gherkinVariables, 
														String splitter) {
		
		String finalCriteria = "";
		String[] auxCriteria = clearedCriteria.split(splitter);
		StringBuilder stringBuilder = new StringBuilder();
		
		for (String aux : auxCriteria) {
			
			String key = "<" + aux + ">";
			List<GherkinVariable> listOfVariables = gherkinVariables.get(key);
			
			if (listOfVariables != null && !listOfVariables.isEmpty()) {
				/*
				GherkinVariable variable = listOfVariables.get(0);
				String variableType = variable.getVariableType().getSimpleName().toLowerCase();
				stringBuilder.append("{" + variableType + "}");
				*/
				//stringBuilder.append(key);
				String regex = "([^\\\"]*)";
				stringBuilder.append(regex);
			} else {
				aux = aux.replaceAll("\"", "\\\\\"");
				stringBuilder.append(aux);
			}
			
		}
		
		finalCriteria = stringBuilder.toString();
		
		return finalCriteria;
	}
	
	public String getFirstTestTag() {
		String tag = "";
		if (this.scenario != null) {
			tag = this.getTagKey(this.scenario.getFirstTag());
		}
		return tag;
	}
	
	public String getFirstTestPlanTag() {
		String tag = "";
		if (this.feature != null) {
			tag = this.getTagKey(this.feature.getFirstTag());
		}
		return tag;
	}
	
	private String getTagKey(String dirtyTag) {
		String tag = "";
		if (dirtyTag != null && !dirtyTag.isEmpty()) {
			String[] tagSplited = dirtyTag.split("_");
			if (tagSplited != null && tagSplited.length > 0) {
				for (int i = 1; i < tagSplited.length; i++) {
					tag += tagSplited[i];
				}
			
			}
		}
		return tag;
	}

}