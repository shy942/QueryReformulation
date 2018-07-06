package utility;

import java.util.ArrayList;
import java.util.HashMap;

public class FileMapLoader {

	public static HashMap<Integer, String> loadIDSrcFileMap(String mapFile) {
		HashMap<Integer, String> tempMap = new HashMap<>();
		ArrayList<String> mapLines = ContentLoader.getAllLinesOptList(mapFile);
		for (String mapLine : mapLines) {
			String[] parts = mapLine.split(":");
			int fileID = Integer.parseInt(parts[0].trim());
			String fileURL = parts[1].trim();
			tempMap.put(fileID, fileURL);
		}
		return tempMap;
	}

}
