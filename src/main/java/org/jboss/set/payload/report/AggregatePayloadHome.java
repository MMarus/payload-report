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

import org.jboss.set.payload.report.bugzilla.BugzillaPayloadHome;
import org.jboss.set.payload.report.container.Container;
import org.jboss.set.payload.report.jira.JiraPayloadHome;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class AggregatePayloadHome implements PayloadHome {
    private List<PayloadHome> payloadHomes = Arrays.asList(Container.get(BugzillaPayloadHome.class), Container.get(JiraPayloadHome.class)); // TODO: configure

    @Override
    public Payload findByPrimaryKey(final String arg) throws ObjectNotFoundException {
        Optional<ObjectNotFoundException> last = Optional.empty();
        for (PayloadHome home : payloadHomes) {
            try {
                return home.findByPrimaryKey(arg);
            } catch (ObjectNotFoundException e) {
                last = Optional.of(e);
            }
        }
        throw last.orElse(new ObjectNotFoundException(arg));
    }
}
