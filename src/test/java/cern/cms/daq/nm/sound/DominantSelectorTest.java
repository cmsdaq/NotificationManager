package cern.cms.daq.nm.sound;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.LogicModuleView;

public class DominantSelectorTest {

	@Test
	public void diffPriorityTest() {

		DominantSelector selector = new DominantSelector();
		Set<Event> events = new HashSet<Event>();

		events.add(generate("e1", Priority.IMPORTANT, LogicModuleView.FlowchartCase1));
		events.add(generate("e2", Priority.CRITICAL, LogicModuleView.FlowchartCase2));

		Pair<Event, Set<Event>> r = selector.selectDominantEvent(events);

		Assert.assertNotNull(r.getLeft());
		Assert.assertNotNull(r.getRight());
		Assert.assertEquals("e2", r.getLeft().getTitle());

		Assert.assertEquals(1, r.getRight().size());
		Event secondary = r.getRight().iterator().next();
		Assert.assertEquals("e1", secondary.getTitle());
	}

	@Test
	public void samePriorityDiffUsefulnessTest() {

		DominantSelector selector = new DominantSelector();
		Set<Event> events = new HashSet<Event>();

		events.add(generate("e1", Priority.CRITICAL, LogicModuleView.FlowchartCase1));
		events.add(generate("e2", Priority.CRITICAL, LogicModuleView.FlowchartCase2));

		Pair<Event, Set<Event>> r = selector.selectDominantEvent(events);

		Assert.assertNotNull(r.getLeft());
		Assert.assertNotNull(r.getRight());
		Assert.assertEquals("e2", r.getLeft().getTitle());

		Assert.assertEquals(1, r.getRight().size());
		Event secondary = r.getRight().iterator().next();
		Assert.assertEquals("e1", secondary.getTitle());
	}

	@Test
	public void samePrioritySameUsefulnessTest() {

		DominantSelector selector = new DominantSelector();
		Set<Event> events = new HashSet<Event>();

		events.add(generate("e1", Priority.CRITICAL, LogicModuleView.FlowchartCase1));
		events.add(generate("e2", Priority.CRITICAL, LogicModuleView.FlowchartCase1));

		Pair<Event, Set<Event>> r = selector.selectDominantEvent(events);

		Assert.assertNotNull(r.getLeft());
		Assert.assertNotNull(r.getRight());

		Assert.assertEquals(1, r.getRight().size());

		// the behavior is random here - misconfiguration of usefulness
	}

	private Event generate(String title, Priority priority, LogicModuleView logicModule) {
		Event e = new Event();
		e.setTitle(title);
		e.setPriority(priority);
		e.setLogicModule(logicModule);
		return e;
	}

}
