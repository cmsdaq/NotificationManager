package cern.cms.daq.nm.persistence;


/**
 * NOTE: do not change the order of this enums as ordinal number is used for db
 * mapping, add only in the end - sync with expert
 * 
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 *
 */
public enum LogicModuleView {
	NoRate("No rate", 10),
	RateOutOfRange("Rate out of range", 9),
	BeamActive("Beam active"),
	RunOngoing("Run ongoing", 100),
	ExpectedRate("Expected rate"),
	Transition("Short transition"),
	LongTransition("Long transition"),
	WarningInSubsystem("Warning in subsystem", 1004),
	SubsystemRunningDegraded("Running degraded", 1006),
	SubsystemError("Subsystem error", 1007),
	SubsystemSoftError("Subsystem soft error", 1005),
	FEDDeadtime("FED deadtime", 1005),
	PartitionDeadtime("Partition deadtime", 1008),
	StableBeams("Stable beams"),
	NoRateWhenExpected("No rate when expected", 104),
	Downtime("Downtime"),
	Deadtime("Deadtime"),
	CriticalDeadtime("Deadtime during run", 105),
	FlowchartCase1("Out of sequence data", 10004),
	FlowchartCase2("Corrupted data", 10005),
	FlowchartCase3("Partition problem", 10006),
	FlowchartCase4("FC4 (deprecated)", 10007),
	FlowchartCase5("Fed stuck", 10008),
	FlowchartCase6("Backpressure", 10009),
	SessionComparator("Session", 15),
	LHCBeamModeComparator("LHC Beam Mode", 20),
	LHCMachineModeComparator("LHC Machine Mode", 21),
	RunComparator("Run", 14),
	LevelZeroStateComparator("Level Zero State", 13),
	TCDSStateComparator("TCDS State", 12),
	DAQStateComparator("DAQ state", 11),
	PiDisconnected(  "PI Disconnected", 10014),
	PiProblem(  "PI Problem", 10014),
	FEDDisconnected(  "FED Disconnected", 10014),
	FMMProblem(  "FMM Problem", 10014),
	UnidentifiedFailure("Unidentified failure", 10000),
	FEROLFifoStuck("Ferol FIFO stuck", 10500),
	RateTooHigh("Rate too high", 10501),
	;

	private final String displayedName;

	/**
	 * 
	 * This parameter will be used to determine dominant event if multiple
	 * events of the same priority arrives. Sound system will then play the one
	 * with higher usefulness. e.g. No rate when expected and FC6 both with
	 * Important priority arrives at the some time. Which one to play? FC6 as it
	 * indicates that system has found the solution - not only found fault
	 * 
	 * NOTE: try to not assign the some usefulness to multiple LM. Otherwise
	 * determining dominant event for events with same priority and usefulness
	 * will be based on alphabetical order
	 * 
	 * <code><pre>
	 * 1-9			not useful for shifter, not interesting, some internal changes
	 * 10-99		not useful, maybe interesting for shifter
	 * 100-999		catch attention before something goes wrong
	 * 1000-9999	useful, maybe not full solutions but some useful context information 
	 * 10000-99999	very useful, e.g. diagnosis, ready suggestions 
	 * </pre></code>
	 */
	private final int usefulness;

	private LogicModuleView(String name) {
		this(name, 1);
	}

	private LogicModuleView(String name, int usefulness) {
		this.displayedName = name;
		this.usefulness = usefulness;
	}

	public String getDisplayedName() {
		return displayedName;
	}

	public int getUsefulness() {
		return usefulness;
	}

}
