package changeset.maker;

import java.util.ArrayList;
import utility.ContentLoader;
import utility.ContentWriter;

public class ChangesetMaker {

	String gitInfoFile;
	String changesetDir;

	public ChangesetMaker(String gitInfoFile) {
		this.gitInfoFile = gitInfoFile;
	}

	protected void makeChangeset(String base, String corpus, String project) {
		ArrayList<String> BugList=ContentLoader.readContent(base+"\\data\\BugIDdateBased.txt");
		System.out.println(BugList);
		ArrayList<String> lines = ContentLoader
				.getAllLinesOptList(this.gitInfoFile);
		String newGitContent="";
		ArrayList<String> listBugNotIncluded=new ArrayList<>();
		for (int i = 0; i < lines.size();) {
			//System.out.println(lines.get(i));
			String currentLine = lines.get(i);
			if (currentLine.matches("\\d+\\s+\\d+")) {
				String[] parts = currentLine.split("\\s+");
				int bugID = Integer.parseInt(parts[0].trim());
				int cfCount = Integer.parseInt(parts[1].trim());
				if (cfCount > 0) {
					ArrayList<String> cfiles = new ArrayList<>();
					ArrayList<String> tempList=new ArrayList<>();
					for (int j = i + 1; j <= i + cfCount; j++) {
						String fileURL = lines.get(j);
						cfiles.add(fileURL);
						
					}
					
					{
					   if(IsBugExist(String.valueOf(bugID), BugList))
					    //&&cfiles.size()<=5)
					    {
					       
					        String outputFile = base+"\\data\\changeset\\" + bugID + ".txt";
					        ContentWriter.writeContent(outputFile, cfiles);
					        //System.out.println(cfiles);
					        newGitContent=newGitContent+currentLine+"\n";
					        for(String l:cfiles)
					        {
					            newGitContent=newGitContent+l+"\n";
					            System.out.println(l);
					         
					        }
					        
					    }
					    else{
					        listBugNotIncluded.add(String.valueOf(bugID));
					    }
					    //if(cfiles.size()>5)System.out.println(bugID);
					}

					i = i + cfCount + 1;
					//System.out.println("Done:" + bugID);
				} else {
					i++;
				}
			} else {
				break;
			}
		}
		//System.out.println(newGitContent);
		ContentWriter.writeContent(base+"\\data\\gitInfoAll"+corpus+"-"+project+".txt", newGitContent);
		ContentWriter.writeContent(base+"\\data\\bugIdsNotCluded.txt", listBugNotIncluded);
	}

	
	protected boolean IsBugExist(String bugID, ArrayList<String> list)
	{
		if(list.contains(bugID)) return true;
		else return false;
	}

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	   
	    String corpus="Eclipse";
	    String project="";
	    String version="";
	    String base= "E:\\PhD\\Repo\\"+corpus+"\\"+project+"\\"+version;
	    String gitInfoFile = base+"\\data\\gitInfo"+project+".txt";
		//String gitInfoFile = "E:\\PhD\\Repo\\"+corpus+"\\data\\gitInfo"+corpus+".txt";
		new ChangesetMaker(gitInfoFile).makeChangeset(base, corpus, project);
	}
}
