package com.seeq.repro

import ExampleGrpcKt
import ExampleOuterClass
import io.grpc.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class ExampleService : ExampleGrpcKt.ExampleCoroutineImplBase() {
    companion object {
        fun doSomeWork() = CompletableFuture<Boolean>().also {
            val context = Context.current()
            val workerThread = thread(start = false, isDaemon = true) {
                println("Started some work...")
                Thread.sleep(5000)  // Fake some long-running work
                it.complete(true)
                println("...completed some work.")
            }

            // This listener will run even if the request isn't canceled, but still serves to demonstrate this behavior.
            val listener = Context.CancellationListener {
                println("Cancelling work...")
                workerThread.interrupt()
                println("...work canceled.")
            }
            context.addListener(listener) { it.run() }

            workerThread.start()
        }
    }

    override fun doThing(request: ExampleOuterClass.Thing): Flow<ExampleOuterClass.Stuff> {
        println("Received ${request.message}")
        val future = doSomeWork()
        val result = ExampleOuterClass.Stuff.newBuilder()
            .setMessage("did a thing")
            .setOtherWorkCompleted(future.get(5, TimeUnit.SECONDS))
            .build()
        return flow {
            emit(result)
        }
    }
}
