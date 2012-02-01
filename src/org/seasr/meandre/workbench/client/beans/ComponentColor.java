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

package org.seasr.meandre.workbench.client.beans;

import com.allen_sauer.gwt.log.client.Log;

/**
 * @author Boris Capitanu
 */

public class ComponentColor {

        private final String _mainColor;

        public ComponentColor(String mainColor) {
            _mainColor = mainColor;
        }

        public String getMainColor() {
            return _mainColor;
        }

        public String getTopBandColor() {
            String topBandColor = "#";

            for (int i = 1; i < _mainColor.length(); i += 2) {
                String hex = _mainColor.substring(i, i + 2);
                int value = Integer.parseInt(hex, 16);
                if (i == 1 || i == 3)
                    value += 13;
                else
                    value += 17;

                if (value > 255) value = 255;
                String hexString = Integer.toHexString(value);
                if (hexString.length() < 2) hexString = "0" + hexString;
                topBandColor += hexString;
            }

            Log.debug("mainColor: " + _mainColor + "  topBandColor: " + topBandColor);
            return topBandColor;
        }

        public String getBorderColor() {
            String borderColor = "#";

            for (int i = 1; i < _mainColor.length(); i += 2) {
                String hex = _mainColor.substring(i, i + 2);
                int value = Integer.parseInt(hex, 16);
                if (i == 1 || i == 3)
                    value -= 18;
                else
                    value -= 24;

                if (value < 0) value = 0;
                String hexString = Integer.toHexString(value);
                if (hexString.length() < 2) hexString = "0" + hexString;
                borderColor += hexString;
            }

            Log.debug("mainColor: " + _mainColor + "  borderColor: " + borderColor);
            return borderColor;
        }
}
