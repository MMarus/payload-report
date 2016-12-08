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

import org.jboss.jbossset.bugclerk.Level;
import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.set.aphrodite.domain.*;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.payload.report.container.Container;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class Main {
    public static void main(final String[] args) throws Exception {
        // normally obtain PayloadHome through injection
        final PayloadHome payloadHome = Container.get(PayloadHomeImpl.class);
        final Payload payload = payloadHome.findByPrimaryKey(args[0]);
        payload.getIssues().stream().sorted(Comparator.comparing(Issue::toString)).forEach((issue) -> {
            //System.out.println(issue.getSignal() + " " + issue.getReport());
            // TODO: move to domain
            final Collection<Violation> violations = issue.getViolations();
            final Level level = violations.stream().map(violation -> violation.getLevel()).reduce((level1, level2) -> max(level1, level2)).orElse(null);
            final Signal signal;
            if (level == Level.ERROR) signal = Signal.RED;
            else if (level == Level.WARNING) signal = Signal.YELLOW;
            else signal = Signal.GREEN;
            System.out.println(issue + " " + signal + " " + violations.stream().map(violation -> violation.getMessage()).collect(Collectors.toList()));
        });
        System.exit(0);
    }

    private static Level max(final Level l1, final Level l2) {
        if (l1 == Level.ERROR || l2 == Level.ERROR) return Level.ERROR;
        if (l1 == Level.WARNING || l2 == Level.WARNING) return Level.WARNING;
        return null;
    }
}
