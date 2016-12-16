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
package org.jboss.set.payload.report.jira;

import com.atlassian.jira.rest.client.api.domain.Version;
import org.jboss.set.payload.report.Issue;
import org.jboss.set.payload.report.Payload;
import org.jboss.set.payload.report.container.Container;

import java.util.Collection;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class JiraPayload implements Payload {
    private final JiraIssueHome issueHome = Container.get(JiraIssueHome.class);

    private final Version version;
    private final String sprint;

    JiraPayload(final Version version, final String sprint) {
        this.version = version;
        this.sprint = sprint;
    }

    @Override
    public String getFixVersion() {
        return version.getName();
    }

    @Override
    public Collection<? extends Issue> getIssues() {
        return issueHome.findByPayload(this);
    }

    @Override
    public String getSprint() {
        return sprint;
    }
}
