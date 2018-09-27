package corpus.maker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import utility.ContentWriter;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import config.StaticData;

public class MethodCorpusDeveloper {

	//String repoName;
	String repoFolder;
	String methodFolder;
	HashMap<String, String> methodmap;
	ArrayList<String> fileList=new ArrayList<>();

	public MethodCorpusDeveloper(String repoFolder, String methodFolder) {
		//this.repoName = repoName;
		this.repoFolder = repoFolder;
		this.methodFolder=methodFolder;
		//this.methodFolder = "/Users/user/Documents/workspace-2016/QueryReformulation/data/BugCorpus/method/";
		this.methodmap = new HashMap<>();
	}

	//public MethodCorpusDeveloper(String methodFolder){
		
	//}
	
	protected void extractMethods(String javaFileURL) {
		// extracting methods from the class
		try {
			CompilationUnit cu = JavaParser.parse(new File(javaFileURL));
			if (cu != null) {
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
		String keysFile = ".\\data\\"
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
		for (String key : this.methodmap.keySet()) {
			index++;
			String methodContent = this.methodmap.get(key);
			System.out.println(javaFileURL);
			//javaFileURL.replaceAll("\\\\", "/");
			String [] spilter=javaFileURL.split("\\\\");
			String filePart="";
			//=spilter[spilter.length-1];
			for(int f=9;f<spilter.length-1;f++)filePart+=spilter[f]+".";
			String lastPart=spilter[spilter.length-1];
			
			filePart=filePart+index+"."+lastPart;
		
			String outFile = this.methodFolder + "\\"+filePart;
			//System.out.println("outfile:        "+outFile);
			
			ContentWriter.writeContent(outFile, methodContent);
			fileList.add(outFile);
		}
		//System.out.println("Methods extracted successfully!");
	}


	public ArrayList<String> returnFiles()
	{
		return fileList;
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
		String repoName="E:\\PhD\\Repo\\Eclipse\\Source\\eclipse.platform.ui-master\\";
		String methodFolder="E:\\PhD\\Repo\\Eclipse\\method\\";
		MethodCorpusDeveloper developer=new MethodCorpusDeveloper(repoName,methodFolder);
		developer.createMethodCorpus(developer.repoFolder);
		developer.saveMethods(methodFolder);
	}
}
