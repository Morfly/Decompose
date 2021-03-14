package com.arkivanov.decompose

import com.arkivanov.decompose.statekeeper.Parcelable
import kotlin.reflect.KClass

interface RouterFactory {

    /**
     * Creates a new [Router].
     *
     * @param initialConfiguration a configuration of a component that should be displayed if there is no saved state
     * @param initialBackStack a stack of component configurations that should be set as back stack if there is no saved state
     * @param configurationClass a [KClass] of the component configurations
     * @param key a key of the [Router], should be unique if there are multiple [Router]s in the same component
     * @param onRestoreStack intercepts the component stack restoration, if the returned list is null then the restored stack is discarded
     * @param handleBackButton determines whether the [Router] should handle back button clicks or not
     * @param componentFactory a factory function that creates new instances of components
     * @return a new instance of [Router]
     */
    fun <C : Parcelable, T : Any> router(
        initialConfiguration: () -> C,
        initialBackStack: () -> List<C> = ::emptyList,
        configurationClass: KClass<out C>,
        key: String = "DefaultRouter",
        onRestoreStack: (List<C>) -> List<C>? = { it },
        handleBackButton: Boolean = false,
        componentFactory: (configuration: C, ComponentContext) -> T
    ): Router<C, T>
}
