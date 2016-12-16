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

import org.jboss.invocation.Interceptor;
import org.jboss.invocation.InterceptorInvocationHandler;
import org.jboss.invocation.Interceptors;
import org.jboss.invocation.proxy.ProxyConfiguration;
import org.jboss.invocation.proxy.ProxyFactory;
import org.jboss.set.payload.report.container.Cache;
import org.jboss.set.payload.report.entity.InstanceLoadInterceptor;

import java.util.concurrent.atomic.AtomicInteger;

import static org.jboss.set.payload.report.util.Util.unchecked;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public abstract class AbstractIssueHome<K, E extends Issue> implements IssueHome<K> {
    private final Cache<K, Issue> cache = new Cache<>();
    private final AtomicInteger proxyNum = new AtomicInteger(0);

    /**
     * @param key primary key
     * @return a cached instance
     */
    private Issue cached(final K key) throws ObjectNotFoundException {
        synchronized (cache) {
            Issue issue = cache.get(key);
            if (issue == null) {
                issue = load(key);
                cache.put(key, issue);
            }
            return issue;
        }
    }

    @Override
    public Issue findByPrimaryKey(final K primaryKey) throws ObjectNotFoundException {
        cached(primaryKey); // preload to check
        return proxy(primaryKey);
    }

    protected abstract Class<E> getEntityClass();

    protected abstract E load(final K primaryKey) throws ObjectNotFoundException;

    protected E proxy(final K primaryKey) {
        final ProxyConfiguration<E> proxyConfiguration = new ProxyConfiguration<E>()
                .setClassLoader(IssueHome.class.getClassLoader())
                .setProxyName(Issue.class.getPackage(), "Issue$Proxy" + proxyNum.getAndIncrement())
                .setSuperClass(getEntityClass());
        final ProxyFactory<E> proxyFactory = new ProxyFactory<E>(proxyConfiguration);
        final Interceptor interceptor = Interceptors.getChainedInterceptor(
                new InstanceLoadInterceptor<>(primaryKey, (key) -> {
                    try {
                        return cached(key);
                    } catch (ObjectNotFoundException e) {
                        // we should never have gotten a proxy that is not backed by an actual instance
                        throw new RuntimeException(e);
                    }
                }),
                Interceptors.getInvokingInterceptor());
        return unchecked(() -> proxyFactory.newInstance(new InterceptorInvocationHandler(interceptor)));
    }
}
