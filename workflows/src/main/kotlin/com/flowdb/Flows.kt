package com.flowdb

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.utilities.ProgressTracker

const val TABLE_NAME = "crypto_values"

@InitiatingFlow
@StartableByRPC
class AddTokenValueFlow(private val token: String, private val value: Int) : FlowLogic<Unit>() {
    override val progressTracker: ProgressTracker = ProgressTracker()

    @Suspendable
    override fun call() {
        val databaseService = serviceHub.cordaService(CryptoValuesDatabaseService::class.java)
        databaseService.addTokenValue(token, value)
    }
}


@InitiatingFlow
@StartableByRPC
class UpdateTokenValueFlow(private val token: String, private val value: Int) : FlowLogic<Unit>() {
    override val progressTracker: ProgressTracker = ProgressTracker()

    @Suspendable
    override fun call() {
        val databaseService = serviceHub.cordaService(CryptoValuesDatabaseService::class.java)
        databaseService.updateTokenValue(token, value)
    }
}


@InitiatingFlow
@StartableByRPC
class DeleteTokenValueFlow(private val token: String)   : FlowLogic<Unit>() {
    override val progressTracker: ProgressTracker = ProgressTracker()

    @Suspendable
    override fun call() {
        val databaseService = serviceHub.cordaService(CryptoValuesDatabaseService::class.java)
        return databaseService.deleteTokenValue(token)
    }
}


@InitiatingFlow
@StartableByRPC
class QueryTokenValueFlow(private val token: String) : FlowLogic<Int>() {
    override val progressTracker: ProgressTracker = ProgressTracker()

    @Suspendable
    override fun call(): Int {
        val databaseService = serviceHub.cordaService(CryptoValuesDatabaseService::class.java)
        return databaseService.queryTokenValue(token)
    }
}