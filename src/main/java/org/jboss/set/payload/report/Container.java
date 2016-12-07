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

import com.atlassian.jira.rest.client.api.JiraRestClient;
import org.jboss.set.aphrodite.Aphrodite;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssueTracker;

import java.util.HashMap;
import java.util.Map;

import static org.jboss.set.payload.report.Util.fetch;
import static org.jboss.set.payload.report.Util.unchecked;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
class Container {
    private static final JiraRestClient jiraRestClient;
    private static final Map<Class, Object> services = new HashMap<>();

    static {
        final Aphrodite aphrodite = unchecked(() -> Aphrodite.instance());
        final JiraIssueTracker jiraIssueTracker = (JiraIssueTracker) unchecked(() -> fetch(aphrodite, "issueTrackers", Map.class)).get("https://issues.jboss.org");
        jiraRestClient = unchecked(() -> fetch(jiraIssueTracker, "restClient", JiraRestClient.class));
    }

    static synchronized <T> T get(Class<T> cls) {
        Object service = services.get(cls);
        if (service == null) {
            service = unchecked(() -> cls.newInstance());
            services.put(cls, service);
        }
        return cls.cast(service);
    }

    static JiraRestClient getJiraRestClient() {
        return jiraRestClient;
    }
}
