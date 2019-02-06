package demo.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import utility.ContentLoader;

public class ResultRenaming {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String eclipseFolder = "data/Motivating/Result/Result/VSM-Eclipse";
		String destFolder = "data/Motivating/Result/Result/VSM";
		File[] files = new File(eclipseFolder).listFiles();
		for (File f : files) {
			try {
				ArrayList<String> lines = ContentLoader.getAllLinesOptList(f
						.getAbsolutePath());
				String firstLine = lines.get(0).trim();
				String bugID = firstLine.split(",")[0];
				System.out.println(bugID);
				String destFile = destFolder + "/" + bugID.trim() + ".txt";

				Files.copy(f.toPath(), new File(destFile).toPath());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}
}
