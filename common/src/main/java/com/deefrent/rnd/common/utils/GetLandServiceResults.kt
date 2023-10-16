package com.deefrent.rnd.common.utils

import org.json.JSONObject

/**
 * The interface Presentment results.
 */
interface GetLandServiceResults {
    /**
     * On success results.
     *
     * @param value the value
     */
    fun onSuccessResults(value: JSONObject)

    /**
     * On error results.
     *
     * @param throwable the throwable
     */
    fun onErrorResults(throwable: String)
}
