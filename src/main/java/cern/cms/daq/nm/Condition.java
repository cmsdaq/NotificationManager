package cern.cms.daq.nm;

public enum Condition{

	// importance
	//FLAT TOP, INJECTION PHYSICS BEAM, CYCLING, 
	//STABLE BEAMS, SETUP, BEAM DUMP, ADJUST, 
	// PREPARE RAMP, RAMP DOWN, INJECTION PROBE BEAM, SQUEEZE, RAMP
	
	Importance_StableBeams("Stable beams", ""), 
	Importance_ShortToStableBeams("30 min. to stable beams", ""), 
	Importance_FlatTop("FLAT TOP",""),
	Importance_InjectionPhysicsBeam("INJECTION PHYSICS BEAM",""),
	Importance_Cycling("CYCLING",""),
	Importance_Setup("SETUP",""),
	Importance_BeamDump("BEAM DUMP",""),
	Importance_Adjust("ADJUST",""),
	Importance_PrepareRamp("PREPARE RAMP",""),
	Importance_RampDown("RAMP DOWN",""),
	Importance_InjectionProbeBeam("INJECTION PROBE BEAM",""),
	Importance_Squeeeze("SQUEEZE",""),
	Importance_Ramp("RAMP",""),
	Importance_All("Always", "Always"), 
	
	//shift condition
	Shift_MyShift("During my shift", "Only during my shift"),
	Shift_Always("Always", "Always"), 
	
	//time condition
	Time_Nighttime("Nighttime", ""), 
	Time_Daytime("Daytime", "");
	

	private Condition( String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	private final String name;
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	private final String description;
	
	
}
