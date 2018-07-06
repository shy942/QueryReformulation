package corpus.maker;

import java.io.File;
import java.util.ArrayList;

import term.filtration.CommonTermFiltration;
import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;
import config.StaticData;

public class SourceCodeTermsFilteration {

	String sourceCodePPFolder;
	String sourceCodeFilteredPPFolder;
	ArrayList <String> frequentKeywordList;
	
	public SourceCodeTermsFilteration()
	{
		
		this.sourceCodePPFolder="./data/ExampleSourceCodeFiles";
		this.frequentKeywordList=new ArrayList<String>();
		this.sourceCodeFilteredPPFolder="./data/ExampleSourceCodeFilesFiltered";
		LoadFrequentTerms();
	}
	
	protected void PerformFilteration()
	{
		File[] files=new File(sourceCodePPFolder).listFiles();
 		int count=0;
		for (File f : files) {
			if (!f.getName().equalsIgnoreCase(".DS_Store")) {
				
				
                ArrayList <String> contentList=ContentLoader.getAllLinesList(f.getAbsolutePath());
				String filePath=contentList.get(0);
				String content="";
				if(contentList.size()>1)content=contentList.get(1);
				String[] spilter = content.split(" ");
                String fileredContent =filePath+"\r\n";
				for (String term : spilter) {
					if (frequentKeywordList.contains(term.trim())) {
						// Skip this keyword
					} else {
						fileredContent += term.trim() + " ";
					}
				}
				System.out.println((++count) + "\n" + fileredContent);

				String outFile = this.sourceCodeFilteredPPFolder + "/"
						+ f.getName();
				ContentWriter.writeContent(outFile, fileredContent);

			}
			
		}
	}
	protected void LoadFrequentTerms()
	{
		//Discard most frequent terms that appear more than 25% of the documents
		CommonTermFiltration obj=new CommonTermFiltration(sourceCodePPFolder);
		obj.identifyCommonTerms();
		this.frequentKeywordList=obj.returnFrequentKeywords();
		System.out.println(MiscUtility.list2Str(frequentKeywordList));
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SourceCodeTermsFilteration obj=new SourceCodeTermsFilteration();
		obj.PerformFilteration();
         //new BugExtractorTermsFilteration().PerformFilteration();
	}

}
