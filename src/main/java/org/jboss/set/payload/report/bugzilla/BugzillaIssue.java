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

import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.payload.report.ViolationHome;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static org.jboss.set.payload.report.container.Container.get;
import static org.jboss.set.payload.report.util.Lazy.lazy;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class BugzillaIssue implements org.jboss.set.payload.report.Issue {
    private final Issue issue;
    private final Supplier<Collection<Violation>> violations;

    public BugzillaIssue() {
        // for proxy
        issue = null;
        violations = null;
    }

    public BugzillaIssue(final Issue issue) {
        this.issue = issue;
        violations = lazy(() -> get(ViolationHome.class).findByIssue(issue));
    }

    @Override
    public List<URL> getDependsOn() {
        return issue.getDependsOn();
    }

    @Override
    public Collection<Violation> getViolations() {
        return violations.get();
    }
}
