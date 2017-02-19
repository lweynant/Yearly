package com.lweynant.yearly.controller;

public interface IExtendeFragmentLifeCycle {
    //return true if you handled the backpress or the home press (I.e in this case the method call will
    // not be forwarded
    public boolean onBackPressed();
    public boolean onOptionsItemHomePressed();
}
