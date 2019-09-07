package cfartefact;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import cartago.AgentId;
import cartago.Artifact;
import cartago.GUARD;
import cartago.OPERATION;
import cartago.OperationException;
import cfartefact.structures.cfAgent;
import cfartefact.structures.cfCoalition;
import cfartefact.structures.cfCoalitionStructure;
import cfartefact.structures.cfConstraintBasic;
import cfartefact.structures.cfConstraintSize;
import cfartefact.structures.cfRule;
import cfartefact.structures.cfTask;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.parser.ParseException;

import cfartefact.algorithms.ICoalitionFormationArtifact;
import cfartefact.algorithms.clink.adapterClink;

public class CFArtefact extends Artifact {
	private final static String  DEFAULT_TYPE 	= "none";
	private final static String  FAKE_TYPE 		= "fake";

	private final Logger mLogger = Logger.getLogger(CFArtefact.class.getName());

	private final String obsRunning 				= "runningAlgorithm";
	private final String obsWaitingInput 			= "waitingForInputs";

	private boolean onlyOneCoalition; // from the coalition structure we pick the coalition with highest value 

	private int mNmbAgents, mNmbPconstraints, mNmbNconstraints, mNmbMCRules = 0;
	private List<String> mOwners = new ArrayList<String>();

	private cfCoalitionStructure 	mCoalitionStructure;
	private Set<String> 			mAgentsTypes			= new HashSet<String>();
//	private Set<cfAgent> 			mSetAgents 				= new HashSet<cfAgent>();
	private Set<cfConstraintBasic> 	mSetPositiveConstraints = new HashSet<cfConstraintBasic>();
	private Set<cfConstraintBasic> 	mSetNegativeConstraints = new HashSet<cfConstraintBasic>();
	private Set<cfConstraintSize> 	mSetSizeConstraints 	= new HashSet<cfConstraintSize>();
	private Set<cfRule> 			mSetRules 				= new HashSet<cfRule>();
	private Set<cfTask> 			mSetTasks				= new HashSet<cfTask>();

//	private BiMap<String, AgentId> agentIds;
	private BiMap<Integer, cfAgent> agentIds;

	private ICoalitionFormationArtifact iSolver;

	void init(String owner, String algorithm) {
		initialisation(owner, algorithm, false);
	}
	void init(String owner, String algorithm, boolean onlyOneCoalition) {
		initialisation(owner, algorithm, onlyOneCoalition);
	}
	private void initialisation(String owner, String algorithm, boolean onlyOneCoalition) {
		mLogger.info("Creating the Coalition Formation Artefact");

		this.onlyOneCoalition		= onlyOneCoalition;

		addOwner(owner);

		try {
			initialiseAlgorithm(algorithm);
		} catch (Exception e) {		
			e.printStackTrace();
			mLogger.info("Error in algorithm initialisation "+e.getMessage());
		}
		
//		agentIds = new ConcurrentHashMap<String, AgentId>();
		agentIds = HashBiMap.create();
		
		mLogger.info("The Coalition Formation Artefact was created");
	}
	
	@OPERATION
	void setupInputs(int nmbAgents, int nmbPconstraints, int nmbNconstraints, int nmbMCRules){	//no types
		mNmbAgents 			= nmbAgents;
		mNmbPconstraints 	= nmbPconstraints;
		mNmbNconstraints 	= nmbNconstraints;
		mNmbMCRules			= nmbMCRules;
	}
	
	@OPERATION
	void addAgentToSet(Object[] names){	//no types
		for(String s: Arrays.copyOf(names, names.length, String[].class))
			addAgent(s,"", new String[0]);
	}
	@OPERATION
	void addAgentToSet(String name,String type){
		addAgent(name, type, new String[0]);
	}
	@OPERATION
	void addAgentToSet(String name,String type,Object[] skills){
		addAgent(name, type, Arrays.copyOf(skills, skills.length, String[].class));
	}
	@OPERATION
	void removeAgentFromSet(String name){	
		cfAgent agent = new cfAgent(name, "");
		if (agentIds.containsKey(agent.hashCode()))
			agentIds.remove(agent.hashCode());
	}
	private void addAgent(String name, String type, String[] skills){
		cfAgent agent = new cfAgent(name, type);
		agent.addSkills(new ArrayList<String>(Arrays.asList(skills)));
		
		if (!agentIds.containsKey(agent.hashCode()))
			agentIds.put(agent.hashCode(), agent);	
	}
	
	@OPERATION
	void addTask(String taskName, Object[] requiredSkills){		
		cfTask tempTask = new cfTask(taskName, new ArrayList<>(Arrays.asList(Arrays.copyOf(requiredSkills, requiredSkills.length, String[].class))));
		this.mSetTasks.add(tempTask);
	}

	@OPERATION (guard="hasAllInputs")
	void runAlgorithm() {
		mLogger.info("Running...");
		defineObsProperty(obsRunning);


		try {
			cfCoalitionStructure cs = iSolver.solveCoalitionStructureGeneration(agentIds.values(),
																				mSetPositiveConstraints,
																				mSetNegativeConstraints,
																				mSetSizeConstraints,
																				mSetTasks,
																				mSetRules);
			updateCoalitionStructure(cs);
		} catch (Exception e) {
			mLogger.info("Exception: "+e.getMessage());
			e.printStackTrace();
		}

		removeObsProperty(obsRunning);
		removeObsProperty(obsWaitingInput);
		mLogger.info("The Constrained Coalition Formation Algorithm has finished");
	}
	@GUARD
	boolean hasAllInputs(){
		boolean ready = true;

		if (getObsProperty(obsWaitingInput) == null)
			defineObsProperty(obsWaitingInput);

		ready = 	(agentIds.size() >= mNmbAgents)
				& 	(mSetPositiveConstraints.size() >= mNmbPconstraints)
				& 	(mSetNegativeConstraints.size() >= mNmbNconstraints)
				& 	(mSetRules.size() >= mNmbMCRules);

		return ready;
	}

	private void updateCoalitionStructure(cfCoalitionStructure cs){
		Literal l;
		if (onlyOneCoalition) {
			if (cs != null) {
				cfCoalition c = cs.getValuableCoalition();
				l = convertCoalitionToLiteral(c);
			}
			else
				l = Literal.parseLiteral("coalition");
		} else {
			l = convertCoalitionStructureToLiteral(cs);
		}
//		defineObsProperty(l.getFunctor(), l.getTermsArray());

		signal(l.getFunctor(), l.getTermsArray()); // VER ISSO AQUI
	}
	private Literal convertCoalitionStructureToLiteral(cfCoalitionStructure cs)  {
		Literal l = Literal.parseLiteral("coalitionStructure");	
		if (cs != null) {
			try {
				StringBuilder s = new StringBuilder();
				s.append('[');
				for(cfCoalition c : cs.getCoalitions()) {
					s.append(convertCoalitionToLiteral(c).toString());
					s.append(',');
				}
				if (s.charAt(s.length()-1) == ',')
					s.deleteCharAt(s.length()-1);
				s.append(']');
				
				l.addTerm(ASSyntax.parseTerm(s.toString()));
			} catch (ParseException e) { e.printStackTrace(); }			
		}
		return l;
	}
	private Literal convertCoalitionToLiteral(cfCoalition c) {
		Literal l = Literal.parseLiteral("coalition");
		StringBuilder s = new StringBuilder();	
		s.append('[');
		for(String a : c.getAgentsName()) {
			s.append(a); 
			s.append(',');
		}
		if (s.charAt(s.length()-1) == ',')
			s.deleteCharAt(s.length()-1);
		s.append(']');
		try {
			l.addTerm(ASSyntax.parseTerm(c.getValue().toString()));
			l.addTerm(ASSyntax.parseTerm(s.toString()));
		} catch (ParseException e) { e.printStackTrace(); }	
		return l;
	}
	
	@OPERATION
	void putType(String type){
		mAgentsTypes.add(type);
	}
	@OPERATION
	void setPositiveConstraint(Object[] constraint){		
//		mSetPositiveConstraints.add(new cfConstraintBasic(agentIds.inverse().get(getCurrentOpAgentId()),Arrays.copyOf(constraint, constraint.length, String[].class)));
//		mSetPositiveConstraints.add(new cfConstraintBasic(agentIds.get(getCurrentOpAgentId()).getName(),Arrays.copyOf(constraint, constraint.length, String[].class)));
		mSetPositiveConstraints.add(new cfConstraintBasic(getCurrentOpAgentId().getAgentName(),Arrays.copyOf(constraint, constraint.length, String[].class)));
	}
	
	@OPERATION
	void setNegativeConstraint(Object[] constraint){
//		mSetNegativeConstraints.add(new cfConstraintBasic(agentIds.inverse().get(getCurrentOpAgentId()),Arrays.copyOf(constraint, constraint.length, String[].class)));
//		mSetNegativeConstraints.add(new cfConstraintBasic(agentIds.get(getCurrentOpAgentId()).getName(),Arrays.copyOf(constraint, constraint.length, String[].class)));
		mSetNegativeConstraints.add(new cfConstraintBasic(getCurrentOpAgentId().getAgentName(),Arrays.copyOf(constraint, constraint.length, String[].class)));
	}
	
	@OPERATION
	void setSizeConstraint(int size, String type){
//		mSetSizeConstraints.add(new cfConstraintSize(agentIds.inverse().get(getCurrentOpAgentId()),size, type));
//		if (agentIds.containsKey(getCurrentOpAgentId()))
//			mSetSizeConstraints.add(new cfConstraintSize(agentIds.get(getCurrentOpAgentId()).getName(),size, type));
//		else
//			mSetSizeConstraints.add(new cfConstraintSize("none",size, type));

		mSetSizeConstraints.add(new cfConstraintSize(getCurrentOpAgentId().getAgentName(),size, type));
	}
		
	@OPERATION
	void setMCRule(Object[] posRule, Object[] negRule, double value){	
		mLogger.info("Received contribution from "+getCurrentOpAgentId());
		cfRule rule = new cfRule(Arrays.copyOf(posRule, posRule.length, String[].class), 
								 Arrays.copyOf(negRule, negRule.length, String[].class), 
								 value);
		mSetRules.add(rule);
	}

	@OPERATION
	void clear() {
//		mSetAgents.clear();
		agentIds.clear();
		mSetPositiveConstraints.clear();
		mSetNegativeConstraints.clear();
		mSetRules.clear();
		mSetTasks.clear();

		updateCoalitionStructure(null);
	}

	@OPERATION 
	void remove(){
		clear();

		try {
			this.dispose(this.getId());
		} catch (OperationException e) {
			e.printStackTrace();
		}
	}

	private void checkPermission(AgentId id){
		if (mOwners.size() > 0){
			if (!mOwners.get(0).equals(id))
				failed("Permission denied");
		}
	}

	private void addOwner(String id){
		if (id.equals(""))
			return;
		if (!mOwners.contains(id))
			mOwners.add(id);
	}

	private void initialiseAlgorithm(String className) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException{		
		Class<?> clazz = Class.forName(className);
		Constructor<?> ctor = clazz.getConstructor();
//		iSolver = (ICoalitionFormationArtifact) ctor.newInstance();

//		iSolver = new CFSSadapter();
		this.iSolver = new adapterClink();
//		iSolver = new adapterDC();

		this.iSolver.initialization();
	}
}