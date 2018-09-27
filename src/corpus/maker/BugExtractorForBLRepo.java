package corpus.maker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utility.ContentWriter;
import utility.MiscUtility;

public class BugExtractorForBLRepo {

	public BugExtractorForBLRepo()
	{
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String XMLfolderPath="E:\\PhD\\Repo\\Eclipse\\bugXML\\EclipseBugRepository.xml";
		new BugExtractorForBLRepo().extractBugReports(XMLfolderPath, "E:\\PhD\\Repo\\Eclipse\\BugDataExtracted", "E:\\PhD\\Repo\\Eclipse\\data\\gitInfoEclipse.txt");
		
	}
	
	
	public void WriteContent(HashMap<String, String> bugInfoHM, HashMap<String, ArrayList<String>> bugFixInfoHM, String bugFolder, String gitFileadddress)
	{
		ArrayList<String> list=new ArrayList<>();
		for(String bugID:bugInfoHM.keySet())
		{
			String outFile=bugFolder+"\\"+bugID+".txt";
			ContentWriter.writeContent(outFile, bugInfoHM.get(bugID));
		}
		
		String gitContent="";
		for(String bugID:bugFixInfoHM.keySet())
		{
			gitContent=gitContent+bugID+" "+bugFixInfoHM.get(bugID).size()+"\n";
			ArrayList<String> listofFiles=bugFixInfoHM.get(bugID);
			for(int i=0;i<listofFiles.size();i++)
			{
				gitContent=gitContent+listofFiles.get(i)+"\n";
			}
		}
		ContentWriter.writeContent(gitFileadddress, gitContent);
	}
	
	protected void extractBugReports(String XMLfolderPath, String bugFolder, String gitFileadddress) {
		try {
			
			HashMap<String, String> bugInfoHM=new HashMap<>();
			HashMap<String, ArrayList<String>> bugFixInfoHM=new HashMap<>();
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(XMLfolderPath));

			// normalize text representation
			doc.getDocumentElement().normalize();

			NodeList listOfBugs = doc.getElementsByTagName("bug");
			int totalBug = listOfBugs.getLength();
			System.out.println("Total Bug: " + totalBug);

			for (int i = 0; i < listOfBugs.getLength(); i++) {

				 Node nNode = listOfBugs.item(i);
		        // System.out.println(nNode.getNodeName());
		         String bugID="";
		         String bugContent="";
		         ArrayList<String> listOfFiles=new ArrayList<>();
		         if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		               Element eElement = (Element) nNode;
		               //System.out.println(eElement.getAttribute("id"));
		               bugID=eElement.getAttribute("id");
		               
		               int bugInfo = eElement.getElementsByTagName("buginformation").getLength();
		              
		               if (bugInfo > 0) {
		            	   String summary = eElement.getElementsByTagName("summary").item(0).getTextContent();
		            	   //System.out.println(summary);
		            	   bugContent=bugContent+summary;
		            	   String description = eElement.getElementsByTagName("description").item(0).getTextContent();
		            	   //System.out.println(description);
		            	   bugContent=bugContent+" "+description;
						
		            	   //System.out.println(bugID+"\n"+bugContent);
		            	   bugInfoHM.put(bugID,bugContent);
		                }
						//int fixfiles = eElement.getElementsByTagName("fixedFiles").getLength();
		               NodeList fixfiles = eElement.getElementsByTagName("fixedFiles");
		               NodeList fixfilesChildren = fixfiles.item(0).getChildNodes();

		               for(int k = 0; k < fixfilesChildren.getLength(); k++) {
		                   Node file = fixfilesChildren.item(k);
		                   //Only want stuff from ELEMENT nodes
		                   if(file.getNodeType() == Node.ELEMENT_NODE) {
		                       //System.out.println(file.getNodeName()+": "+file.getTextContent());
		                	   listOfFiles.add(file.getTextContent());
		                   }
		               }	
		               bugFixInfoHM.put(bugID, listOfFiles);
					}
		    }
			MiscUtility.showResult(10, bugInfoHM);
			MiscUtility.showResult(90, bugFixInfoHM);
			WriteContent(bugInfoHM, bugFixInfoHM, bugFolder, gitFileadddress);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}
