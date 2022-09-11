public class Triplet<L, M, R>  {
    private L left;
    private M mid;
    private R right;

    public Triplet() {
    }

    public Triplet(L left, M mid,R right) {
        this.left= left;
        this.mid= mid;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public M getMid() {
        return mid;
    }

    public R getRight() {
        return right;
    }

    public void setLeft(L left) {
        this.left = left;
    }

    public void setMid(M mid) {
        this.mid = mid ;
    }

    public void setRight(R right) {
        this.right = right;
    }
}
