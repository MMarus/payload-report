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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class IssueHome {
    private final JiraRestClient jiraRestClient = Container.getJiraRestClient();

    public Collection<Issue> findByPayload(final Payload payload) {
        final String jql = "project = JBEAP AND (fixVersion = " + payload.getFixVersion() + " OR Sprint = \"" + payload.getSprint() + "\")";
        // Note that the following fields: summary, issuetype, created, updated, project and status are required.
        final Set<String> fields = new HashSet<>(Arrays.asList("summary", "issuetype", "created", "updated", "project", "status", "key"));
        final Iterable<com.atlassian.jira.rest.client.api.domain.Issue> issues = jiraRestClient.getSearchClient().searchJql(jql, null, null, fields).claim().getIssues();
        issues.forEach((issue -> {
            System.out.println(issue.getKey());
        }));
        return null;
    }
}
