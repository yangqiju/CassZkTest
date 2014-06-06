/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.curator.framework.imps;

import org.apache.curator.test.BaseClassForTests;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestNamespaceFacade extends BaseClassForTests
{
    @Test
    public void     testInvalid() throws Exception
    {
        try
        {
            CuratorFrameworkFactory.builder().namespace("/snafu").retryPolicy(new RetryOneTime(1)).connectString("").build();
            Assert.fail();
        }
        catch ( IllegalArgumentException e )
        {
            // correct
        }
    }

    @Test
    public void     testGetNamespace() throws Exception
    {
        CuratorFramework    client = CuratorFrameworkFactory.newClient(server.getConnectString(), new RetryOneTime(1));
        CuratorFramework    client2 = CuratorFrameworkFactory.builder().namespace("snafu").retryPolicy(new RetryOneTime(1)).connectString("").build();
        try
        {
            client.start();

            CuratorFramework fooClient = client.usingNamespace("foo");
            CuratorFramework barClient = client.usingNamespace("bar");

            Assert.assertEquals(client.getNamespace(), "");
            Assert.assertEquals(client2.getNamespace(), "snafu");
            Assert.assertEquals(fooClient.getNamespace(), "foo");
            Assert.assertEquals(barClient.getNamespace(), "bar");
        }
        finally
        {
            CloseableUtils.closeQuietly(client2);
            CloseableUtils.closeQuietly(client);
        }
    }

    @Test
    public void     testSimultaneous() throws Exception
    {
        CuratorFramework    client = CuratorFrameworkFactory.newClient(server.getConnectString(), new RetryOneTime(1));
        try
        {
            client.start();

            CuratorFramework fooClient = client.usingNamespace("foo");
            CuratorFramework barClient = client.usingNamespace("bar");

            fooClient.create().forPath("/one");
            barClient.create().forPath("/one");

            Assert.assertNotNull(client.getZookeeperClient().getZooKeeper().exists("/foo/one", false));
            Assert.assertNotNull(client.getZookeeperClient().getZooKeeper().exists("/bar/one", false));
        }
        finally
        {
            CloseableUtils.closeQuietly(client);
        }
    }

    @Test
    public void     testCache() throws Exception
    {
        CuratorFramework    client = CuratorFrameworkFactory.newClient(server.getConnectString(), new RetryOneTime(1));
        try
        {
            client.start();

            Assert.assertEquals(client.usingNamespace("foo"), client.usingNamespace("foo"));
            Assert.assertNotSame(client.usingNamespace("foo"), client.usingNamespace("bar"));
        }
        finally
        {
            CloseableUtils.closeQuietly(client);
        }
    }

    @Test
    public void     testBasic() throws Exception
    {
        CuratorFramework    client = CuratorFrameworkFactory.newClient(server.getConnectString(), new RetryOneTime(1));
        try
        {
            client.start();

            client.create().forPath("/one");
            Assert.assertNotNull(client.getZookeeperClient().getZooKeeper().exists("/one", false));

            client.usingNamespace("space").create().forPath("/one");
            Assert.assertNotNull(client.getZookeeperClient().getZooKeeper().exists("/space", false));

            client.usingNamespace("name").create().forPath("/one");
            Assert.assertNotNull(client.getZookeeperClient().getZooKeeper().exists("/name", false));
        }
        finally
        {
            CloseableUtils.closeQuietly(client);
        }
    }

    @Test
    public void     testIsStarted() throws Exception
    {
        CuratorFramework    client = CuratorFrameworkFactory.newClient(server.getConnectString(), new RetryOneTime(1));
        client.start();
        CuratorFramework    namespaced = client.usingNamespace(null);

        Assert.assertEquals(client.isStarted(), namespaced.isStarted());

        client.close();
        Assert.assertEquals(client.isStarted(), namespaced.isStarted());
    }
}
