/*
 * Copyright 2014 Groupon, Inc
 * Copyright 2014 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.billing.platform.jndi;

import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.DataSource;

import org.killbill.billing.platform.jndi.utils.JavaBeanReferenceMaker;

import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;

public class ReferenceableDataSourceSpy<T extends DataSource> extends DataSourceSpy implements Referenceable {

    private static final JavaBeanReferenceMaker referenceMaker = new JavaBeanReferenceMaker();

    private final T dataSource;

    public ReferenceableDataSourceSpy(final T realDataSource) {
        super(realDataSource);
        this.dataSource = realDataSource;
    }

    public T getDataSource() {
        return dataSource;
    }

    @Override
    public Reference getReference() throws NamingException {
        final Reference dataSourceReference = referenceMaker.createReference(dataSource);
        final Reference reference = new Reference(dataSourceReference.getClassName(), ReferenceableDataSourceSpyFactory.class.getName(), null);

        for (final Enumeration e = dataSourceReference.getAll(); e.hasMoreElements(); ) {
            final RefAddr addr = (RefAddr) e.nextElement();
            reference.add(addr);
        }

        return reference;
    }

    //@Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("javax.sql.DataSource.getParentLogger() is not currently supported by " + this.getClass().getName());
    }
}
