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
package org.jboss.set.payload.report.entity;

import org.jboss.invocation.Interceptor;
import org.jboss.invocation.InterceptorContext;

import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class InstanceLoadInterceptor<K, E> implements Interceptor {
    // EJB 3.2 3.4.7 bypass identity methods on the proxy
    private static final Method EQUALS;
    private static final Method HASH_CODE;
    private static final Method TO_STRING;

    static {
        try {
            final Class<Object> cls = Object.class;
            EQUALS = cls.getDeclaredMethod("equals", Object.class);
            HASH_CODE = cls.getDeclaredMethod("hashCode");
            TO_STRING = cls.getDeclaredMethod("toString");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final K primaryKey;
    private final Function<K, E> supplier;

    public InstanceLoadInterceptor(final K primaryKey, final Function<K, E> supplier) {
        this.primaryKey = primaryKey;
        this.supplier = supplier;
    }

    @Override
    public Object processInvocation(final InterceptorContext context) throws Exception {
        // TODO: should really be per method interceptors as opposed to per instance
        if (context.getMethod().equals(EQUALS)) return primaryKey.equals(context.getParameters()[0]);
        if (context.getMethod().equals(HASH_CODE)) return primaryKey.hashCode();
        if (context.getMethod().equals(TO_STRING)) return primaryKey.toString(); // TODO: maybe add container id
        context.setTarget(supplier.apply(primaryKey));
        return context.proceed();
    }
}
