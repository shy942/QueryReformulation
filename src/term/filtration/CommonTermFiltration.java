package term.filtration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import config.StaticData;
import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;

public class CommonTermFiltration {

	String corpusFolder;
	final double FILTER_THRESHOLD = 0.25;

	HashMap<String, ArrayList<String>> termDocMap;
	ArrayList<String> frequentWordList=new ArrayList<String>();
	int TOTAL_DOC = 0;

	public CommonTermFiltration(String corpusFolder) {
		this.corpusFolder = corpusFolder;
		this.termDocMap = new HashMap<>();
		this.loadTermDocMap();
	}

	protected void loadTermDocMap() {
		// loading term doc map
		File[] files = new File(this.corpusFolder).listFiles();
		this.TOTAL_DOC = files.length;
		for (File file : files) {
			String content = ContentLoader.readContentSimple(file.getAbsolutePath());
					//(file.getAbsolutePath());
			String[] terms = content.split("\\s+|\\p{Punct}+"); // split by
																// space or
																// punctuation
			String docID = file.getName(); // only the file name
			for (String term : terms) {
				if (this.termDocMap.containsKey(term)) {
					ArrayList<String> docs = this.termDocMap.get(term);
					if (!docs.contains(docID)) {
						docs.add(docID);
						this.termDocMap.put(term, docs);
					}
				} else {
					ArrayList<String> docs = new ArrayList<>();
					docs.add(docID);
					this.termDocMap.put(term, docs);
				}
			}
		}

	}

	public void identifyCommonTerms() {
		// identify the common terms
		System.out.println("Common terms:");
		for (String key : this.termDocMap.keySet()) {
			int docFreq = this.termDocMap.get(key).size();
			double ratio = (double) docFreq / TOTAL_DOC;
			if (ratio >= FILTER_THRESHOLD) {
				System.out.println(key + ": " + ratio);
				frequentWordList.add(key.toLowerCase());
			}
		}
	}
	public ArrayList<String> returnFrequentKeywords()
	{
		return frequentWordList;
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String corpusFolder = "./data/ExampleSourceCodeFiles";
		String corpusFolder=StaticData.BUGDIR+"/BugReportsTitleAndDescription/ProcessedData/";
		new CommonTermFiltration(corpusFolder).identifyCommonTerms();
	}
}
