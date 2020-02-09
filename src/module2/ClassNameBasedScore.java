package module2;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.tartarus.snowball.ext.porterStemmer;

import utility.ContentLoader;
import utility.ContentWriter;
import utility.CosineSimilarityVectors;
import utility.MiscUtility;

public class ClassNameBasedScore {
    String QueryFolderPath;
    String IDsourcePath;
    HashMap<String, String> QueryMap;
    HashMap<String, String> IDsourceMap;

    public ClassNameBasedScore(String QueryFolderPath, String IDsourcePath) {
        // TODO Auto-generated constructor stub
        this.QueryFolderPath=QueryFolderPath;
        this.IDsourcePath=IDsourcePath;
        this.QueryMap=new HashMap<>();
        this.IDsourceMap=new HashMap<>();
    }
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        //Required input
        //Bug Report Query
        String corpus="Spring";
        String project="ROO";
        String version="1_1_0";
        String base= "E:\\PhD\\Repo\\"+corpus+"\\"+project+"\\"+version;
        
        String test="test1";
        
        String QueryFolderPath=base+"\\data\\testsetForBL\\"+test+"\\";
        String IDsourcePath=base+"\\data\\changeset-pointer\\ID-SourceFile.txt";
        String outputFolder=base+"\\data\\module2\\"+test+"\\";
        //Source File Names
        ClassNameBasedScore obj=new ClassNameBasedScore(QueryFolderPath, IDsourcePath);
        obj.QueryMap=obj.LoadQueryAll(QueryFolderPath);
        //System.out.println(obj.QueryMap);
        obj.IDsourceMap=obj.LoadIDsource(IDsourcePath);
        System.out.println(obj.IDsourceMap);
        obj.masterCalculator( outputFolder);
        //obj.calculateSimilarity("code folding needs little nudge build build extension extensions page plug manifest editor flip source page note newly generated markup folding associated enter space source page code folding appears", "org.eclipse.ui.internal.texteditor.HippieCompletionCodeEngine.java");
    }

    public void masterCalculator(String outputFolder)
    {
        for(String qid:this.QueryMap.keySet())
        {
            ArrayList<String> output=new ArrayList<>();
            for(String sid:this.IDsourceMap.keySet())
            {
                String queryInfor=this.QueryMap.get(qid);
                String idsInfo=this.IDsourceMap.get(sid);
                double score=calculateSimilarity(queryInfor, idsInfo);
                System.out.println(qid+" "+sid+" "+score);
                if(score>0.0)output.add(sid+":"+score);
            }
            ContentWriter.writeContent(outputFolder+qid+".txt", output);
        }
    }
    
    public HashMap<String, String> LoadQueryAll(String QueryFolderPath)
    {
        File file=new File(QueryFolderPath);
        for (File fileEntry : file.listFiles()) {
     
           String content=ContentLoader.readContentSimple(QueryFolderPath+fileEntry.getName());
           this.QueryMap.put(fileEntry.getName(), content);
        }
        return this.QueryMap;
    }
    public HashMap<String, String> LoadIDsource(String IDsourcePath)
    {
        String content=ContentLoader.readContentSimple(IDsourcePath);
        String[] spilterline=content.split("\n");
        for(String line:spilterline)
        {
            String[] spiltercolon=line.split(":");
            this.IDsourceMap.put(spiltercolon[0], spiltercolon[1]);
        }
        return this.IDsourceMap;
    }
    public Double calculateSimilarity(String bugContent, String fullNameStr)
    {
        Double score=0.0;
        ArrayList<String> listBugContent=MiscUtility.str2List(bugContent);
       // System.out.println(listBugContent);
        String classNameStr=convertedFullnameToClassName(fullNameStr);
        ArrayList<String> listClassNameStr=processClassName(classNameStr);
       // System.out.println(listClassNameStr);
        
        ArrayList<String> uniqueBugContent=convertToUniqueWords(listBugContent);
        ArrayList<String> uniqueClassNameContent=convertToUniqueWords(listClassNameStr);
        score=SimiScoreCalc(uniqueBugContent, uniqueClassNameContent);
        return score;
    }
    
    public Double SimiScoreCalc(ArrayList<String> uniqueBugContent, ArrayList<String> uniqueClassNameContent)
    {
        Double score=0.0;
       // System.out.println(uniqueBugContent);
      //  System.out.println(uniqueClassNameContent);
        int count=0;
        for(String qword:uniqueBugContent)
        {
            for(String cword:uniqueClassNameContent)
            {
                //System.out.println(qword+" "+cword);
                if(qword.trim().equalsIgnoreCase(cword.trim())==true)
                {
                   // System.out.println(qword+" "+cword);
                    count++;
                }
            }
        }
        //System.out.println(count);
        score=Double.valueOf(count)/Double.valueOf(uniqueClassNameContent.size());
       // System.out.println(score);
        return score;
    }

    public String convertedFullnameToClassName(String fullNameStr)
    {
        String classNameStr="";
        //System.out.println(fullNameStr);
        String[] spilter=fullNameStr.split("[.]");
        classNameStr=spilter[spilter.length-2];
        return classNameStr;
    }
    
    public ArrayList<String> processClassName(String classNameStr)
    {
        ArrayList<String> processedCName=new ArrayList<>();
        String[] words = classNameStr.split("(?=[A-Z])");
        processedCName = new ArrayList<String>(Arrays.asList(words));
        return processedCName;
    }
    
    public ArrayList <String> convertToUniqueWords(ArrayList<String> inputString)
    {
        ArrayList<String> outputList=new ArrayList<>();
        for(String word:inputString)
        {
            if(outputList.contains(word))
            {
                //Do nothing
            }
            else
            {
                outputList.add(word.toLowerCase());
            }
        }
        return outputList;
    }
    
}
