public class Triplet<L, M, R>  {
    private final L left;
    private final M mid;
    private final R right;

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
}
