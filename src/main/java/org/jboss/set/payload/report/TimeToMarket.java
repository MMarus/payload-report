/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2020, Red Hat, Inc., and individual contributors
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

import org.jboss.set.payload.report.container.Container;
import org.jboss.set.payload.report.jira.JiraIssueHome;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TimeToMarket {
    private static class Result {
        int numIssues;
        long totalResolutionAge;
        long totalTimeToMarket;
    }

    public static void main(final String[] args) throws Exception {
        if (args.length == 0)
            reportUnassigned();
        else {
            final PayloadHome payloadHome = Container.get(AggregatePayloadHome.class);
            final Payload payload = payloadHome.findByPrimaryKey(args[0]);
            //final Date releaseDate = payload.getReleaseDate().orElseThrow(() -> new IllegalStateException("Version " + payload + " has not been released yet"));
            System.out.println(payload.getFixVersion() + ":");
            report(payload.getIssues());
        }
        System.exit(0);
    }

    private static void report(final Collection<? extends Issue> issues) {
        final Result result = new Result();
        issues.stream().sorted(Comparator.comparing(Issue::toString)).collect(
                () -> result,
                (r, issue) -> {
                    System.out.println(issue);
                    final Date creationDate = issue.getCreationDate();
                    final Optional<Date> resolutionDate = issue.getResolutionDate();
                    // issue might be part of sprint, but did not get resolved
                    if (!resolutionDate.isPresent()) {
                        System.err.println("Missing resolution date on " + issue);
                        return;
                    }
                    final Optional<Payload> payload = issue.getPayload();
                    if (!payload.isPresent()) {
                        System.err.println("Missing payload on " + issue);
                        return;
                    }
                    final Optional<Date> releaseDate = payload.get().getReleaseDate();
                    if (!releaseDate.isPresent()) {
                        System.err.println("Missing release date on " + issue);
                        return;
                    }
                    final long resolutionAge = resolutionDate.get().getTime() - creationDate.getTime();
                    final long ttm = releaseDate.get().getTime() - creationDate.getTime();
                    r.numIssues++;
                    r.totalResolutionAge += resolutionAge;
                    r.totalTimeToMarket += ttm;
                },
                (r1, r2) -> {
                    System.out.println("r1 = " + r1);
                    System.out.println("r2 = " + r2);
                }
        );

        System.out.println("Total issues: " + result.numIssues);
        System.out.println("Average resolution age: " + TimeUnit.MILLISECONDS.toDays(result.totalResolutionAge / result.numIssues));
        System.out.println("Average time to market: " + TimeUnit.MILLISECONDS.toDays(result.totalTimeToMarket / result.numIssues));
    }

    private static void reportUnassigned() {
        final String jql = "project = JBEAP AND \"Target Release\" in (7.0.z.GA, 7.1.z.GA, 7.2.z.GA) AND assignee was EMPTY AND status in (Closed, Verified)";
        final JiraIssueHome issueHome = Container.get(JiraIssueHome.class);
        report(issueHome.findByJQL(jql));
    }
}
