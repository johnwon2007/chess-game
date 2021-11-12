package chess;

import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ChessRuleBook {
    Chessboard board;
    ChessTurnTracker currTurn;
    ChessCheckTracker checkTracker;

    ChessRuleBook(Chessboard board, ChessTurnTracker currTurn){
        this.board = board;
        this.currTurn = currTurn;
        checkTracker = new ChessCheckTracker(board, currTurn);
    }

    Set<Coord> currLegalMoveSet(){
        Piece pieceToMove = board.pieceAt(currTurn.moveFrom);
        Set<Coord> legalMoves = new HashSet<>();

        if(pieceToMove == null || pieceToMove instanceof Edge) return legalMoves;

        Set<Coord> moveDirection = pieceToMove.moveDirection();

        if(pieceToMove instanceof Pawn){
            return pawnLegalMoveSet();
        }

        for (Coord direction: moveDirection) {
            int i = 1;
            Coord moveTo;
            do{
                moveTo = currTurn.moveFrom.add(direction.multiply(i));

                // If moving into an allied piece or moving out of board, stop checking further down the line.
                if(!board.coordInBoard(moveTo) || board.isAlliedPiece(moveTo)) break;

                // If Empty square or an Enemy Piece,
                else {
                    // If not a King piece, then no problem
                    if(!(pieceToMove instanceof King)) legalMoves.add(moveTo);
                    // If is a King piece, then check if the landing square is being attacked
                    else if (!checkTracker.isAttacked(moveTo)) legalMoves.add(moveTo);

                    // If moving into an Enemy Piece, stop checking further down the line.
                    if(board.hasPieceAt(moveTo)) break;
                }

                //If the piece can move in a line, proceed to check if the next square in the line is legal
                i++;

            }while(pieceToMove.canMoveInLine());

        }

        return legalMoves;
    }

    private Set<Coord> pawnLegalMoveSet(){
        Piece pieceToMove = board.pieceAt(currTurn.moveFrom);
        Coord primaryMoveDirection = new Coord(-1, -1);
        for(Coord move: pieceToMove.moveDirection()) primaryMoveDirection = move;

        if(primaryMoveDirection.x == -1) {
            throw new AssertionError("Pawn.moveDirection() was not called properly");
        }

        Set<Coord> legalMoves = new HashSet<>();

        Coord moveTo = currTurn.moveFrom.add(primaryMoveDirection);

        // If move is in bounds and is not moving into a piece, add to legal moves.
        if(board.coordInBoard(moveTo) && !board.hasPieceAt(moveTo)) legalMoves.add(moveTo);

        // If moving back 2 tiles places the pawn out of bounds, then it is at its starting square
        // and can move 2 squares forward.
        Coord moveTwoTiles = primaryMoveDirection.multiply(2);
        if(board.coordInBoard(currTurn.moveFrom.subtract(moveTwoTiles))){
            Coord moveTwo = currTurn.moveFrom.add(moveTwoTiles);
            if(board.coordInBoard(moveTwo) && !board.hasPieceAt(moveTwo)) legalMoves.add(moveTwo);
        }

        // To test if any taking move is legal TODO
        Set<Coord> potentialTakes = new HashSet<>();
        potentialTakes.add(primaryMoveDirection.add(1, 0).add(currTurn.moveFrom));
        potentialTakes.add(primaryMoveDirection.add(-1, 0).add(currTurn.moveFrom));

        for (Coord takeTo: potentialTakes) {
            if(!board.coordInBoard(takeTo) ||       // If move is not in bounds OR
               board.isAlliedPiece(takeTo) ||       // If move is an ally OR
               !board.hasPieceAt(takeTo) ||         // If move goes to an empty square OR
               takeTo != board.enPassantSquare()) { // If move is not a valid enPassantSquare,
                continue;}

            // If moving into empty square, add to legal moves.
            else legalMoves.add(takeTo);


        }


        return legalMoves;
    }



}


class ChessCheckTracker {
    private Chessboard board;
    private ChessTurnTracker currTurn;
    private Set<LineOfSight> lineOfSights;

    ChessCheckTracker(Chessboard board, ChessTurnTracker currTurn){
        this.board = board;
        this.currTurn = currTurn;
        this.lineOfSights = new HashSet<>();
    }

    private Coord alliedKingCoord(){
        for (Coord coord: board.getBoard().keySet()) {
            if(board.pieceAt(coord) instanceof King && board.pieceAt(coord).color() == board.currColor()){
                return coord;
            }
        }
        throw new AssertionError("Missing King Piece on the Board");
    }

    boolean isAttacked(Coord coord){
        return false;
    }




}


class LineOfSight {
    Piece pieceWithSight;
    List<Coord> lineOfSight = new ArrayList<>();

    LineOfSight(){


    }
}