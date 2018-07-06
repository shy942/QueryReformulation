package bug.keyword.maker;

import java.util.ArrayList;
import java.util.HashMap;
import utility.ContentLoader;

public class BugKeywordLoader {

	String bugKeywordFile;
	HashMap<Integer, HashMap<Integer, Integer>> bugKeywordMap;
	final double DFRATIO_THRESHOLD = 0.25;

	public BugKeywordLoader(String bugKeywordFile) {
		this.bugKeywordFile = bugKeywordFile;
		this.bugKeywordMap = new HashMap<>();
	}

	public HashMap<Integer, HashMap<Integer, Integer>> loadBugKeywordMap() {
		ArrayList<String> lines = ContentLoader
				.getAllLinesOptList(this.bugKeywordFile);
		for (String bkLine : lines) {
			String[] parts = bkLine.split(":");
			//discard unusual lines
			if (parts.length <2)
				continue;
			int bugID = Integer.parseInt(parts[0].trim());
			String[] items = parts[1].split("\\s+");
			HashMap<Integer, Integer> wordcount = new HashMap<>();
			for (String item : items) {
				int keyword = Integer.parseInt(item);
				if (wordcount.containsKey(keyword)) {
					int updated = wordcount.get(keyword) + 1;
					wordcount.put(keyword, updated);
				} else {
					wordcount.put(keyword, 1);
				}
			}
			this.bugKeywordMap.put(bugID, wordcount);
		}

		// now discard the highly frequent items
		this.discardBugKeywords();

		return this.bugKeywordMap;
	}

	protected void discardBugKeywords() {
		// discard the bug keywords
		HashMap<Integer, Double> corpusMap = new HashMap<>();
		for (int bugID : this.bugKeywordMap.keySet()) {
			HashMap<Integer, Integer> wordcount = this.bugKeywordMap.get(bugID);
			for (int key : wordcount.keySet()) {
				if (corpusMap.containsKey(key)) {
					double updated = corpusMap.get(key) + 1;
					corpusMap.put(key, updated);
				} else {
					corpusMap.put(key, 1.0);
				}
			}
		}
		// do the normalization
		for (int key : corpusMap.keySet()) {
			double ratio = corpusMap.get(key) / this.bugKeywordMap.size();
			corpusMap.put(key, ratio);
		}
		// discard the bug keywords
		for (int bugID : this.bugKeywordMap.keySet()) {
			HashMap<Integer, Integer> wordcount = this.bugKeywordMap.get(bugID);
			HashMap<Integer, Integer> refined = new HashMap<>();
			for (int wordKey : wordcount.keySet()) {
				if (corpusMap.containsKey(wordKey)) {
					double ratio = corpusMap.get(wordKey);
					if (ratio < DFRATIO_THRESHOLD) {
						refined.put(wordKey, wordcount.get(wordKey));
					}
				}
			}
			this.bugKeywordMap.put(bugID, refined);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String bugKeywordFile = "./data/bug-keyword-pointer/Bug-ID-Keyword-ID-Mapping.txt";
		System.out.println(new BugKeywordLoader(bugKeywordFile)
				.loadBugKeywordMap().size());
	}
}
