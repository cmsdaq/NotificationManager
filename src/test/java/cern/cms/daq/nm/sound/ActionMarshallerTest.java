package cern.cms.daq.nm.sound;

import org.junit.Assert;
import org.junit.Test;

public class ActionMarshallerTest {

	@Test
	public void test() {

		String source = "<CommandSequence><alarm sender=\"WBM\" sound=\"mysound.wav\" talk=\"a text to play\">a text to display</alarm></CommandSequence>";
		ActionMarshaller instance = new ActionMarshaller();
		Alarm a = instance.parseInput2(source);

		Assert.assertNotNull(a);
		Assert.assertEquals("a text to play", a.getTalk());
		Assert.assertEquals("a text to display", a.getText());
		Assert.assertEquals("WBM", a.getSender());
		Assert.assertEquals("mysound.wav", a.getSound());

	}

}