package org.jboss.set.payload.report;

import org.jboss.set.payload.report.container.Container;

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
        final PayloadHome payloadHome = Container.get(AggregatePayloadHome.class);
        final Payload payload = payloadHome.findByPrimaryKey(args[0]);
        final Date releaseDate = payload.getReleaseDate().orElseThrow(() -> new IllegalStateException("Version " + payload + " has not been released yet"));
        final Result result = new Result();
        payload.getIssues().stream().sorted(Comparator.comparing(Issue::toString)).collect(
                () -> result,
                (r, issue) -> {
                    System.out.println(issue);
                    final Date creationDate = issue.getCreationDate();
                    final Optional<Date> resolutionDate = issue.getResolutionDate();
                    // issue might be part of sprint, but did not get resolved
                    if (!resolutionDate.isPresent())
                        return;
                    final long resolutionAge = resolutionDate.get().getTime() - creationDate.getTime();
                    final long ttm = releaseDate.getTime() - creationDate.getTime();
                    r.numIssues++;
                    r.totalResolutionAge += resolutionAge;
                    r.totalTimeToMarket += ttm;
                },
                (r1, r2) -> {
                    System.out.println("r1 = " + r1);
                    System.out.println("r2 = " + r2);
                }
            );
        System.out.println(payload.getFixVersion() + ":");
        System.out.println("Total issues: " + result.numIssues);
        System.out.println("Average resolution age: " + TimeUnit.MILLISECONDS.toDays(result.totalResolutionAge / result.numIssues));
        System.out.println("Average time to market: " + TimeUnit.MILLISECONDS.toDays(result.totalTimeToMarket / result.numIssues));
        System.exit(0);
    }
}
