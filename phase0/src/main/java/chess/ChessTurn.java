package chess;

public class ChessTurn {
    final Coord moveFrom;
    final Coord moveTo;
    final boolean isCheck;

    public ChessTurn(Coord from, Coord to, boolean check){
        this.moveFrom = from;
        this.moveTo = to;
        isCheck = check;
    }


}