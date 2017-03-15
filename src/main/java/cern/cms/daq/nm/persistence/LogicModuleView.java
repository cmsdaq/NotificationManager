package cern.cms.daq.nm.persistence;

public enum LogicModuleView {
	NoRate("No rate"),
	RateOutOfRange("Rate out of range"),
	BeamActive("Beam active"),
	RunOngoing("Run ongoing"),
	ExpectedRate("Expected rate"),
	Transition("Short transition"),
	LongTransition("Long transition"),
	WarningInSubsystem("Warning in subsystem"),
	SubsystemRunningDegraded("Running degraded"),
	SubsystemError("Subsystem error"),
	SubsystemSoftError("Subsystem soft error"),
	FEDDeadtime("FED deadtime"),
	PartitionDeadtime("Partition deadtime"),
	StableBeams("Stable beams"),
	NoRateWhenExpected("No rate when expected"),
	Downtime("Downtime"),
	Deadtime("Deadtime"),
	CriticalDeadtime("Critical deadtime"),
	FlowchartCase1("FC1"),
	FlowchartCase2("FC2"),
	FlowchartCase3("FC3"),
	FlowchartCase4("FC4"),
	FlowchartCase5("FC5"),
	FlowchartCase6("FC6"),
	SessionComparator("Session"),
	LHCBeamModeComparator("LHC Beam Mode"),
	LHCMachineModeComparator("LHC Machine Mode"),
	RunComparator("Run"),
	LevelZeroStateComparator("Level Zero State"),
	TCDSStateComparator("TCDS State"),
	DAQStateComparator("DAQ state");

	private final String displayedName;

	private LogicModuleView(String name) {
		this.displayedName = name;
	}

	public String getDisplayedName() {
		return displayedName;
	}
	

}
