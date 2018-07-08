package BugLocator;

import java.util.ArrayList;
import java.util.HashMap;

import utility.ContentLoader;



public class BLPerformanceCalc {

	String repoName;
	String resultFile;
	int TOPK;
	ArrayList<Integer> selectedBugs;
	HashMap<String, Integer> rankMap;
	HashMap<Integer, ArrayList<String>> resultMap;
	boolean IROnly;

	public BLPerformanceCalc(String outputFileName, int TOPK) {
		this.resultFile = outputFileName;
		this.TOPK = TOPK;
		this.selectedBugs = new ArrayList<>();
		this.rankMap = new HashMap<>();
		this.resultMap = extractResults();
	}

	protected HashMap<Integer, ArrayList<String>> extractResults() {
		ArrayList<String> lines =ContentLoader.getAllLinesList(this.resultFile);
				//.getAllLinesOptList(this.resultFile);
		HashMap<Integer, ArrayList<String>> resultMap = new HashMap<>();
		for (String line : lines) {
			String[] parts = line.trim().split(",");
			int bugID = Integer.parseInt(parts[0]);
			int rank = Integer.parseInt(parts[2].trim());
			// if (rank >= 0 && rank < TOPK) {
			//if (selectedBugs.contains(bugID)) {
				String fileURL = parts[1].trim();
				String key = bugID + "-" + fileURL;
				if(!this.selectedBugs.contains(bugID)){
					this.selectedBugs.add(bugID);
				}
				if (resultMap.containsKey(bugID)) {
					ArrayList<String> files = resultMap.get(bugID);
					files.add(fileURL);
					resultMap.put(bugID, files);
				} else {
					ArrayList<String> files = new ArrayList<>();
					files.add(fileURL);
					resultMap.put(bugID, files);
				}
				// storing the ranks
				this.rankMap.put(key, rank);
			//}
		}
		// }
		// System.out.println(repoName + ": Results:" + resultMap.size() + "\t"
		// + selectedBugs.size());
		return resultMap;
	}

	protected double getTopKAcc() {
		int found = 0;
		for (int bugID : this.selectedBugs) {
			if (resultMap.containsKey(bugID)) {
				ArrayList<String> resFiles = resultMap.get(bugID);
				for (String rFile : resFiles) {
					String key = bugID + "-" + rFile;
					if (rankMap.containsKey(key)) {
						int rank = rankMap.get(key);
						if (rank >= 0 && rank < TOPK) {
							//if (checkEntryFound(goldFiles, rFile)) {
								found++;
								// System.out.println(bugID+"\t"+key+"\t"+rank);
								// System.out.println(bugID);
								break;
							//}
						}
					}
				}
			}
		}
		return (double) found / this.selectedBugs.size();
	}

	protected ArrayList<String> getCanonicalURLs(ArrayList<String> goldFiles) {
		ArrayList<String> canonicalList = new ArrayList<>();
		for (String fileURL : goldFiles) {
			String canonical = fileURL.replace('/', '.');
			canonicalList.add(canonical);
		}
		return canonicalList;
	}

	protected boolean checkEntryFound(ArrayList<String> goldFiles,
			String resultEntry) {
		ArrayList<String> canonicalGoldFiles = getCanonicalURLs(goldFiles);
		for (String goldFile : canonicalGoldFiles) {
			if (goldFile.endsWith(resultEntry)) {
				return true;
			}
		}
		return false;
	}

	protected double getRecall(int bugID, ArrayList<String> goldFiles,
			ArrayList<String> resultFiles) {
		int matched = 0;
		for (String rFile : resultFiles) {
			//if (checkEntryFound(goldFiles, rFile)) {
				String key = bugID + "-" + rFile;
				int rank = rankMap.get(key);
				if (rank >= 0 && rank < TOPK) {
					matched++;
				}
			//}
		}
		return (double) matched / resultFiles.size();
	}

	protected double getMeanRecall() {
		double sumRecall = 0;
		for (int bugID : this.selectedBugs) {
			if (resultMap.containsKey(bugID)) {
				ArrayList<String> resultFiles = resultMap.get(bugID);
				//ArrayList<String> goldFiles = GoldsetLoader.goldsetLoader(
				//		repoName, bugID);
				double recall = getRecall(bugID, null, resultFiles);
				sumRecall += recall;
			}
		}
		return sumRecall / this.selectedBugs.size();
	}

	protected double getPrecisionK(ArrayList<Integer> resRanks, int K) {
		// double linePrec = 0;
		int found = 0;
		for (int rank : resRanks) {
			if (rank >= 0 && rank < K) {
				found++;
			}
		}
		return (double) found / (K);
	}

	protected double getMeanAvgPrecisionAtK() {
		// getting precision at K
		double sumPrecK = 0;
		for (int bugID : this.selectedBugs) {
			if (resultMap.containsKey(bugID)) {
				ArrayList<String> resultFiles = resultMap.get(bugID);
				ArrayList<Integer> resranks = new ArrayList<>();

				double sumLinePrec = 0;
				int found = 0;
				
				for (String rFile : resultFiles) {
					String key = bugID + "-" + rFile;
					int rank = rankMap.get(key);					

					if (rank >= 0 && rank < TOPK) {
						found++;
						//int index = resultFiles.indexOf(rFile);
						//sumLinePrec += getPrecisionK(resranks, index + 1);
						sumLinePrec+= (double)found/(rank+1);
						resranks.add(rank);
					}
					
					if (resranks.size() == TOPK) {
						break;
					}
				}

				if (found > 0) {
					double avgPrec = sumLinePrec / found;
					sumPrecK += avgPrec;
				}
			}
		}
		return sumPrecK / this.selectedBugs.size();
	}

	protected double getMRRK(int TOPK) {
		double sumRRK = 0;
		for (int bugID : this.selectedBugs) {
			if (resultMap.containsKey(bugID)) {
				ArrayList<String> resultFiles = resultMap.get(bugID);
				int minRank = 10000;
				for (String rFile : resultFiles) {
					String key = bugID + "-" + rFile;
					int rank = rankMap.get(key);
					if (rank < minRank) {
						minRank = rank;
					}
				}
				if (minRank == 0) {
					sumRRK += 1;
				} else if (minRank > 0 && minRank < TOPK) {
					sumRRK += 1.0 / (minRank + 1);
				}
			}
		}
		return sumRRK / this.selectedBugs.size();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int TOPK = 10;
		//String fileName="./data/buglocator/eclipseoutput.txt";
		String fileName="./data/Results/100-1000-rankedResult.txt";
		BLPerformanceCalc bcalc = new BLPerformanceCalc(fileName, TOPK);
		double topk = bcalc.getTopKAcc();
		System.out.println("Top-K: " + topk);
		double preck = bcalc.getMeanAvgPrecisionAtK();
		System.out.println("MAP@K: " + preck);
		double recallk = bcalc.getMeanRecall();
		System.out.println("MR@K: " + recallk);
		double rrK = bcalc.getMRRK(TOPK);
		System.out.println("MRR@K: " + rrK);
	}
}
