package corpus.maker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import utility.ContentLoader;

public class GoldSetExistancePreparation {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        //String SourceFolderPath="E:\\PhD\\LSI\\Repo\\AspectJ\\processedSourceCodes\\";
        
        
        //String gitFilePath="E:\\PhD\\LSI\\Repo\\AspectJ\\gitInfoAspectJSingleFile.txt";
        //new GoldSetExistancePreparation().checkExist(SourceFolderPath, gitFilePath);
        
      
    }

    
    public static void checkExist(String SourceFolderPath, String gitFilePath)
    {
        ArrayList<String> list_of_source_files=LoadSourceInformation(SourceFolderPath);
        HashMap<String, ArrayList<String>> gitInfoHM=getGitOutput(gitFilePath);
        int count=0;
        for(String key:gitInfoHM.keySet())
        {
            System.out.println("BugID: "+key);
            ArrayList<String> list_of_source=gitInfoHM.get(key);
            System.out.println(list_of_source.size());
            int found=0;
            
            for(String fileKey:list_of_source)
            {
               
                if(list_of_source_files.contains(fileKey))
                {
                    ++found;
                    //System.out.println(fileKey+" ");
                    //System.out.println(++found);
                }
                if(list_of_source.size()==found)
                {
                    System.out.println(++count);
                }
            }
        }
    }
    
    public static ArrayList<String> LoadSourceInformation(String SourceFolderPath)
    {
        //Load source file information
        ArrayList<String> list_of_source_files=new ArrayList();
        File[] files = new File(SourceFolderPath).listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("Directory: " + file.getName());
                
            } else {
                System.out.println("File: " + file.getName());
                list_of_source_files.add(file.getName());
            }
        }
        
        return list_of_source_files;
    }
    
    public static HashMap<String, ArrayList<String>> getGitOutput (String gitPath) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        HashMap<String, ArrayList<String>>hm=new HashMap<>();
       ArrayList<String> lines = ContentLoader
                        .readContent(gitPath);
                for (int i = 0; i < lines.size();) {
                    String currentLine = lines.get(i);
                    String[] items = currentLine.split("\\s+");
                    if (items.length == 2) {
                        String bugID = items[0].trim();
                        int filecount = Integer.parseInt(items[1].trim());
                        if(filecount>0)
                        {
                        ArrayList<String> tempList = new ArrayList<>();
                        for (int currIndex = i + 1; currIndex <= i + filecount; currIndex++) {
                            if(!tempList.contains(lines.get(currIndex)))tempList.add(lines.get(currIndex));
                        }
                        // now store the change set to bug
                        hm.put(bugID, tempList);
                        }
                        // now update the counter
                        i = i + filecount;
                        i++;
                    }
                }
                System.out.println("Changeset reloaded successfully for :"
                        + hm.size());
                return hm;
    
    }
    
    public static void copyBugData(String inputFolderPath, String outputFolderPath)
    {
        
    }
}
