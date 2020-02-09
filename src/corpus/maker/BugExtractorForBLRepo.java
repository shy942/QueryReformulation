package corpus.maker;

import java.io.File;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.swing.internal.plaf.metal.resources.metal;

import utility.ContentWriter;
import utility.MiscUtility;

public class BugExtractorForBLRepo {

	public BugExtractorForBLRepo()
	{
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String corpus="Apache";
		String project="HBASE";
		String version="1_2_4";
		String base="E:\\PhD\\Repo\\"+corpus+"\\"+project+"\\"+version;
		String XMLfolderPath=base+"\\bugXML\\"+project+"_"+version+".xml";
		//String outputBase="E:\\PhD\\ASE2019\\BugReports\\"+corpus;
		//String outputBaseGoldSet="E:\\PhD\\ASE2019\\GoldSet\\"+corpus;
		new BugExtractorForBLRepo().extractBugReports(XMLfolderPath, base, base+"\\data\\gitInfo"+project+".txt");
		//new BugExtractorForBLRepo().createGodSets(XMLfolderPath, outputBaseGoldSet);
		
	}
	
	
	public void WriteContent(HashMap<String, String> bugInfoHM, HashMap<String, ArrayList<String>> bugFixInfoHM, String bugFolder, String gitFileadddress, HashMap<String, Long> dateInforMillisSorted)
	{
	   
	    String timeSortedBugId="";
        for(String bugID:dateInforMillisSorted.keySet())
        {
            timeSortedBugId=timeSortedBugId+bugID+"\n";
           
        }
        ContentWriter.writeContent(bugFolder+"\\data\\BugIDdateBased.txt", timeSortedBugId);
		ArrayList<String> list=new ArrayList<>();
		for(String bugID:bugInfoHM.keySet())
		{
			String outFile=bugFolder+"\\BugDataExtracted\\"+bugID+".txt";
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
	public void WriteContentGoldSet(HashMap<String, ArrayList<String>> bugFixInfoHM, String bugFolder)
    {
	    ArrayList<String> list=new ArrayList<>();
        for(String bugID:bugFixInfoHM.keySet())
        {
            String outFile=bugFolder+"\\"+bugID+".txt";
            ContentWriter.writeContent(outFile, bugFixInfoHM.get(bugID));
        }
        
    }
	protected void extractBugReports(String XMLfolderPath, String bugFolder, String gitFileadddress) {
		try {
			HashMap<String, Long> dateInforMillis=new HashMap<>();
			HashMap<String, String> bugInfoHM=new HashMap<>();
			HashMap<String, ArrayList<String>> bugFixInfoHM=new HashMap<>();
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(XMLfolderPath));

			// normalize text representation
			doc.getDocumentElement().normalize();
			ArrayList<String> allBugIDs=new ArrayList<>();
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
		               allBugIDs.add(bugID);
		               String opendate=eElement.getAttribute("opendate");
		               long milis=processOpenDate(opendate);
		               dateInforMillis.put(bugID, milis);
		               int bugInfo = eElement.getElementsByTagName("buginformation").getLength();
		              
		               if (bugInfo > 0) {
		            	   String summary = eElement.getElementsByTagName("summary").item(0).getTextContent();
		            	   //System.out.println(summary);
		            	   bugContent=bugContent+summary+"\n";
		            	   String description = eElement.getElementsByTagName("description").item(0).getTextContent();
		            	   //System.out.println(description);
		            	   bugContent=bugContent+description;
						
		            	   //System.out.println(bugID+"\n"+bugContent);
		            	   bugInfoHM.put(bugID,bugContent);
		                }
						//int fixfiles = eElement.getElementsByTagName("fixedFiles").getLength();
		               NodeList fixfiles = eElement.getElementsByTagName("fixedFiles");
		               NodeList fixfilesChildren = fixfiles.item(0).getChildNodes();

		              
		               
		               //For AspectJ
		               /*for(int k = 0; k < fixfilesChildren.getLength(); k++) {
                           Node file = fixfilesChildren.item(k);
                           //Only want stuff from ELEMENT nodes
                           if(file.getNodeType() == Node.ELEMENT_NODE) {
                               //System.out.println(file.getNodeName()+": "+file.getTextContent());
                               String fileAddressFull=file.getTextContent();
                               String [] spilter=fileAddressFull.split("/");
                               listOfFiles.add(spilter[spilter.length-1]);
                           }
                       }*/
		               //For Others
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
			//MiscUtility.showResult(10, bugInfoHM);
			//MiscUtility.showResult(90, bugFixInfoHM);
			HashMap<String, Long> dateInforMillisSorted=MiscUtility.sortByValuesIncrement(dateInforMillis);
			WriteContent(bugInfoHM, bugFixInfoHM, bugFolder, gitFileadddress, dateInforMillisSorted);
			ContentWriter.writeContent(bugFolder+"/data/BugIDs.txt", allBugIDs);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	protected long processOpenDate(String dateInfo)
	{
	    long processedTime=0;
	    System.out.println(dateInfo);
	    String[] spilter=dateInfo.split(" ");
	    String datePart1=spilter[0];
	    System.out.println(datePart1);
	    String[] spilter2=datePart1.split("-");
	    long millis=0;
	    String ddmmyyyy=spilter2[2]+"-"+spilter2[1]+"-"+spilter2[0];
	    ArrayList<Date> date=new ArrayList<>();
	    SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
	    try {
            Date date1=(Date) format.parse(ddmmyyyy);
            System.out.println(date1);
            
            millis = date1.getTime();
            System.out.println(millis);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	   return millis;
	   
	}
	
	protected void createGodSets(String XMLfolderPath, String bugFolder) {
        try {
            
            
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
                       
                       
                       NodeList fixfiles = eElement.getElementsByTagName("fixedFiles");
                       NodeList fixfilesChildren = fixfiles.item(0).getChildNodes();

                       for(int k = 0; k < fixfilesChildren.getLength(); k++) {
                           Node file = fixfilesChildren.item(k);
                           //Only want stuff from ELEMENT nodes
                           if(file.getNodeType() == Node.ELEMENT_NODE) {
                               System.out.println(file.getNodeName()+": "+file.getTextContent());
                               String fixedFile=file.getTextContent();
                              
                               if(fixedFile.contains(".")) 
                                   {
                                       String[] spilter4=fixedFile.split("\\.");
                                       fixedFile=spilter4[0]+".";
                                       for(int j=1;j<spilter4.length-2;j++)
                                       {
                                           fixedFile=fixedFile+spilter4[j]+"/";
                                       }
                                       fixedFile=fixedFile+spilter4[spilter4.length-2]+".java";
                                       System.out.println(fixedFile);
                                       listOfFiles.add(fixedFile);
                                   }
                               System.out.println(fixedFile);
                               if(fixedFile.contains(".")==false)listOfFiles.add(fixedFile);
                               
                           }
                       }    
                       bugFixInfoHM.put(bugID, listOfFiles);
                    }
            }
            
            //MiscUtility.showResult(90, bugFixInfoHM);
            WriteContentGoldSet(bugFixInfoHM, bugFolder);

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
