/*
 * ============================================================================
 * Copyright (c) 2011 Schneider-Electric. All rights reserved.
 *
 * This file contains trade secrets of Schneider-Electric. No part may be
 * reproduced or transmitted in any form by any means or for any purpose without
 * the express written permission of Schneider-Electric.
 * ============================================================================
 */
package com.pelco.lilac;

import java.util.HashMap;
import java.util.logging.Level;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.pelco.webapp.platform.api.framework.client.logging.Logging;
import com.pelco.webapp.platform.api.framework.client.util.ClearCacheEvent;
import com.pelco.webapp.platform.ui.client.IAppGinjector;
import com.pelco.webapp.platform.ui.client.expanded.ExpandedView;
import com.pelco.webapp.platform.ui.client.login.events.PageResetRequest;
import com.pelco.webapp.platform.ui.client.mainwindow.IMainWindow;
import com.pelco.webapp.platform.ui.client.resources.CoreResources;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 *
 * LOGGING INFO: To view logging information, enter the showlogs URL parameter
 * in the browser and set it equals to true. This is currently only implemented
 * to show on the login page. Will be added to all other pages later. You will
 * still see logging in the development console without the URL parameter. i.e.
 * &showlogs=true
 */
public class Lilac implements EntryPoint {
    /**
     * The application needs a single Ginjector.
     */
    private final IAppGinjector ginjector = GWT.create(IAppGinjector.class);

    /** id of the loading message. */
    private static final String LOADING_MESSAGE_ID = "loadingMessage";

    @Override
    public final void onModuleLoad() {
        //CSOFF: EmptyBlock
        Logging.initializeLogging(Level.INFO,
                new HashMap<String, Level>() { {
                    put("com.pelco.lilac.licensing.LicenseCard", Level.FINEST);
                } });
        //CSON: EmptyBlock

        String view = Window.Location.getParameter("view");
        String path = Window.Location.getPath();
        if ((view != null) && view.equals("expanded")) {
            // adding split point here, reduces initial download size approximately 37%
            GWT.runAsync(new RunAsyncCallback() {
                @Override
                public void onFailure(final Throwable caught) {
                    // need to do something
                    ginjector.getEventBus().fireEvent(new ClearCacheEvent());
                }

                @Override
                public void onSuccess() {
                    ExpandedView eview = ginjector.getExpandedView();
                    eview.start();
                }
              });
        } else if (path.endsWith("Setup.html")) {
            HTML html = new HTML(
                                 "This is <b>HTML</b>.  It will be interpreted as such if you specify "
                                 + "the <span style='font-family:fixed'>asHTML</span> flag.", true);
            RootLayoutPanel.get().add(html);


        } else {
            Window.addCloseHandler(new CloseHandler<Window>() {
                @Override
                public void onClose(final CloseEvent<Window> event) {
                    ginjector.getEventBus().fireEvent(new PageResetRequest());
                    ginjector.getEventBus().fireEvent(new ClearCacheEvent());
                }
            });

            final IMainWindow mainWindow = ginjector.getMainWindow();

            RootLayoutPanel.get().add((Widget) mainWindow);
            final Element loadingMsg = DOM.getElementById(LOADING_MESSAGE_ID);
            if (loadingMsg != null) {
                loadingMsg.removeFromParent();
            }

            // Connect the activity manager to our main widget
            final ActivityManager activityManager = ginjector.getActivityManager();
            activityManager.setDisplay(mainWindow);

            // Go to default place
            final PlaceHistoryHandler historyHandler = ginjector.getPlaceHistoryHandler();
            historyHandler.handleCurrentHistory();
        }

        StyleInjector.inject(CoreResources.resources().nunito().toString());
    }
}
