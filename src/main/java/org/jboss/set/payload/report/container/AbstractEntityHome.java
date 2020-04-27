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
package org.jboss.set.payload.report.container;

import org.jboss.invocation.Interceptor;
import org.jboss.invocation.InterceptorInvocationHandler;
import org.jboss.invocation.Interceptors;
import org.jboss.invocation.proxy.ProxyConfiguration;
import org.jboss.invocation.proxy.ProxyFactory;
import org.jboss.set.payload.report.ObjectNotFoundException;
import org.jboss.set.payload.report.entity.InstanceLoadInterceptor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.jboss.set.payload.report.util.Util.unchecked;

public abstract class AbstractEntityHome<K, E> implements EntityHome<K, E> {
    private final Cache<K, E> cache = new Cache<>();
    private final AtomicInteger proxyNum = new AtomicInteger(0);

    protected void cache(final K key, final Supplier<E> supplier) {
        synchronized (cache) {
            if (!cache.containsKey(key)) {
                cache.put(key, supplier.get());
            }
        }
    }

    /**
     * @param key primary key
     * @return a cached instance
     */
    private E cached(final K key) throws ObjectNotFoundException {
        synchronized (cache) {
            E issue = cache.get(key);
            if (issue == null) {
                issue = load(key);
                cache.put(key, issue);
            }
            return issue;
        }
    }

    @Override
    public final E findByPrimaryKey(final K primaryKey) throws ObjectNotFoundException {
        cached(primaryKey); // preload to check
        return proxy(primaryKey);
    }

    protected abstract Class<E> getEntityClass();

    protected abstract E load(final K primaryKey) throws ObjectNotFoundException;

    protected E proxy(final K primaryKey) {
        final Class<E> clz = getEntityClass();
        final ProxyConfiguration<E> proxyConfiguration = new ProxyConfiguration<E>()
                .setClassLoader(clz.getClassLoader())
                .setProxyName(clz.getPackage(), clz.getSimpleName() + "$Proxy" + proxyNum.getAndIncrement())
                .setSuperClass(clz);
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
