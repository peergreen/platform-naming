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

import static org.osgi.service.jndi.JNDIConstants.JNDI_URLSCHEME;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;

@Component
@Provides
@Instantiate
public class JavaURLContextFactory implements ObjectFactory {

    public static final String JAVA_SCHEME = "java";

    @ServiceProperty(name = JNDI_URLSCHEME,
                     value = JAVA_SCHEME)
    private String jndiUrlScheme;


    private final Context javaURLContext;

    public JavaURLContextFactory(@Requires(filter="(Context=JavaURLContext)") Context javaURLContext) {
        this.javaURLContext = javaURLContext;
    }


    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
            Hashtable<?,?> environment) throws Exception {
        return javaURLContext;
    }
}
