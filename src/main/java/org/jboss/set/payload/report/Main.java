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

import org.jboss.jbossset.bugclerk.Severity;
import org.jboss.jbossset.bugclerk.Violation;
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
        final PayloadHome payloadHome = Container.get(AggregatePayloadHome.class);
        final Payload payload = payloadHome.findByPrimaryKey(args[0]);
        final int numIssues = payload.getIssues().stream().sorted(Comparator.comparing(Issue::toString)).mapToInt((issue) -> {
            //System.out.println(issue.getSignal() + " " + issue.getReport());
            // TODO: move to domain
            try {
                final Collection<Violation> violations = issue.getViolations();
                final Severity severity = violations.stream().map(violation -> violation.getLevel()).reduce((level1, level2) -> max(level1, level2)).orElse(null);
                final Signal signal;
                if (severity == Severity.BLOCKER || severity == Severity.CRITICAL) signal = Signal.RED;
                else if (severity == Severity.MAJOR) signal = Signal.YELLOW;
                else signal = Signal.GREEN;
                System.out.println(issue + " " + signal + " " + violations.stream().map(violation -> violation.getMessage()).collect(Collectors.toList()));
                System.out.println("   " + issue.getDependsOn());
            } catch (Exception e) {
                System.out.println(issue + " " + e.toString());
                //throw e;
            }
            return 1;
        }).sum();
        System.out.println("Total issues: " + numIssues);
        System.exit(0);
    }

    private static int level(final Severity severity) {
        switch (severity) {
            case BLOCKER:
                return 5;
            case CRITICAL:
                return 4;
            case MAJOR:
                return 3;
            case MINOR:
                return 2;
            case TRIVIAL:
                return 1;
            default:
                return 0;
        }
    }

    private static Severity max(final Severity l1, final Severity l2) {
        if (level(l1) > level(l2)) return l1; else return l2;
    }
}
