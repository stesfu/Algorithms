/** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * File name  :  T3Player.java
 * Purpose    :  Implementing the selection mechanics of minimax and alpha-beta pruning algorithms
 * @author    :  Salem Tesfu 
 * Date       :  2020-02-28
 *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

package t3;

import java.util.*;

/**
 * Artificial Intelligence responsible for playing the game of T3! Implements
 * the alpha-beta-pruning mini-max search algorithm
 */
public class T3Player {

    /**
     * Workhorse of an AI T3Player's choice mechanics that, given a game state,
     * makes the optimal choice from that state as defined by the mechanics of the
     * game of Tic-Tac-Total. Note: In the event that multiple moves have
     * equivalently maximal minimax scores, ties are broken by move col, then row,
     * then move number in ascending order (see spec and unit tests for more info).
     * The agent will also always take an immediately winning move over a delayed
     * one (e.g., 2 moves in the future).
     * 
     * @param state The state from which the T3Player is making a move decision.
     * @return The T3Player's optimal action.
     */
    public T3Action choose(T3State state) {
        return alphaBeta(state, Integer.MIN_VALUE, Integer.MAX_VALUE, true).action;
    }

    /**
     * Performs alpha-beta pruning to ensure sub optimal actions are not taken
     * 
     * @param state  The state from which the T3Player is making a move decision.
     * @param alpha  int best value guaranteed for the max
     * @param beta   int best value guaranteed for the min
     * @param player boolean specifies whether or not the player is the maximizing
     *               one
     * @return tuple that contains the optimal action that led there and an int
     *         specifying win (1), tie(0), loss(-1)
     */
    private MiniMaxTuple alphaBeta(T3State state, int alpha, int beta, boolean player) {

        if (state.isWin() && (!player)) {
            return new MiniMaxTuple(null, -1);
        }

        else if (state.isTie()) {
            return new MiniMaxTuple(null, 0);
        }

        else if (state.isWin() && player) {
            return new MiniMaxTuple(null, 1);
        } else {
            MiniMaxTuple v = new MiniMaxTuple(null, 0);
            if (player) {
                v.score = Integer.MIN_VALUE;

                for (Map.Entry<T3Action, T3State> entry : state.getTransitions().entrySet()) {

                    MiniMaxTuple compareMiniMax = alphaBeta(entry.getValue(), alpha, beta, false);

                    compareMiniMax.action = entry.getKey();

                    if (state.getNextState(compareMiniMax.action).isWin()) {
                        compareMiniMax.score = 1;
                        return compareMiniMax;
                    }

                    if (v.score < compareMiniMax.score) {
                        v.action = compareMiniMax.action;
                        v.score = compareMiniMax.score;
                    }

                    alpha = Math.max(alpha, v.score);
                    if (beta <= alpha) {
                        break;
                    }
                }
                return v;

            } else {
                v.score = Integer.MAX_VALUE;
                for (Map.Entry<T3Action, T3State> entry : state.getTransitions().entrySet()) {
                    MiniMaxTuple compareMiniMax = alphaBeta(entry.getValue(), alpha, beta, true);
                    compareMiniMax.action = entry.getKey();

                    if (state.getNextState(compareMiniMax.action).isWin()) {
                        compareMiniMax.score = -1;
                        return compareMiniMax;
                    }
                    if (v.score > compareMiniMax.score || state.getNextState(compareMiniMax.action).isWin()) {
                        v.action = compareMiniMax.action;
                        v.score = compareMiniMax.score;
                    }

                    beta = Math.min(beta, v.score);
                    if (beta <= alpha) {
                        break;
                    }
                }
                return v;
            }

        }

    }

    /**
     * Tuple used to keep track of the action and score of all the possible moves
     */
    private class MiniMaxTuple {
        int score;
        T3Action action;

        /**
         * Constructs a tuple to be used to compare in the alpha beta pruning method
         * 
         * @param T3Action action that *led to* this move
         * @param int score used to prioritize moves for win, ties, and losses
         */
        MiniMaxTuple(T3Action action, int score) {
            this.action = action;
            this.score = score;
        }

    }

}
