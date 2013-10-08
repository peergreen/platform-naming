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

import static com.peergreen.naming.internal.ContextType.APP;
import static com.peergreen.naming.internal.ContextType.COMP;
import static com.peergreen.naming.internal.ContextType.GLOBAL;
import static com.peergreen.naming.internal.ContextType.MODULE;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.service.jndi.JNDIContextManager;

import com.peergreen.naming.JavaContextFactory;
import com.peergreen.naming.JavaContextFactoryListener;
import com.peergreen.naming.internal.context.ContextImpl;

@Component
@Instantiate
@Provides
public class DefaultJavaContextFactory implements JavaContextFactory {

    /**
     * JNDI context manager.
     */
    private final JNDIContextManager jndiContextManager;

    /**
     * java:global context = new InitialContext().
     */
    private Context globalContext;

    /**
     * List of listeners registered for this context factory.
     */
    private final List<JavaContextFactoryListener> listeners;

    public DefaultJavaContextFactory(@Requires JNDIContextManager jndiContextManager) {
        this.jndiContextManager = jndiContextManager;
        this.listeners = new CopyOnWriteArrayList<>();
    }

    @Validate
    public void init() throws NamingException {
        this.globalContext = jndiContextManager.newInitialContext();
    }

    @Invalidate
    public void close() throws NamingException {
        this.globalContext.close();
    }

    @Override
    public Context createContext(String name) throws NamingException {
        // Create a new environment
        ContextImpl ctx = new ContextImpl(name);
        return ctx;
    }

    @Bind(optional=true, aggregate=true)
    public void bindJavaContextFactoryListener(JavaContextFactoryListener javaContextFactoryListener) {
        listeners.add(javaContextFactoryListener);
    }

    @Unbind(optional=true, aggregate=true)
    public void unbindJavaContextFactoryListener(JavaContextFactoryListener javaContextFactoryListener) {
        listeners.remove(javaContextFactoryListener);
    }

    @Override
    public Context createContext(String name, Context javaAppContext, Context javaModuleContext) throws NamingException {

        // Create a new environment
        ContextImpl ctx = new ContextImpl(name);

        // Create subContext
        ContextImpl compCtx = (ContextImpl) ctx.createSubcontext(COMP.getName());

        // Add global
        ctx.addBinding(GLOBAL.getName(), globalContext);

        // module may use comp context
        Context moduleCtx = null;
        if (javaModuleContext == null) {
            moduleCtx = compCtx;
        } else {
            moduleCtx = javaModuleContext;
        }
        ctx.addBinding(MODULE.getName(), moduleCtx);


        // App context (if not defined, reuse module context)
        Context appCtx = null;
        if (javaAppContext == null) {
            appCtx = moduleCtx;
        } else {
            appCtx = javaAppContext;
        }

        ctx.addBinding(APP.getName(), appCtx);

        // Apply listeners
        for (JavaContextFactoryListener listener : listeners) {
            listener.handle(ctx);
        }

        return ctx;

    }

}
