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

package org.seasr.meandre.workbench.client.beans.session;

import java.util.Date;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Maintains the user session metadata
 *
 * @author capitanu
 *
 */
public class WBSession implements IsSerializable {

    private String _sid;
    private String _userName;
    private String _password;
    private Set<String> _userRoles;
    private Date _date;
    private String _hostName;
    private int _port;

    /**
     * Empty constructor - not used - needed by GWT
     */
    public WBSession() {}

    /**
     * Constructor
     *
     * @param sid The session id
     * @param userName The user name
     * @param password The user password
     * @param userRoles The user roles
     * @param hostName The host name
     * @param port The port number
     */
    public WBSession(String sid, String userName, String password, Set<String> userRoles, String hostName, int port) {
        _sid = sid;
        _userName = userName;
        _password = password;
        _userRoles = userRoles;
        _hostName = hostName;
        _port = port;
        _date = new Date();
    }

    /**
     * Returns the session id
     *
     * @return The session id
     */
    public String getSid() {
        return _sid;
    }

    /**
     * Returns the user name
     *
     * @return The user name
     */
    public String getUserName() {
        return _userName;
    }

    /**
     * Returns the user password
     * Note: This is only temporary and it's used for flow execution - it will be removed once the job API is implemented
     *
     * @return The user password
     */
    public String getPassword() {
        return _password;
    }

    /**
     * Returns the user roles
     *
     * @return The user roles
     */
    public Set<String> getUserRoles() {
        return _userRoles;
    }

    /**
     * Returns the session date
     *
     * @return The session date
     */
    public Date getDate() {
        return _date;
    }

    /**
     * Returns the session host name
     *
     * @return The session host name
     */
    public String getHostName() {
        return _hostName;
    }

    /**
     * Returns the session port number
     *
     * @return The session port number
     */
    public int getPort() {
        return _port;
    }
}
