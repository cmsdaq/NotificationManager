package cern.cms.daq.nm.sound;

import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.Test;

import cern.cms.daq.nm.persistence.EventType;
import cern.cms.daq.nm.persistence.LogicModuleView;

public class SoundConfigurationReaderTest {

	@Test
	public void conditionStartTest() {

		SoundConfigurationReader configurator = new SoundConfigurationReader();
		Properties properties = new Properties();

		properties.put("sound.select.RunOngoing.start", "ItsAKindOfMagic.wav");
		Set<Triple<LogicModuleView, EventType, Sound>> r = configurator.readSoundSelectConfigurations(properties);

		Assert.assertEquals(1, r.size());
		Triple<LogicModuleView, EventType, Sound> r1 = r.iterator().next();
		Assert.assertEquals(LogicModuleView.RunOngoing, r1.getLeft());
		Assert.assertEquals(EventType.ConditionStart, r1.getMiddle());
	}

	@Test
	public void conditionUpdateTest() {

		SoundConfigurationReader configurator = new SoundConfigurationReader();
		Properties properties = new Properties();

		properties.put("sound.select.NoRateWhenExpected.update", "DingDong.wav");
		Set<Triple<LogicModuleView, EventType, Sound>> r = configurator.readSoundSelectConfigurations(properties);

		Assert.assertEquals(1, r.size());
		Triple<LogicModuleView, EventType, Sound> r1 = r.iterator().next();
		Assert.assertEquals(LogicModuleView.NoRateWhenExpected, r1.getLeft());
		Assert.assertEquals(EventType.ConditionUpdate, r1.getMiddle());
	}

	@Test
	public void conditionEndTest() {

		SoundConfigurationReader configurator = new SoundConfigurationReader();
		Properties properties = new Properties();

		properties.put("sound.select.NoRateWhenExpected.end", "DingDong.wav");
		Set<Triple<LogicModuleView, EventType, Sound>> r = configurator.readSoundSelectConfigurations(properties);

		Assert.assertEquals(1, r.size());
		Triple<LogicModuleView, EventType, Sound> r1 = r.iterator().next();
		Assert.assertEquals(LogicModuleView.NoRateWhenExpected, r1.getLeft());
		Assert.assertEquals(EventType.ConditionEnd, r1.getMiddle());
	}

	@Test
	public void transitionTest() {

		SoundConfigurationReader configurator = new SoundConfigurationReader();
		Properties properties = new Properties();

		properties.put("sound.select.LHCBeamModeComparator", "DingDong.wav");
		Set<Triple<LogicModuleView, EventType, Sound>> r = configurator.readSoundSelectConfigurations(properties);

		Assert.assertEquals(1, r.size());
		Triple<LogicModuleView, EventType, Sound> r1 = r.iterator().next();
		Assert.assertEquals(LogicModuleView.LHCBeamModeComparator, r1.getLeft());
		Assert.assertEquals(EventType.Single, r1.getMiddle());
	}

	@Test
	public void soundByFilenameTest() {

		SoundConfigurationReader configurator = new SoundConfigurationReader();
		Properties properties = new Properties();

		properties.put("sound.select.RunOngoing.start", "ItsAKindOfMagic.wav");
		Set<Triple<LogicModuleView, EventType, Sound>> r = configurator.readSoundSelectConfigurations(properties);

		Assert.assertEquals(1, r.size());
		Triple<LogicModuleView, EventType, Sound> r1 = r.iterator().next();
		Assert.assertEquals(Sound.NEW_RUN, r1.getRight());
	}

	@Test
	public void soundByEnumNameTest() {

		SoundConfigurationReader configurator = new SoundConfigurationReader();
		Properties properties = new Properties();

		properties.put("sound.select.RunOngoing.start", "NEW_RUN");
		Set<Triple<LogicModuleView, EventType, Sound>> r = configurator.readSoundSelectConfigurations(properties);

		Assert.assertEquals(1, r.size());
		Triple<LogicModuleView, EventType, Sound> r1 = r.iterator().next();
		Assert.assertEquals(Sound.NEW_RUN, r1.getRight());
	}

	@Test
	public void misspelledLogicModuleTest() {
		SoundConfigurationReader configurator = new SoundConfigurationReader();
		Properties properties = new Properties();
		properties.put("sound.select.RunOngoingAAA.start", "NEW_RUN");
		Set<Triple<LogicModuleView, EventType, Sound>> r = configurator.readSoundSelectConfigurations(properties);
		Assert.assertEquals(0, r.size());

		// Other configurations will be read anyway
		properties.put("sound.select.RunOngoing.start", "NEW_RUN");
		Set<Triple<LogicModuleView, EventType, Sound>> r2 = configurator.readSoundSelectConfigurations(properties);
		Assert.assertEquals(1, r2.size());
	}

	@Test
	public void misspelledTypeTest() {
		SoundConfigurationReader configurator = new SoundConfigurationReader();
		Properties properties = new Properties();
		properties.put("sound.select.RunOngoing.startAA", "NEW_RUN");
		Set<Triple<LogicModuleView, EventType, Sound>> r = configurator.readSoundSelectConfigurations(properties);
		Assert.assertEquals(0, r.size());

		// Other configurations will be read anyway
		properties.put("sound.select.RunOngoing.start", "NEW_RUN");
		Set<Triple<LogicModuleView, EventType, Sound>> r2 = configurator.readSoundSelectConfigurations(properties);
		Assert.assertEquals(1, r2.size());
	}

	@Test
	public void misspelledSoundTest() {
		SoundConfigurationReader configurator = new SoundConfigurationReader();
		Properties properties = new Properties();
		properties.put("sound.select.RunOngoing.start", "NEW_RUNAA");
		Set<Triple<LogicModuleView, EventType, Sound>> r = configurator.readSoundSelectConfigurations(properties);
		Assert.assertEquals(0, r.size());

		// Other configurations will be read anyway
		properties.put("sound.select.RunOngoing.start", "NEW_RUN");
		Set<Triple<LogicModuleView, EventType, Sound>> r2 = configurator.readSoundSelectConfigurations(properties);
		Assert.assertEquals(1, r2.size());
	}

	@Test
	public void notRelatedPropertyTest() {
		SoundConfigurationReader configurator = new SoundConfigurationReader();
		Properties properties = new Properties();
		properties.put("notification.port", "55555");
		Set<Triple<LogicModuleView, EventType, Sound>> r = configurator.readSoundSelectConfigurations(properties);
		Assert.assertEquals(0, r.size());

		// Other configurations will be read anyway
		properties.put("sound.select.RunOngoing.start", "NEW_RUN");
		Set<Triple<LogicModuleView, EventType, Sound>> r2 = configurator.readSoundSelectConfigurations(properties);
		Assert.assertEquals(1, r2.size());
	}

	@Test
	public void valueNotStringTest() {
		SoundConfigurationReader configurator = new SoundConfigurationReader();
		Properties properties = new Properties();
		properties.put("sound.select.RunOngoing.start", 5);
		Set<Triple<LogicModuleView, EventType, Sound>> r = configurator.readSoundSelectConfigurations(properties);
		Assert.assertEquals(0, r.size());

		// Other configurations will be read anyway
		properties.put("sound.select.RunOngoing.start", "NEW_RUN");
		Set<Triple<LogicModuleView, EventType, Sound>> r2 = configurator.readSoundSelectConfigurations(properties);
		Assert.assertEquals(1, r2.size());
	}

	@Test
	public void wrongFormatOfConfiguration() {
		SoundConfigurationReader configurator = new SoundConfigurationReader();
		Properties properties = new Properties();
		properties.put("sound.select.a.b.c.d", "a");
		Set<Triple<LogicModuleView, EventType, Sound>> r = configurator.readSoundSelectConfigurations(properties);
		Assert.assertEquals(0, r.size());

		// Other configurations will be read anyway
		properties.put("sound.select.RunOngoing.start", "NEW_RUN");
		Set<Triple<LogicModuleView, EventType, Sound>> r2 = configurator.readSoundSelectConfigurations(properties);
		Assert.assertEquals(1, r2.size());
	}

}
