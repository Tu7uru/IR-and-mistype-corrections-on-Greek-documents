package utils;

import java.util.ArrayList;
import java.util.Comparator;

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

    // Usage of comparator
    public static Comparator<Triplet> OnTripletRight = new Comparator<Triplet>() {

        // Comparing attributes of students
        public int compare(Triplet t1, Triplet t2) {
            int ED1 = (int)t1.right;
            int ED2 = (int)t2.right;

            // Returning in ascending order
            return ED1 - ED2;
        }
    };

//    public ArrayList<Triplet> sortOnEditDistance(ArrayList<Triplet> results) {
//        ArrayList<Triplet> sortedResults = new ArrayList<>();
//
//        Collections.sort(results, Triplet);
//        for(int index1 = 0; index1 < results.size(); index1++ ) {
//            for(int index2 = 0; index2 < results.size(); index2++) {
//                if(results.get(index1).right )
//            }
//        }
//    }
}
