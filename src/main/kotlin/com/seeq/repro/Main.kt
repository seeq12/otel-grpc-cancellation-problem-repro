package com.seeq.repro

import io.grpc.ServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    val port = 50051
    val server = ServerBuilder.forPort(port)
        .addService(ExampleService())
        .addService(ProtoReflectionService.newInstance())
        .build()
        .start();

    Runtime.getRuntime().addShutdownHook(thread(start = false) {
        try {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            e.printStackTrace(System.err)
        }
    })

    println("Started server on port $port")
    server.awaitTermination()
}
