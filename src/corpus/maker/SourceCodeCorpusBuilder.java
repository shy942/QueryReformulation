package corpus.maker;

import java.io.File;
import java.util.ArrayList;

import source.visitor.CommentFilterer;
import utility.ContentLoader;
import utility.ContentWriter;
import config.StaticData;;

public class SourceCodeCorpusBuilder {

    File sourceCodeFolder;
	String sourceCodePPFolder;
	ArrayList<String> javaFilePaths;
	ArrayList<String> javaFilePathsLastName;
	int noOfFile=0;
	String base;
	public SourceCodeCorpusBuilder(String base)
	{
		this.base=base;
		this.sourceCodeFolder=new File(base+"\\Source\\eclipse-3.1Test\\");
		this.sourceCodePPFolder=base+"\\ProcessedSourceCorpusFeb19eclipse-3.1Test\\";
		this.javaFilePaths=new ArrayList<String>();
		this.javaFilePathsLastName=new ArrayList<String>();
		this.noOfFile=0;
		this.loadJavaFilesOnly(sourceCodeFolder);
	}
	
	protected void createPreprocessedRepo()
	{
		int file_track=0;
		ArrayList<String> listofFiles=new ArrayList<>();
		int i=0;
		for (String s : javaFilePaths)
	    {
		    i++;
		    if(i>3) break;
	        String fileName=javaFilePathsLastName.get(file_track++);
	    	//Remove initial copyright comment
			CommentFilterer cf=new CommentFilterer(s,fileName);
			cf.discardClassHeaderComment();
			
			String repoFolder=this.base+"\\Source\\eclipse-3.1FromBLIA\\";
			//String repoFolder="E:\\BugLocator\\Source\\swt-3.1\\";
			String methodFolder=this.base+"\\methodDec25\\";
			MethodCorpusDeveloper developer=new MethodCorpusDeveloper(repoFolder, methodFolder,this.base);
			//developer.createMethodCorpus(developer.repoFolder);
			developer.extractMethods(s);
			developer.saveMethods(s);
			String packageName=developer.getPackageName();
			ArrayList<String> fileList=developer.returnFiles();
			//String content=ContentLoader.readContentSimple("./data/processed/"+fileName);
			String preprocessed="";
			for(String file:fileList)
			{
				String content=ContentLoader.readContentSimple(file);
			
				SourceCodePreprocessor scbpp=new SourceCodePreprocessor(content);
				
				preprocessed=preprocessed+scbpp.performNLP()+"\n";
				
			}
			
			String [] spilter=s.split("\\\\");
			String filePart="";
			
				
			
			filePart=packageName+"."+spilter[spilter.length-1];
			if(!listofFiles.contains(filePart))listofFiles.add(filePart);
			
			System.out.println(filePart);
			//System.out.println(file_track+" Preprocessed:"+this.sourceCodePPFolder+filePart);
			ContentWriter.writeContent(this.sourceCodePPFolder+filePart, preprocessed);
		}
		System.out.println("Total no. of files: "+file_track);
		ContentWriter.writeContent("E:\\PhD\\Repo\\Eclipse\\data\\SourceFileNames.txt", listofFiles);
	}
	
	public void loadJavaFilesOnly(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				loadJavaFilesOnly(fileEntry);
			} else {
				// System.out.println(fileEntry.getAbsolutePath());
				if (fileEntry.getName().endsWith(".java")) {
					this.javaFilePaths.add(fileEntry.getAbsolutePath());
					this.javaFilePathsLastName.add(noOfFile++,
							fileEntry.getName());
				}
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String base="E:\\PhD\\Repo\\Eclipse\\";
		new SourceCodeCorpusBuilder( base).createPreprocessedRepo();
		//This is a simple change.
	}

}
