package svn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
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

public class SVNUtilCopy {
    private static final Logger LOGGER=LoggerFactory.getLogger(SVNUtilCopy.class);
    private String svnUrl;
    private String filePath;
    private String userName;
    private String password;
    private File localFile;
    
    private final int latest_revision=-1;
    
    public SVNUtilCopy(String svnUrl,String userName,String password,File file) {
        this(userName,password,file);
        this.svnUrl=svnUrl;
    }
    
    public SVNUtilCopy(String userName,String password,File localFile) {
        this.userName=userName;
        this.password=password;
        this.localFile=localFile;
    }
    
    
    public SVNUtilCopy(String url, String filePath, String userName,
            String password, File localFile) {
        this(url, userName, password,localFile);
        this.filePath=filePath;
    }

    public void checkOutFromSVN() throws SVNException {
        System.setProperty("javax.net.debug", "all");
        System.setProperty("svnkit.http.sslProtocols", "SSLv3");
        setupLibrary();
        
        SVNURL url = SVNURL.parseURIEncoded(this.svnUrl);
        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager clientManager = SVNClientManager.newInstance(options,
                userName, password);
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);
        long workVersion = updateClient.doCheckout(url, this.localFile,
                SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.FILES, false);
        LOGGER.info("Current version:{}", workVersion);
    }
    
    
    public void updateFromSVN() throws SVNException {
        System.setProperty("svnkit.http.sslProtocols", "SSLv3");
        setupLibrary();
        
        DefaultSVNOptions options=SVNWCUtil.createDefaultOptions(true);
        SVNClientManager clientManager=SVNClientManager.newInstance(options, userName, password);
        SVNUpdateClient updateClient=clientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);
        long workVersion=updateClient.doUpdate(localFile, SVNRevision.HEAD,SVNDepth.INFINITY,false,false);
        LOGGER.info("Current version:{}",workVersion);
    }
    
    public void getFileFromSVN() throws SVNException, FileNotFoundException {
        System.setProperty("svnkit.http.sslProtocols", "SSLv3");
        System.setProperty("https.protocols", "SSLv3,SSLv2Hello");

        setupLibrary();
        System.setProperty("javax.net.ssl.trustStore", "E:/cert/client.p12");
        
        SVNRepository repository=SVNRepositoryFactory.create(SVNURL.parseURIEncoded(this.svnUrl));
        ISVNAuthenticationManager authManager=SVNWCUtil.createDefaultAuthenticationManager(userName, password);
        repository.setAuthenticationManager(authManager);
        
        SVNNodeKind nodeKind=repository.checkPath(this.filePath,latest_revision);
        if (nodeKind==SVNNodeKind.FILE) {
            SVNProperties properties=new SVNProperties();
            OutputStream contents=new FileOutputStream(this.localFile);
            long workVersion=repository.getFile(this.filePath,latest_revision, properties, contents);
            LOGGER.info("Current version:{}",workVersion);
        }else {
            LOGGER.error("Must be a file!current:{},url:{}",nodeKind,this.svnUrl);
        }
    }
    
    
    /*
     * Initializes the library to work with a repository via 
     * different protocols.
     */
    private void setupLibrary() {
        /*
         * For using over http:// and https://
         */
        DAVRepositoryFactory.setup();
        /*
         * For using over svn:// and svn+xxx://
         */
        SVNRepositoryFactoryImpl.setup();
        
        /*
         * For using over file:///
         */
        FSRepositoryFactory.setup();
    }
    
    public static void main(String[] args) throws SVNException{
//    	util.updateFromSVN();
    }
    
}