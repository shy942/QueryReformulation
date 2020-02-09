package Gold.set.maker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;

public class GSMaker {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileAddress="E:/PhD/Repo/Apache/HIVE/1_2_1/bugXML/HIVE_1_2_1.xml";
		GSMaker obj=new GSMaker();
		ArrayList<String> rawList=obj.readFile(fileAddress);
		HashMap<String, ArrayList<String>> hm=obj.preProcessList(rawList);
		obj.writeGoleSet(hm);
	}

	public void writeGoleSet(HashMap<String, ArrayList<String>> hm)
	{ 
		ArrayList<String> writeContent=new ArrayList<>();
		for(String key: hm.keySet())
		{
			if(key.matches("^[0-9]*$"))
			{
				System.out.println(key);
				ArrayList<String> list=hm.get(key);
				int length=list.size();
				writeContent.add(key+" "+length);
				for(String line:list)
				{
					writeContent.add(line);
				}
			}
		}
		ContentWriter.writeContent("E:/PhD/Repo/Apache/HIVE/1_2_1/data/gitInfoApaceHIVE.txt", writeContent);
	}
	
	public ArrayList<String> readFile(String gitfileAddress)
	{
		ArrayList<String> rawList=ContentLoader.getAllLinesList(gitfileAddress);
		//for(String line:rawList)System.out.println(line);
		return rawList;
	}
	public HashMap<String, ArrayList<String>> preProcessList(ArrayList<String> rawList)
	{
		HashMap<String, ArrayList<String>> hm=new HashMap<>();
		int i=0;
		String bugID="";
		ArrayList<String> fileList=new ArrayList<>();
		int count=0;
		for(String line: rawList){
			//System.out.println(line);
			String[] spilter2=line.split("	");
			System.out.println(++count+" "+spilter2.length+" "+line);
			if(spilter2.length==2){
				//System.out.println(bugID+""+ spilter2[1]);
				String file=spilter2[1];
				if(file.contains(".java"))
				{
				String[] spilter3=file.split("/");
				String fileWithDot="";
				for(int s=4;s<spilter3.length-1;s++)fileWithDot+=spilter3[s]+".";
				fileWithDot=fileWithDot+spilter3[spilter3.length-1];
				//System.out.println(bugID+" "+fileWithDot);
				fileList.add(fileWithDot);
				}
			}
			if(line.contains("Bug")&&spilter2.length!=2){
				if(i>0)hm.put(bugID, fileList);
				i++;
				System.out.println(++count+" "+spilter2.length+" "+line);
				int occur=line.indexOf("Bug");
				String[] spilter=line.split(" ");
				int found=0;
				for(int j=0;j<spilter.length;j++){
					if(spilter[j].equalsIgnoreCase("Bug")){
						found=j;break;}
					}
					System.out.println(found);
					if(found==1)bugID=spilter[found+1];
							
					System.out.println(bugID);
					fileList=new ArrayList<>();
				}
						
			}
			
		
		//MiscUtility.showResult(10, hm);
		return hm;
	}
}
