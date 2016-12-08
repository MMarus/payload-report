/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2016, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.set.payload.report;

import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class Issue extends JiraIssue {
    private static final URL DUMMY_URL;

    static {
        try {
            DUMMY_URL = new URL("file://dummy");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    // take this over from super
    private URL url;

    public Issue() {
        super(DUMMY_URL);
    }

//    public Issue(final URL url) {
//        super(url);
//    }

    public String getReport() {
        throw new RuntimeException("NYI: org.jboss.set.payload.report.Issue.getReport");
    }

    public URL getURL() {
        return url;
    }

    public Signal getSignal() {
        throw new RuntimeException("NYI: org.jboss.set.payload.report.Issue.getSignal");
    }

    public void setURL(final URL url) {
        this.url = url;
    }
}
