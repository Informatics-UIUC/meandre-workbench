package org.meandre.workbench.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public abstract class ContextPopupMenuItem extends Composite implements ClickListener {
    private final HorizontalPanel _hpMain;
    private Label _lblText = new Label();
    private Image _imgIcon;

    public ContextPopupMenuItem(String text, Image icon) {
        _hpMain = new HorizontalPanel() {
            public void onBrowserEvent(Event event) {
                handleBrowserEvent(event);
                super.onBrowserEvent(event);
            }
        };

        initWidget(_hpMain);
        setStyleName("context-popup-menuitem");

        _lblText.setStylePrimaryName("context-popup-menuitem");
        _lblText.addStyleDependentName("label");
        _lblText.setText(text);
        _imgIcon = icon;

        if (_imgIcon != null) {
            _imgIcon.setStylePrimaryName("context-popup-menuitem");
            _imgIcon.addStyleDependentName("icon");
            _hpMain.add(_imgIcon);
        }

        _hpMain.add(_lblText);
        _hpMain.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);

        _hpMain.sinkEvents(Event.ONMOUSEOVER);
        _hpMain.sinkEvents(Event.ONMOUSEOUT);
        _hpMain.sinkEvents(Event.ONCLICK);
    }

    public ContextPopupMenuItem(String text) {
        this(text, null);
    }

    private void handleBrowserEvent(Event event) {
        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEOVER:
                addStyleName("context-popup-menuitem-selected");
                break;

            case Event.ONMOUSEOUT:
                removeStyleName("context-popup-menuitem-selected");
                break;

            case Event.ONCLICK:
                removeStyleName("context-popup-menuitem-selected");
                onClick(this);
                break;
        }
    }

    public String getText() {
        return _lblText.getText();
    }

    public Image getImage() {
        return _imgIcon;
    }
}
