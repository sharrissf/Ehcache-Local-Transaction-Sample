package org.sharrissf.ehcache.sample;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.TransactionController;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfiguration.TransactionalMode;
import net.sf.ehcache.config.Configuration;

import org.sharrissf.ehcache.sample.Person.Gender;

public class LocalTransactionSample {
	private CacheManager cacheManager;
	private Ehcache cache1;
	private Ehcache cache2;
	private TransactionController transactionManager;

	public void start() {
		initializeCache();
		this.transactionManager = cacheManager.getTransactionController();

		// Single cache Cases
		showSingleCacheAddAndCommit();

		showSingleCacheAddAndRollback();

		showSingleCacheUpdateAndCommit();

		showSingleCacheUpdateAndRollback();

		// Two cache cases
		showMultiCacheAddAndCommit();

		showMultiCacheAddAndRollback();

		showMultiCacheUpdateAndCommit();

		showMultiCacheUpdateAndRollback();

	}

	private void showMultiCacheAddAndRollback() {
		System.out
				.println("\nTransactionally adding a person to cache1 and cache2 and rolling back");
		transactionManager.begin();
		cache1.put(new Element(4, new Person("Julie shmo", 33, Gender.FEMALE,
				"berry", "Lakeville", "NJ")));

		cache2.put(new Element("Julie shmo", 4));
		transactionManager.rollback();
		System.out.println("Cache1 size: " + getCache1Size() + " Cache2 size: "
				+ getCache2Size());
	}

	private void showMultiCacheAddAndCommit() {
		System.out
				.println("\nTransactionally adding a person to cache1 and cache2");
		transactionManager.begin();
		cache1.put(new Element(3, new Person("Steve morris", 13, Gender.FEMALE,
				"King", "Parisippany", "NJ")));

		cache2.put(new Element("Steve Morris", 3));
		transactionManager.commit();
		System.out.println("Cache1 size: " + getCache1Size() + " Cache2 size: "
				+ getCache2Size());
	}

	private void showSingleCacheUpdateAndRollback() {
		Element e;
		String state;
		System.out
				.println("\nTransactionally updating a person to cache1 then rollback");
		transactionManager.begin();
		e = cache1.get(1);
		state = ((Person) e.getValue()).getAddress().getState();
		((Person) e.getValue()).getAddress().setState("PA");
		cache1.put(e);
		transactionManager.rollback();

		transactionManager.begin();

		System.out
				.println("Rolled back so state should stay the same for person 1 from: "
						+ state
						+ " to: "
						+ ((Person) cache1.get(1).getValue()).getAddress()
								.getState());
		transactionManager.commit();
		System.out.println("Cache1 size: " + getCache1Size() + " Cache2 size: "
				+ getCache2Size());
	}

	private void showMultiCacheUpdateAndRollback() {
		Element e;
		String state;
		System.out
				.println("\nTransactionally updating a person to cache1 and cache 2 then rollback");
		transactionManager.begin();
		e = cache1.get(1);
		state = ((Person) e.getValue()).getAddress().getState();
		((Person) e.getValue()).getAddress().setState("NJ");
		cache1.put(e);
		e = cache2.get("Steve Morris");
		cache2.put(new Element("Steve Morris", 36));
		transactionManager.rollback();

		transactionManager.begin();

		System.out
				.println("Rolled back so state should stay the same for person 1 from: "
						+ state
						+ " to: "
						+ ((Person) cache1.get(1).getValue()).getAddress()
								.getState()
						+ " cache2 id was: "
						+ e.getValue()
						+ " Now is: " + cache2.get("Steve Morris").getValue());
		transactionManager.commit();
		System.out.println("Cache1 size: " + getCache1Size() + " Cache2 size: "
				+ getCache2Size());
	}

	private void showMultiCacheUpdateAndCommit() {
		Element e;
		String state;
		System.out
				.println("\nTransactionally updating a person to cache1 and cache 2 then commit");
		transactionManager.begin();
		e = cache1.get(1);
		state = ((Person) e.getValue()).getAddress().getState();
		((Person) e.getValue()).getAddress().setState("PA");
		cache1.put(e);
		e = cache2.get("Steve Morris");
		cache2.put(new Element("Steve Morris", 33));
		transactionManager.commit();

		transactionManager.begin();

		System.out.println("Commit so state should update from: " + state
				+ " to: "
				+ ((Person) cache1.get(1).getValue()).getAddress().getState()
				+ " cache2 id was: " + e.getValue() + " Now is: "
				+ cache2.get("Steve Morris").getValue());
		transactionManager.commit();
		System.out.println("Cache1 size: " + getCache1Size() + " Cache2 size: "
				+ getCache2Size());
	}

	private void showSingleCacheUpdateAndCommit() {
		System.out
				.println("\nTransactionally updating a person to cache1 then commit");
		transactionManager.begin();
		Element e = cache1.get(1);
		String state = ((Person) e.getValue()).getAddress().getState();
		((Person) e.getValue()).getAddress().setState("WI");
		cache1.put(e);
		transactionManager.commit();
		transactionManager.begin();

		System.out.println("Changed state for person 1 from: " + state
				+ " to: " + ((Person) e.getValue()).getAddress().getState());
		transactionManager.commit();
		System.out.println("Cache1 size: " + getCache1Size() + " Cache2 size: "
				+ getCache2Size());
	}

	private void showSingleCacheAddAndRollback() {
		System.out
				.println("\nTransactionally adding a person to cache1 then rollback");
		transactionManager.begin();
		cache1.put(new Element(2, new Person("Joe Harris", 34, Gender.MALE,
				"brannan", "SF", "CA")));
		transactionManager.rollback();
		System.out.println("Cache1 size: " + getCache1Size() + " Cache2 size: "
				+ getCache2Size());
	}

	private void showSingleCacheAddAndCommit() {
		System.out.println("Transactionally adding a person to cache1");
		transactionManager.begin();
		cache1.put(new Element(1, new Person("Steve Harris", 88, Gender.MALE,
				"4th st", "Lombard", "CA")));
		transactionManager.commit();
		System.out.println("Cache1 size: " + getCache1Size() + " Cache2 size: "
				+ getCache2Size());
	}

	private int getCache1Size() {
		try {
			transactionManager.begin();
			return cache1.getSize();
		} finally {
			transactionManager.commit();
		}
	}

	private int getCache2Size() {
		try {
			transactionManager.begin();
			return cache2.getSize();
		} finally {
			transactionManager.commit();
		}

	}

	private void initializeCache() {

		// Create Cache
		Configuration cacheManagerConfig = new Configuration();
		cacheManagerConfig.addDefaultCache(new CacheConfiguration());

		// Creation cache1
		CacheConfiguration cacheConfig = new CacheConfiguration("cache1", -1)
				.eternal(true);
		cacheConfig.transactionalMode(TransactionalMode.LOCAL);
		cacheManagerConfig.addCache(cacheConfig);

		// Creation cache2
		cacheConfig = new CacheConfiguration("cache2", -1).eternal(true);
		cacheConfig.transactionalMode(TransactionalMode.LOCAL);
		cacheManagerConfig.addCache(cacheConfig);

		cacheManager = new CacheManager(cacheManagerConfig);

		cache1 = cacheManager.getEhcache("cache1");
		cache2 = cacheManager.getEhcache("cache2");

	}

	public final static void main(String[] args) throws Exception {
		new LocalTransactionSample().start();
	}
}
