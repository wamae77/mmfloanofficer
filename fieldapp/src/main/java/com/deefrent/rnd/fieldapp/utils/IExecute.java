package com.deefrent.rnd.fieldapp.utils;

import retrofit2.Response;

public interface IExecute <T>{
    /** The callback to be executed. */
    void run(Response<T> result, Throwable t);
}
