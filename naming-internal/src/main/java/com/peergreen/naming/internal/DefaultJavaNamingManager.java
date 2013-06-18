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
package com.peergreen.naming.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;

import com.peergreen.naming.JavaNamingManager;

/**
 *
 *
 * @author Florent Benoit
 */
@Component
@Provides
@Instantiate
public class DefaultJavaNamingManager implements JavaNamingManager {

    /**
     * Current active context by threads
     */
    private final ThreadLocal<Context> threadActiveContext;

    /**
     * Previous active context by threads
     */
    private final ThreadLocal<Context> threadPreviousContext;

    /**
     * Context by classloader.
     */
    private final Map<ClassLoader, Context> contextByClassLoaders;


    public DefaultJavaNamingManager() {
        this.threadActiveContext = new InheritableThreadLocal<>();
        this.threadPreviousContext = new InheritableThreadLocal<>();
        this.contextByClassLoaders = new ConcurrentHashMap<>();
    }


    @Override
    public void bindThreadContext(Context javaContext) {
        threadPreviousContext.set(threadActiveContext.get());
        threadActiveContext.set(javaContext);

    }

    @Override
    public void unbindThreadContext() {
        threadActiveContext.set(threadPreviousContext.get());
        threadPreviousContext.set(null);

    }

    @Override
    public void bindClassLoaderContext(ClassLoader classLoader, Context javaContext) {
        contextByClassLoaders.put(classLoader, javaContext);
    }

    @Override
    public void unbindClassLoaderContext(ClassLoader classLoader) {
        contextByClassLoaders.remove(classLoader);
    }


    @Override
    public Context getContext() {
        // Search in thread
        Context context = threadActiveContext.get();
        if (context != null) {
            return context;
        }

        // search for classloader
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        context = contextByClassLoaders.get(classloader);
        if (context != null) {
            return context;
        }
        // search with parent classloader
        ClassLoader parentclassloader = classloader.getParent();
        if (parentclassloader != null) {
            context = contextByClassLoaders.get(parentclassloader);
            if (context != null) {
                return context;
            }
        }



        return context;
    }

}
