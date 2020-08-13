package marc.java_utilities.gherkin.objects;

import java.util.ArrayList;
import java.util.List;

public class GherkinCriteria {

	public static final String FEATURE = "Feature";
	public static final String SCENARIO = "Scenario";
	public static final String GIVEN = "Given";
	public static final String WHEN = "When";
	public static final String THEN = "Then";
	
	private String type;
	private String line;
	private String annotationLine;
	private List<String> tags;
	
	public GherkinCriteria(String type, String line, String annotationLine) {
		this.type = type;
		this.line = line;
		this.annotationLine = annotationLine;
		this.tags = new ArrayList<String>();
	}
	
	public GherkinCriteria(String type, String line, String annotationLine, List<String> tags) {
		this(type, line, annotationLine);
		this.tags = tags;
	}
	
	public GherkinCriteria(String type, String line) {
		this.type = type;
		this.line = line;
		this.annotationLine = line;
		this.tags = new ArrayList<String>();
	}
	
	public GherkinCriteria(String type, String line, List<String> tags) {
		this(type, line);
		this.tags = tags;
	}
	
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLine() {
		return this.line;
	}

	public void setLine(String line) {
		this.line = line;
	}
	
	public String getAnnotationLine() {
		return this.annotationLine;
	}

	public void setAnnotationLine(String annotationLine) {
		this.annotationLine = annotationLine;
	}

	public List<String> getTags() {
		return (this.tags != null) ? this.tags : new ArrayList<String>();
	}
	
	public String getFirstTag() {
		String firstTag = "";
		if (this.tags != null && !this.tags.isEmpty()) {
			firstTag = this.tags.get(0);
		}
		return firstTag;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public void addTag(String tag) {
		if (this.tags == null) {
			this.tags = new ArrayList<String>();
		}
		
		this.tags.add(tag);
	}

}