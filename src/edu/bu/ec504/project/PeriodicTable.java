package edu.bu.ec504.project;

public enum PeriodicTable {

    H(1),
    C(6),
    N(7),
    O(8),
    S(16)
    ;


    private final int atomicNumber;
    PeriodicTable(int atomicNum){
        atomicNumber= atomicNum;
    }

    public int getAtomicNumber(){
        return atomicNumber;
    }
}
