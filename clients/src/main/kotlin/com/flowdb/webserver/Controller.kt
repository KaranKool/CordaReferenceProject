package com.flowdb.webserver

import com.flowdb.AddTokenValueFlow
import com.flowdb.QueryTokenValueFlow
import com.flowdb.TokenModel
import com.flowdb.UpdateTokenValueFlow
import net.corda.core.concurrent.match
import net.corda.core.messaging.FlowHandle
import net.corda.core.utilities.getOrThrow
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Define your API endpoints here.
 */
@CrossOrigin
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
class Controller(rpc: NodeRPCConnection) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy

    @GetMapping(value = ["/queryToken"], produces = arrayOf("application/json"))
    private fun queryTokenFun(req: RequestEntity<TokenModel>): ResponseEntity<Any> {
        logger.info("Accessing api for Querying Token In DB")
        val flowHandle: FlowHandle<Int> =proxy.startFlowDynamic(
            QueryTokenValueFlow::class.java,
            req.body.tokenName
        )
        try{
            val res = flowHandle.use { flowHandle.returnValue.getOrThrow() }
            val result = TokenModel(req.body.tokenName,res)
            logger.info("Query Successful for Token:"+req.body.tokenName)
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(result)
        }
        catch (e:Throwable){
            logger.error(e.message)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.message)
        }

    }

    @PostMapping(value = ["/addToken"], produces = arrayOf("application/json"))
    private fun addTokenToDB(req: RequestEntity<TokenModel>): ResponseEntity<String> {
        logger.info("Accessing api for Adding Token to DB")
        val flowHandle = proxy.startFlowDynamic(
            AddTokenValueFlow::class.java,
            req.body.tokenName,
            req.body.tokenValue
        )
        flowHandle.use { flowHandle.returnValue.getOrThrow() }
        logger.info("Token:${req.body.tokenName} with Value:${req.body.tokenValue} successfully added to DB")
        return ResponseEntity
                .status(201)
                .body("Token:${req.body.tokenName} with Value:${req.body.tokenValue} successfully added to DB")
    }

    @PostMapping(value = ["/updateToken"], produces = arrayOf("application/json"))
    private fun updateTokeInDB(req: RequestEntity<TokenModel>): ResponseEntity<String> {
        logger.info("Accessing api for Updating Token to DB")
        val flowHandle = proxy.startFlowDynamic(
                UpdateTokenValueFlow::class.java,
                req.body.tokenName,
                req.body.tokenValue
        )
        flowHandle.use { flowHandle.returnValue.getOrThrow() }
        logger.info("Token:${req.body.tokenName} with Value:${req.body.tokenValue} successfully updated to DB")
        return ResponseEntity
                .status(201)
                .body("Token:${req.body.tokenName} with Value:${req.body.tokenValue} successfully updated to DB")
    }
}