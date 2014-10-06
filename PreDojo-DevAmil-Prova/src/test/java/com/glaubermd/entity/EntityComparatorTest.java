package com.glaubermd.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.Date;

import org.junit.Test;

public class EntityComparatorTest {
	
	private static final int BEFORE = -1;
	private static final int AFTER = 1;
	private static final int EVEN = 0;

	@Test
	public void compareRankingEntries() {
		
		RankingEntry re1 = new RankingEntry(1,0);
		re1.addKill(new Date());
		re1.addKill(new Date());
		re1.addAward(AwardTypeEnum.KILL_STREAK);
		re1.addDeath();
		
		RankingEntry re2 = new RankingEntry(1,0);
		re2.addKill(new Date());
		re2.addKill(new Date());
		
		// System.out.println(re1);
		// System.out.println(re2);
		
		assertNotSame(re1, re2);
		assertEquals(BEFORE, re1.compareTo(re2));
		
		RankingEntry re3 = new RankingEntry(1,0);
		re3.addKill(new Date());
		re3.addKill(new Date());
		re3.addAward(AwardTypeEnum.KILL_STREAK);
		re3.addDeath();
		
		RankingEntry re4 = new RankingEntry(1,0);
		re4.addKill(new Date());
		re4.addKill(new Date());
		re4.addAward(AwardTypeEnum.IMMORTAL);
		re4.addDeath();
		
		// System.out.println(re3);
		// System.out.println(re4);
		
		assertNotSame(re3, re4);
		assertEquals(EVEN, re3.compareTo(re4));
		
		RankingEntry re5 = new RankingEntry(1,0);
		re5.addAward(AwardTypeEnum.KILL_STREAK);
		
		RankingEntry re6 = new RankingEntry(1,0);
		
		// System.out.println(re5);
		// System.out.println(re6);
		
		assertNotSame(re5, re6);
		assertEquals(AFTER, re5.compareTo(re6));
	}
	
	@Test
	public void comparePlayers() {
		
		RankingEntry re3 = new RankingEntry(1,0);
		re3.addKill(new Date());
		re3.addKill(new Date());
		re3.addAward(AwardTypeEnum.KILL_STREAK);
		re3.addDeath();
		
		RankingEntry re4 = new RankingEntry(1,0);
		re4.addKill(new Date());
		re4.addKill(new Date());
		re4.addAward(AwardTypeEnum.IMMORTAL);
		re4.addDeath();
		
		// System.out.println(re3);
		// System.out.println(re4);
		
		assertNotSame(re3, re4);
		assertEquals(EVEN, re3.compareTo(re4));
		
		Player p1 = new Player("P1");
		p1.setRanking(re3);
		Player p2 = new Player("P1");
		p2.setRanking(re4);
		
		System.out.println(p1);
		System.out.println(p2);
		
		assertEquals(EVEN, p1.compareTo(p2));
		
	}
	
}
