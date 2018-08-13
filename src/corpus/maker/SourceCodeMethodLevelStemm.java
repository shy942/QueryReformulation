package corpus.maker;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import stemmer.Stemmer;
import utility.ContentLoader;
import utility.ContentLoaderQR;
import utility.ContentWriter;
import utility.MiscUtility;

public class SourceCodeMethodLevelStemm {

	public HashMap <String, String>sourceContentMethod;
	public HashMap<String, String> sourceContent;
	Stemmer stemmer;
	ArrayList<String> stopwords;
	public SourceCodeMethodLevelStemm(String metInPath, String inPath)
	{
		this.sourceContentMethod=this.LoadContent(metInPath);
		this.sourceContent=this.LoadContent(inPath);
		this.stemmer = new Stemmer();
		this.loadStopWords();
	}
	protected void loadStopWords() {
		this.stopwords = ContentLoader.readContent("./data/stop_words.txt");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        //For Mac
		//new SourceCodeMethodLevelStemm().PreprocessMethodLevelFiles("/Users/user/Documents/workspace-Sep16/QueryReformulation/data/ExampleSourceCodeFilesSortedContents/");
		//For Windows
		SourceCodeMethodLevelStemm obj=new SourceCodeMethodLevelStemm("C:\\Users\\Mukta\\Dropbox\\WorkinginHome\\SCAM\\QueryReformulation\\data\\ExampleSourceCodeFilesMethodLevel\\","E:\\PhD\\Data\\ProcessedSourceForBL\\");
	    //MiscUtility.showResult(10, obj.sourceContentMethod);
	    //MiscUtility.showResult(10, obj.sourceContent);
	    obj.ProcessContent(obj.sourceContentMethod, "E:\\PhD\\Data\\ProcessedSourceMethodLevel\\");
	}

	public HashMap<String, String> LoadContent(String folderPath)
	{
		HashMap<String, String> hm=new HashMap<>();
		File[] files = new File(folderPath).listFiles();
		HashMap<String, ArrayList<String>> docMap = new HashMap<>();
		for (File file : files) {
			if(!file.getName().equals(".DS_Store")){
				String content=ContentLoaderQR.readContentSimple(file.getAbsolutePath());
				hm.put(file.getName(), content);
			}
		}
		return hm;
	}
	
	public void ProcessContent(HashMap <String, String>sourceContentMethod, String outFolder)
	{
		
		for(String Sid:sourceContentMethod.keySet())
		{
			String content=sourceContentMethod.get(Sid);
			String[] spilter=content.split("\n");
			if(spilter.length>2)
			{
			//line 1 contains full path
			//line 2 contains no. of methods
			//line 3 to line n contain method contents.
			String filePathOld=spilter[0];
			
			//System.out.println(filePathOld);
			int index = nthOccurrence(filePathOld, '/', 7);
			 filePathOld = filePathOld.substring(index+1, filePathOld.length());
			String filePathNew=filePathOld.replaceAll("/", ".");
			
			if(this.sourceContent.containsKey(filePathNew.trim()))
				{
					ArrayList<String> outContent=new ArrayList<>();
					System.out.println(filePathNew);
				
			
					int noOfMethod=Integer.valueOf(spilter[1]);
					if(noOfMethod>0){
				    for(int i=0;i<noOfMethod;i++)
				    {
				    	String methodContent=spilter[i+2];
				    	//System.out.println(methodContent);
						String afterStopWordRemoval=this.StopWordRemover(methodContent);
						//System.out.println(afterStopWordRemoval);
						
						String ProcessedContent=this.performNLP(afterStopWordRemoval);
						//System.out.println(ProcessedContent);
						outContent.add(ProcessedContent);
				    }
				    ContentWriter.writeContent(outFolder+filePathNew, outContent);
				}
				}
			}
		}
	}
	
	protected ArrayList<String> splitContent(String content) {
		String[] words = content.split("\\s+|\\p{Punct}+|\\d+");
		return new ArrayList<String>(Arrays.asList(words));
	}

	protected String performStemming(String word) {
		return stemmer.stripAffixes(word);
		//return word;
	}
	
	protected String StopWordRemover(String content)
	{
		String processedContent="";
		String[] spilter=content.split(" ");
		for(String word:spilter)
		{
			if(this.stopwords.contains(word))
			{
				//do nothing
			}
			else
			{
				processedContent+=word+" ";
			}
		}
		
		return processedContent;
	}
	public String performNLP(String content) {
		// performing NLP operations
		ArrayList<String> words = splitContent(content);
		//ArrayList<String> refined = removeStopWords(words);
		ArrayList<String> stemmed = new ArrayList<String>();
		for (String word : words) {
			if (!word.trim().isEmpty()) {
				String stemmedWord = performStemming(word.trim());
				if (stemmedWord.length() >= 3) {
					stemmedWord=stemmedWord.toLowerCase(Locale.ENGLISH);
					stemmedWord=stemmedWord.trim();
					stemmedWord=stemmedWord.replaceAll("�", "");
					stemmedWord=stemmedWord.replaceAll("�", "");
					stemmed.add(stemmedWord);
				}
			}
		}

		return MiscUtility.list2Str(stemmed);

	}
	public static int nthOccurrence(String s, char c, int occurrence) {
	    return nthOccurrence(s, 0, c, 0, occurrence);
	}

	public static int nthOccurrence(String s, int from, char c, int curr, int expected) {
	    final int index = s.indexOf(c, from);
	    if(index == -1) return -1;
	    return (curr + 1 == expected) ? index : 
	        nthOccurrence(s, index + 1, c, curr + 1, expected);
	}

}
