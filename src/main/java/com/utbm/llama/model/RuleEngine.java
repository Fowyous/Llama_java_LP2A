package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.rules.*;

public class RuleEngine {

	List<Rule> rules = new ArrayList<>();
	//constructor: we will define all the rules here.
	public RuleEngine(){

		rules.add(new PlayCardRule());
		rules.add(new DrawCardRule());
		rules.add(new QuitRoundRule());
	}
}
