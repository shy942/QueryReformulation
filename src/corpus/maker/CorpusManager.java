package corpus.maker;



import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.github.javaparser.JavaParser;
import com.github.javaparser.TokenMgrError;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;
import stopwords.StopWordManager;
import utility.ContentLoader;

public class CorpusManager {

    String corpusName;
    String corpusDir;
    int filemodified = 0;
    int failed = 0;
    StopWordManager manager = null;
    String algoDir = "lucene";

    public CorpusManager(String corpusName, String corpusDir) {
        // initialization
        this.corpusName = corpusName;
        // using the temp. code folder
        this.corpusDir = corpusDir;
        //this.manager = new StopWordManager();
    }

    @Deprecated
    protected void normalizePageCMV(String javaFileName, boolean CCD) {
        // normalize the Java File name
        try {
            String content = ContentLoader.readContentSimple(javaFileName);
            CompilationUnit cu = JavaParser.parse(new ByteArrayInputStream(
                    content.getBytes()));
            if (cu != null) {
                SourceVisitor visitor = new SourceVisitor();
                cu.accept(visitor, null);

                // now access the extracted information
                ArrayList<String> classNames = visitor.classNames;
                ArrayList<String> methodNames = visitor.methodNames;
                ArrayList<String> methodcalls = visitor.methodcalls;
                ArrayList<String> attributes = visitor.attributeNames;

                String filecontent = new String();

                // lets skip the comments for now.

            
                try {
                    if (classNames != null && !classNames.isEmpty())
                        for (String className : classNames)
                            filecontent += className + "\n";
                } catch (Exception e2) {
                }
                try {
                    if (methodNames != null && !methodNames.isEmpty())
                        for (String method : methodNames)
                            filecontent += method + "\n";
                } catch (Exception e3) {
                }
                try {
                    if (methodcalls != null && !methodcalls.isEmpty())
                        for (String methodcall : methodcalls)
                            filecontent += methodcall + "\n";
                } catch (Exception e4) {
                }
                try {
                    if (attributes != null && !attributes.isEmpty())
                        for (String attribute : attributes)
                            filecontent += attribute + "\n";
                } catch (Exception e5) {
                }

                // remove punctuation and others
                filecontent = removeSpecialChars(filecontent);
                if (CCD) {
                    filecontent = decomposeCCLine(filecontent);
                }

                // saving the file
                System.out.println(filecontent);
                //saveJavaFile(filecontent, javaFileName);
                filemodified++;
            }
        } catch (Exception e) {
            // do nothing
            // System.err.println("Failed :" + javaFileName + " " +
            // e.getMessage());
            // e.printStackTrace();
            failed++;
        } catch (TokenMgrError err) {
            // do nothing
            // System.err.println("Failed:" + javaFileName + " "+
            // err.getMessage());
            failed++;
        }
    }

    protected String removeSpecialChars(String sentence) {
        // removing special characters
        String regex = "\\p{Punct}+|\\s+|\\d+";
        String[] parts = sentence.split(regex);
        String refined = new String();
        for (String str : parts) {
            refined += str + " ";
        }
        // if(modifiedWord.isEmpty())modifiedWord=word;
        return refined.trim();
    }

    @Deprecated
    protected String decomposeCamelCase(String ccToken) {
        // decomposing camel case tokens using regex
        String camRegex = "([a-z])([A-Z]+)";
        String replacement = "$1\t$2";
        String decomposed = ccToken.replaceAll(camRegex, replacement);
        return decomposed;
    }

    protected String decomposeCCLine(String line) {
        String[] tokens = line.split("\\s+");
        String temp = new String();
        for (String token : tokens) {
            String decomposed = decomposeCamelCase(token);
            temp += decomposed + " ";
        }
        return temp.trim();
    }

    protected void normalizePageAllContent(String javaFileName,
            boolean removePunctuation) {
        String content = ContentLoader.readContentSimple(javaFileName);
        String[] lines = content.split("\n");
        String newcontent = new String();
        for (String line : lines) {
            String modified = removeSpecialChars(line);
            // new addition: decomposing camel cases
            // String decomposed = decomposeCCLine(modified);
            // newcontent += decomposed + "\n";
            modified = manager.getRefinedSentence(modified);
            newcontent += modified + "\n";
        }
        saveJavaFile(newcontent, javaFileName);
        filemodified++;
    }

    protected void saveJavaFile(String newContent, String javaFileName) {
        // overwriting the file
        try {
            FileWriter fwriter = new FileWriter(new File(javaFileName));
            fwriter.write(newContent);
            fwriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void createNormalizedCorpus(String srcDir) {
        // creating normalized corpus
        File dir = new File(srcDir);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    createNormalizedCorpus(f.getAbsolutePath());
                } else {
                    if (f.getName().endsWith(".java")) {
                        normalizePageCMV(f.getAbsolutePath(), true);
                        // normalizePageCMV(f.getAbsolutePath(),true,true);
                    }
                }
            }
        } else {
            if (dir.getName().endsWith(".java")) {
                normalizePageCMV(dir.getAbsolutePath(), true);
                // normalizePageCMV(dir.getAbsolutePath(),true,true);
            }
        }
    }

 




  

    public static void main(String[] args) {
        String corpusName = "\\EclipseV3.1\\";
        CorpusManager manager = new CorpusManager(corpusName,"E:\\PhD\\Repo\\Eclipse\\Source");
     
        manager.createNormalizedCorpus(manager.corpusDir+manager.corpusName);
       // System.out.println("File modified:" + manager.filemodified
                //+ " , Failed:" + manager.failed);
    
       
    }
}
