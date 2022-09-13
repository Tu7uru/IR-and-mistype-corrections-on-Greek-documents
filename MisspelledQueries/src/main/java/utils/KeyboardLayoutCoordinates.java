package utils;

public class KeyboardLayoutCoordinates {
    private final Integer row;
    private final Integer col;
    private final Integer upperCase;

    public KeyboardLayoutCoordinates(Integer row, Integer col,Integer upperCase) {
        this.row= row;
        this.col= col;
        this.upperCase = upperCase;
    }

    public Integer getCol() {
        return col;
    }

    public Integer getRow() {
        return row;
    }

    public Integer getUpperCase() {
        return upperCase;
    }
}
