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
import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.set.payload.report.Issue;
import org.jboss.set.payload.report.ObjectNotFoundException;
import org.jboss.set.payload.report.Payload;
import org.jboss.set.payload.report.PayloadHome;
import org.jboss.set.payload.report.Signal;
import org.jboss.set.payload.report.ViolationHome;
import org.jboss.set.payload.report.container.Container;
import org.joda.time.DateTime;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Supplier;

import static org.jboss.set.payload.report.container.Container.get;
import static org.jboss.set.payload.report.util.Lazy.lazy;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class JiraIssue extends org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue implements Issue {
    private static final URL DUMMY_URL;
    private final PayloadHome payloadHome = Container.get(JiraPayloadHome.class);

    private final com.atlassian.jira.rest.client.api.domain.Issue delegate;
    private Optional<Payload> payload;
    private Optional<Date> resolutionDate;

    static {
        try {
            DUMMY_URL = new URL("file://dummy");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    // take this over from super
    private URL url;

    private final Supplier<Collection<Violation>> violations = lazy(() -> get(ViolationHome.class).findByIssue(this));

    // for proxy purposes only
    public JiraIssue() {
        super(DUMMY_URL);
        delegate = null;
    }

    protected JiraIssue(com.atlassian.jira.rest.client.api.domain.Issue delegate) {
        super(DUMMY_URL);
        this.delegate = delegate;
    }

//    public Issue(final URL url) {
//        super(url);
//    }

    private String findPayloadFixVersion() {
        final Iterator<Version> fixVersions = delegate.getFixVersions().iterator();
        while (fixVersions.hasNext()) {
            final Version version = fixVersions.next();
            final String name = version.getName();
            if (name.endsWith(".GA") && !name.contains(".z"))
                return name;
        }
        return null;
    }

    @Override
    public Date getCreationDate() {
        return super.getCreationTime().orElseThrow(() -> new IllegalStateException("No creation date on " + this));
    }

    public String getReport() {
        throw new RuntimeException("NYI: org.jboss.set.payload.report.jira.Issue.getReport");
    }

    @Override
    public Optional<Payload> getPayload() {
        if (payload == null) {
            final String fixVersion = findPayloadFixVersion();
            if (fixVersion != null) {
                try {
                    payload = Optional.of(payloadHome.findByPrimaryKey(fixVersion));
                } catch (ObjectNotFoundException e) {
                    payload = Optional.empty();
                }
            } else {
                payload = Optional.empty();
            }
        }
        return payload;
    }

    @Override
    public Optional<Date> getResolutionDate() {
        if (resolutionDate != null)
            return resolutionDate;
        // this is slow, but right now there are no other means
        final String value = (String) delegate.getField("resolutiondate").getValue();
        if (value != null) {
            resolutionDate = Optional.of(new DateTime(value).toDate());
        } else {
            resolutionDate = Optional.empty();
        }
        return resolutionDate;
    }

    public Signal getSignal() {
        throw new RuntimeException("NYI: org.jboss.set.payload.report.jira.Issue.getSignal");
    }

    public Collection<Violation> getViolations() {
        return violations.get();
    }

    public URL getURL() {
        return url;
    }

    public void setURL(final URL url) {
        this.url = url;
    }
}
