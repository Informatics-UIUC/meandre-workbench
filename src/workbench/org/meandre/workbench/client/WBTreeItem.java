package org.meandre.workbench.client;

//==============
// Java Imports
//==============

import java.util.Iterator;

//===============
// Other Imports
//===============

import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HTML;

/**
 * <p>
 * Title: Workbench Tree Item
 * </p>
 *
 * <p>
 * Description: A class that subclasses TreeItem to provide popup panel
 * functionality for tree item nodes.
 * </p>
 *
 * <p>
 * Copyright: UIUC Copyright (c) 2007
 * </p>
 *
 * <p>
 * Company: Automated Learning Group at NCSA, UIUC
 * </p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
public class WBTreeItem extends TreeItem {

	// ==============
	// Data Members
	// ==============

	/* Tree item popup panel object for this tree node. */
	private TreeItemPopUp _ppp = null;
	private static ContextPopup s_contextPopup = null;

	private HTML _lab = null;

	// ==============
	// Constructors
	// ==============

	/**
	 * Constructor for WBTreeItem that assigns an input string as display text
	 * for this tree item and a second string as popup text (HTML) for a popup
	 * panel that appears when this tree nodes label is rolled over. If the
	 * popup text input is null then no popup panel is constructed for this tree
	 * item.
	 *
	 * @param txt
	 *            String Text to display as label for this tree item.
	 * @param puString
	 *            String Text (HTML) to display in popup panel for this tree
	 *            item. If this input is null, no popup panel will be
	 *            constructed for this node.
	 */
	public WBTreeItem(String txt, String puString) {

		if (puString != null) {
			_ppp = new TreeItemPopUp(puString);
			_ppp.addStyleName("port-popup");
		}

		ContextPopup.disableContextMenu(this.getElement());

		_lab = new HTML(txt) {
			public void onBrowserEvent(Event event) {
			    switch (DOM.eventGetType(event)) {
			        case Event.ONMOUSEOVER:
			            if (_ppp != null) {
			                _ppp.setPopupPosition(this.getAbsoluteLeft()
			                        + this.getOffsetWidth() + 5, this
			                        .getAbsoluteTop());
			                _ppp.show();
			            }

			            break;
			        case Event.ONMOUSEOUT:
			            if (_ppp != null) {
			                _ppp.hide();
			            }
			            break;

			        case Event.ONCLICK:
			            if (WBTreeItem.s_contextPopup != null)
			                WBTreeItem.s_contextPopup.hide();
			            updateSelection();
			            break;

			        case Event.ONMOUSEDOWN:
			            if (DOM.eventGetButton(event) == Event.BUTTON_RIGHT
			                    && WBTreeItem.this.getUserObject() != null ) {
			                int mouseX = DOM.eventGetClientX(event);
			                int mouseY = DOM.eventGetClientY(event);

			                updateSelection();

			                if (WBTreeItem.s_contextPopup == null)
			                    WBTreeItem.s_contextPopup = new ContextPopup(WBTreeItem.this);
			                else
			                    WBTreeItem.s_contextPopup.setTargetTreeItem(WBTreeItem.this);

			                WBTreeItem.s_contextPopup.setPopupPosition(mouseX + 5, mouseY + 5);
			                WBTreeItem.s_contextPopup.show();
			            }
			            break;
			    }
			}

            private void updateSelection() {
                Iterator itty = WBTreeItem.this.getTree().treeItemIterator();
                while (itty.hasNext()) {
                    WBTreeItem wbti = (WBTreeItem) itty.next();
                    if (wbti.getUserObject() != null) {
                        if (wbti == WBTreeItem.this) {
                            wbti._lab.addStyleName("gwt-TreeItem-selected");
                        } else {
                            wbti._lab.removeStyleName("gwt-TreeItem-selected");
                        }
                    }
                }
            }
		};

		this.setWidget(_lab);

	}

	// =================
	// Package Methods
	// =================

	public String getText() {
		return _lab.getText();
	}

}
