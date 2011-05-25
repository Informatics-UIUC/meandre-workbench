package org.seasr.meandre.workbench.executables;

import java.io.File;
import java.io.InputStream;
import org.seasr.meandre.workbench.bootstrap.utils.FileUtils;

import org.meandre.executables.shutdown.ShutdownControlThread;
import org.meandre.executables.shutdown.Shutdownable;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.SessionManager;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
/**
 * commandline interface to start up a MeandreServer (Infrastructure-Server). 
 */

public class RunWorkbenchCMD{
    
    public static final String APP_NAME = "MeandreWorkbench";

    public final static String SESSION_COOKIE_NAME = "WORKBENCH_SESSIONID";

    /** port specified on the commandline to run the server on*/
    private static int _serverPort;

    /** port specified on the commandline to listen to local shutdown commands.*/
    private static int _shutdownPort;

    /** directory for installation files specified on commandline.
     */
    private static File _installDir;

    /** top level of the workbench dist/lib files. */
    //private static File _jettyHomeDir;

    public static void main(String[] args) throws Exception{
        parseArgs(args);

        ShutdownableWorkbench server = new ShutdownableWorkbench(_serverPort);
        //ShutdownableWorkbench server = new ShutdownableWorkbench(_serverPort,
        //    _jettyHomeDir.toString());

        ShutdownControlThread sct = new ShutdownControlThread(
                server, _shutdownPort, _installDir, APP_NAME);

        log("RunWorkbenchCMD: Starting ShutdownControlThread");
        sct.start();
        log("RunWorkbenchCMD: Starting Workbench");
        server.start();
    }

    /**
     * populate the static variables of this class.
     * @throws JSAPException 
     * @throws UnknownHostException 
     */
    private static void parseArgs(String[] args) throws JSAPException{

        JSAP jsap = makeCommandLineParser();
        
        JSAPResult config = jsap.parse(args);

        if (!config.success()) {
            RunWorkbenchCMD.exitWithError(config, jsap);
        }

        _installDir = config.getFile("installationDir");
        //_jettyHomeDir = config.getFile("jettyHomeDir");
        _serverPort = config.getInt("workbenchPort");
        _shutdownPort = config.getInt("shutdownPort");
        
        if(!_installDir.exists()){
            _installDir.mkdirs();
        }
        log("Installation Dir = " + _installDir);
        //log("Jetty Home Dir = " + _jettyHomeDir);
        log("Shutdown Port = " + _shutdownPort);
        log("Workbench Port = " + _serverPort);
    }

    private static JSAP makeCommandLineParser() throws JSAPException{
        JSAP jsap = new JSAP();
        
        //dir for installation files
        FlaggedOption installDirOpt = new FlaggedOption("installationDir");
        installDirOpt.setShortFlag('d');
        installDirOpt.setLongFlag("install-dir");
        installDirOpt.setStringParser(FileStringParser.getParser());
        installDirOpt.setRequired(true);
        installDirOpt.setHelp("Any persistent files needed by the workbench will" +
            "be written to this dir. If this directory does not " +
            "exist it will be created and populated with a default installation");
        jsap.registerParameter(installDirOpt);

        //dir where workbench lib/ is at
        /*FlaggedOption jettyDirOpt = new FlaggedOption("jettyHomeDir");
        jettyDirOpt.setShortFlag('j');
        jettyDirOpt.setLongFlag("jetty-home-dir");
        jettyDirOpt.setStringParser(FileStringParser.getParser());
        jettyDirOpt.setRequired(true);
        jettyDirOpt.setHelp("Jetty home directory is the top level of the " +
            "workbench specific lib/ directory." );
        jsap.registerParameter(jettyDirOpt);
        */


        //port number for the workbench server
        FlaggedOption workbenchPortOpt = new FlaggedOption("workbenchPort");
        workbenchPortOpt.setShortFlag('p');
        workbenchPortOpt.setLongFlag("workbench-port");
        workbenchPortOpt.setStringParser(JSAP.INTEGER_PARSER);
        workbenchPortOpt.setRequired(false);
        workbenchPortOpt.setDefault("1712");
        workbenchPortOpt.setHelp("The port number of the " +
            " Meandre Workbench instance's webservices." +
            " Defaults to '1712' if unspecified." );
        jsap.registerParameter(workbenchPortOpt);

        //port number for the shutdown controller
        FlaggedOption shutdownPortOpt = new FlaggedOption("shutdownPort");
        shutdownPortOpt.setShortFlag('s');
        shutdownPortOpt.setLongFlag("shutdown-port");
        shutdownPortOpt.setStringParser(JSAP.INTEGER_PARSER);
        shutdownPortOpt.setRequired(false);
        shutdownPortOpt.setDefault("1710");
        shutdownPortOpt.setHelp("The port number of the " +
            " Shutdown listener service. Run ShutdownWorkbenchCMD with the" +
            " same port number (and securityTokenDirectory equal to this" +
            " server's install-directory) to shutdown this MeandreServer." +
            " Defaults to '1710' if unspecified." );
        jsap.registerParameter(shutdownPortOpt);

        return jsap;
    }

    /**
     * error message when the result of the parse fails. The contents of
     * this method are copied from the JASP tutorial at:
     * http://www.martiansoftware.com/jsap/doc/
     * 
     * @param parseResult the jsapResult returned when the commandline args
     * where parsed. assumes this 'has errors'
     * @param jsap the jsap used to parse the commandline args that created
     * the error result parseResult
     */
    private static void exitWithError(JSAPResult parseResult, JSAP jsap){
        System.err.println();

        for (java.util.Iterator errs = parseResult.getErrorMessageIterator();
            errs.hasNext();) {
            System.err.println("Error: " + errs.next());
        }

        System.err.println();
        System.err.println("Usage: java " +
                RunWorkbenchCMD.class.getName());
        System.err.println("                "
                + jsap.getUsage());
        System.err.println();
        System.err.println(jsap.getHelp());
        System.exit(1);
    }
    
    private static void log(String msg){
        System.out.println(msg);
    }
    /** a version of Workbench that conforms to the Shutdownable interface.
     * This is so a ShutdownControlThread can shut down our server.
     */
    private static class ShutdownableWorkbench implements Shutdownable{

        /** the jetty server */
        Server __server;

        public ShutdownableWorkbench(int port/*, String sJettyHomeDir*/) 
                throws Exception{
            /*String jettyHome = sJettyHomeDir;
            String sWarFile = sJettyHomeDir + File.separator + 
                    "Meandre-Workbench.war";
            String sJettyDescriptorFile = sJettyHomeDir + File.separator + 
            "bootstrap" + File.separator + "workbench-jetty.xml";
            
            
            System.out.println("Setting JETTY_HOME to " + jettyHome);
            System.out.println("Using session cookie name: " + 
                    SESSION_COOKIE_NAME);
            */
            String wbWarFileName = "Meandre-Workbench.war";
            String wbJettyDescriptorFileName = "workbench-jetty.xml";

            Class clazz = this.getClass();
            InputStream warStream = clazz.getResourceAsStream(
                    "/" + wbWarFileName);
            InputStream descriptorStream = clazz.getResourceAsStream(
                    "/" + wbJettyDescriptorFileName);            
            File warFile = File.createTempFile(
                    wbWarFileName.substring(
                            0, wbWarFileName.lastIndexOf('.') + 1), ".war");
            File descriptorFile = File.createTempFile(
                    wbJettyDescriptorFileName.substring(
                        0, wbJettyDescriptorFileName.lastIndexOf('.') + 1), ".xml");

            warFile.deleteOnExit();
            descriptorFile.deleteOnExit();

            FileUtils.copyFileFromStream(warStream, warFile);
            FileUtils.copyFileFromStream(descriptorStream, descriptorFile);

            wbWarFileName = warFile.getPath();
            wbJettyDescriptorFileName = descriptorFile.getPath();

            System.out.println("Workbench WAR: " + wbWarFileName);
            System.out.println("Jetty descriptor: " + wbJettyDescriptorFileName);
            __server = new Server();

            Connector connector = new SelectChannelConnector();
            connector.setPort(Integer.getInteger("jetty.port", port).intValue());
            __server.setConnectors(new Connector[] { connector });

            WebAppContext webapp = new WebAppContext();
            webapp.setContextPath("/");
            webapp.setWar(wbWarFileName);
            webapp.setDefaultsDescriptor(wbJettyDescriptorFileName);

            SessionManager sessionManager = webapp.getSessionHandler().
                    getSessionManager();
            sessionManager.setSessionCookie(SESSION_COOKIE_NAME);
            sessionManager.setSessionURL(SESSION_COOKIE_NAME.toLowerCase());

            __server.setHandler(webapp);
        }

        public void start() throws Exception{
            __server.start();
            __server.join();
        }

        public void stop(){
            try{
                __server.stop();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

}
