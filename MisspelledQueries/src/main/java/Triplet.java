public class Triplet {
        public final Integer row;
        public final Integer col;
        public final Integer upperCase;

        public Triplet(Integer row, Integer col,Integer upperCase) {
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
