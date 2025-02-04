package com.arkivanov.sample.dynamicfeatures.shared.root.dynamicfeature

import com.arkivanov.decompose.RouterState
import com.arkivanov.decompose.value.Value

interface DynamicFeature<out T : Any> {

    val routerState: Value<RouterState<*, Child<T>>>

    sealed interface Child<out T : Any> {
        class Loading(val name: String) : Child<Nothing>
        class Feature<out T : Any>(val feature: T) : Child<T>
        class Error(val name: String) : Child<Nothing>
    }
}
