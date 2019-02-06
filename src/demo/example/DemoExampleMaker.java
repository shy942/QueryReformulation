package demo.example;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import difflib.myers.MyersDiff;
import utility.ContentLoader;

public class DemoExampleMaker {

	public DemoExampleMaker() {
		// default constructor
	}

	public HashMap<Integer, ArrayList<String>> LoadGoldSet(String goldsetFile) {
		HashMap<Integer, ArrayList<String>> goldStMap = new HashMap<>();
		ArrayList<String> lines = ContentLoader.getAllLinesList(goldsetFile);
		for (int i = 0; i < lines.size();) {
			String currentLine = lines.get(i);
			String[] items = currentLine.split("\\s+");
			if (items.length == 2) {
				int bugID = Integer.parseInt(items[0].trim());
				int filecount = Integer.parseInt(items[1].trim());
				if (filecount > 0) {
					ArrayList<String> tempList = new ArrayList<>();
					for (int currIndex = i + 1; currIndex <= i + filecount; currIndex++) {
						if (!tempList.contains(lines.get(currIndex)))
							tempList.add(lines.get(currIndex));
					}
					// now store the change set to bug
					goldStMap.put(bugID, tempList);
				}
				// now update the counter
				i = i + filecount;
				i++;
			}
		}
		return goldStMap;
	}

	protected HashMap<Integer, ArrayList<String>> collectResultMap(
			String resultFolder) {
		File[] files = new File(resultFolder).listFiles();
		HashMap<Integer, ArrayList<String>> resultMap = new HashMap<>();
		for (File f : files) {
			ArrayList<String> lines = ContentLoader.getAllLinesOptList(f
					.getAbsolutePath());
			int bugID = Integer.parseInt(f.getName().split("\\.")[0].trim());
			ArrayList<String> tempList = new ArrayList<>();
			for (String line : lines) {
				String[] parts = line.split(",");
				String fileURL = parts[1].trim();
				tempList.add(fileURL);
			}
			resultMap.put(bugID, tempList);
		}
		return resultMap;
	}

	protected int getQE(ArrayList<String> goldset, ArrayList<String> results) {
		// collecting the first QE
		int foundIndex = 0;
		int index = 0;
		for (String result : results) {
			index++;
			if (goldset.contains(result)) {
				foundIndex = index;
				return foundIndex;
			}
		}
		return 0;
	}

	protected void collectDemoExamples() {
		long start = System.currentTimeMillis();
		String goldsetFile = "data/Motivating/Goldset/gitInfoAllEclipse.txt";
		String resultFolder = "data/Motivating/Result/Result/BLUAMIR";
		String baseResultFolder = "data/Motivating/Result/Result/VSM";
		HashMap<Integer, ArrayList<String>> goldsetMap = LoadGoldSet(goldsetFile);
		// System.out.println("Gold set:" + goldsetMap.size());
		HashMap<Integer, ArrayList<String>> resultMap = collectResultMap(resultFolder);
		// System.out.println("Result set:" + resultMap.size());
		HashMap<Integer, ArrayList<String>> baseResultMap = collectResultMap(baseResultFolder);

		for (int bugID : goldsetMap.keySet()) {
			ArrayList<String> goldset = goldsetMap.get(bugID);
			ArrayList<String> presults = resultMap.get(bugID);
			if (baseResultMap.containsKey(bugID)) {
				ArrayList<String> bresults = baseResultMap.get(bugID);
				int myQE = getQE(goldset, presults);
				int bQE = getQE(goldset, bresults);
				if (myQE > 0 && myQE < bQE && bQE>=5 && myQE==1) {
					System.out.println(bugID + "\t" + myQE + "\t" + bQE);
				}
			}
		}

		long end = System.currentTimeMillis();
		System.out.println("Time elpased:" + (end - start) / 1000 + " s");

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new DemoExampleMaker().collectDemoExamples();
	}
}
