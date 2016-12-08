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

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A simple in memory cache which clears on GC.
 *
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class Cache<K, V> implements Map<K, V> {
    private final HashMap<K, WeakReference<V>> map = new HashMap<>();

    @Override
    public void clear() {
        // TODO: does not take into account in-use entries
        map.clear();
    }

    @Override
    public boolean containsKey(final Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        throw new RuntimeException("NYI: org.jboss.set.payload.report.container.Cache.containsValue");
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new RuntimeException("NYI: org.jboss.set.payload.report.container.Cache.entrySet");
    }

    @Override
    public V get(final Object key) {
        final WeakReference<V> ref = map.get(key);
        if (ref == null) return null;
        return ref.get();
    }

    @Override
    public boolean isEmpty() {
        throw new RuntimeException("NYI: org.jboss.set.payload.report.container.Cache.isEmpty");
    }

    @Override
    public Set<K> keySet() {
        throw new RuntimeException("NYI: org.jboss.set.payload.report.container.Cache.keySet");
    }

    @Override
    public V put(final K key, final V value) {
        // TODO: use queue?
        // TODO: should the cache entry not always be empty?
        final WeakReference<V> prevRef = map.put(key, new WeakReference<V>(value));
        if (prevRef == null) return null;
        return prevRef.get();
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        throw new RuntimeException("NYI: org.jboss.set.payload.report.container.Cache.putAll");
    }

    @Override
    public V remove(final Object key) {
        throw new RuntimeException("NYI: org.jboss.set.payload.report.container.Cache.remove");
    }

    @Override
    public int size() {
        throw new RuntimeException("NYI: org.jboss.set.payload.report.container.Cache.size");
    }

    @Override
    public Collection<V> values() {
        throw new RuntimeException("NYI: org.jboss.set.payload.report.container.Cache.values");
    }
}
