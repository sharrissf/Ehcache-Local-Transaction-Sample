package org.sharrissf.ehcache.sample;

/**
 * Sample showing the usage of local transactions directly from Ehcache
 * 
 */
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
		try {
			cache1.put(new Element(4, new Person("Julie shmo", 33,
					Gender.FEMALE, "berry", "Lakeville", "NJ")));

			cache2.put(new Element("Julie shmo", 4));
		} finally {
			transactionManager.rollback();
			System.out.println("Cache1 size: " + getCache1Size()
					+ " Cache2 size: " + getCache2Size());
		}
	}

	private void showMultiCacheAddAndCommit() {
		boolean completed = false;
		System.out
				.println("\nTransactionally adding a person to cache1 and cache2");
		transactionManager.begin();
		try {
			cache1.put(new Element(3, new Person("Steve morris", 13,
					Gender.FEMALE, "King", "Parisippany", "NJ")));

			cache2.put(new Element("Steve Morris", 3));
			completed = true;
		} finally {
			if (completed)
				transactionManager.commit();
			else
				transactionManager.rollback();
			System.out.println("Cache1 size: " + getCache1Size()
					+ " Cache2 size: " + getCache2Size());
		}
	}

	private void showSingleCacheUpdateAndRollback() {
		Element e;
		String state;
		System.out
				.println("\nTransactionally updating a person to cache1 then rollback");
		transactionManager.begin();
		try {
			e = cache1.get(1);
			state = ((Person) e.getValue()).getAddress().getState();
			((Person) e.getValue()).getAddress().setState("PA");
			cache1.put(e);
		} finally {
			transactionManager.rollback();
		}

		transactionManager.begin();
		try {
			System.out
					.println("Rolled back so state should stay the same for person 1 from: "
							+ state
							+ " to: "
							+ ((Person) cache1.get(1).getValue()).getAddress()
									.getState());
		} finally {
			transactionManager.commit();
			System.out.println("Cache1 size: " + getCache1Size()
					+ " Cache2 size: " + getCache2Size());
		}
	}

	private void showMultiCacheUpdateAndRollback() {
		Element e;
		String state;
		System.out
				.println("\nTransactionally updating a person to cache1 and cache 2 then rollback");
		transactionManager.begin();
		try {
			e = cache1.get(1);
			state = ((Person) e.getValue()).getAddress().getState();
			((Person) e.getValue()).getAddress().setState("NJ");
			cache1.put(e);
			e = cache2.get("Steve Morris");
			cache2.put(new Element("Steve Morris", 36));
		} finally {
			transactionManager.rollback();
		}
		transactionManager.begin();
		try {
			System.out
					.println("Rolled back so state should stay the same for person 1 from: "
							+ state
							+ " to: "
							+ ((Person) cache1.get(1).getValue()).getAddress()
									.getState()
							+ " cache2 id was: "
							+ e.getValue()
							+ " Now is: "
							+ cache2.get("Steve Morris").getValue());
		} finally {
			transactionManager.commit();
		}
		System.out.println("Cache1 size: " + getCache1Size() + " Cache2 size: "
				+ getCache2Size());
	}

	private void showMultiCacheUpdateAndCommit() {
		Element e;
		String state;
		boolean complete = false;
		System.out
				.println("\nTransactionally updating a person to cache1 and cache 2 then commit");
		transactionManager.begin();
		try {
			e = cache1.get(1);
			state = ((Person) e.getValue()).getAddress().getState();
			((Person) e.getValue()).getAddress().setState("PA");
			cache1.put(e);
			e = cache2.get("Steve Morris");
			cache2.put(new Element("Steve Morris", 33));
			complete = true;
		} finally {
			if (complete)
				transactionManager.commit();
			else
				transactionManager.rollback();
		}
		transactionManager.begin();
		try {
			System.out.println("Commit so state should update from: "
					+ state
					+ " to: "
					+ ((Person) cache1.get(1).getValue()).getAddress()
							.getState() + " cache2 id was: " + e.getValue()
					+ " Now is: " + cache2.get("Steve Morris").getValue());
		} finally {
			transactionManager.commit();
			System.out.println("Cache1 size: " + getCache1Size()
					+ " Cache2 size: " + getCache2Size());
		}
	}

	private void showSingleCacheUpdateAndCommit() {
		boolean complete = false;
		Element e;
		String state;
		System.out
				.println("\nTransactionally updating a person to cache1 then commit");
		transactionManager.begin();

		try {
			e = cache1.get(1);
			state = ((Person) e.getValue()).getAddress().getState();
			((Person) e.getValue()).getAddress().setState("WI");
			cache1.put(e);
			complete = true;
		} finally {
			if (complete)
				transactionManager.commit();
			else
				transactionManager.rollback();
		}
		transactionManager.begin();
		try {
			System.out
					.println("Changed state for person 1 from: " + state
							+ " to: "
							+ ((Person) e.getValue()).getAddress().getState());
		} finally {
			transactionManager.commit();
		}
		System.out.println("Cache1 size: " + getCache1Size() + " Cache2 size: "
				+ getCache2Size());
	}

	private void showSingleCacheAddAndRollback() {
		System.out
				.println("\nTransactionally adding a person to cache1 then rollback");
		transactionManager.begin();
		try {
			cache1.put(new Element(2, new Person("Joe Harris", 34, Gender.MALE,
					"brannan", "SF", "CA")));
		} finally {
			transactionManager.rollback();
		}
		System.out.println("Cache1 size: " + getCache1Size() + " Cache2 size: "
				+ getCache2Size());
	}

	private void showSingleCacheAddAndCommit() {
		System.out.println("Transactionally adding a person to cache1");
		transactionManager.begin();
		boolean complete = false;
		try {
			cache1.put(new Element(1, new Person("Steve Harris", 88,
					Gender.MALE, "4th st", "Lombard", "CA")));
			complete = true;
		} finally {
			if (complete)
				transactionManager.commit();
			else
				transactionManager.rollback();
		}
		System.out.println("Cache1 size: " + getCache1Size() + " Cache2 size: "
				+ getCache2Size());
	}

	private int getCache1Size() {
		transactionManager.begin();
		try {
			return cache1.getSize();
		} finally {
			transactionManager.commit();
		}
	}

	private int getCache2Size() {
		transactionManager.begin();
		try {
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
