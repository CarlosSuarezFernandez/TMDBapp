package com.carlosdev.tmdbapp

import com.carlosdev.tmdbapp.core.illegalState
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class FlowTest<T>(channel: Channel<T>) {

    private val iterator = channel.iterator()

    suspend fun next(): T = if (iterator.hasNext()) {
        iterator.next()
    } else illegalState()

    suspend fun last(): T = if (iterator.hasNext()) {
        var last: T? = null
        try {
            withTimeout(FLOW_TEST_TIMEOUT) {
                while (iterator.hasNext()) {
                    last = iterator.next()
                }
            }
        } catch (_: Throwable) {
            // no-op
        }

        last ?: illegalState()
    } else illegalState()

    suspend fun optNext(): T? = try {
        withTimeout(FLOW_TEST_TIMEOUT) {
            if (iterator.hasNext()) {
                iterator.next()
            } else null
        }
    } catch (_: Throwable) {
        null
    }
}

suspend fun <T> Flow<T>.test(body: suspend FlowTest<T>.() -> Unit) {
    val flow: Flow<T> = this@test
    val channel = Channel<T>(Channel.UNLIMITED, onBufferOverflow = BufferOverflow.SUSPEND)
    coroutineScope {
        val job = launch(start = CoroutineStart.UNDISPATCHED, context = Dispatchers.Unconfined) {
            flow.collect { item: T ->
                channel.send(item)
            }
            channel.close()
        }

        FlowTest(channel).run {
            body()
        }

        job.cancel()
    }
}

private const val FLOW_TEST_TIMEOUT = 100L