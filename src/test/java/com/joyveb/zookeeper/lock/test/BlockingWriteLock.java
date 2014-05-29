package com.joyveb.zookeeper.lock.test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.recipes.lock.LockListener;
import org.apache.zookeeper.recipes.lock.WriteLock;

public class BlockingWriteLock {
	 private String _name;
	    private String _path;
	    private WriteLock _writeLock;
	    private CountDownLatch _lockAcquiredSignal = new CountDownLatch(1);
	    
	    private AtomicLong num = new AtomicLong();

	    public static final List<ACL> DEFAULT_ACL = ZooDefs.Ids.OPEN_ACL_UNSAFE;

	    public BlockingWriteLock(String name, ZooKeeper zookeeper, String path) {
	        this(name, zookeeper, path, DEFAULT_ACL);
	    }

	    public BlockingWriteLock(String name, ZooKeeper zookeeper, String path, List<ACL> acl) {
	        _name = name;
	        _path = path;
	        _writeLock = new WriteLock(zookeeper, path, acl, new SyncLockListener());
	        _writeLock.setRetryDelay(10);
	    }

	    public void lock() throws InterruptedException, KeeperException {
	        _writeLock.lock();
	        _lockAcquiredSignal.await();
	    }

	    public boolean lock(long timeout, TimeUnit unit) throws InterruptedException, KeeperException {
	        _writeLock.lock();
	        return _lockAcquiredSignal.await(timeout, unit);
	    }

	    public boolean tryLock() throws InterruptedException, KeeperException {
	        return lock(1, TimeUnit.SECONDS);
	    }

	    public void unlock() {
	        _writeLock.unlock();
	    }

	    class SyncLockListener implements LockListener {

	        @Override
	        public void lockAcquired() {
	            _lockAcquiredSignal.countDown();
	        }

	        @Override
	        public void lockReleased() {
	        }
	    }
}
