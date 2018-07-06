package corpus.maker;

import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.text.StyleContext.SmallAttributeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import BugLocator.SourceFileExistTest;
import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;
import config.StaticData;

public class BugExtractorForBugLocator {
	
	//String xmlFileName;
	ArrayList<String> bugIDs;
	HashMap<String,ArrayList<String>> gitInfoMap;
	ArrayList<String> extractedBugInfo;
	HashMap<String, ArrayList<String>> xmlContent;

	public BugExtractorForBugLocator(String xmlFileName) {
		//this.xmlFileName ="./data/XMLfiles/"+xmlFileName;
		this.bugIDs=new ArrayList<String>();
		this.gitInfoMap=new HashMap<String,ArrayList<String>>();
		this.extractedBugInfo=new ArrayList<String>();
		this.xmlContent=new HashMap<String,ArrayList<String>>();
	}
	public BugExtractorForBugLocator() {
		
		this.bugIDs=new ArrayList<String>();
		this.gitInfoMap=new HashMap<String,ArrayList<String>>();
		this.xmlContent=new HashMap<String,ArrayList<String>>();
		this.extractedBugInfo=new ArrayList<String>();
	}
	 
	protected void LoadFiles(String bugIDpath, String gitInfopath)
	 {
		 this.bugIDs=ContentLoader.getAllLinesList(bugIDpath);
		 ArrayList <String> gitContent=ContentLoader.getAllLinesList(gitInfopath);
		 //Create a hushMap to contain git information for easier further processing 
		 for(int i=0;i<gitContent.size();i++)
		 {
			 ArrayList <String> sourceList=new ArrayList<String>();
			 
			 String[] spilter=gitContent.get(i).split("\\s+");
			 String bugID=spilter[0];
			 int nnoOfFixFiles=Integer.valueOf(spilter[1]);
			 
			 for(int j=i+1;j<=i+nnoOfFixFiles;j++)
			 {
				 sourceList.add(gitContent.get(j));
			 }
			 gitInfoMap.put(bugID, sourceList); 
			 i=i+nnoOfFixFiles;
		 }
		 //MiscUtility.showResult(10, gitInfoMap);
	 }
	 
	 
	 
	
	 
	 
	protected ArrayList<String> extractBugReports(String xmlFileName, String outfile) {
		
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(xmlFileName));

			// normalize text representation
			doc.getDocumentElement().normalize();

			NodeList listOfBugs = doc.getElementsByTagName("bug");
			int totalBug = listOfBugs.getLength();
			System.out.println("Total Bug: " + totalBug);
			String BugID="";
			for (int i = 0; i < listOfBugs.getLength(); i++) {

				Node BugReport = listOfBugs.item(i);
				if (BugReport.getNodeType() == Node.ELEMENT_NODE) {

					String xmlcontent = new String();
					Element eElement = (Element) BugReport;
					BugID = eElement.getElementsByTagName("bug_id")
							.item(0).getTextContent();
					xmlcontent +=BugID+"\n";
					
					String openDate=eElement.getElementsByTagName("creation_ts").item(0).getTextContent();
					xmlcontent+=openDate+"\n";
					
					
					String fixDate=eElement.getElementsByTagName("delta_ts").item(0).getTextContent();
					xmlcontent+=fixDate+"\n";
					
  
					String title = eElement.getElementsByTagName("short_desc")
							.item(0).getTextContent();
					xmlcontent += title + "\n";

					int numOfLong_desc = eElement.getElementsByTagName(
							"long_desc").getLength();
					if (numOfLong_desc > 0) {
						for (int j = 0; j < 1; j++) {
							String description = eElement
									.getElementsByTagName("thetext").item(j)
									.getTextContent();
							description = description.trim();
							int comment_seq=j+1;
							String processedContent=sentenceExamples(description, comment_seq); 
							String filteredContent=SimpleFilteration(processedContent);
							xmlcontent +=filteredContent.trim(); 
							
						}
					} 
					
					
					this.extractedBugInfo.add(xmlcontent);
					//ContentWriter.appendContent(outfile, extractedBugInfo);
					//if(BugID.equals("118107"))System.out.println(xmlcontent);

				}

			}

			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		return extractedBugInfo;

	}

	
	
	public String SimpleFilteration(String content)
	{
		String filteredConetnt="";
		String[] spilter=content.split("\\s+");
		for(String word:spilter)
		{
			filteredConetnt+=word.trim()+" ";
		}
		//System.out.println("===============\n"+filteredConetnt);
		return filteredConetnt;
	}
	
	
	
	static String sentenceExamples(String commen_col, int comment_seq) {
		//static void sentenceExamples() {
			 
			String processedContent="";
		      Locale currentLocale = new Locale ("en","US");
		      BreakIterator sentenceIterator = 
		         BreakIterator.getSentenceInstance(currentLocale);
		      String someText=commen_col;
		      //String someText = "She stopped.  " +
		                       //"She said, \"Hello there,\" and then went on.";
		      try {
		    	  processedContent=markBoundaries(someText, sentenceIterator, comment_seq);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		     return processedContent;
		      //return sentecnce_coll;
		  }
	static String markBoundaries(String target, BreakIterator iterator, int comment_seq) throws IOException {

		  String returnContent="";
		  String[] sentecnce_coll=new String[1000];
		  int index=0;
	      StringBuffer markers = new StringBuffer();
	      markers.setLength(target.length() + 1);
	      for (int k = 0; k < markers.length(); k++) {
	         markers.setCharAt(k,' ');
	      }

	      iterator.setText(target);
	      int boundary = iterator.first();
	      int i=0;
	      int pre=0;
	      int comment=0;
	      while (boundary != BreakIterator.DONE) {
	         markers.setCharAt(boundary,'^');
	         //System.out.println("i: "+(++i));
	         ++i;
	         if(i>1)
	        	 {
	        	 //System.out.print("--------------------------------------------------------------------------------\n");
	        	 //System.out.println(target.subSequence(pre, boundary));
	        	 String sentence=String.valueOf(target.subSequence(pre, boundary));
	        	 sentecnce_coll[++index]=sentence;
	        	 comment++;
	        	 //System.out.println("\nSentence "+comment_seq+" "+comment+"\n");
	        	 if(sentence.length()>0)returnContent=returnContent+sentence+"\n";
				//fw.write("\nSentence "+comment_seq+" "+comment+"\n");
				//fw.write(sentence);
				//fw_all.write(sentence+"\n"+"\n");
	        	 pre=boundary;
	        	 }
	         boundary = iterator.next();
	         
	      }
	 
	      return returnContent;
	   }
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
		BugExtractorForBugLocator obj=new BugExtractorForBugLocator();;
		//ArrayList<String> extractedBugInfo=new ArrayList<>();
		String XMLfolderPath="./data/XMLfiles/";
		File[] files = new File(XMLfolderPath).listFiles();
		for (File file : files) {
	        if (file.isDirectory()) {
	            System.out.println("Directory:  " + file.getName());
	            
	        } else {
	            System.out.println("File: " + file.getName());
	         
	            obj.extractBugReports("./data/XMLfiles/"+file.getName(),"./data/BugCorpus/ReadyForXMLwrite.txt"); 
	           
	        }
	    }
		 System.out.println(obj.extractedBugInfo.size()); 
		 
		 
		 String folderPath="/Users/user/Downloads/buglocator1/Src/eclipse.platform.ui-master";
		 SourceFileExistTest objS1=new SourceFileExistTest(folderPath);
		 File currentDir = new File(folderPath); // current directory
		 objS1.displayDirectoryContents(currentDir,objS1,1);
		 ArrayList<String> list1=new ArrayList<>();
		 list1=objS1.returnList();
		 
		 String folderPath2="./data/processed";
		 SourceFileExistTest objS2=new SourceFileExistTest(folderPath2);
		 File currentDir2 = new File(folderPath2); // current directory
		 objS2.displayDirectoryContents(currentDir2,objS2,0);
		 ArrayList<String> list2=new ArrayList<>();
		 list2=objS2.returnList();
		 
		 
		 System.out.println("========================"+list1);
		 System.out.println("========================"+list2);
		 
		 obj.CreateXMLcontentToWrite(obj.extractedBugInfo, list1,list2);
		 obj.ceateXMLforBugLocator(obj.xmlContent, "./data/Eclipse.xml");
	}

	
	
	private void CreateXMLcontentToWrite(ArrayList<String> extractedBugInfo, ArrayList<String> listExist,  ArrayList< String> listExist2) {
		// TODO Auto-generated method stub
		this.LoadFiles("./data/bugIDs.txt", "./data/GitInfoFile2.txt");
		for(int i=0;i<this.extractedBugInfo.size();i++)
		{
			String bugInfo=this.extractedBugInfo.get(i);
			String[] spilter=bugInfo.split("\n");
			String bugID=spilter[0];
			if(this.bugIDs.contains(bugID)&&spilter.length>4)
			{
				ArrayList<String> saveData=new ArrayList<String>();
				String opendate=spilter[1];
				String fixdate=spilter[2];
				String summary=spilter[3];
				String description=spilter[4];
				saveData.add(opendate);
				saveData.add(fixdate);
				saveData.add(summary);
				saveData.add(description);
				int atLeast1SourceFile=0;
				if(gitInfoMap.containsKey(bugID)) 
				{
					ArrayList<String> sourceList=new ArrayList<>();
					sourceList=gitInfoMap.get(bugID);
					int length=sourceList.size();
					for(int j=0;j<length;j++)
					{
						String link=sourceList.get(j);
					    //System.out.println("Old: "+link);
					    
					    int index = nthOccurrence(link, '/', 3);
					    //if(index>0)System.out.println("New: "+link.substring(index+1, link.length()));
						link=link.substring(index+1, link.length());
						if(link.contains(".java"))
						{
							link=link.replaceAll("/", ".");
							if(listExist.contains(link)&&listExist2.contains(link))
							{
								saveData.add(link);
								atLeast1SourceFile++;
							}
						}
					}
					//System.out.println(bugID+" "+sourceList.size());
					if(atLeast1SourceFile>0)xmlContent.put(bugID, saveData);
				}
				
			}
		}
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
	
	protected void ceateXMLforBugLocator(HashMap<String, ArrayList<String>> xmlContent, String XMLoutFile)
	{
		MiscUtility.showResult(10, xmlContent);
		try
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			Element root = doc.createElement("bugrepository");
			Attr reponame=doc.createAttribute("name");
			reponame.setValue("Eclipse");
			root.setAttributeNode(reponame);
	        doc.appendChild(root);
	        
	        for(String key:xmlContent.keySet())
	        {
	        	ArrayList<String> list=xmlContent.get(key);
	        	Element bug = doc.createElement("bug");
	    		root.appendChild(bug);
	    		
	    		bug.setAttribute("id", key);
	    		bug.setAttribute("opendate", list.get(0));
	    		bug.setAttribute("fixdate", list.get(1));
	    		
//	    		Attr id  = doc.createAttribute("id");
//	    		id.setValue(key);
//	    		bug.setAttributeNode(id);
//	    		
//	    		Attr opendate=doc.createAttribute("opendate");
//	    		opendate.setValue(list.get(0));
//	    		bug.setAttributeNode(opendate);
//	    		
//	    		Attr fixdate=doc.createAttribute("fixdate");
//	    		fixdate.setValue(list.get(1)+"\n");
//	    		bug.setAttributeNode(fixdate);
	    		
	    		Element buginformation = doc.createElement("buginformation");
	    		bug.appendChild(buginformation);
	    		
	    		Element summary = doc.createElement("summary");
	    		summary.appendChild(doc.createTextNode(list.get(2)));
	    		buginformation.appendChild(summary);
	    		
	    		Element description = doc.createElement("description");
	    		description.appendChild(doc.createTextNode(list.get(3)));
	    		buginformation.appendChild(description);
	    		
	    		
	    		Element fixedFiles = doc.createElement("fixedFiles");
	    		bug.appendChild(fixedFiles);
	    		
	    		
	    		for(int l=4;l<list.size();l++)
	    		{
	    			Element file = doc.createElement("file");
	    			file.appendChild(doc.createTextNode(list.get(l)));
	    			fixedFiles.appendChild(file);
	    		}
	    		
	    		
	        }	
	        
	        // write the content into xml file
	         TransformerFactory transformerFactory = TransformerFactory.newInstance();
	         Transformer transformer = transformerFactory.newTransformer();
	         transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			 transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			 transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	         DOMSource source = new DOMSource(doc);
	         StreamResult result = new StreamResult(new File(XMLoutFile));
	         transformer.transform(source, result); 
	         
	         // Output to console for testing
	         //StreamResult consoleResult = new StreamResult(System.out);
	         //transformer.transform(source, consoleResult);
			
			
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
		
	}
}
