package com.flowdb

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class TokenModel(val tokenName:String,
                      val tokenValue:Int)