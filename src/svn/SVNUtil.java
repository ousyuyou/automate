package svn;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
  
public class SVNUtil {
	private static Logger logger = Logger.getLogger(SVNUtil.class);
	private static String svnRootPath = "";
	private static File CERT_FILE_PATH = new File("e:/cert/");
	/**
	 * @param args
	 */
	public static void main(String[] args) throws SVNException{
		// TODO Auto-generated method stub
		ConfigFile config = new ConfigFile(new File("e:/cert/config"));
		String configListFile = config.getPropertyValue("check", "change_list_file");
		svnRootPath = config.getPropertyValue("global", "svn_root_path");
		
		getHistory(configListFile);
//		checkOutFromSVN();
	}
	
	 public static void checkOutFromSVN(String svnUrl,String localFile) throws SVNException {
	        System.setProperty("javax.net.debug", "all");
	        System.setProperty("svnkit.http.sslProtocols", "SSLv3");
//	        setupLibrary();
	        
	        SVNURL url = SVNURL.parseURIEncoded(svnUrl);
	        SVNClientManager clientManager = authSvn(svnRootPath,CERT_FILE_PATH);
	        
	        SVNUpdateClient updateClient = clientManager.getUpdateClient();
	        updateClient.setIgnoreExternals(false);
	        long workVersion = updateClient.doCheckout(url, new File(localFile),
	                SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.FILES, false);
	}
	/**
	 * init svn
	 */
	public static void setupLibrary() {  
        DAVRepositoryFactory.setup();  
        SVNRepositoryFactoryImpl.setup();  
        FSRepositoryFactory.setup();  
    }
	
	@SuppressWarnings("deprecation")
	public static SVNClientManager authSvn(String svnRoot,File certSavePath) throws SVNException{
        // init  
        setupLibrary();  
  
        // create connection  
        SVNRepository repository = null;  
        try {  
            repository = SVNRepositoryFactory.create(SVNURL  
                    .parseURIEncoded(svnRoot));  
        } catch (SVNException e) {  
            logger.error(e.getErrorMessage(), e);  
            return null;  
        }  
        //from jre 7,SSLv3 is not supported;when use,change the java.security setting
        System.setProperty("svnkit.http.sslProtocols", "SSLv3");
//        System.setProperty("javax.net.debug", "all");
        System.setProperty("https.protocols", "SSLv3,SSLv2Hello");
//        System.setProperty("javax.net.ssl.trustStore", "d:/cert/ca.crt");
        
        // auth
        ISVNAuthenticationManager authManager = 
        		SVNWCUtil.createDefaultAuthenticationManager(certSavePath);
        //first time,this is necessary
//        SVNWCUtil.createDefaultAuthenticationManager(certSavePath, "username", "password", true);
        
        repository.setAuthenticationManager(authManager);
        
        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);  
        SVNClientManager clientManager = SVNClientManager.newInstance(options,  
                authManager);  
        clientManager.setAuthenticationManager(authManager);
        
        return clientManager;  
    }
	
	public static void getHistory_bak(String path, String username, String password){
		long startRevision = 0;
		long endRevision = -1;//HEAD (the latest) revision
		setupLibrary();
		
		SVNRepository repository = null;
		try {
			repository = SVNRepositoryFactory.create( SVNURL.parseURIEncoded( path ) );
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager( username, password );
			char[] passphrase;
			repository.setAuthenticationManager( authManager );
			Collection logEntries = null;
			logEntries = repository.log( new String[] { "" } , null , startRevision , endRevision , true , true );
			
			for ( Iterator<SVNLogEntry> entries = logEntries.iterator( ); entries.hasNext( ); ) {
				SVNLogEntry logEntry = ( SVNLogEntry ) entries.next( );
				System.out.println( "---------------------------------------------" );
				System.out.println ("revision: " + logEntry.getRevision( ) );
				System.out.println( "author: " + logEntry.getAuthor( ) );
				System.out.println( "date: " + logEntry.getDate( ) );
				System.out.println( "log message: " + logEntry.getMessage( ) );
			
			if ( logEntry.getChangedPaths( ).size( ) > 0 ) {
				  System.out.println( );
				  System.out.println( "changed paths:" );
				  Set changedPathsSet = logEntry.getChangedPaths( ).keySet( );

				  for ( Iterator<SVNLogEntryPath> changedPaths = changedPathsSet.iterator( ); changedPaths.hasNext( ); ) {
				  SVNLogEntryPath entryPath = ( SVNLogEntryPath ) logEntry.getChangedPaths( ).get( changedPaths.next( ) );
				  	System.out.println( " "
				  	+ entryPath.getType( )
				 	+ " "
				  	+ entryPath.getPath( )
				  	+ ( ( entryPath.getCopyPath( ) != null ) ? " (from "
				  			+ entryPath.getCopyPath( ) + " revision "
				  			+ entryPath.getCopyRevision( ) + ")" : "" ) );
				  	}
				 }
			}

		}catch(SVNException svne){
			//TODO
			svne.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public static void getHistory(String path) throws SVNException{
		SVNClientManager clientManager = authSvn(svnRootPath,CERT_FILE_PATH);
        
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date begin = null;
        Date end = null;
        try{
            begin = format.parse("2016-09-01");
            end = format.parse("2016-09-22");
        } catch (ParseException ex){
        	ex.printStackTrace();
        	return;
        }

        final List<SVNLogEntry> history = new ArrayList<SVNLogEntry>();
      
        File f = new File(path);
        File[] paths = new File[]{f};
        
        clientManager.getLogClient().doLog(paths, SVNRevision.create(begin), SVNRevision.create(end), false,  true, Integer.MAX_VALUE,
        		new ISVNLogEntryHandler(){
        			public void handleLogEntry(SVNLogEntry logEntry) throws SVNException{
        				System.out.println ("revision: " + logEntry.getRevision( ) );
        				history.add(logEntry);
        			}
        		});

        for(SVNLogEntry logEntry:history){
			System.out.println( "---------------------------------------------" );
			System.out.println ("revision: " + logEntry.getRevision( ) );
			System.out.println( "author: " + logEntry.getAuthor( ) );
			System.out.println( "date: " + logEntry.getDate( ) );
			System.out.println( "log message: " + logEntry.getMessage( ) );
		
			if ( logEntry.getChangedPaths( ).size( ) > 0 ) {
			  System.out.println( );
			  System.out.println( "changed paths:" );
			  Set changedPathsSet = logEntry.getChangedPaths( ).keySet( );

			  for ( Iterator<SVNLogEntryPath> changedPaths = changedPathsSet.iterator( ); changedPaths.hasNext( ); ) {
			  SVNLogEntryPath entryPath = ( SVNLogEntryPath ) logEntry.getChangedPaths( ).get( changedPaths.next( ) );
			  	System.out.println( " "
			  	+ entryPath.getType( )
			 	+ " "
			  	+ entryPath.getPath( )
			  	+ ( ( entryPath.getCopyPath( ) != null ) ? " (from "
			  			+ entryPath.getCopyPath( ) + " revision "
			  			+ entryPath.getCopyRevision( ) + ")" : "" ) );
			  	}
			 }
        	
        }
	}

		
}
