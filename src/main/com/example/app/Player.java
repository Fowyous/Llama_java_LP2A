import java.util.*;
class Player {
	public enum CardType {
		ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN
	}//these  card types are temporary (to be changed).


	private List<CardType> hand = new ArrayList<>();


	/**
	 * Adds a card to the hand.
	 * @param card the card to add.
	 * @return true if card successfully added.
	 * @throws todo
	 */
	public boolean addCard(CardType card){
		return Hand.add(card);
	}

	/**
	 * Removes the first occurrance of a card from hand.
	 * @param card the card to remove.
	 * @return true if the hand contains the specific card.
	 * @throws todo
	 */
	public boolean removeCard(CardType card){
		return Hand.remove(card);
	}

	/**
	 * removes the card at a specific position.
	 * @param index the index of the card to remove.
	 * @return the removed card.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public CardType removeCard(int index){
		return Hand.remove(index);
	}

	/**
	 * returns the score
	 * @return the score. (not implemented yet).
	 */
	int getScore(){
		//todo : implement scoring logic
		return 0;
	}
}
