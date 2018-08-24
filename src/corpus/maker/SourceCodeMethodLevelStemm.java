package corpus.maker;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.SSLEngineResult.HandshakeStatus;

import stemmer.Stemmer;
import utility.ContentLoader;
import utility.ContentLoaderQR;
import utility.ContentWriter;
import utility.MiscUtility;

public class SourceCodeMethodLevelStemm {

	public HashMap <String, String>sourceContentMethod;
	public HashMap<String, String> sourceContent;
	public HashMap<String, ArrayList<String>> processedContent;
	
	HashMap<String, Integer> IDkeywordMap;
	HashMap<String, Integer> IDSourceMap;
	Stemmer stemmer;
	ArrayList<String> stopwords;
	public SourceCodeMethodLevelStemm(String metInPath, String inPath, String IDkeywordAddress, String IDSourceAddress)
	{
		this.sourceContentMethod=this.LoadContent(metInPath);
		this.sourceContent=this.LoadContent(inPath);
		this.processedContent=new HashMap<>();
		this.IDkeywordMap=this.loadHashMap(IDkeywordAddress);
		this.IDSourceMap=this.loadHashMap(IDSourceAddress);
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
		SourceCodeMethodLevelStemm obj=new SourceCodeMethodLevelStemm("C:\\Users\\Mukta\\Dropbox\\WorkinginHome\\SCAM\\QueryReformulation\\data\\ExampleSourceCodeFilesMethodLevel\\","E:\\PhD\\Data\\NotProcessedSourceMethodLevel\\",".\\data\\ID-Keyword.txt",".\\data\\changeset-pointer\\ID-SourceFile.txt");
	    //MiscUtility.showResult(10, obj.sourceContentMethod);
	    //MiscUtility.showResult(10, obj.sourceContent);
	    obj.processedContent=obj.DoNotProcessContent(obj.sourceContentMethod, "E:\\PhD\\Data\\NotProcessedSourceMethodLevel\\");
	    obj.ConvertToNumber();
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
	
	public HashMap<String, ArrayList<String>> DoNotProcessContent(HashMap <String, String>sourceContentMethod, String outFolder)
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
			//if(filePathNew.equalsIgnoreCase("org.eclipse.jface.viewers.IStructuredSelection.java")){
			int noOfMethod=Integer.valueOf(spilter[1]);
			if(noOfMethod>0){
			if(this.sourceContent.containsKey(filePathNew.trim()))
				{
					ArrayList<String> outContent=new ArrayList<>();
					//System.out.println(filePathNew);
					for(int i=2;i<spilter.length;i++)
				    {
				    	String methodContent=spilter[i];
				    	if(!methodContent.equalsIgnoreCase(""))
				    	{
				    		outContent.add(methodContent);
				    	}
				    }
					this.processedContent.put(filePathNew, outContent);
				    ContentWriter.writeContent(outFolder+filePathNew, outContent);
				}
			}
			}
			//}
		}
		return this.processedContent;
	}
	
	
	public HashMap<String, ArrayList<String>> ProcessContent(HashMap <String, String>sourceContentMethod, String outFolder)
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
			//if(filePathNew.equalsIgnoreCase("org.eclipse.jface.viewers.IStructuredSelection.java")){
			int noOfMethod=Integer.valueOf(spilter[1]);
			if(noOfMethod>0){
			if(this.sourceContent.containsKey(filePathNew.trim()))
				{
					ArrayList<String> outContent=new ArrayList<>();
					//System.out.println(filePathNew);
					for(int i=2;i<spilter.length;i++)
				    {
				    	String methodContent=spilter[i];
				    	if(!methodContent.equalsIgnoreCase(""))
				    	{
				    		
				    	//System.out.println(i);
						String afterStopWordRemoval=this.StopWordRemover(methodContent);
						//System.out.println(afterStopWordRemoval);
						
						String ProcessedContent=this.performNLP(afterStopWordRemoval);
						//System.out.println(ProcessedContent);
						outContent.add(ProcessedContent);
				    	}
				    }
					this.processedContent.put(filePathNew, outContent);
				    ContentWriter.writeContent(outFolder+filePathNew, outContent);
				}
			}
			}
			//}
		}
		return this.processedContent;
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

	   public HashMap<String, Integer> loadHashMap(String address)
	    {
		   HashMap<String, Integer> temp=new HashMap<>();
	        ArrayList<String> lines=ContentLoader.getAllLinesList(address);
	        for(String line:lines)
	        {
	        	//System.out.println(line);
	        	String[] spilter=line.split(":");
	        	if(spilter.length>1)
	        	{
	        		int id=Integer.valueOf(spilter[0]);
	        		String word=spilter[1].trim();
	        		temp.put(word, id);
	        	}
	        	
	        }
	        return temp;
	    }
	
	public void ConvertToNumber()
	{
		ArrayList <String> outputContent=new ArrayList<>();
		for(String Sid:this.processedContent.keySet())
		{
			if(this.IDSourceMap.containsKey(Sid.trim())&&this.processedContent.containsKey(Sid.trim()))
			{
				int sourceIntId=this.IDSourceMap.get(Sid);	
				//if(sourceIntId==2190){
				String contentEachSource="";
				String contentToWrite="";
				int containContent=0;
				int no_of_method=0;
				String ppcontent= this.processedContent.get(Sid).get(0);
				for(String pcontent:this.processedContent.get(Sid))
				{
					
					int found=0;
					contentToWrite=sourceIntId+"\n";
					
					ArrayList<Integer> content=new ArrayList<>();
					String[] spilter=pcontent.split(" ");
					for(int i=0;i<spilter.length;i++)
					{
						String key=spilter[i].trim();
						if(this.IDkeywordMap.containsKey(key))
						{
							content.add(this.IDkeywordMap.get(key));
							//System.out.print(key+" "+this.IDkeywordMap.get(key));
							found++;
							containContent=1;
						}
					}
					if(found!=0)
					{
						no_of_method++;
						System.out.println(found+" "+content.size());
						String output=MiscUtility.listInt2Str(content);
						contentEachSource+="\n"+output;
						
					}
				}
				//System.out.println(sourceIntId+": "+output);
				if(containContent==1)
					{
						contentToWrite+=no_of_method+contentEachSource;
						outputContent.add(contentToWrite);
					}
			}
			//}
		}
		ContentWriter.writeContent(".\\data\\Sid-MatchWord2.txt", outputContent);
	}
}
