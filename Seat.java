// Cooper Kelley (clk200002)
import java.io.*;
import java.util.*;

public class Seat {

    private int row;
    private char seat, type;

    Seat() {
        row = -1;
        seat = '0';
        type = '0';
    }

    Seat(int r, char s, char t) {
        row = r;
        seat = s;
        type = t;
    }

    void setRow(int r) { row = r; }
    void setSeat(char s) { seat = s; }
    void setType(char t) { type = t; }

    int getRow() { return row; }
    char getSeat() { return seat; }
    char getType() { return type; }

    
}
