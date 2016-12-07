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
import com.atlassian.jira.rest.client.api.domain.Version;
import org.jboss.set.aphrodite.Aphrodite;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssueTracker;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.jboss.set.payload.report.Util.fetch;
import static org.jboss.set.payload.report.Util.unchecked;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class PayloadHomeImpl implements PayloadHome {
    private final JiraRestClient jiraRestClient = Container.getJiraRestClient();

    @Override
    public Payload findByPrimaryKey(final String arg) throws ObjectNotFoundException {
        // TODO: cache
        // TODO: match with pattern
        final String sprint = "EAP " + arg.substring(0, arg.length() - 3); // strip .GA
        final Version version = StreamSupport.stream(jiraRestClient.getProjectClient().getProject("JBEAP").claim().getVersions().spliterator(), false)
                .filter(v -> v.getName().equals(arg))
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(arg));
        System.out.println("version = " + version);
        return new PayloadImpl(version, sprint);
    }
}
