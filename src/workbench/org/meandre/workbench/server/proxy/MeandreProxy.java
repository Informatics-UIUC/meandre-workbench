package org.meandre.workbench.server.proxy;

//==============
// Java Imports
//==============

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

//===============
// Other Imports
//===============

/**
 * <p>Title: Meandre Proxy</p>
 *
 * <p>Description: Create a meandre autheticated proxy to access the web
 * services</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA</p>
 *
 * @author Xavier Llor&agrave;, D. Searsmith
 * @version 1.0
 */
public class MeandreProxy {

        //==============
        // Data Members
        //==============

        /** The logger for the bootstrapper */
        protected static Logger log = null;

        /** The user name */
        private String sUserName;

        /** The password */
        private String sPassword;

        /** The password */
        private String sBaseURL;

        /** The password */
        private String sUPEncoding;

        /** Cached roles */
        private HashMap mapRoles;

        /** Is the proxy ready? */
        private boolean bIsReady;

        /** Did the last call succeed */
        private boolean bWasCallOK;

        //=============
        // Static Code
        //=============

        // Initializing the logger and its handlers
        static {
                log = Logger.getLogger(org.meandre.workbench.bootstrap.jetty.Bootstrapper.class.getName());
                log.setLevel(Level.CONFIG);
                log.addHandler(org.meandre.workbench.bootstrap.jetty.Bootstrapper.handler);
        }


        //==============
        // Constructors
        //==============

        /** Creates a Meandre Proxy
         *
         * @param sUser The user of the proxy
         * @param sPasswd The password of the proxy
         * @param sURL The Meandre server URL
         */
        public MeandreProxy ( String sUser, String sPasswd, String sURL ) {
                this.sUserName = sUser;
                this.sPassword = sPasswd;
                this.sBaseURL  = sURL;

                String sUserPassword = sUserName + ":" + sPassword;
                this.sUPEncoding = new sun.misc.BASE64Encoder().encode (sUserPassword.getBytes());

                // Force a first authetication for role caching
                this.bIsReady = null!=getRoles();
        }

        /** Returns true if the proxy was successfully initialized; false otherwise.
         *
         * @return True is successfully initialized
         */
        public boolean isReady() {
                return bIsReady;
        }

        /** Returns true if the last call was completed successfully.
         *
         * @return True if everything when well. False otherwise
         */
        public boolean getCallOk () {
                return bWasCallOK;
        }

        /** Gets the user name.
         *
         * @return The user name
         */
        public String getName () {
                return sUserName;
        }

        /** Flushes the cached roles.
         *
         */
        public void flushRoles () {
                mapRoles = null;
        }

        /** Return the roles for the user of this proxy.
         *
         * @return The set of granted role for the proxy user
         */
        public Map getRoles() {
                if ( mapRoles==null ) {
                        String sRoles = executeGetRequest(sBaseURL+"services/about/user_roles.txt");
                        if ( sRoles==null )
                                return null;
                        else {
                                // Parse and generate the sets
                                String [] sa = sRoles.split("\n");
                                mapRoles = new HashMap();
                                for (int i = 0, n = sa.length; i < n; i++){
                                    String sRole = sa[i];
                                    mapRoles.put(sRole, Boolean.TRUE);
                                }
                        }
                }

                return mapRoles;
        }

        /** Return the list of locations for the user of this proxy.
         *
         * @return The array of location for this user
         */
        public Map[] getLocations() {
                bWasCallOK = true;
                Map[] mapaRes = null;

                if ( mapRoles==null ) {
                        String sLocations = executeGetRequest(sBaseURL+"services/locations/list.txt");
                        if ( sLocations==null ) {
                                bWasCallOK = false;
                                return null;
                        }
                        else {
                                // Parse and generate the sets
                                String [] sa = sLocations.split("\n");
                                int iMax = sa.length/2;
                                mapaRes = new Map[iMax];
                                for ( int i=0 ; i<iMax ; i++ ) {
                                        mapaRes[i] = new HashMap();
                                        mapaRes[i].put("location", sa[i*2]);
                                        mapaRes[i].put("description", sa[i*2+1]);
                                }
                        }
                }

                return mapaRes;
        }

        /** Does an authenticated get request against the provided URL. Returns null if the
         * request failed
         *
         * @param sURL The URL
         * @return The content
         */
        private String executeGetRequest ( String sURL ) {
                try {
                    log.warning(sURL);
                        // Create the URL
                        URL url = new URL(sURL);

                        // Create and authenticated connection
                        URLConnection uc = url.openConnection();
                        uc.setRequestProperty ("Authorization", "Basic " + sUPEncoding);

                        // Pull the stuff out of the Meandre server
                        InputStream is = (InputStream)uc.getInputStream();
                        byte [] ba = new byte[is.available()];
                        is.read(ba);
                        is.close();

                        // Get the returned text
                        return new String(ba,"UTF-8");
                }
                catch ( IOException e ) {
                        log.warning(e.toString());
                        return null;
                }
        }

}
