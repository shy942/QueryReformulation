package corpus.maker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import utility.ContentWriter;
import utility.MiscUtility;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;

import config.StaticData;

public class MethodCorpusDeveloper {

	//String repoName;
	String repoFolder;
	String methodFolder;
	HashMap<String, String> methodmap;
	ArrayList<String> fileList=new ArrayList<>();
	String base;
	String packageName;
	ArrayList<String> listNoOfMethod;
	public MethodCorpusDeveloper(String repoFolder, String methodFolder, String base) {
		//this.repoName = repoName;
		this.repoFolder = repoFolder;
		this.methodFolder=methodFolder;
		//this.methodFolder = "/Users/user/Documents/workspace-2016/QueryReformulation/data/BugCorpus/method/";
		this.methodmap = new HashMap<>();
		this.base=base;
		this.packageName="";
		this.listNoOfMethod=new ArrayList<>();
	}

	//public MethodCorpusDeveloper(String methodFolder){
		
	//}
	
	protected void extractMethods(String javaFileURL) {
		// extracting methods from the class
		try {
			CompilationUnit cu = JavaParser.parse(new File(javaFileURL));
			
			
			if (cu != null) {
				PackageDeclaration packageDec=cu.getPackage();
				if(packageDec!=null){
				//System.out.println(packageDec.toString());
				String packageName=packageDec.toString();
				String[] spilter=packageName.split(" ");
				String fileName=spilter[1];
				//System.out.print(fileName);
				spilter=fileName.split(";");
				fileName=spilter[0];
				//System.out.print(fileName);
				this.packageName=fileName;
				MethodVisitor visitor = new MethodVisitor();
				cu.accept(visitor, null);
				ArrayList<String> methods = visitor.methods;
				// now add them to method map
				int index = 0;
				for (String method : methods) {
					index++;
					String key = javaFileURL + "#" + index;
					//System.out.println(key);
					this.methodmap.put(key, method);
				}
				
			}
				
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected void saveCorpusKeys() {
		// save the corpus keys
		String keysFile = this.base+".\\data\\"
				+ ".keys";
		ArrayList<String> keylist = new ArrayList<>();
		int index = 0;
		for (String key : this.methodmap.keySet()) {
			index++;
			keylist.add(index + ":" + key);
		}
		// now save the keys
		ContentWriter.writeContent(keysFile, keylist);
		//System.out.println("Keys saved successfully!");
	}

	protected void saveMethods(String javaFileURL) {
		// save the corpus keys
		saveCorpusKeys();
		// now save the extracted methods
		int index = 0;
		int no_of_method=0;
		String [] spilter=javaFileURL.split("\\\\");
        String filePart="";
    
        
        String lastPart=spilter[spilter.length-1];
		for (String key : this.methodmap.keySet()) {
			index++;
			String methodContent = this.methodmap.get(key);
			//System.out.println(javaFileURL);
			//javaFileURL.replaceAll("\\\\", "/");
			
			
			filePart=this.packageName+"."+index+"."+lastPart;
		
			String outFile = this.methodFolder + "\\"+filePart;
			System.out.println("filePart:        "+filePart);
			SourceCodePreprocessor scbpp=new SourceCodePreprocessor(methodContent);
            
            String preprocessed=scbpp.performNLP();
            System.out.println(preprocessed);
			ContentWriter.writeContent(outFile, preprocessed);
			fileList.add(outFile);
		}
		no_of_method=index;
        this.listNoOfMethod.add(this.packageName+"."+lastPart+","+no_of_method);
		//System.out.println("Methods extracted successfully!");
	}


	public ArrayList<String> returnFiles()
	{
		return fileList;
	}
	public String getPackageName()
	{
		return this.packageName;
	}
	
	public void writeContent()
	{
	    ContentWriter.writeContent(this.base+".\\data\\"+"listNoOfMethod.txt", this.listNoOfMethod);
	}
	
	protected void createMethodCorpus(String srcDir) {
		// creating normalized corpus
		File dir = new File(srcDir);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File f : files) {
				if (f.isDirectory()) {
					createMethodCorpus(f.getAbsolutePath());
				} else {
					if (f.getName().endsWith(".java")) {
						extractMethods(f.getAbsolutePath());
						saveMethods(f.getAbsolutePath());
						methodmap.clear();
					}
				}
			}
		} else {
			if (dir.getName().endsWith(".java")) {
				extractMethods(dir.getAbsolutePath());
				saveMethods(dir.getAbsolutePath());
				methodmap.clear();
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String base="E:\\PhD\\Repo\\SWT";
		String repoName=base+"\\Source\\swt-3.659BLA\\";
		String methodFolder=base+"\\methodDec21\\";
		MethodCorpusDeveloper developer=new MethodCorpusDeveloper(repoName,methodFolder,base);
		developer.createMethodCorpus(developer.repoFolder);
		developer.saveMethods(methodFolder);
		developer.writeContent();
	}
}
