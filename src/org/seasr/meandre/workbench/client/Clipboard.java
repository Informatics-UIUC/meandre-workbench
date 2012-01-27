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

package org.seasr.meandre.workbench.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.seasr.meandre.workbench.client.beans.repository.WBConnectorDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentInstanceDescription;
import org.seasr.meandre.workbench.client.listeners.ClipboardListener;

/**
 * A singleton class that maintains the state of the clipboard.
 *
 * @author Boris Capitanu
 *
 */
public class Clipboard {

    private static Clipboard _instance = null;

    /**
     * Provides access to the singleton instance of Clipboard
     *
     * @return The singleton Clipboard instance
     */
    public static Clipboard getInstance() {
        if (_instance == null)
            _instance = new Clipboard();

        return _instance;
    }


    private final Set<ClipboardListener> _clipboardListeners = new HashSet<ClipboardListener>();

    private List<WBExecutableComponentInstanceDescription> _components;
    private List<WBConnectorDescription> _connectors;


    /**
     * Private constructor - initializes the clipboard
     */
    private Clipboard() {
        _components = null;
        _connectors = null;
    }

    public void put(List<WBExecutableComponentInstanceDescription> components, List<WBConnectorDescription> connectors) {
        _components = components;
        _connectors = connectors;

        for (ClipboardListener listener : _clipboardListeners)
            listener.onCopyToClipboard();
    }

    public List<WBExecutableComponentInstanceDescription> getComponents() {
        return _components;
    }

    public List<WBConnectorDescription> getConnectors() {
        return _connectors;
    }

    public boolean isEmpty() {
        return _components == null && _connectors == null;
    }

    public void reset() {
        _components = null;
        _connectors = null;

        for (ClipboardListener listener : _clipboardListeners)
            listener.onClipboardReset();
    }

    public void addListener(ClipboardListener listener) {
        _clipboardListeners.add(listener);
    }

    public void removeListener(ClipboardListener listener) {
        _clipboardListeners.remove(listener);
    }
}
