/**
 * Copyright 2013 Peergreen S.A.S.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.peergreen.naming.internal.context;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * Implementation of the NamingEnumeration for list operations Each element is
 * of type NameClassPair.
 * @author Florent Benoit
 */
public class NamingEnumerationImpl implements NamingEnumeration<NameClassPair> {

    /**
     * list of names.
     */
    private final Iterator<String> names;

    /**
     * List of bindings.
     */
    private final Map<String, Object> bindings;

    /**
     * Constructor. Called by list()
     * @param bindings list of bindings
     */
    NamingEnumerationImpl(final Map<String, Object> bindings) {
        this.bindings = bindings;
        this.names = bindings.keySet().iterator();
    }

    /**
     * Determines whether there are any more elements in the enumeration.
     * @return true if there is more in the enumeration ; false otherwise.
     * @throws NamingException If a naming exception is encountered while
     *         attempting to determine whether there is another element in the
     *         enumeration.
     */
    @Override
    public boolean hasMore() throws NamingException {
        return names.hasNext();
    }

    /**
     * Retrieves the next element in the enumeration.
     * @return The possibly null element in the enumeration. null is only valid
     *         for enumerations that can return null (e.g. Attribute.getAll()
     *         returns an enumeration of attribute values, and an attribute
     *         value can be null).
     * @throws NamingException If a naming exception is encountered while
     *         attempting to retrieve the next element. See NamingException and
     *         its subclasses for the possible naming exceptions.
     */
    @Override
    public NameClassPair next() throws NamingException {
        String name = names.next();
        String className = bindings.get(name).getClass().getName();
        return new NameClassPair(name, className);
    }

    /**
     * Closes this enumeration.
     */
    @Override
    public void close() {
    }

    /**
     * Returns the next element of this enumeration if this enumeration object
     * has at least one more element to provide.
     * @return the next element of this enumeration.
     */
    @Override
    public NameClassPair nextElement() {
        try {
            return next();
        } catch (NamingException e) {
            throw new NoSuchElementException(e.toString());
        }
    }

    /**
     * Tests if this enumeration contains more elements.
     * @return <code>true</code> if and only if this enumeration object
     *         contains at least one more element to provide; <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean hasMoreElements() {
        try {
            return hasMore();
        } catch (NamingException e) {
            return false;
        }
    }

}
