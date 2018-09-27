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
	
	public SourceCodeCorpusBuilder()
	{
		this.sourceCodeFolder=new File("E:\\PhD\\Repo\\Eclipse\\Source\\eclipse.platform.ui-master\\");
		this.sourceCodePPFolder="E:\\PhD\\Repo\\Eclipse\\ProcessedSourceCorpus\\";
		this.javaFilePaths=new ArrayList<String>();
		this.javaFilePathsLastName=new ArrayList<String>();
		this.noOfFile=0;
		this.loadJavaFilesOnly(sourceCodeFolder);
	}
	
	protected void createPreprocessedRepo()
	{
		int file_track=0;
	
		for (String s : javaFilePaths)
	    {
	        String fileName=javaFilePathsLastName.get(file_track++);
	    	//Remove initial copyright comment
			CommentFilterer cf=new CommentFilterer(s,fileName);
			cf.discardClassHeaderComment();
			String repoFolder="E:\\PhD\\Repo\\Eclipse\\Source\\eclipse.platform.ui-master\\";
			//String repoFolder="E:\\BugLocator\\Source\\swt-3.1\\";
			String methodFolder="E:\\PhD\\Repo\\Eclipse\\method\\";
			MethodCorpusDeveloper developer=new MethodCorpusDeveloper(repoFolder, methodFolder);
			//developer.createMethodCorpus(developer.repoFolder);
			developer.extractMethods(s);
			developer.saveMethods(s);
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
			for(int f=9;f<spilter.length-1;f++)filePart+=spilter[f]+".";
			filePart=filePart+spilter[spilter.length-1];
			
			System.out.println(file_track+" Preprocessed:"+this.sourceCodePPFolder+filePart);
			ContentWriter.writeContent(this.sourceCodePPFolder+filePart, preprocessed);
		}
		System.out.println("Total no. of files: "+file_track);
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
		new SourceCodeCorpusBuilder().createPreprocessedRepo();
		//This is a simple change.
	}

}
