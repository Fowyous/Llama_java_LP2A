package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.rules.*;

import java.util.ArrayList;
import java.util.List;

/**
 * the rule engine checks wether all rules apply and applies them. check the commenting of each rule for more information.
 */
public class RuleEngine {

	List<Rule> rules = new ArrayList<>();
	//constructor: we will define all the rules here.
	public RuleEngine(){

		rules.add(new PlayCardRule());
		rules.add(new DrawCardRule());
		rules.add(new QuitRoundRule());
	}

	/**
	 * checks wether the move violates the rules of the game 
	 * @param move the move that is going to be played.
	 * @param game the current game.
	 * @return true if the move doesn't violate the rules of the game.
	 */
	public boolean validateMove(Move move, Game game){
		for (Rule rule : rules){
			if (rule.isApplicable(move, game)){
				if (!rule.validate(move, game)){
					return false;
				}
			}
		}
		return true;

	}
}
