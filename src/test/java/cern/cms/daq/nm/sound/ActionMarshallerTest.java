package cern.cms.daq.nm.sound;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 *
 */
public class ActionMarshallerTest {

	@Test
	public void oneActionInCommandSequenceWrapperTest() {

		String source = "<CommandSequence><alarm sender=\"WBM\" sound=\"mysound.wav\" talk=\"a text to play\">a text to display</alarm></CommandSequence>";
		ActionMarshaller instance = new ActionMarshaller();
		Alarm a = instance.parseInput(source).iterator().next();

		Assert.assertNotNull(a);
		Assert.assertEquals("a text to play", a.getTalk());
		Assert.assertEquals("a text to display", a.getText());
		Assert.assertEquals("WBM", a.getSender());
		Assert.assertEquals("mysound.wav", a.getSound());

	}

	@Test
	public void oneActionWithoutCommandSequenceWrapperTest() {

		String source = "<alarm sender=\"WBM\" sound=\"mysound.wav\" talk=\"a text to play\">a text to display</alarm>";
		ActionMarshaller instance = new ActionMarshaller();
		Alarm a = instance.parseInput(source).iterator().next();

		Assert.assertNotNull(a);
		Assert.assertEquals("a text to play", a.getTalk());
		Assert.assertEquals("a text to display", a.getText());
		Assert.assertEquals("WBM", a.getSender());
		Assert.assertEquals("mysound.wav", a.getSound());

	}

	@Test
	public void multipleActionInCommandSequenceWrapperTest() {

		String source = "<CommandSequence><alarm sender=\"WBM\" sound=\"mysound.wav\" talk=\"a text to play\">a text to display</alarm><alarm sender=\"WBM\" sound=\"mysound2.wav\" talk=\"a 2nd text to play\">a 2nd text to display</alarm></CommandSequence>";
		ActionMarshaller instance = new ActionMarshaller();
		Iterator<Alarm> it = instance.parseInput(source).iterator();
		Alarm a = it.next();
		Assert.assertNotNull(a);
		Assert.assertEquals("a text to play", a.getTalk());
		Assert.assertEquals("a text to display", a.getText());
		Assert.assertEquals("WBM", a.getSender());
		Assert.assertEquals("mysound.wav", a.getSound());

		a = it.next();
		Assert.assertNotNull(a);
		Assert.assertEquals("a 2nd text to play", a.getTalk());
		Assert.assertEquals("a 2nd text to display", a.getText());
		Assert.assertEquals("WBM", a.getSender());
		Assert.assertEquals("mysound2.wav", a.getSound());
	}

}
