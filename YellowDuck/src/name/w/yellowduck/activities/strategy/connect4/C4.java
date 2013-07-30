/* 
 *
 * Copyright (C) 2013 The PlayTractor Team (support@playtractor.com)
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, see <http://www.gnu.org/licenses/>.
 */


package name.w.yellowduck.activities.strategy.connect4;

import java.util.Random;

public class C4 extends Object {

	private final int C4_NONE      =2;
	public static final int C4_MAX_LEVEL =20;
	
	private class GameState extends Object {
		private char board[][];           /* The board configuration of the game state.  */
	    /* board[x][y] specifies the position of the   */
	    /* xth column and the yth row of the board,    */
	    /* where column and row numbering starts at 0. */
	    /* (The 0th row is the bottom row.)            */
	    /* A value of 0 specifies that the position is */
	    /* occupied by a piece owned by player 0, a    */
	    /* value of 1 specifies that the position is   */
	    /* occupied by a piece owned by player 1, and  */
	    /* a value of C4_NONE specifies that the       */
	    /* position is unoccupied.                     */
    
	    private int score_array[][];  /* An array specifying statistics on both      */
	    /* players.  score_array[0] specifies the      */
	    /* statistics for player 0, while              */
	    /* score_array[1] specifies the statistics for */
	    /* player 1.                                   */
	    
	    int score[]=new int[2];           /* The actual scores of each player, deducible */
	    /* from score_array, but kept separately for   */
	    /* efficiency.  The score of player x is the   */
	    /* sum of score_array[x].  A score is          */
	    /* basically a function of how many winning    */
	    /* positions are still available to the        */
	    /* and how close he/she is to achieving each   */
	    /* of these positions.                         */
	    
	    private short winner;       /* The winner of the game - either 0, 1 or     */
	    /* C4_NONE.  Deducible from score_array, but   */
	    /* kept separately for efficiency.             */
	    
	    private int num_of_pieces;      /* The number of pieces currently occupying    */
	    /* board spaces.  Deducible from board, but    */
	    /* kept separately for efficiency.             */
	    
	};
		
    private int size_x, size_y, num_to_connect;
    int win_places;
    
    int map[][][];  /* map[x][y] is an array of win place indices, */
    /* terminated by a -1.                         */
    
    private int magic_win_number;
    private boolean game_in_progress, move_in_progress;
    //private long poll_interval, next_poll;
    private GameState state_stack[]=new GameState[C4_MAX_LEVEL+1];
    private int current_state;
    private int depth;
    private int states_allocated;
    private int drop_order[];
	
    private Random random;
	/****************************************************************************/
	/**                                                                        **/
	/**  This function sets up a new game.  This must be called exactly once   **/
	/**  before each game is started.  Before it can be called a second time,  **/
	/**  end_game() must be called to destroy the previous game.               **/
	/**                                                                        **/
	/**  width and height are the desired dimensions of the game board, while  **/
	/**  num is the number of pieces required to connect in a row in order to  **/
	/**  win the game.                                                         **/
	/**                                                                        **/
	/****************************************************************************/
	
    public C4() {
    	super();
    	for (int i = 0;i < state_stack.length; ++i) {
    		state_stack[i]=new GameState();
    	}
    	this.random=new Random();
    }
	public void c4_new_game(int width, int height, int num) {
	    int i, j, k, x;
	    int win_index, column;
	    int win_indices[];
	
	    //assert(!game_in_progress);
	    //assert(width >= 1 && height >= 1 && num >= 1);
	
	    size_x = width;
	    size_y = height;
	    num_to_connect = num;
	    magic_win_number = 1 << num_to_connect;
	    win_places = this.num_of_win_places(size_x, size_y, num_to_connect);
	
	    /* Set up the board */
	
	    depth = 0;
	    current_state = 0;
	
	    state_stack[current_state].board = new char[size_x][size_y];
	    for (i=0; i<size_x; i++) {
	        for (j=0; j<size_y; j++)
	            state_stack[current_state].board[i][j] = C4_NONE;
	    }
	
	    /* Set up the score array */
	
	    state_stack[current_state].score_array=new int[2][win_places];
	    for (i=0; i<win_places; i++) {
	        state_stack[current_state].score_array[0][i] = 1;
	        state_stack[current_state].score_array[1][i] = 1;
	    }
	
	    state_stack[current_state].score[0] = state_stack[current_state].score[1] = win_places;
	    state_stack[current_state].winner = C4_NONE;
	    state_stack[current_state].num_of_pieces = 0;
	
	    states_allocated = 1;
	
	    /* Set up the map */
	
	    map = new int[size_x][size_y][num_to_connect*4 + 1];
	    for (i=0; i<size_x; i++) {
	        for (j=0; j<size_y; j++) {
	            map[i][j][0] = -1;
	        }
	    }
	
	    win_index = 0;
	
	    /* Fill in the horizontal win positions */
	    for (i=0; i<size_y; i++)
	        for (j=0; j<size_x-num_to_connect+1; j++) {
	            for (k=0; k<num_to_connect; k++) {
	                win_indices = map[j+k][i];
	                for (x=0; win_indices[x] != -1; x++)
	                    ;
	                win_indices[x++] = win_index;
	                win_indices[x] = -1;
	            }
	            win_index++;
	        }
	
	    /* Fill in the vertical win positions */
	    for (i=0; i<size_x; i++)
	        for (j=0; j<size_y-num_to_connect+1; j++) {
	            for (k=0; k<num_to_connect; k++) {
	                win_indices = map[i][j+k];
	                for (x=0; win_indices[x] != -1; x++)
	                    ;
	                win_indices[x++] = win_index;
	                win_indices[x] = -1;
	            }
	            win_index++;
	        }
	
	    /* Fill in the forward diagonal win positions */
	    for (i=0; i<size_y-num_to_connect+1; i++)
	        for (j=0; j<size_x-num_to_connect+1; j++) {
	            for (k=0; k<num_to_connect; k++) {
	                win_indices = map[j+k][i+k];
	                for (x=0; win_indices[x] != -1; x++)
	                    ;
	                win_indices[x++] = win_index;
	                win_indices[x] = -1;
	            }
	            win_index++;
	        }
	
	    /* Fill in the backward diagonal win positions */
	    for (i=0; i<size_y-num_to_connect+1; i++)
	        for (j=size_x-1; j>=num_to_connect-1; j--) {
	            for (k=0; k<num_to_connect; k++) {
	                win_indices = map[j-k][i+k];
	                for (x=0; win_indices[x] != -1; x++)
	                    ;
	                win_indices[x++] = win_index;
	                win_indices[x] = -1;
	            }
	            win_index++;
	        }
	
	    /* Set up the order in which automatic moves should be tried. */
	    /* The columns nearer to the center of the board are usually  */
	    /* better tactically and are more likely to lead to a win.    */
	    /* By ordering the search such that the central columns are   */
	    /* tried first, alpha-beta cutoff is much more effective.     */
	
	    drop_order = new int[size_x];
	    column = (size_x-1) / 2;
	    for (i=1; i<=size_x; i++) {
	        drop_order[i-1] = column;
	        column += (((i%2)!=0)? i : -i);
	    }
	
	    game_in_progress = true;
	}
	
	
	/****************************************************************************/
	/**                                                                        **/
	/**  This function drops a piece of the specified player into the          **/
	/**  specified column.  A value of TRUE is returned if the drop is         **/
	/**  successful, or FALSE otherwise.  A drop is unsuccessful if the        **/
	/**  specified column number is invalid or full.  If the drop is           **/
	/**  successful and row is a non-NULL pointer, the row where the piece     **/
	/**  ended up is returned through the row pointer.  Note that column and   **/
	/**  row numbering start at 0.                                             **/
	/**                                                                        **/
	/****************************************************************************/
	
	public boolean c4_make_move(int player, int column, int row[]) {
	    int result; 
	
	    //assert(game_in_progress);
	    //assert(!move_in_progress);
	
	    if (column >= size_x || column < 0)
	        return false;
	
	    result = this.drop_piece(this.real_player(player), column);
	    if (row!=null && result >= 0)
	        row[0] = result;
	    return (result >= 0);
	}
	
	
	/****************************************************************************/
	/**                                                                        **/
	/**  This function instructs the computer to make a move for the specified **/
	/**  player.  level specifies the number of levels deep the computer       **/
	/**  should search the game tree in order to make its decision.  This      **/
	/**  corresponds to the number of "moves" in the game, where each player's **/
	/**  turn is considered a move.  A value of TRUE is returned if a move was **/
	/**  made, or FALSE otherwise (i.e. if the board is full).  If a move was  **/
	/**  made, the column and row where the piece ended up is returned through **/
	/**  the column and row pointers (unless a pointer is NULL, in which case  **/
	/**  it won't be used to return any information).  Note that column and    **/
	/**  row numbering start at 0.  Also note that for a standard 7x6 game of  **/
	/**  Connect-4, the computer is brain-dead at levels of three or less,     **/
	/**  while at levels of four or more the computer provides a challenge.    **/
	/**                                                                        **/
	/****************************************************************************/
	public boolean c4_auto_move(int player, int level, int column[], int row[]) {
	    int i, best_column = -1, goodness = 0, best_worst = -(Integer.MAX_VALUE);
	    int num_of_equal = 0, real_player, current_column, result;
	
	    //assert(game_in_progress);
	    //assert(!move_in_progress);
	    //assert(level >= 1 && level <= C4_MAX_LEVEL);
	
	    real_player = this.real_player(player);
	
	    /* It has been proven that the best first move for a standard 7x6 game  */
	    /* of connect-4 is the center column.  See Victor Allis' masters thesis */
	    /* ("ftp://ftp.cs.vu.nl/pub/victor/connect4.ps") for this proof.        */
	
	    if (state_stack[current_state].num_of_pieces < 2 &&
	                        size_x == 7 && size_y == 6 && num_to_connect == 4 &&
	                        (state_stack[current_state].num_of_pieces == 0 ||
	                        		state_stack[current_state].board[3][0] != C4_NONE)) {
	        if (column!=null)
	            column[0] = 3;
	        if (row!=null)
	            row[0] = state_stack[current_state].num_of_pieces;
	        this.drop_piece(real_player,3);
	        return true;
	    }
	    
	    {
	        //user already put two consecutive pieces at the bottom line and no piece there
	        int other_player = this.other(player);
	        int x0=-1, x1=-1;
	        for (i = 0; i < size_x; ++i) {
	            if (state_stack[current_state].board[i][0] == other_player) {
	                x0=i;
	                break;
	            }
	        }
	        for (i = x0; i >= 0 && i < size_x; ++i) {
	            if (state_stack[current_state].board[i][0] != other_player) {
	                x1=i-1;
	                break;
	            }
	        }
	        if (x0 > 0 && x1-x0+1 >= 2 && x1 < size_x - 1) {
	            if (state_stack[current_state].board[x0-1][0] == C4_NONE && state_stack[current_state].board[x1+1][0] == C4_NONE) {
	                int _col=x0-1;
	                if (size_x - x1 > x0)
	                    _col=x1+1;
	                if (state_stack[current_state].board[_col][0] == C4_NONE) {
	                    int _row=this.drop_piece(real_player,_col);
	                    if (column!=null)
	                        column[0] = _col;
	                    if (row!=null)
	                        row[0] = _row;
	                    return true;
	                }
	            }
	        }
	    }
	
	    move_in_progress = true;
	
	    /* Simulate a drop in each of the columns and see what the results are. */
	
	    for (i=0; i<size_x; i++) {
	        this.push_state();
	        current_column = drop_order[i];
	
	        /* If this column is full, ignore it as a possibility. */
	        if (this.drop_piece(real_player, current_column) < 0) {
	            this.pop_state();
	            continue;
	        }
	
	        /* If this drop wins the game, take it! */
	        else if (state_stack[current_state].winner == real_player) {
	            best_column = current_column;
	            this.pop_state();
	            break;
	        }
	
	        /* Otherwise, look ahead to see how good this move may turn out */
	        /* to be (assuming the opponent makes the best moves possible). */
	        else {
	            //next_poll = clock() + poll_interval;
	            goodness =this.evaluate(real_player,level,-(Integer.MAX_VALUE),-best_worst);
	        }
	
	        /* If this move looks better than the ones previously considered, */
	        /* remember it.                                                   */
	        if (goodness > best_worst) {
	            best_worst = goodness;
	            best_column = current_column;
	            num_of_equal = 1;
	        }
	
	        /* If two moves are equally as good, make a random decision. */
	        else if (goodness == best_worst) {
	            num_of_equal++;
	            int nxt=this.random.nextInt();
	            if (nxt < 0)
	            	nxt=0-nxt;
	            nxt=nxt % 10000;
	            if (nxt < (int)(((float)1/(float)num_of_equal) * 10000))
	                best_column = current_column;
	        }
	
	        this.pop_state();
	    }
	
	    move_in_progress = false;
	
	    /* Drop the piece in the column decided upon. */
	
	    if (best_column >= 0) {
	        result = this.drop_piece(real_player, best_column);
	        if (column!=null)
	            column[0] = best_column;
	        if (row!=null)
	            row[0] = result;
	        return true;
	    }
	    else
	        return false;
	}
	
	
	/****************************************************************************/
	/**                                                                        **/
	/**  This function returns a two-dimensional array containing the state of **/
	/**  the game board.  Do not modify this array.  It is assumed that a game **/
	/**  is in progress.  The value of this array is dynamic and will change   **/
	/**  to reflect the state of the game as the game progresses.  It becomes  **/
	/**  and stays undefined when the game ends.                               **/
	/**                                                                        **/
	/**  The first dimension specifies the column number and the second        **/
	/**  dimension specifies the row number, where column and row numbering    **/
	/**  start at 0 and the bottow row is considered the 0th row.  A value of  **/
	/**  0 specifies that the position is occupied by a piece owned by player  **/
	/**  0, a value of 1 specifies that the position is occupied by a piece    **/
	/**  owned by player 1, and a value of C4_NONE specifies that the position **/
	/**  is unoccupied.                                                        **/
	/**                                                                        **/
	/****************************************************************************/
	
	public char[][] c4_board() {
	    //assert(game_in_progress);
	    return state_stack[current_state].board;
	}
	
	
	/****************************************************************************/
	/**                                                                        **/
	/**  This function returns the "score" of the specified player.  This      **/
	/**  score is a function of how many winning positions are still available **/
	/**  to the player and how close he/she is to achieving each of these      **/
	/**  positions.  The scores of both players can be compared to observe how **/
	/**  well they are doing relative to each other.                           **/
	/**                                                                        **/
	/****************************************************************************/
	public int c4_score_of_player(int player) {
	    //assert(game_in_progress);
	    return state_stack[current_state].score[this.real_player(player)];
	}
	
	
	/****************************************************************************/
	/**                                                                        **/
	/**  This function returns TRUE if the specified player has won the game,  **/
	/**  and FALSE otherwise.                                                  **/
	/**                                                                        **/
	/****************************************************************************/
	public boolean c4_is_winner(int player) {
	    //assert(game_in_progress);
	    return (state_stack[current_state].winner == this.real_player(player));
	}
	
	
	/****************************************************************************/
	/**                                                                        **/
	/**  This function returns TRUE if the board is completely full, and FALSE **/
	/**  otherwise.                                                            **/
	/**                                                                        **/
	/****************************************************************************/
	public boolean c4_is_tie() {
	    //assert(game_in_progress);
	    return (state_stack[current_state].num_of_pieces == size_x * size_y);
	}
	
	
	/****************************************************************************/
	/**                                                                        **/
	/**  This function returns the coordinates of the winning connections of   **/
	/**  the winning player.  It is assumed that a player has indeed won the   **/
	/**  game.  The coordinates are returned in x1, y1, x2, y2, where (x1, y1) **/
	/**  specifies the lower-left piece of the winning connection, and         **/
	/**  (x2, y2) specifies the upper-right piece of the winning connection.   **/
	/**  If more than one winning connection exists, only one will be          **/
	/**  returned.                                                             **/
	/**                                                                        **/
	/****************************************************************************/
	public void c4_win_coords(int coords[]) { //x1,y1,x2,y2
	    int i, j, k;
	    int winner, win_pos = 0;
	    boolean found;
	
	    //assert(game_in_progress);
	
	    winner = state_stack[current_state].winner;
	    //assert(winner != C4_NONE);
	
	    while (state_stack[current_state].score_array[winner][win_pos] != magic_win_number)
	        win_pos++;
	
	    /* Find the lower-left piece of the winning connection. */
	
	    found = false;
	    for (j=0; j<size_y && !found; j++)
	        for (i=0; i<size_x; i++)
	            for (k=0; map[i][j][k] != -1; k++)
	                if (map[i][j][k] == win_pos) {
	                	coords[0]=i; coords[1]=j;
	                    found = true;
	                    break;
	                }
	
	    /* Find the upper-right piece of the winning connection. */
	
	    found = false;
	    for (j=size_y-1; j>=0 && !found; j--)
	        for (i=size_x-1; i>=0; i--)
	            for (k=0; map[i][j][k] != -1; k++)
	                if (map[i][j][k] == win_pos) {
	                	coords[2]=i; coords[3]=j;
	                    found = true;
	                    break;
	                }
	}
	
	
	/****************************************************************************/
	/**                                                                        **/
	/**  This function ends the current game.  It is assumed that a game is    **/
	/**  in progress.  It is illegal to call any other game function           **/
	/**  immediately after this one except for c4_new_game(), c4_poll() and    **/
	/**  c4_reset().                                                           **/
	/**                                                                        **/
	/****************************************************************************/
	public void c4_end_game() {
	    int i, j;
	
	//    assert(game_in_progress);
	//    assert(!move_in_progress);
	
	    /* Free up the memory used by the map. */
	/*
	    for (i=0; i<size_x; i++) {
	        for (j=0; j<size_y; j++)
	            free(map[i][j]);
	        free(map[i]);
	    }
	    free(map);
	
	    /* Free up the memory of all the states used. */
	/*
	    for (i=0; i<states_allocated; i++) {
	        for (j=0; j<size_x; j++)
	            free(state_stack[i].board[j]);
	        free(state_stack[i].board);
	        free(state_stack[i].score_array[0]);
	        free(state_stack[i].score_array[1]);
	    }
	 */   
	    states_allocated = 0;
	
	    /* Free up the memory used by the drop_order array. */
	
	    //free(drop_order);
	
	    game_in_progress = false;
	}
	
	
	/****************************************************************************/
	/**                                                                        **/
	/**  This function resets the state of the algorithm to the starting state **/
	/**  (i.e., no game in progress and a NULL poll function).  There should   **/
	/**  no reason to call this function unless for some reason the calling    **/
	/**  algorithm loses track of the game state.  It is illegal to call any   **/
	/**  other game function immediately after this one except for             **/
	/**  c4_new_game(), c4_poll() and c4_reset().                              **/
	/**                                                                        **/
	/****************************************************************************/
	public void c4_reset() {
	    //assert(!move_in_progress);
	    if (game_in_progress)
	        this.c4_end_game();
	}
	
	/****************************************************************************/
	/****************************************************************************/
	/**                                                                        **/
	/**  The following functions are local to this file and should not be      **/
	/**  called externally.                                                    **/
	/**                                                                        **/
	/****************************************************************************/
	/****************************************************************************/
	
	
	/****************************************************************************/
	/**                                                                        **/
	/**  This function returns the number of possible win positions on a board **/
	/**  of dimensions x by y with n being the number of pieces required in a  **/
	/**  row in order to win.                                                  **/
	/**                                                                        **/
	/****************************************************************************/
	private int num_of_win_places(int x, int y, int n) {
	    if (x < n && y < n)
	        return 0;
	    else if (x < n)
	        return x * ((y-n)+1);
	    else if (y < n)
	        return y * ((x-n)+1);
	    else
	        return 4*x*y - 3*x*n - 3*y*n + 3*x + 3*y - 4*n + 2*n*n + 2;
	}
	
	
	/****************************************************************************/
	/**                                                                        **/
	/**  This function updates the score of the specified player in the        **/
	/**  context of the current state,  given that the player has just placed  **/
	/**  a game piece in column x, row y.                                      **/
	/**                                                                        **/
	/****************************************************************************/
	private void update_score(int player, int x, int y) {
	    int i;
	    int win_index;
	    int this_difference = 0, other_difference = 0;
	    int current_score_array[][] = state_stack[current_state].score_array;
	    int other_player = this.other(player);
	
	    for (i=0; map[x][y][i] != -1; i++) {
	        win_index = map[x][y][i];
	        this_difference += current_score_array[player][win_index];
	        other_difference += current_score_array[other_player][win_index];
	
	        current_score_array[player][win_index] <<= 1;
	        current_score_array[other_player][win_index] = 0;
	
	        if (current_score_array[player][win_index] == magic_win_number)
	            if (state_stack[current_state].winner == C4_NONE)
	            	state_stack[current_state].winner = (short)player;
	    }
	
	    state_stack[current_state].score[player] += this_difference;
	    state_stack[current_state].score[other_player] -= other_difference;
	}
	
	
	/****************************************************************************/
	/**                                                                        **/
	/**  This function drops a piece of the specified player into the          **/
	/**  specified column.  The row where the piece ended up is returned, or   **/
	/**  -1 if the drop was unsuccessful (i.e., the specified column is full). **/
	/**                                                                        **/
	/****************************************************************************/
	private int drop_piece(int player, int column) {
	    int y = 0;
	
	    while (state_stack[current_state].board[column][y] != C4_NONE && ++y < size_y)
	        ;
	
	    if (y == size_y)
	        return -1;
	
	    state_stack[current_state].board[column][y] = (char)player;
	    state_stack[current_state].num_of_pieces++;
	    this.update_score(player,column,y);
	
	    return y;
	}
	
	private void pop_state() {
		current_state = --depth;
	}
	
	/****************************************************************************/
	/**                                                                        **/
	/**  This function pushes the current state onto a stack.  popdir() is     **/
	/**  used to pop from this stack.                                          **/
	/**                                                                        **/
	/**  Technically what it does, since the current state is considered to    **/
	/**  be the top of the stack, is push a copy of the current state onto     **/
	/**  the stack right above it.  The stack pointer (depth) is then          **/
	/**  incremented so that the new copy is considered to be the current      **/
	/**  state.  That way, all pop_state() has to do is decrement the stack    **/
	/**  pointer.                                                              **/
	/**                                                                        **/
	/**  For efficiency, memory for each stack state used is only allocated    **/
	/**  once per game, and reused for the remainder of the game.              **/
	/**                                                                        **/
	/****************************************************************************/
	private void push_state() {
	    int i, win_places_array_size;
	    int old_state, new_state;
	
	    win_places_array_size = win_places;
	    old_state = depth++;
	    new_state = depth;
	
	    if (depth == states_allocated) {
	
	        /* Allocate space for the board */
	
	        state_stack[new_state].board = new char[size_x][size_y];
	
	        /* Allocate space for the score array */
	
	        state_stack[new_state].score_array =new int[2][win_places_array_size];
	
	        states_allocated++;
	    }
	
	    /* Copy the board */
	
        //memcpy(new_state->board[i], old_state->board[i], size_y);
	    for (i=0; i<size_x; i++) {
	    	for (int j=0; j < size_y; ++j)
	    		state_stack[new_state].board[i][j]=state_stack[old_state].board[i][j];
	    }
	
	    /* Copy the score array */
	    //memcpy(new_state->score_array[0], old_state->score_array[0],
	    //       win_places_array_size);
	    for (i = 0; i < win_places_array_size; ++i) {
	    	state_stack[new_state].score_array[0][i]=state_stack[old_state].score_array[0][i];
	    }
//	    memcpy(new_state->score_array[1], old_state->score_array[1],
//	           win_places_array_size);
	    for (i = 0; i < win_places_array_size; ++i) {
	    	state_stack[new_state].score_array[1][i]=state_stack[old_state].score_array[1][i];
	    }
	
	    state_stack[new_state].score[0] = state_stack[old_state].score[0];
	    state_stack[new_state].score[1] = state_stack[old_state].score[1];
	    state_stack[new_state].winner = state_stack[old_state].winner;
	
	    current_state = new_state;
	}
	
	
	/****************************************************************************/
	/**                                                                        **/
	/**  This recursive function determines how good the current state may     **/
	/**  turn out to be for the specified player.  It does this by looking     **/
	/**  ahead level moves.  It is assumed that both the specified player and  **/
	/**  the opponent may make the best move possible.  alpha and beta are     **/
	/**  used for alpha-beta cutoff so that the game tree can be pruned to     **/
	/**  avoid searching unneccessary paths.                                   **/
	/**                                                                        **/
	/**  The specified poll function (if any) is called at the appropriate     **/
	/**  intervals.                                                            **/
	/**                                                                        **/
	/**  The worst goodness that the current state can produce in the number   **/
	/**  of moves (levels) searched is returned.  This is the best the         **/
	/**  specified player can hope to achieve with this state (since it is     **/
	/**  assumed that the opponent will make the best moves possible).         **/
	/**                                                                        **/
	/****************************************************************************/
	private int evaluate(int player, int level, int alpha, int beta) {
	    int i, goodness, best, maxab;
	    
	    if (level == depth)
	        return this.goodness_of(player);
	    else {
	        /* Assume it is the other player's turn. */
	        best = -(Integer.MAX_VALUE);
	        maxab = alpha;
	        for(i=0; i<size_x; i++) {
	            this.push_state();
	            if (this.drop_piece(this.other(player), drop_order[i]) < 0) {
	                this.pop_state();
	                continue;
	            }
	            else if (state_stack[current_state].winner == this.other(player))
	                goodness = Integer.MAX_VALUE - depth;
	            else
	                goodness = this.evaluate(this.other(player), level, beta, -maxab);
	            if (goodness > best) {
	                best = goodness;
	                if (best > maxab)
	                    maxab = best;
	            }
	            this.pop_state();
	            if (best > beta)
	                break;
	        }
	
	        /* What's good for the other player is bad for this one. */
	        return -best;
	    }
	}
	public boolean c4_is_game_in_progress() {
	    return game_in_progress;
	}
	
	/* Some macros for convenience. */
	private int other(int x) {
	    return x ^ 1;
	}
	private int real_player(int x) {
	    return x&1;
	}
	/* The "goodness" of the current state with respect to a player is the */
	/* score of that player minus the score of the player's opponent.  A   */
	/* positive value will result if the specified player is in a better   */
	/* situation than his/her opponent.                                    */
	private int goodness_of(int player) {
	    return (state_stack[current_state].score[player] - state_stack[current_state].score[this.other(player)]);
	}
}
