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
		this.sourceCodeFolder=StaticData.SOURCECODEDIR;
		this.sourceCodePPFolder=StaticData.PREPROCESSEDSOURCECODEDIR+"/"+"ProcessedFiles";
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
			
			String repoFolder="/Users/user/Documents/workspace-2016/QueryReformulation/data/processed";
			String methodFolder="/Users/user/Documents/workspace-2016/QueryReformulation/data/BugCorpus/method/";
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
				/*String preprocessed=scbpp.performNLP();
				String [] spilter=file.split("/");
				String fileNameOnly=spilter[spilter.length-1];
				System.out.println("fileNameOnly "+fileNameOnly);
				String outFile="./data/ExampleSourceCodeFilesMethodLevel/"+fileNameOnly;
				ContentWriter.writeContent(outFile, s.replace("\\", "/")+"\r\n"+preprocessed);
				System.out.println(file_track+" Preprocessed:"+file);*/
			}
			String [] spilter=s.split("/");
			String fileNameOnly=spilter[spilter.length-1];
			System.out.println("fileNameOnly "+fileNameOnly);
			String outFile="./data/ExampleSourceCodeFilesMethodLevel/"+fileNameOnly;
			ContentWriter.writeContent(outFile, s.replace("\\", "/")+"\r\n"+fileList.size()+"\r\n"+preprocessed);
			System.out.println(file_track+" Preprocessed:"+s);
			
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
