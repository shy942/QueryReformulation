package config;

import java.io.File;

public interface StaticData {
	
	//public final attributes
	//public static String QRData="F:/MyWorks/Thesis Works/Crowdsource_Knowledge_Base/QuReCK/experiment";
	public static String QRData="C:/Users/Mukta/Dropbox/WorkinginHome/SCAM/Implementation";
	
	public static String BUGDIR="/Users/user/Dropbox/WorkinginHome/SCAM/Implementation/DataCollection/BugReports";
	public static String STOPWORDFILE="/Users/user/Documents/workspace2/AdjacencyDataBaseList/data/stop-words-english-total.txt";
	public static String PROCESSEDBUGREPORTS="/Users/user/Dropbox/WorkinginHome/SCAM/Implementation/ProcesedData/BugReports";
    public static File SOURCECODEDIR=new File("/Users/user/eclipse.platform.ui");
    public static String PREPROCESSEDSOURCECODEDIR="/Users/user/Dropbox/WorkinginHome/SCAM/Implementation/ProcesedData/SourceCodes";
    public static String OUTPUTFOLDER="/Users/user/Dropbox/WorkinginHome/SCAM/Implementation/Output";
	
	//public static String SOTraceQData="C:/My MSc/ThesisWorks/Crowdsource_Knowledge_Base/SOTraceQData";
	//public static String BugZillaDump="C:/My MSc/ThesisWorks/Crowdsource_Knowledge_Base/BugZillaDataDump";
	public static double SIGNIFICANCE_THRESHOLD=0.0001;
	public final static int WINDOW_SIZE=2;
	static String Database_name= "CodeTokenRec";//"stackoverflow2014p3";//  "vendasta";
	public static String connectionString="jdbc:sqlserver://localhost:1433;databaseName="+Database_name+";integratedSecurity=true";
	public static String Driver_name="com.microsoft.sqlserver.jdbc.SQLServerDriver";	
}
