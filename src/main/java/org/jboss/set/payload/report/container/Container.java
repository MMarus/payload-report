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
package org.jboss.set.payload.report.container;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import org.jboss.set.aphrodite.Aphrodite;
import org.jboss.set.aphrodite.issue.trackers.bugzilla.BugzillaIssueTracker;
import org.jboss.set.aphrodite.issue.trackers.common.AbstractIssueTracker;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssueTracker;
import org.jboss.set.aphrodite.spi.IssueTrackerService;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.jboss.set.payload.report.util.Util.fetch;
import static org.jboss.set.payload.report.util.Util.unchecked;

/**
 * A simple container that contains simplistic services.
 *
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class Container {
    private static final URI SERVER_URI;
    private static final JiraRestClient jiraRestClient;
    private static final Map<Class, Object> services = new HashMap<>();

    static {
        final Aphrodite aphrodite = unchecked(() -> Aphrodite.instance());
        final Map<String, IssueTrackerService> issueTrackers = unchecked(() -> fetch(aphrodite, "issueTrackers", Map.class));
        final JiraIssueTracker jiraIssueTracker = (JiraIssueTracker) issueTrackers.get("https://issues.jboss.org");
        SERVER_URI = unchecked(() -> fetch(jiraIssueTracker, AbstractIssueTracker.class, "baseUrl", URL.class).toURI());
        jiraRestClient = unchecked(() -> fetch(jiraIssueTracker, "restClient", JiraRestClient.class));
        final BugzillaIssueTracker bugzillaIssueTracker = (BugzillaIssueTracker) issueTrackers.get("https://bugzilla.redhat.com");
        services.put(BugzillaIssueTracker.class, bugzillaIssueTracker);
    }

    public static synchronized <T> T get(Class<T> cls) {
        Object service = services.get(cls);
        if (service == null) {
            service = unchecked(() -> cls.newInstance());
            services.put(cls, service);
        }
        return cls.cast(service);
    }

    @Deprecated
    public static JiraRestClient getJiraRestClient() {
        return jiraRestClient;
    }

    // TODO: too ugly
    @Deprecated
    public static URI getServerURI() {
        return SERVER_URI;
    }
}
