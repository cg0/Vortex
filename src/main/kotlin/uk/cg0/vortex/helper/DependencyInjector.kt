package uk.cg0.vortex.helper

import java.lang.Exception
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

fun <T> injectFunction(kFunction: KFunction<T>, providedArguments: HashMap<String, Any>): T {
    val expectedFunctionArguments = kFunction.parameters
    val injectedArguments = HashMap<KParameter, Any>()

    for (argument in expectedFunctionArguments) {
        if (argument.name in providedArguments) {
            // If we provided the arguments then inject them as a priority
            injectedArguments[argument] = providedArguments[argument.name] ?: continue
        } else {
            // If we did not provide the arguments then we should attempt to create them
            // Right now we're not gonna do much to create classes, only attempt if they have no arguments
            val argumentClass = argument.type.classifier as KClass<*>
            var foundConstructor = false
            for (constructor in argumentClass.constructors) {
                if (constructor.parameters.isEmpty()) {
                    injectedArguments[argument] = injectFunction(constructor, HashMap())
                    foundConstructor = true
                }
            }

            if (!foundConstructor) {
                if (!argument.isOptional) {
                    throw Exception("Unable to inject variable into function: ${argument.name}")
                }
            }
        }
    }

    return kFunction.callBy(injectedArguments)
}

fun <T> injectConstructor(kClass: KClass<*>, providedArguments: HashMap<String, Any>): T {
    val instance = injectFunction(kClass.primaryConstructor as KFunction<Any>, providedArguments)
    return instance as T
}