package cern.cms.daq.nm.persistence;


/**
 * NOTE: do not change the order of this enums as ordinal number is used for db
 * mapping, add only in the end - sync with expert
 *
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
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

    PiDisconnected("PI Disconnected", 10014),
    PiProblem("PI Problem", 10014),
    FEDDisconnected("FED Disconnected", 10014),
    FMMProblem("FMM Problem", 10014),
    UnidentifiedFailure("Unidentified failure", 9000),

    FEROLFifoStuck("Ferol FIFO stuck", 10500),

    RuFailed("Ferol FIFO stuck", 9500),

    LinkProblem("Link problem", 10010),
    RuStuckWaiting("RU stuck waiting", 10010),
    RuStuck("RU stuck", 10010),
    RuStuckWaitingOther("RU stuck wating for other", 10010),
    HLTProblem("HLT problem", 10010),
    BugInFilterfarm("Bug in filter farm", 10010),
    OnlyFedStoppedSendingData("The only FEE stopped sending data", 10010),
    OutOfSequenceData("Out of sequence data received", 10010),
    CorruptedData("Corrupted data received", 10010),

    RateTooHigh("Rate too high", 10501),

    ContinousSoftError("Continuous soft error", 1010),
    StuckAfterSoftError("Stuck after soft error", 1011),
    LengthyFixingSoftError("Lengthy fixing sof terror", 1012),;

    private final String displayedName;

    /**
     * This parameter will be used to determine dominant event if multiple
     * events of the same priority arrives. Sound system will then play the one
     * with higher usefulness. e.g. No rate when expected and FC6 both with
     * Important priority arrives at the some time. Which one to play? FC6 as it
     * indicates that system has found the solution - not only found fault
     * <p>
     * NOTE: try to not assign the some usefulness to multiple LM. Otherwise
     * determining dominant event for events with same priority and usefulness
     * will be based on alphabetical order
     * <p>
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
