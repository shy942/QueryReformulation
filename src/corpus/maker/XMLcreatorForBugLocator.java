package corpus.maker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLcreatorForBugLocator {
    ArrayList<String> bugIDs;

    ArrayList<String> extractedBugInfo;
    HashMap<String, ArrayList<String>> xmlContent;
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String XMLfilePath="E:\\BugLocator\\data\\ZXingBugRepository.xml";
        XMLcreatorForBugLocator obj=new XMLcreatorForBugLocator();
        obj.extractBugReports(XMLfilePath);
        System.out.println(obj.extractedBugInfo);
    }
public XMLcreatorForBugLocator() {
        
        this.bugIDs=new ArrayList<String>();
        
        this.xmlContent=new HashMap<String,ArrayList<String>>();
        this.extractedBugInfo=new ArrayList<String>();
    }
protected ArrayList<String> extractBugReports(String xmlFileName) {
        
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(xmlFileName));

            // normalize text representation
            doc.getDocumentElement().normalize();
            
            Element root = (Element) doc.getElementsByTagName("bugrepository");
            String attName=root.getElementsByTagName("name").item(0).getTextContent();
            
            NodeList listOfBugs = doc.getElementsByTagName("bug");
            int totalBug = listOfBugs.getLength();
            System.out.println("Total Bug: " + totalBug);
            String BugID="";
            for (int i = 0; i < listOfBugs.getLength(); i++) {

                Node BugReport = listOfBugs.item(i);
                if (BugReport.getNodeType() == Node.ELEMENT_NODE) {

                    String xmlcontent = new String();
                    Element eElement = (Element) BugReport;
                    BugID = eElement.getElementsByTagName("bug")
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
                            //String processedContent=sentenceExamples(description, comment_seq); 
                            //String filteredContent=SimpleFilteration(processedContent);
                            xmlcontent +=description; 
                            
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
}
