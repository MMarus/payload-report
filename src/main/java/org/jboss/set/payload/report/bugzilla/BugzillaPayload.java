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
package org.jboss.set.payload.report.bugzilla;

import org.jboss.set.payload.report.Issue;
import org.jboss.set.payload.report.Payload;
import org.jboss.set.payload.report.container.Container;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class BugzillaPayload implements Payload {
    private static final BugzillaIssueHome BUGZILLA_ISSUE_HOME = Container.get(BugzillaIssueHome.class);

    private final org.jboss.set.aphrodite.domain.Issue issue;

    BugzillaPayload(final org.jboss.set.aphrodite.domain.Issue issue) {
        this.issue = issue;
    }

    @Override
    public String getFixVersion() {
        // TODO: jira specific
        throw new RuntimeException("NYI: org.jboss.set.payload.report.bugzilla.BugzillaPayload.getFixVersion");
    }

    @Override
    public Collection<Issue> getIssues() {
        return issue.getDependsOn().parallelStream().map((url) -> BUGZILLA_ISSUE_HOME.proxy(url)).collect(Collectors.toSet());
    }

    @Override
    public String getSprint() {
        // TODO: jira specific
        throw new RuntimeException("NYI: org.jboss.set.payload.report.bugzilla.BugzillaPayload.getSprint");
    }
}
