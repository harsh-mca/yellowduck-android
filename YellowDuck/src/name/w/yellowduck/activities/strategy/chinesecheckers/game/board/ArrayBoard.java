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


package name.w.yellowduck.activities.strategy.chinesecheckers.game.board;

import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.IllegalMoveException;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.CkMove;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.NoSuchPlayerException;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes.SlotType;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.Directions.Direction;

import java.util.List;
import java.util.ArrayList;

/**
 * This class represents a Board as an array.
 */
public class ArrayBoard implements Board {

	/**
	 * the size of the board array
	 */
	private static final int BOARD_SIZE = 121;

	/**
	 * a static array containing the indexes of the slots of the six branches of
	 * the star. In each branch, the 3 first indexes are those of the slots of
	 * the end of the branch.
	 * 
	 */
	private static final int BOARD_BRANCHES[][] = {
			{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 },
			{ 21, 22, 34, 19, 20, 32, 33, 44, 45, 55 },
			{ 97, 109, 110, 74, 84, 85, 95, 96, 107, 108 },
			{ 118, 119, 120, 111, 112, 113, 114, 115, 116, 117 },
			{ 86, 98, 99, 65, 75, 76, 87, 88, 100, 101 },
			{ 10, 11, 23, 12, 24, 35, 13, 25, 36, 46 } };

	/**
	 * the neighbours for each pawn
	 */
	private static final int NEIGHBOURS_ARRAY[][] = {
	/*
	 * Positions of pawns :
	 *  0 NORTHWEST 1 NORTHEAST 2 EAST 3 SOUTHEAST 4 SOUTHWEST 5 WEST
	 * 
	 * slots -1 are outside the board
	 */

	{ -1, -1, -1, 2, 1, -1 }, // slot 0
			{ -1, 0, 2, 4, 3, -1 }, // slot 1
			{ 0, -1, -1, 5, 4, 1 }, // slot 2

			{ -1, 1, 4, 7, 6, -1 }, // slot 3
			{ 1, 2, 5, 8, 7, 3 }, // slot 4
			{ 2, -1, -1, 9, 8, 4 }, // slot 5

			{ -1, 3, 7, 15, 14, -1 }, // slot 6
			{ 3, 4, 8, 16, 15, 6 }, // slot 7
			{ 4, 5, 9, 17, 16, 7 }, // slot 8
			{ 5, -1, -1, 18, 17, 8 }, // slot 9

			{ -1, -1, 11, 23, -1, -1 }, // slot 10
			{ -1, -1, 12, 24, 23, 10 }, // slot 11
			{ -1, -1, 13, 25, 24, 11 }, // slot 12
			{ -1, -1, 14, 26, 25, 12 }, // slot 13
			{ -1, 6, 15, 27, 26, 13 }, // slot 14
			{ 6, 7, 16, 28, 27, 14 }, // slot 15
			{ 7, 8, 17, 29, 28, 15 }, // slot 16
			{ 8, 9, 18, 30, 29, 16 }, // slot 17
			{ 9, -1, 19, 31, 30, 17 }, // slot 18
			{ -1, -1, 20, 32, 31, 18 }, // slot 19
			{ -1, -1, 21, 33, 32, 19 }, // slot 20
			{ -1, -1, 22, 34, 33, 20 }, // slot 21
			{ -1, -1, -1, -1, 34, 21 }, // slot 22

			{ 10, 11, 24, 35, -1, -1 }, // slot 23
			{ 11, 12, 25, 36, 35, 23 }, // slot 24
			{ 12, 13, 26, 37, 36, 24 }, // slot 25
			{ 13, 14, 27, 38, 37, 25 }, // slot 26
			{ 14, 15, 28, 39, 38, 26 }, // slot 27
			{ 15, 16, 29, 40, 39, 27 }, // slot 28
			{ 16, 17, 30, 41, 40, 28 }, // slot 29
			{ 17, 18, 31, 42, 41, 29 }, // slot 30
			{ 18, 19, 32, 43, 42, 30 }, // slot 31
			{ 19, 20, 33, 44, 43, 31 }, // slot 32
			{ 20, 21, 34, 45, 44, 32 }, // slot 33
			{ 21, 22, -1, -1, 45, 33 }, // slot 34

			{ 23, 24, 36, 46, -1, -1 }, // slot 35
			{ 24, 25, 37, 47, 46, 35 }, // slot 36
			{ 25, 26, 38, 48, 47, 36 }, // slot 37
			{ 26, 27, 39, 49, 48, 37 }, // slot 38
			{ 27, 28, 40, 50, 49, 38 }, // slot 39
			{ 28, 29, 41, 51, 50, 39 }, // slot 40
			{ 29, 30, 42, 52, 51, 40 }, // slot 41
			{ 30, 31, 43, 53, 52, 41 }, // slot 42
			{ 31, 32, 44, 54, 53, 42 }, // slot 43
			{ 32, 33, 45, 55, 54, 43 }, // slot 44
			{ 33, 34, -1, -1, 55, 44 }, // slot 45

			{ 35, 36, 47, 56, -1, -1 }, // slot 46
			{ 36, 37, 48, 57, 56, 46 }, // slot 47
			{ 37, 38, 49, 58, 57, 47 }, // slot 48
			{ 38, 39, 50, 59, 58, 48 }, // slot 49
			{ 39, 40, 51, 60, 59, 49 }, // slot 50
			{ 40, 41, 52, 61, 60, 50 }, // slot 51
			{ 41, 42, 53, 62, 61, 51 }, // slot 52
			{ 42, 43, 54, 63, 62, 52 }, // slot 53
			{ 43, 44, 55, 64, 63, 53 }, // slot 54
			{ 44, 45, -1, -1, 64, 54 }, // slot 55

			{ 46, 47, 57, 66, 65, -1 }, // slot 56
			{ 47, 48, 58, 67, 66, 56 }, // slot 57
			{ 48, 49, 59, 68, 67, 57 }, // slot 58
			{ 49, 50, 60, 69, 68, 58 }, // slot 59
			{ 50, 51, 61, 70, 69, 59 }, // slot 60
			{ 51, 52, 62, 71, 70, 60 }, // slot 61
			{ 52, 53, 63, 72, 71, 61 }, // slot 62
			{ 53, 54, 64, 73, 72, 62 }, // slot 63
			{ 54, 55, -1, 74, 73, 63 }, // slot 64

			{ -1, 56, 66, 76, 75, -1 }, // slot 65
			{ 56, 57, 67, 77, 76, 65 }, // slot 66
			{ 57, 58, 68, 78, 77, 66 }, // slot 67
			{ 58, 59, 69, 79, 78, 67 }, // slot 68
			{ 59, 60, 70, 80, 79, 68 }, // slot 69
			{ 60, 61, 71, 81, 80, 69 }, // slot 70
			{ 61, 62, 72, 82, 81, 70 }, // slot 71
			{ 62, 63, 73, 83, 82, 71 }, // slot 72
			{ 63, 64, 74, 84, 83, 72 }, // slot 73
			{ 64, -1, -1, 85, 84, 73 }, // slot 74

			{ -1, 65, 76, 87, 86, -1 }, // slot 75
			{ 65, 66, 77, 88, 87, 75 }, // slot 76
			{ 66, 67, 78, 89, 88, 76 }, // slot 77
			{ 67, 68, 79, 90, 89, 77 }, // slot 78
			{ 68, 69, 80, 91, 90, 78 }, // slot 79
			{ 69, 70, 81, 92, 91, 79 }, // slot 80
			{ 70, 71, 82, 93, 92, 80 }, // slot 81
			{ 71, 72, 83, 94, 93, 81 }, // slot 82
			{ 72, 73, 84, 95, 94, 82 }, // slot 83
			{ 73, 74, 85, 96, 95, 83 }, // slot 84
			{ 74, -1, -1, 97, 96, 84 }, // slot 85

			{ -1, 75, 87, 99, 98, -1 }, // slot 86
			{ 75, 76, 88, 100, 99, 86 }, // slot 87
			{ 76, 77, 89, 101, 100, 87 }, // slot 88
			{ 77, 78, 90, 102, 101, 88 }, // slot 89
			{ 78, 79, 91, 103, 102, 89 }, // slot 90
			{ 79, 80, 92, 104, 103, 90 }, // slot 91
			{ 80, 81, 93, 105, 104, 91 }, // slot 92
			{ 81, 82, 94, 106, 105, 92 }, // slot 93
			{ 82, 83, 95, 107, 106, 93 }, // slot 94
			{ 83, 84, 96, 108, 107, 94 }, // slot 95
			{ 84, 85, 97, 109, 108, 95 }, // slot 96
			{ 85, -1, -1, 110, 109, 96 }, // slot 97

			{ -1, 86, 99, -1, -1, -1 }, // slot 98
			{ 86, 87, 100, -1, -1, 98 }, // slot 99
			{ 87, 88, 101, -1, -1, 99 }, // slot 100
			{ 88, 89, 102, -1, -1, 100 }, // slot 101
			{ 89, 90, 103, 111, -1, 101 }, // slot 102
			{ 90, 91, 104, 112, 111, 102 }, // slot 103
			{ 91, 92, 105, 113, 112, 103 }, // slot 104
			{ 92, 93, 106, 114, 113, 104 }, // slot 105
			{ 93, 94, 107, -1, 114, 105 }, // slot 106
			{ 94, 95, 108, -1, -1, 106 }, // slot 107
			{ 95, 96, 109, -1, -1, 107 }, // slot 108
			{ 96, 97, 110, -1, -1, 108 }, // slot 109
			{ 97, -1, -1, -1, -1, 109 }, // slot 110

			{ 102, 103, 112, 115, -1, -1 }, // slot 111
			{ 103, 104, 113, 116, 115, 111 }, // slot 112
			{ 104, 105, 114, 117, 116, 112 }, // slot 113
			{ 105, 106, -1, -1, 117, 113 }, // slot 114

			{ 111, 112, 116, 118, -1, -1 }, // slot 115
			{ 112, 113, 117, 119, 118, 115 }, // slot 116
			{ 113, 114, -1, -1, 119, 116 }, // slot 117

			{ 115, 116, 119, 120, -1, -1 }, // slot 118
			{ 116, 117, -1, -1, 120, 118 }, // slot 119

			{ 118, 119, -1, -1, -1, -1 }, // slot 120
	};

	/**
	 * the distances to goal for each pawn
	 */
	private static final int[][] DISTANCES_ARRAY = { { 16, 12, 4, 0, 4, 12 }, // slot
			// 0
			{ 15, 11, 4, 1, 5, 12 }, // slot 1
			{ 15, 12, 5, 1, 4, 11 }, // slot 2
			{ 14, 10, 4, 2, 6, 12 }, // slot 3
			{ 14, 11, 5, 2, 5, 11 }, // slot 4
			{ 14, 12, 6, 2, 4, 10 }, // slot 5
			{ 13, 9, 4, 3, 7, 12 }, // slot 6
			{ 13, 10, 5, 3, 6, 11 }, // slot 7
			{ 13, 11, 6, 3, 5, 10 }, // slot 8
			{ 13, 12, 7, 3, 4, 9 }, // slot 9
			{ 12, 4, 0, 4, 12, 16 }, // slot 10
			{ 12, 5, 1, 4, 11, 15 }, // slot 11
			{ 12, 6, 2, 4, 10, 14 }, // slot 12
			{ 12, 7, 3, 4, 9, 13 }, // slot 13
			{ 12, 8, 4, 4, 8, 12 }, // slot 14
			{ 12, 9, 5, 4, 7, 11 }, // slot 15
			{ 12, 10, 6, 4, 6, 10 }, // slot 16
			{ 12, 11, 7, 4, 5, 9 }, // slot 17
			{ 12, 12, 8, 4, 4, 8 }, // slot 18
			{ 12, 13, 9, 4, 3, 7 }, // slot 19
			{ 12, 14, 10, 4, 2, 6 }, // slot 20
			{ 12, 15, 11, 4, 1, 5 }, // slot 21
			{ 12, 16, 12, 4, 0, 4 }, // slot 22
			{ 11, 4, 1, 5, 12, 15 }, // slot 23
			{ 11, 5, 2, 5, 11, 14 }, // slot 24
			{ 11, 6, 3, 5, 10, 13 }, // slot 25
			{ 11, 7, 4, 5, 9, 12 }, // slot 26
			{ 11, 8, 5, 5, 8, 11 }, // slot 27
			{ 11, 9, 6, 5, 7, 10 }, // slot 28
			{ 11, 10, 7, 5, 6, 9 }, // slot 29
			{ 11, 11, 8, 5, 5, 8 }, // slot 30
			{ 11, 12, 9, 5, 4, 7 }, // slot 31
			{ 11, 13, 10, 5, 3, 6 }, // slot 32
			{ 11, 14, 11, 5, 2, 5 }, // slot 33
			{ 11, 15, 12, 5, 1, 4 }, // slot 34
			{ 10, 4, 2, 6, 12, 14 }, // slot 35
			{ 10, 5, 3, 6, 11, 13 }, // slot 36
			{ 10, 6, 4, 6, 10, 12 }, // slot 37
			{ 10, 7, 5, 6, 9, 11 }, // slot 38
			{ 10, 8, 6, 6, 8, 10 }, // slot 39
			{ 10, 9, 7, 6, 7, 9 }, // slot 40
			{ 10, 10, 8, 6, 6, 8 }, // slot 41
			{ 10, 11, 9, 6, 5, 7 }, // slot 42
			{ 10, 12, 10, 6, 4, 6 }, // slot 43
			{ 10, 13, 11, 6, 3, 5 }, // slot 44
			{ 10, 14, 12, 6, 2, 4 }, // slot 45
			{ 9, 4, 3, 7, 12, 13 }, // slot 46
			{ 9, 5, 4, 7, 11, 12 }, // slot 47
			{ 9, 6, 5, 7, 10, 11 }, // slot 48
			{ 9, 7, 6, 7, 9, 10 }, // slot 49
			{ 9, 8, 7, 7, 8, 9 }, // slot 50
			{ 9, 9, 8, 7, 7, 8 }, // slot 51
			{ 9, 10, 9, 7, 6, 7 }, // slot 52
			{ 9, 11, 10, 7, 5, 6 }, // slot 53
			{ 9, 12, 11, 7, 4, 5 }, // slot 54
			{ 9, 13, 12, 7, 3, 4 }, // slot 55
			{ 8, 4, 4, 8, 12, 12 }, // slot 56
			{ 8, 5, 5, 8, 11, 11 }, // slot 57
			{ 8, 6, 6, 8, 10, 10 }, // slot 58
			{ 8, 7, 7, 8, 9, 9 }, // slot 59
			{ 8, 8, 8, 8, 8, 8 }, // slot 60
			{ 8, 9, 9, 8, 7, 7 }, // slot 61
			{ 8, 10, 10, 8, 6, 6 }, // slot 62
			{ 8, 11, 11, 8, 5, 5 }, // slot 63
			{ 8, 12, 12, 8, 4, 4 }, // slot 64
			{ 7, 3, 4, 9, 13, 12 }, // slot 65
			{ 7, 4, 5, 9, 12, 11 }, // slot 66
			{ 7, 5, 6, 9, 11, 10 }, // slot 67
			{ 7, 6, 7, 9, 10, 9 }, // slot 68
			{ 7, 7, 8, 9, 9, 8 }, // slot 69
			{ 7, 8, 9, 9, 8, 7 }, // slot 70
			{ 7, 9, 10, 9, 7, 6 }, // slot 71
			{ 7, 10, 11, 9, 6, 5 }, // slot 72
			{ 7, 11, 12, 9, 5, 4 }, // slot 73
			{ 7, 12, 13, 9, 4, 3 }, // slot 74
			{ 6, 2, 4, 10, 14, 12 }, // slot 75
			{ 6, 3, 5, 10, 13, 11 }, // slot 76
			{ 6, 4, 6, 10, 12, 10 }, // slot 77
			{ 6, 5, 7, 10, 11, 9 }, // slot 78
			{ 6, 6, 8, 10, 10, 8 }, // slot 79
			{ 6, 7, 9, 10, 9, 7 }, // slot 80
			{ 6, 8, 10, 10, 8, 6 }, // slot 81
			{ 6, 9, 11, 10, 7, 5 }, // slot 82
			{ 6, 10, 12, 10, 6, 4 }, // slot 83
			{ 6, 11, 13, 10, 5, 3 }, // slot 84
			{ 6, 12, 14, 10, 4, 2 }, // slot 85
			{ 5, 1, 4, 11, 15, 12 }, // slot 86
			{ 5, 2, 5, 11, 14, 11 }, // slot 87
			{ 5, 3, 6, 11, 13, 10 }, // slot 88
			{ 5, 4, 7, 11, 12, 9 }, // slot 89
			{ 5, 5, 8, 11, 11, 8 }, // slot 90
			{ 5, 6, 9, 11, 10, 7 }, // slot 91
			{ 5, 7, 10, 11, 9, 6 }, // slot 92
			{ 5, 8, 11, 11, 8, 5 }, // slot 93
			{ 5, 9, 12, 11, 7, 4 }, // slot 94
			{ 5, 10, 13, 11, 6, 3 }, // slot 95
			{ 5, 11, 14, 11, 5, 2 }, // slot 96
			{ 5, 12, 15, 11, 4, 1 }, // slot 97
			{ 4, 0, 4, 12, 16, 12 }, // slot 98
			{ 4, 1, 5, 12, 15, 11 }, // slot 99
			{ 4, 2, 6, 12, 14, 10 }, // slot 100
			{ 4, 3, 7, 12, 13, 9 }, // slot 101
			{ 4, 4, 8, 12, 12, 8 }, // slot 102
			{ 4, 5, 9, 12, 11, 7 }, // slot 103
			{ 4, 6, 10, 12, 10, 6 }, // slot 104
			{ 4, 7, 11, 12, 9, 5 }, // slot 105
			{ 4, 8, 12, 12, 8, 4 }, // slot 106
			{ 4, 9, 13, 12, 7, 3 }, // slot 107
			{ 4, 10, 14, 12, 6, 2 }, // slot 108
			{ 4, 11, 15, 12, 5, 1 }, // slot 109
			{ 4, 12, 16, 12, 4, 0 }, // slot 110
			{ 3, 4, 9, 13, 12, 7 }, // slot 111
			{ 3, 5, 10, 13, 11, 6 }, // slot 112
			{ 3, 6, 11, 13, 10, 5 }, // slot 113
			{ 3, 7, 12, 13, 9, 4 }, // slot 114
			{ 2, 4, 10, 14, 12, 6 }, // slot 115
			{ 2, 5, 11, 14, 11, 5 }, // slot 116
			{ 2, 6, 12, 14, 10, 4 }, // slot 117
			{ 1, 4, 11, 15, 12, 5 }, // slot 118
			{ 1, 5, 12, 15, 11, 4 }, // slot 119
			{ 0, 4, 12, 16, 12, 4 }, // slot 120

	};

	/**
	 * the distances to middle for each pawn
	 */
	private static final int[][] DISTANCES_TO_MIDDLE = {
			{ 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 0, 0, 0, 0, 50, 75, 100,
					75, 50, 0, 0, 0, 0, 0, 0, 0, 37, 62, 87, 87, 62, 37, 0, 0,
					0, 0, 0, 25, 50, 75, 100, 75, 50, 25, 0, 0, 0, 12, 37, 62,
					87, 87, 62, 37, 12, 0, 0, 25, 50, 75, 100, 75, 50, 25, 0,
					0, 12, 37, 62, 87, 87, 62, 37, 12, 0, 0, 0, 25, 50, 75,
					100, 75, 50, 25, 0, 0, 0, 0, 0, 37, 62, 87, 87, 62, 37, 0,
					0, 0, 0, 0, 0, 0, 50, 75, 100, 75, 50, 0, 0, 0, 0, 50, 50,
					50, 50, 50, 50, 50, 50, 50, 50 }, // NORTH and SOUTH
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12, 25, 37, 50, 50,
					50, 50, 50, 0, 0, 0, 12, 25, 37, 50, 62, 75, 50, 50, 50, 0,
					0, 25, 37, 50, 62, 75, 87, 100, 50, 50, 0, 37, 50, 62, 75,
					87, 100, 87, 75, 50, 50, 62, 75, 87, 100, 87, 75, 62, 50,
					50, 75, 87, 100, 87, 75, 62, 50, 37, 0, 50, 50, 100, 87,
					75, 62, 50, 37, 25, 0, 0, 50, 50, 50, 75, 62, 50, 37, 25,
					12, 0, 0, 0, 50, 50, 50, 50, 50, 37, 25, 12, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // NORTHEAST and SOUTHWEST
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 50, 50, 50, 50, 50, 37, 25, 12, 0,
					0, 0, 0, 0, 50, 50, 50, 75, 62, 50, 37, 25, 12, 0, 0, 0,
					50, 50, 100, 87, 75, 62, 50, 37, 25, 0, 0, 50, 75, 87, 100,
					87, 75, 62, 50, 37, 0, 50, 62, 75, 87, 100, 87, 75, 62, 50,
					0, 37, 50, 62, 75, 87, 100, 87, 75, 50, 0, 0, 25, 37, 50,
					62, 75, 87, 100, 50, 50, 0, 0, 0, 12, 25, 37, 50, 62, 75,
					50, 50, 50, 0, 0, 0, 0, 0, 12, 25, 37, 50, 50, 50, 50, 50,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } // SOUTHEAST and NORTHWEST
	};

	/**
	 * the coordinates view of each slot
	 */
	private static final int[][] COORDINATES = { { 0, 12 }, // slot 0
			{ 1, 11 }, // slot 1
			{ 1, 13 }, // slot 2
			{ 2, 10 }, // slot 3
			{ 2, 12 }, // slot 4
			{ 2, 14 }, // slot 5
			{ 3, 9 }, // slot 6
			{ 3, 11 }, // slot 7
			{ 3, 13 }, // slot 8
			{ 3, 15 }, // slot 9
			{ 4, 0 }, // slot 10
			{ 4, 2 }, // slot 11
			{ 4, 4 }, // slot 12
			{ 4, 6 }, // slot 13
			{ 4, 8 }, // slot 14
			{ 4, 10 }, // slot 15
			{ 4, 12 }, // slot 16
			{ 4, 14 }, // slot 17
			{ 4, 16 }, // slot 18
			{ 4, 18 }, // slot 19
			{ 4, 20 }, // slot 20
			{ 4, 22 }, // slot 21
			{ 4, 24 }, // slot 22
			{ 5, 1 }, // slot 23
			{ 5, 3 }, // slot 24
			{ 5, 5 }, // slot 25
			{ 5, 7 }, // slot 26
			{ 5, 9 }, // slot 27
			{ 5, 11 }, // slot 28
			{ 5, 13 }, // slot 29
			{ 5, 15 }, // slot 30
			{ 5, 17 }, // slot 31
			{ 5, 19 }, // slot 32
			{ 5, 21 }, // slot 33
			{ 5, 23 }, // slot 34
			{ 6, 2 }, // slot 35
			{ 6, 4 }, // slot 36
			{ 6, 6 }, // slot 37
			{ 6, 8 }, // slot 38
			{ 6, 10 }, // slot 39
			{ 6, 12 }, // slot 40
			{ 6, 14 }, // slot 41
			{ 6, 16 }, // slot 42
			{ 6, 18 }, // slot 43
			{ 6, 20 }, // slot 44
			{ 6, 22 }, // slot 45
			{ 7, 3 }, // slot 46
			{ 7, 5 }, // slot 47
			{ 7, 7 }, // slot 48
			{ 7, 9 }, // slot 49
			{ 7, 11 }, // slot 50
			{ 7, 13 }, // slot 51
			{ 7, 15 }, // slot 52
			{ 7, 17 }, // slot 53
			{ 7, 19 }, // slot 54
			{ 7, 21 }, // slot 55
			{ 8, 4 }, // slot 56
			{ 8, 6 }, // slot 57
			{ 8, 8 }, // slot 58
			{ 8, 10 }, // slot 59
			{ 8, 12 }, // slot 60
			{ 8, 14 }, // slot 61
			{ 8, 16 }, // slot 62
			{ 8, 18 }, // slot 63
			{ 8, 20 }, // slot 64
			{ 9, 3 }, // slot 65
			{ 9, 5 }, // slot 66
			{ 9, 7 }, // slot 67
			{ 9, 9 }, // slot 68
			{ 9, 11 }, // slot 69
			{ 9, 13 }, // slot 70
			{ 9, 15 }, // slot 71
			{ 9, 17 }, // slot 72
			{ 9, 19 }, // slot 73
			{ 9, 21 }, // slot 74
			{ 10, 2 }, // slot 75
			{ 10, 4 }, // slot 76
			{ 10, 6 }, // slot 77
			{ 10, 8 }, // slot 78
			{ 10, 10 }, // slot 79
			{ 10, 12 }, // slot 80
			{ 10, 14 }, // slot 81
			{ 10, 16 }, // slot 82
			{ 10, 18 }, // slot 83
			{ 10, 20 }, // slot 84
			{ 10, 22 }, // slot 85
			{ 11, 1 }, // slot 86
			{ 11, 3 }, // slot 87
			{ 11, 5 }, // slot 88
			{ 11, 7 }, // slot 89
			{ 11, 9 }, // slot 90
			{ 11, 11 }, // slot 91
			{ 11, 13 }, // slot 92
			{ 11, 15 }, // slot 93
			{ 11, 17 }, // slot 94
			{ 11, 19 }, // slot 95
			{ 11, 21 }, // slot 96
			{ 11, 23 }, // slot 97
			{ 12, 0 }, // slot 98
			{ 12, 2 }, // slot 99
			{ 12, 4 }, // slot 100
			{ 12, 6 }, // slot 101
			{ 12, 8 }, // slot 102
			{ 12, 10 }, // slot 103
			{ 12, 12 }, // slot 104
			{ 12, 14 }, // slot 105
			{ 12, 16 }, // slot 106
			{ 12, 18 }, // slot 107
			{ 12, 20 }, // slot 108
			{ 12, 22 }, // slot 109
			{ 12, 24 }, // slot 110
			{ 13, 9 }, // slot 111
			{ 13, 11 }, // slot 112
			{ 13, 13 }, // slot 113
			{ 13, 15 }, // slot 114
			{ 14, 10 }, // slot 115
			{ 14, 12 }, // slot 116
			{ 14, 14 }, // slot 117
			{ 15, 11 }, // slot 118
			{ 15, 13 }, // slot 119
			{ 16, 12 }, // slot 120
	};

	/**
	 * the content of the board
	 */
	private SlotType[] board = new SlotType[BOARD_SIZE];

	/**
	 * returns an array representing the slots' coordinates
	 * of the star's "branch" corresponding to the given SlotType
	 */
	private static int[] getBranch(SlotType st) {
		switch (st) {
		case NORTH:
			return BOARD_BRANCHES[0];
		case NORTHEAST:
			return BOARD_BRANCHES[1];
		case SOUTHEAST:
			return BOARD_BRANCHES[2];
		case SOUTH:
			return BOARD_BRANCHES[3];
		case SOUTHWEST:
			return BOARD_BRANCHES[4];
		case NORTHWEST:
			return BOARD_BRANCHES[5];

		default:
			return new int[0];
		}
	}

	/**
	 * The default constructor to create an Arrayoard
	 * @param players The list of players
	 * @throws IllegalArgumentException
	 */
	public ArrayBoard(List<Player> players) throws IllegalArgumentException {
		for (Player p1 : players)
			for (Player p2 : players)
				if (!p1.equals(p2) && p1.getSlotType() == p2.getSlotType())
					throw new IllegalArgumentException(
							"2 players with same slotType! " + p1.getName()
									+ " and " + p2.getName());

		for (int i = 0; i < board.length; i++)
			board[i] = SlotType.EMPTY;

		for (Player p : players)
			for (int i : getBranch(p.getSlotType()))
				board[i] = p.getSlotType();

	}

	/**
	 * The constructor to create an ArrayBoard with another.
	 * @param ab The ArrayBoard to copy
	 */
	public ArrayBoard(ArrayBoard ab) {
		for (int i = 0; i < BOARD_SIZE; i++)
			this.board[i] = ab.board[i];
	}

	/**
	 * The constructor to create an ArrayBoard with a SlotType[]
	 * @param st The SlotType array
	 */
	public ArrayBoard(SlotType[] st) {
		for (int i = 0; i < BOARD_SIZE; i++)
			this.board[i] = st[i];
	}

	/**
	 * The constructor to create an ArrayBoard with a SlotType
	 * @param st The SLotType
	 */
	public ArrayBoard(SlotType st) {
		for (int i = 0; i < BOARD_SIZE; i++)
			this.board[i] = st;
	}

	/**
	 * @see game.board.Board#applyMove(game.move.CkMove)
	 */
	public void applyMove(CkMove m) throws IllegalMoveException {
		int source = m.getSource();
		int target = m.getTarget();
		Player p = m.getPlayer();

		if (!(board[source] == p.getSlotType())
				|| !(board[target] == SlotType.EMPTY))
			throw new IllegalMoveException(m);

		board[source] = SlotType.EMPTY;
		board[target] = p.getSlotType();
	}

	/**
	 * @see game.board.Board#backMove(game.move.CkMove)
	 */
	public void backMove(CkMove m) throws IllegalMoveException {
		int target = m.getSource();
		int source = m.getTarget();
		Player p = m.getPlayer();

		if (!(board[source] == p.getSlotType())
				|| !(board[target] == SlotType.EMPTY))
			throw new IllegalMoveException(m);

		board[source] = SlotType.EMPTY;
		board[target] = p.getSlotType();
	}

	/**
	 * @see game.board.Board#getResultBoard(game.move.CkMove)
	 */
	public Board getResultBoard(CkMove m) throws IllegalMoveException {
		ArrayBoard b = new ArrayBoard(this);
		b.applyMove(m);
		return b;
	}

	/**
	 * @see game.board.Board#getBoard()
	 */
	public SlotType[] getBoard() {
		return board;
	}

	/**
	 * @see game.board.Board#allPawns(game.player.Player)
	 */
	public List<Integer> allPawns(Player p) {
		ArrayList<Integer> v = new ArrayList<Integer>(10);
		for (int i = 0; i < board.length; i++)
			if (p.getSlotType() == board[i])
				v.add(i);
		return v;
	}

	/**
	 * @see game.board.Board#getNeighbour(java.lang.Integer, game.board.Directions.Direction)
	 */
	public Integer getNeighbour(Integer slot, Direction d)
			throws NoSuchNeighbourException {
		int ngb = -1;

		switch (d) {
		case NORTHWEST:
			ngb = NEIGHBOURS_ARRAY[slot][0];
			break;

		case NORTHEAST:
			ngb = NEIGHBOURS_ARRAY[slot][1];
			break;

		case EAST:
			ngb = NEIGHBOURS_ARRAY[slot][2];
			break;

		case SOUTHEAST:
			ngb = NEIGHBOURS_ARRAY[slot][3];
			break;

		case SOUTHWEST:
			ngb = NEIGHBOURS_ARRAY[slot][4];
			break;

		case WEST:
			ngb = NEIGHBOURS_ARRAY[slot][5];
			break;
		}

		if (ngb == -1)
			throw new NoSuchNeighbourException();

		return ngb;
	}

	/**
	 * @see game.board.Board#getSlotType(java.lang.Integer)
	 */
	public SlotType getSlotType(Integer slot) {
		if (slot < 0 || slot > 120)
			throw new IllegalArgumentException("unvalid slot - not in range : "
					+ slot);

		return board[slot];
	}

	/**
	 * @see game.board.Board#isOccupied(java.lang.Integer)
	 */
	public boolean isOccupied(Integer slot) {
		return !isEmpty(slot);
	}

	/**
	 * @see game.board.Board#isEmpty(java.lang.Integer)
	 */
	public boolean isEmpty(Integer slot) {
		return (board[slot] == SlotType.EMPTY);
	}

	/**
	 * @see game.board.Board#getWinnerSlotType()
	 */
	public SlotType getWinnerSlotType() throws NoSuchPlayerException {

		for (SlotType playerST : SlotType.values()) {
			if (playerST != SlotType.EMPTY && playerST != SlotType.OUT) {
				boolean won = true;
				for (int i : getBranch(SlotTypes.getGoal(playerST))) {
					won = won && board[i] == playerST;
					if (!won)
						break;
				}
				if (won) {
					return playerST;
				}
			}
		}

		SlotType blocker = getBlockingPlayer();
		if (blocker != SlotType.EMPTY) {
			return SlotTypes.getGoal(blocker);
		}

		throw new NoSuchPlayerException(SlotType.EMPTY);
	}

	/**
	 * Get the blocking player
	 * @return The blocking Palyer
	 */
	private SlotType getBlockingPlayer() {

		for (SlotType st : SlotType.values()) {
			SlotType opponentST = SlotTypes.getGoal(st);
			boolean blockedPawn = false;
			boolean blockers = true;
			int[] branch = getBranch(st);
			for (int i = 0; i < branch.length; i++) {
				if (i < 3) {// checking for potential blocked pawns
					blockedPawn = blockedPawn || board[branch[i]] == st;
				} else {// checking for potential blockers
					if (!blockers)
						break;
					blockers = blockers && board[branch[i]] == opponentST;
				}

			}

			if (blockedPawn && blockers)
				return st;
		}

		return SlotType.EMPTY;
	}

	/**
	 * @see game.board.Board#getCoordinate(java.lang.Integer)
	 */
	public Coordinate getCoordinate(Integer slot) {
		int[] c = COORDINATES[slot];
		return new Coordinate(c[1], c[0]);
	}

	/**
	 * returns the distance of a given slot to a camp, given as a "SlotType".
	 * This is 0 if the slot is in the goal.
	 * @param slot The number of the slot
	 * @param s The SlotType
	 * @return The distance of a given slot to a camp
	 */
	public static int distanceToCamp(Integer slot, SlotType s) {
		int index = 3;
		switch (s) {
		case NORTHEAST:
			index = 4;
			break;
		case SOUTHEAST:
			index = 5;
			break;
		case SOUTH:
			index = 0;
			break;
		case SOUTHWEST:
			index = 1;
			break;
		case NORTHWEST:
			index = 2;
			break;
		}

		return DISTANCES_ARRAY[slot][index];

	}

	/**
	 * @see game.board.Board#getCamp(java.lang.Integer)
	 */
	public SlotType getCamp(Integer slot) {

		for (int i : BOARD_BRANCHES[0])
			if (slot == i)
				return SlotType.NORTH;

		for (int i : BOARD_BRANCHES[1])
			if (slot == i)
				return SlotType.NORTHEAST;

		for (int i : BOARD_BRANCHES[2])
			if (slot == i)
				return SlotType.SOUTHEAST;

		for (int i : BOARD_BRANCHES[3])
			if (slot == i)
				return SlotType.SOUTH;

		for (int i : BOARD_BRANCHES[4])
			if (slot == i)
				return SlotType.SOUTHWEST;

		for (int i : BOARD_BRANCHES[5])
			if (slot == i)
				return SlotType.NORTHWEST;

		return SlotType.EMPTY;
	}

	/**
	 * @see game.board.Board#getDistanceToMiddle(java.lang.Integer)
	 */
	public int getDistanceToMiddle(Integer slot) {
		SlotType type = getSlotType(slot);

		if (getCamp(slot) == SlotTypes.getGoal(type))
			return 100;
		if (getCamp(slot) == type)
			return -100;

		switch (type) {

		case NORTH:
		case SOUTH:
			return DISTANCES_TO_MIDDLE[0][slot];

		case NORTHEAST:
		case SOUTHWEST:
			return DISTANCES_TO_MIDDLE[1][slot];

		case SOUTHEAST:
		case NORTHWEST:
			return DISTANCES_TO_MIDDLE[2][slot];
		}

		return -1;

	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (!(o instanceof ArrayBoard))
			return false;
		else {
			ArrayBoard a = (ArrayBoard) o;
			boolean res = true;
			for (int i = 0; i < BOARD_SIZE; i++) {
				res = res && (a.board[i] == board[i]);
				if (!res)
					return false;
			}

			return res;
		}
	}

	/**
	 * hashcode method written with technique of the book "Effective Java"
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int value = 17;

		for (int i = 0; i < BOARD_SIZE; i++) {
			int localHash = board[i].hashCode();
			value = 37 * value + localHash;
		}

		return value;
	}

	/**
	 * Get the integer of a slot
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @return The number of a slot
	 */
	public static int getIntegerSlot(int x, int y) {
		if (y > 16 || x > 24 || x < 0 || y < 0)
			return -1;

		int[][] coordinate = new int[17][25];

		for (int i = 0; i < coordinate.length; i++)
			for (int j = 0; j < coordinate[i].length; j++)
				coordinate[i][j] = -1;

		// The upper branch
		coordinate[0][12] = 0;
		coordinate[1][11] = 1;
		coordinate[1][13] = 2;
		coordinate[2][10] = 3;
		coordinate[2][12] = 4;
		coordinate[2][14] = 5;
		coordinate[3][9] = 6;
		coordinate[3][11] = 7;
		coordinate[3][13] = 8;
		coordinate[3][15] = 9;

		// Middle Board
		int j = 10;
		int l = 0;
		for (int k = 4; k < 13; k++) {
			for (int i = 0 + l; i < coordinate[k].length - l; i += 2) {
				coordinate[k][i] = j;
				j++;
			}
			l += (k < 8 ? 1 : -1);
		}

		// The down branch
		coordinate[13][9] = j++;
		coordinate[13][11] = j++;
		coordinate[13][13] = j++;
		coordinate[13][15] = j++;
		coordinate[14][10] = j++;
		coordinate[14][12] = j++;
		coordinate[14][14] = j++;
		coordinate[15][11] = j++;
		coordinate[15][13] = j++;
		coordinate[16][12] = j;

		return coordinate[y][x];
	}

}
