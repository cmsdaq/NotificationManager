package cern.cms.daq.nm.task;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import cern.cms.daq.nm.persistence.DummyUser;

@Ignore
public class UserShiftTest {

	static Calendar now;

	private static UserShift instance;
	private static DummyUser du;

	public static int correctionAfter = 0;

	public static int correctionBefore = 0;

	@BeforeClass
	public static void a() {

		EntityManagerFactory factory2 = Persistence.createEntityManagerFactory("shifts");
		instance = new UserShift2Stub(factory2);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2015);
		cal.set(Calendar.MONTH, 8);
		cal.set(Calendar.DAY_OF_MONTH, 7);
		cal.set(Calendar.HOUR_OF_DAY, 13);
		cal.set(Calendar.MINUTE, 37);
		cal.set(Calendar.SECOND, 0);
		now = cal;
		du = new DummyUser();
		du.setId(448336L);
	}

	@Test
	public void myShiftTest() {

		now.set(Calendar.DAY_OF_MONTH, 7);
		now.set(Calendar.HOUR_OF_DAY, 1);
		assertFalse(instance.isShiftOn(du));

		now.set(Calendar.DAY_OF_MONTH, 14);
		now.set(Calendar.HOUR_OF_DAY, 23);
		assertFalse(instance.isShiftOn(du));
	}

	/**
	 * when before shift correction > 0
	 */
	@Test
	public void positiveBeforeShiftCorrectionTest() {

		now.set(Calendar.DAY_OF_MONTH, 7);
		now.set(Calendar.HOUR_OF_DAY, 13);
		correctionBefore = 2;

		// during shift
		assertTrue(instance.isShiftOn(du));

		// before shift in correction
		now.set(Calendar.HOUR_OF_DAY, 11);
		assertTrue(instance.isShiftOn(du));

		// before shift outsite correction
		now.set(Calendar.HOUR_OF_DAY, 8);
		assertFalse(instance.isShiftOn(du));
	}

	/**
	 * when before shift correction < 0
	 */
	@Test
	public void negativeBeforeShiftCorrectionTest() {

		now.set(Calendar.DAY_OF_MONTH, 7);
		now.set(Calendar.HOUR_OF_DAY, 16);
		correctionBefore = -2;

		// during shift
		assertTrue(instance.isShiftOn(du));

		// before shift inside correction
		now.set(Calendar.HOUR_OF_DAY, 15);
		assertTrue(instance.isShiftOn(du));

		// before shift outsite correction
		now.set(Calendar.HOUR_OF_DAY, 13);
		assertFalse(instance.isShiftOn(du));
	}

	/**
	 * when before shift correction = 0
	 */
	@Test
	public void neutralBeforeShiftCorrectionTest() {

		now.set(Calendar.DAY_OF_MONTH, 7);
		now.set(Calendar.HOUR_OF_DAY, 16);
		correctionBefore = 0;

		// during shift
		assertTrue(instance.isShiftOn(du));

		// before shift
		now.set(Calendar.HOUR_OF_DAY, 11);
		assertFalse(instance.isShiftOn(du));
	}

	/**
	 * when after shift correction = 0
	 */
	@Test
	public void neutralAfterShiftCorrectionTest() {
		now.set(Calendar.DAY_OF_MONTH, 14);
		now.set(Calendar.HOUR_OF_DAY, 10);
		correctionAfter = 0;

		// during shift
		assertTrue(instance.isShiftOn(du));

		// after shift
		now.set(Calendar.HOUR_OF_DAY, 13);
		assertFalse(instance.isShiftOn(du));
	}

	/**
	 * when after shift correction > 0
	 */
	@Test
	public void positiveAfterShiftCorrectionTest() {
		now.set(Calendar.DAY_OF_MONTH, 14);
		now.set(Calendar.HOUR_OF_DAY, 10);
		correctionAfter = 2;

		// during shift
		assertTrue(instance.isShiftOn(du));

		// after shift inside correction
		now.set(Calendar.HOUR_OF_DAY, 13);
		assertTrue(instance.isShiftOn(du));

		// after shift outside correction
		now.set(Calendar.HOUR_OF_DAY, 15);
		assertFalse(instance.isShiftOn(du));
	}
	
	/**
	 * when after shift correction < 0
	 */
	@Test
	public void negativeAfterShiftCorrectionTest() {
		now.set(Calendar.DAY_OF_MONTH, 14);
		now.set(Calendar.HOUR_OF_DAY, 8);
		correctionAfter = -2;

		// during shift
		assertTrue(instance.isShiftOn(du));

		// after shift inside correction
		now.set(Calendar.HOUR_OF_DAY, 9);
		assertTrue(instance.isShiftOn(du));

		// after shift outside correction
		now.set(Calendar.HOUR_OF_DAY, 11);
		assertFalse(instance.isShiftOn(du));
	}

}

class UserShift2Stub extends UserShift {

	public UserShift2Stub(EntityManagerFactory shiftEMF) {
		super(shiftEMF);
	}

	@Override
	protected Date getNow() {
		Date now = UserShiftTest.now.getTime();
		System.out.println("Pretending is now: " + now);
		return now;
	}

	@Override
	protected int getCorrectionAfter() {
		return UserShiftTest.correctionAfter;
	}

	@Override
	protected int getCorrectionBefore() {
		return UserShiftTest.correctionBefore;
	}

}