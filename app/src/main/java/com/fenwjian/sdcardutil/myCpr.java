package com.fenwjian.sdcardutil;

//参见plod->mdict->myCpr
//swappable-comparable-updatable

import android.support.annotation.NonNull;

public class myCpr<T1 extends Comparable<T1>,T2 extends Comparable<T2>> implements Updatable{
    public T1 key;
    public T2 value;
    public myCpr(T1 k,T2 v){
        key=k;value=v;
    }
    public int compareTo(myCpr<T1,T2> other) {
        //if()
        return this.key.compareTo(other.key);
        //else
        //	return
    }
    public String toString(){
        return key+"_"+value;
    }

    public myCpr<T2,T1> Swap() {
        return new myCpr<T2,T1>(value,key);
    }

    @Override
    public void Update(Updatable o) {
        value = ((myCpr<T1,T2>)o).value;
    }

    @Override
    public int compareTo(@NonNull Updatable o) {
        return compareTo((myCpr<T1,T2> )o);
    }

    public myCpr<T1,T2> copy() {
        return new myCpr<T1,T2>(key,value);
    }
}