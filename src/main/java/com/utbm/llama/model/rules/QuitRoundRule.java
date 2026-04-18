package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;
import main.java.com.utbm.llama.model.Player;
import main.java.com.utbm.llama.model.enums.MoveType;
import main.java.com.utbm.llama.model.enums.State;

import java.util.Objects;

public class QuitRoundRule implements Rule{
	@Override
	public boolean isApplicable(Move move, Game game){

		Objects.requireNonNull(move, "move cannot be null");
		return move.getType() == MoveType.QUIT_ROUND;
	}

	@Override
	public boolean validate(Move move, Game game){

		Objects.requireNonNull(move, "move cannot be null");

		Player player = move.getPlayer();
		Objects.requireNonNull(player, "move.player cannot be null");
		return player.getState() != State.QUITTING;

	}

	@Override
	public void apply(Move move, Game game) {

	}
}
