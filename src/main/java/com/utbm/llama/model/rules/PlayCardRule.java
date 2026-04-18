package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.enums.CardType;

import java.util.Map;
import java.util.Objects;

/**
 * this is the rule where we determine what card the player is allowed to play depending on the card on
 * top of the discard pile. For example if the discard pile has a one the player can only play a two.
 */

public class PlayCardRule implements Rule {

	//this map contains on the left the card type of the top card and on the right the card type of the card that we can play and doesn't violate the rules of the game.
	private static final Map<CardType, CardType> VALID_NEXT_CARDS = Map.ofEntries(
			Map.entry(CardType.ONE, CardType.TWO),    
			Map.entry(CardType.TWO, CardType.THREE),   
			Map.entry(CardType.THREE, CardType.FOUR),    
			Map.entry(CardType.FOUR, CardType.FIVE),   
			Map.entry(CardType.FIVE, CardType.SIX),    
			Map.entry(CardType.SIX, CardType.LLAMA),    
			Map.entry(CardType.LLAMA, CardType.ONE));



	@Override
	public boolean isApplicable(Move move, Game game){

		// if the move is not playing a card then it doesn't consern this rule.
		Objects.requireNonNull(move, "move cannot be null");
		Objects.requireNonNull(game, "game cannot be null");
		
		return move.getType() == MoveType.PLAY_CARD;

	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * if the card played is one number above the one on top of the draw pile then the rule is respected. If the card on top of the draw pile is a Llama we return true if the card played is a one.
	 * the discard pile should not be empty before this move is played
	 */
	@Override
	public boolean validate(Move move, Game game){
		Objects.requireNonNull(move, "move cannot be null");
		Objects.requireNonNull(game, "game cannot be null");
		CardType topCard = game.getDiscardPile().peek();

		if (topCard == null) {    
			throw new IllegalStateException("Discard pile is empty");
		}
			// we verify wether the card on top of the pile and the card in the move are applicable.

		CardType allowed = VALID_NEXT_CARDS.get(topCard);        
		return allowed != null && move.getCard() == allowed;

	}
}
