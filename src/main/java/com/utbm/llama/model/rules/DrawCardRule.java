package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.Deck;
import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;
import main.java.com.utbm.llama.model.Player;
import main.java.com.utbm.llama.model.enums.MoveType;
import main.java.com.utbm.llama.model.enums.State;

import java.util.Objects;

public class DrawCardRule implements Rule{
	@Override
	public boolean isApplicable(Move move, Game game){
	
		Objects.requireNonNull(move, "move cannot be null");
		return move.getType() == MoveType.DRAW_CARD;

	}

	/**
	 * {@inheritDoc}     
	 * <p>
	 * This rule is valid only if the draw pile is not empty.
	 */
	@Override
	public boolean validate(Move move, Game game){

		Objects.requireNonNull(game, "game cannot be null");

		Objects.requireNonNull(move, "move cannot be null");

		Player player = move.getPlayer();
		if (player.getState() == State.QUITTING){
			return false;
		}
		Deck drawPile = game.getDrawPile();
		return drawPile != null && !drawPile.isEmpty();
	}

	@Override
	public void apply(Move move, Game game) {

	}
}
