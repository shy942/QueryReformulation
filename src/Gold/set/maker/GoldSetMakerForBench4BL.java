package Gold.set.maker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utility.ContentWriter;
import utility.MiscUtility;

public class GoldSetMakerForBench4BL {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        
        String project="Spring";
        String subproject="ROO";
        String version="1_1_0";
        new GoldSetMakerForBench4BL().extractBugReports("E:\\PhD\\Repo\\"+project+"\\"+subproject+"\\"+version+"\\bugXML/"+subproject+"_"+version+".xml","E:\\PhD\\Repo\\"+project+"\\"+subproject+"\\"+version+"\\" );
    }
    protected void extractBugReports(String XMLfolderPath, String bugFolderAddress) {
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
            //MiscUtility.showResult(10, bugInfoHM);
            //MiscUtility.showResult(90, bugFixInfoHM);
            
            WriteContentGoldSet(bugFixInfoHM, bugFolderAddress);
            ContentWriter.writeContent(bugFolderAddress+"/data/BugIDs.txt", allBugIDs);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
    public void WriteContentGoldSet(HashMap<String, ArrayList<String>> bugFixInfoHM, String bugFolder)
    {
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
        ContentWriter.writeContent(bugFolder+"/data/gitInfo.txt", gitContent);
        
    }
}
