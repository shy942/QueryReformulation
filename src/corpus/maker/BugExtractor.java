package corpus.maker;

import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utility.ContentWriter;
import config.StaticData;

public class BugExtractor {

	String xmlFileName;
	String outDir;

	public BugExtractor(String xmlFileName, int year) {
		this.xmlFileName = StaticData.BUGDIR + "/BugAllContent/XMLfiles/" + xmlFileName;
		this.outDir = StaticData.BUGDIR + "/BugAllContent/ExtractedData/"+ year;
	}
	
	public BugExtractor(String xmlFileName) {
		this.xmlFileName = StaticData.BUGDIR + "/BugAllContent/XMLfiles/" + xmlFileName;
		this.outDir = StaticData.BUGDIR + "/BugAllContent/ExtractedData/";
	}


	protected void extractBugReports() {
		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(this.xmlFileName));

			// normalize text representation
			doc.getDocumentElement().normalize();

			NodeList listOfBugs = doc.getElementsByTagName("bug");
			int totalBug = listOfBugs.getLength();
			System.out.println("Total Bug: " + totalBug);

			for (int i = 0; i < listOfBugs.getLength(); i++) {

				Node BugReport = listOfBugs.item(i);
				if (BugReport.getNodeType() == Node.ELEMENT_NODE) {

					String xmlcontent = new String();
					Element eElement = (Element) BugReport;
					String BugID = eElement.getElementsByTagName("bug_id")
							.item(0).getTextContent();

					String outFile = this.outDir + "/" + BugID + ".txt";

					String title = eElement.getElementsByTagName("short_desc")
							.item(0).getTextContent();
					xmlcontent += title + " ";

					int numOfLong_desc = eElement.getElementsByTagName(
							"long_desc").getLength();
					if (numOfLong_desc > 0) {
						for (int j = 0; j < numOfLong_desc; j++) {
							String description = eElement
									.getElementsByTagName("thetext").item(j)
									.getTextContent();
							description = description.trim();
							int comment_seq=j+1;
							//String processedContent=sentenceExamples(description, comment_seq); 
							//xmlcontent +=processedContent.trim();
							xmlcontent += description + " ";
						}
					}

					// now saving the bug report
					ContentWriter.writeContent(outFile, xmlcontent);
					System.out.println("Saved:" + BugID);

				}

			}

			// JOptionPane.showMessageDialog(null,
			// "The XML file successfully transferred into a text file");
		} catch (Exception exc) {
			exc.printStackTrace();
		}

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
	        	 System.out.print("--------------------------------------------------------------------------------\n");
	        	 System.out.println(target.subSequence(pre, boundary));
	        	 String sentence=String.valueOf(target.subSequence(pre, boundary));
	        	 sentecnce_coll[++index]=sentence;
	        	 comment++;
	        	 System.out.println("\nSentence "+comment_seq+" "+comment+"\n");
	        	 if(sentence.length()>0)returnContent=returnContent+sentence+"\n";
				//fw.write("\nSentence "+comment_seq+" "+comment+"\n");
				//fw.write(sentence);
				//fw_all.write(sentence+"\n"+"\n");
	        	 pre=boundary;
	        	 }
	         boundary = iterator.next();
	         
	      }
	      //return(sentecnce_coll);
	      //System.out.println(target);
	      //System.out.println(markers);
	      return returnContent;
	   }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String XMLfolderPath=StaticData.BUGDIR+"/BugAllContent/XMLfiles/";
		File[] files = new File(XMLfolderPath).listFiles();
		for (File file : files) {
	        if (file.isDirectory()) {
	            System.out.println("Directory: " + file.getName());
	            
	        } else {
	            System.out.println("File: " + file.getName());
	            new BugExtractor(file.getName()).extractBugReports();
	        }
	    }
		
		
		//String xmlFileName="show_bug_plat_UI_Closed_fixed_800_1stJan2015_to_26thFeb2016.xml";
		//int year=2015;
		//new BugExtractor(xmlFileName, year).extractBugReports();
		//This is a simple change to the fine.

	}

}
