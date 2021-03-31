package com.arkivanov.sample.counter.shared.counter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.instancekeeper.InstanceKeeper
import com.arkivanov.decompose.instancekeeper.getOrCreate
import com.arkivanov.decompose.statekeeper.*
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.decompose.value.reduce
import com.arkivanov.sample.counter.shared.counter.Counter.Model
import com.badoo.reaktive.disposable.scope.DisposableScope
import com.badoo.reaktive.maybe.maybeTimer
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.repeatWhen
import com.badoo.reaktive.single.singleOf

class CounterComponent(
    componentContext: ComponentContext,
    index: Int
) : Counter, ComponentContext by componentContext {

    private val handler =
        instanceKeeper.getOrCreate(KEY_STATE) {
            Handler(stateKeeper.consume(KEY_STATE) ?: State(index = index))
        }

    override val model: Value<Model> = handler.state.map { it.toModel() }

    init {
        stateKeeper.register(KEY_STATE) { handler.state.value }
    }

    private fun State.toModel(): Model =
        Model(
            text = "Counter${index}: ${count.toString().padStart(length = 3, padChar = '0')}"
        )

    private companion object {
        private const val KEY_STATE = "STATE"
    }

    @Parcelize
    private data class State(
        val index: Int,
        val count: Int = 0
    ) : Parcelable {
        override fun asHolder(): ParcelableHolder = Holder(this)

        private class Holder(override val value: State) : ParcelableHolder() {
            override fun encodeWithCoder(coder: NSCoder) {
                coder.encodeInt_(value.index, "index")
                coder.encodeInt_(value.count, "count")
            }

            override fun initWithCoder(coder: NSCoder): Holder =
                Holder(
                    State(
                        index = coder.decodeIntForKey_("index"),
                        count = coder.decodeIntForKey_("count")
                    )
                )
        }
    }

    private class Handler(initialState: State) : InstanceKeeper.Instance, DisposableScope by DisposableScope() {
        val state = MutableValue(initialState)

        init {
            singleOf(Unit)
                .repeatWhen { _, _ -> maybeTimer(250L, mainScheduler) }
                .subscribeScoped(isThreadLocal = true) {
                    state.reduce { it.copy(count = it.count + 1) }
                }
        }

        override fun onDestroy() {
            dispose()
        }
    }
}
