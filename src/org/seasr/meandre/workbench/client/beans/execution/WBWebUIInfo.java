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

package org.seasr.meandre.workbench.client.beans.execution;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Used to pass information about components with WebUIs through RPC
 * 
 * @author Boris Capitanu
 *
 */
public class WBWebUIInfo implements IsSerializable {

    private String _hostName;
    private int _port;
    private String _token;
    private String _uri;

    /**
     * Needed by GWT serialization - not used otherwise
     */
    public WBWebUIInfo() {
        this(null, -1, null, null);
    }

    /**
     * Constructor
     *
     * @param hostName The hostname where the WebUI is running
     * @param port The port where the WebUI can be accessed
     * @param token The token for the executing instance
     * @param uri
     */
    public WBWebUIInfo(String hostName, int port, String token, String uri) {
        _hostName = hostName;
        _port = port;
        _token = token;
        _uri = uri;
    }

    /**
     * Returns the host name where the WebUI is running at
     *
     * @return The hostname where the WebUI is running at
     */
    public String getHostName() {
        return _hostName;
    }

    /**
     * Returns the port where the WebUI can be accessed
     *
     * @return The port where the WebUI can be accessed
     */
    public int getPort() {
        return _port;
    }

    /**
     * Returns the token for the executing instance
     *
     * @return The token for the executing instance
     */
    public String getToken() {
        return _token;
    }

    public String getURI() {
        return _uri;
    }

    /**
     * Returns the complete URL to access the WebUI
     *
     * @return The complete URL where the WebUI can be accessed at
     */
    public String getWebUIUrl() {
        return "http://" + _hostName + ":" + _port;
    }

    /**
     * Returns the abort url
     *
     * @return The abort url
     */
    public String getAbortUrl() {
        return getWebUIUrl() + "/admin/abort.txt";
    }
}
