package BugLocator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import utility.ContentLoader;

public class XMLInputCreator {

	HashMap<String,ArrayList<String>> gitInfoMap;
	HashMap<String, ArrayList<String>> xmlContent;
	
	public XMLInputCreator()
	{
	    this.xmlContent=new HashMap<String,ArrayList<String>>();
	    this.gitInfoMap=new HashMap<String,ArrayList<String>>();
	}
	
	protected void LoadFiles(String bugIDpath, String gitInfopath)
	 {
		
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
	 
	
	
	protected void ceateXMLforBugLocator(HashMap<String, ArrayList<String>> xmlContent, String XMLoutFile)
	{
		try
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			Element root = doc.createElement("bugrepository");
	        doc.appendChild(root);
	        
	        for(String key:xmlContent.keySet())
	        {
	        	ArrayList<String> list=xmlContent.get(key);
	        	Element bug = doc.createElement("bug");
	    		root.appendChild(bug);
	    		
	    		Attr id  = doc.createAttribute("id");
	    		id.setValue(list.get(0));
	    		bug.setAttributeNode(id);
	    		
	    		Attr opendate=doc.createAttribute("opendate");
	    		opendate.setValue(list.get(1));
	    		bug.setAttributeNode(opendate);
	    		
	    		Attr fixdate=doc.createAttribute("fixdate");
	    		fixdate.setValue(list.get(2));
	    		bug.setAttributeNode(fixdate);
	    		
	        }
	        
	        // write the content into xml file
	         TransformerFactory transformerFactory = TransformerFactory.newInstance();
	         Transformer transformer = transformerFactory.newTransformer();
	         DOMSource source = new DOMSource(doc);
	         StreamResult result = new StreamResult(new File(XMLoutFile));
	         transformer.transform(source, result);
	         
	         // Output to console for testing
	         StreamResult consoleResult = new StreamResult(System.out);
	         transformer.transform(source, consoleResult);
			
			
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
		
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
