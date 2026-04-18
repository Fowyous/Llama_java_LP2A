package main.java.com.utbm.llama.model.rules;

public class DrawCardRule {
	public boolean isApplicable(Move move, Game game){
		if (move == null || game == null) return false;
		
		return move.getType() == MoveType.DRAW_CARD;

	}

	/**
	 * {@inheritDoc}     
	 * <p>
	 * This rule is valid only if the draw pile is not empty.
	 */
	public boolean validate(Move move, Game game){

		if (move == null || game == null) return false;
		Deck drawPile = game.getDrawPile();
		return drawPile != null && !drawPile.isEmpty();
	}
}
