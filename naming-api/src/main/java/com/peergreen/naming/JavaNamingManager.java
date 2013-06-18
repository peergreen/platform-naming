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
package com.peergreen.naming;

import javax.naming.Context;

/**
 * Manages the java: context
 * @author Florent Benoit
 */
public interface JavaNamingManager {

    /**
     * Bind the given context on the current thread
     * @param javaContext the context to set
     * @return the previous context
     */
    void bindThreadContext(Context javaContext);

    /**
     * Unbind the current context.
     */
    void unbindThreadContext();

    /**
     * Bind the given context for the given classloader
     * @param classLoader the classloader to use
     * @param javaContext the context to set
     */
    void bindClassLoaderContext(ClassLoader classLoader, Context javaContext);

    /**
     * Unbind the current context
     * @param classLoader the associated classloader
     */
    void unbindClassLoaderContext(ClassLoader classLoader);


    /**
     * Gets the current context.
     */
    Context getContext();

}
