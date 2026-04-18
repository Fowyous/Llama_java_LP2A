package main.java.com.utbm.llama.model.rules;

import java.util.Objects;

public class QuitRoundRule implements Rule{
	@Override
	public boolean isApplicable(Move move, Game game){

		if (move == null || game == null) return false;
		return move.getType() == MoveType.QUIT_ROUND;
	}

	@Override
	public boolean validate(Move move, Game game){

		Objects.requireNonNull(move, "move cannot be null");

		Player player = move.getPlayer();
		Objects.requireNonNull(player, "move.player cannot be null");
		return player.getState() != State.QUITTING;

	}
}
