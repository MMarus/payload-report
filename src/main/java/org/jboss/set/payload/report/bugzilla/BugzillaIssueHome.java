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

import org.jboss.set.aphrodite.issue.trackers.bugzilla.BugzillaIssueTracker;
import org.jboss.set.aphrodite.spi.NotFoundException;
import org.jboss.set.payload.report.ObjectNotFoundException;
import org.jboss.set.payload.report.container.AbstractEntityHome;
import org.jboss.set.payload.report.container.Container;

import java.net.URL;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class BugzillaIssueHome extends AbstractEntityHome<URL, BugzillaIssue> {
    private final static BugzillaIssueTracker BUGZILLA_ISSUE_TRACKER = Container.get(BugzillaIssueTracker.class);

    @Override
    protected Class<BugzillaIssue> getEntityClass() {
        return BugzillaIssue.class;
    }

    @Override
    protected BugzillaIssue load(final URL primaryKey) throws ObjectNotFoundException {
        try {
            return new BugzillaIssue(BUGZILLA_ISSUE_TRACKER.getIssue(primaryKey));
        } catch (NotFoundException e) {
            throw new ObjectNotFoundException(primaryKey.toString(), e);
        }
    }

    @Override
    protected BugzillaIssue proxy(final URL primaryKey) {
        return super.proxy(primaryKey);
    }
}
