package cern.cms.daq.nm;

import java.util.Arrays;
import java.util.List;

public class Constants {

	// configuration from
	// http://www.forumsys.com/tutorials/integration-how-to/ldap/online-ldap-test-server/
	public static final String testLDAP = "ldap://ldap.forumsys.com:389";
	public static final String testDC = "dc=example,dc=com";

	// configuration from
	// https://espace.cern.ch/mmmservices-help/AccessingYourMailbox/Pages/default.aspx
	public final static String cernLDAP = "ldap://ldap.cern.ch:636";
	public final static String cernDC = "ou=users,o=cern,c=ch";

	public static String password = "password";

	public static List<Condition> importanceConditionsList = Arrays.asList(
			/*Condition.Importance_Adjust, */
			Condition.Importance_All /*, Condition.Importance_BeamDump, Condition.Importance_Cycling,
			Condition.Importance_FlatTop, Condition.Importance_InjectionPhysicsBeam,
			Condition.Importance_InjectionProbeBeam, Condition.Importance_PrepareRamp, Condition.Importance_Ramp,
			Condition.Importance_RampDown, Condition.Importance_Setup, Condition.Importance_ShortToStableBeams,
			Condition.Importance_Squeeeze, Condition.Importance_StableBeams*/);

	public static List<Condition> shiftConditionsList = Arrays.asList(Condition.Shift_MyShift, Condition.Shift_Always);

	public static List<Condition> timeConditionsList = Arrays.asList(Condition.Time_Daytime, Condition.Time_Nighttime);
}
