/**
 * University of Illinois/NCSA
 * Open Source License
 *
 * Copyright (c) 2008, Board of Trustees-University of Illinois.
 * All rights reserved.
 *
 * Developed by:
 *
 * Automated Learning Group
 * National Center for Supercomputing Applications
 * http://www.seasr.org
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal with the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimers.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimers in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the names of Automated Learning Group, The National Center for
 *    Supercomputing Applications, or University of Illinois, nor the names of
 *    its contributors may be used to endorse or promote products derived from
 *    this Software without specific prior written permission.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * WITH THE SOFTWARE.
 */

package org.seasr.meandre.workbench.bootstrap;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.SessionManager;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * @author Boris Capitanu
 *
 */
public class JettyBootstrapper {
    /**
     * NOTE REGARDING SESSIONS:
     *
     * When the SESSION_COOKIE_NAME below is set to a fixed value, the
     * "session overwriting" issue will manifest itself if two instances
     * of the Workbench are ran on the same machine (two Jetty instances
     * running on different ports) and the same browser instance (with
     * two tabs) is used to "hit" the two Workbench instances.
     * If you encounter this situation, setting the SESSION_COOKIE_NAME
     * to something non-fixed (like adding the current timestamp to the
     * name, will solve that problem at the expense of having multiple
     * cookies that may be lying around unused after a while)
     */

    /** The base Meandre Workbench port */
    public static final int BASE_PORT = 1712;

    public final static String SESSION_COOKIE_NAME = "WORKBENCH_SESSIONID";

    /**
     * Bootstraps Meandre-Workbench
     *
     * @param args command line arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        final String JETTY_HOME = (args.length > 0) ? args[0] : System.getProperty("user.dir");

        System.out.println("Setting JETTY_HOME to " + JETTY_HOME);
        System.out.println("Using session cookie name: " + SESSION_COOKIE_NAME);

        Server server = new Server();

        Connector connector = new SelectChannelConnector();
        connector.setPort(Integer.getInteger("jetty.port", BASE_PORT).intValue());
        server.setConnectors(new Connector[] { connector });

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar(JETTY_HOME + "/war");
        webapp.setDefaultsDescriptor(JETTY_HOME + "/bootstrap/workbench-jetty.xml");

        SessionManager sessionManager = webapp.getSessionHandler().getSessionManager();
        sessionManager.setSessionCookie(SESSION_COOKIE_NAME);
        sessionManager.setSessionURL(SESSION_COOKIE_NAME.toLowerCase());

        server.setHandler(webapp);
        server.start();
        server.join();
    }

}
