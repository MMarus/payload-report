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

import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.RuleEngine;
import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import static org.jboss.set.payload.report.util.Util.fetch;
import static org.jboss.set.payload.report.util.Util.unchecked;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class ViolationHome {
    private final static RuleEngine RULE_ENGINE;
    private final static KieSession KIE_SESSION;

    static {
        RULE_ENGINE = new RuleEngine(new HashMap(0), new AphroditeClient());
        KIE_SESSION = unchecked(() -> fetch(RULE_ENGINE, "ksession", KieSession.class));
    }

    public Collection<Violation> findByIssue(final org.jboss.set.aphrodite.domain.Issue issue) {
        KIE_SESSION.getFactHandles(new ClassObjectFilter(Violation.class)).forEach(factHandle -> {
            KIE_SESSION.delete(factHandle);
        });
        final Candidate candidate = new Candidate(issue);
        RULE_ENGINE.processBugEntry(Arrays.asList(candidate));
        return candidate.getViolations();
    }
}
