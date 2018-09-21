package Gold.set.maker;

import java.util.ArrayList;
import java.util.HashMap;

import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;

public class GSMaker {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileAddress="C:\\Users\\Mukta\\Workspace-2018\\eclipse.platform.swt\\filename.txt";
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
		ContentWriter.writeContent(".//data//ginInfoSWT.txt", writeContent);
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
		for(String line: rawList){
			
			if(line.contains("Bug")&&line.contains("fix")){
				if(i>0)hm.put(bugID, fileList);
				i++;
				//System.out.println((i)+" "+line);
				int occur=line.indexOf("Bug");
				String[] spilter=line.split(" ");
				int found=0;
				for(int j=0;j<spilter.length;j++){
					if(spilter[j].equalsIgnoreCase("Bug"))found=j;
				}
				bugID=spilter[found+1];
				
				//System.out.println(bugID);
				fileList=new ArrayList<>();
				}
				else{
				String[] spilter2=line.split("	");
				if(spilter2.length==2){
					//System.out.println(bugID+""+ spilter2[1]);
					String file=spilter2[1];
					if(file.contains(".java")){
			        String[] spilter3=file.split("/");
			        String fileWithDot="";
			        for(int s=4;s<spilter3.length-1;s++)fileWithDot+=spilter3[s]+".";
			        fileWithDot=fileWithDot+spilter3[spilter3.length-1];
					//System.out.println(bugID+" "+fileWithDot);
					fileList.add(fileWithDot);
					}
				}
			}
		}
		//MiscUtility.showResult(10, hm);
		return hm;
	}
}
