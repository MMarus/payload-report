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
package org.jboss.set.payload.report.container;

import org.junit.Test;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class CacheTestCase {
    @Test
    public void test1() {
        final Cache<String, Object> cache = new Cache<>();
        final String key = "key";
        Object value = new Object();
        cache.put(key, value);
        assertNotNull(cache.get(key));
    }

    @Test
    public void test2() {
        final Cache<String, Object> cache = new Cache<>();
        final String key = "key";
        Object value = new Object();
        cache.put(key, value);
        assertNotNull(cache.get(key));
        value = null;
        // this is JDK specific
        System.gc();
        assertNull(cache.get(key));
    }
}
