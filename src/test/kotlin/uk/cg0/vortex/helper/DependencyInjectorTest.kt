package uk.cg0.vortex.helper

import junit.framework.TestCase.*
import org.junit.Test
import java.lang.Exception

class DependencyInjectorTest {
    @Test
    fun `Can we inject a basic function with no args`() {
        assertTrue(injectFunction(DependencyInjectorTest::returnTrue, HashMap()))
        assertFalse(injectFunction(DependencyInjectorTest::returnFalse, HashMap()))
    }

    @Test
    fun `Can we inject a function with a variable arg`() {
        val variables = HashMap<String, Any>()
        variables["x"] = true

        assertTrue(injectFunction(DependencyInjectorTest::returnX, variables))

        variables["x"] = false
        assertFalse(injectFunction(DependencyInjectorTest::returnX, variables))
    }

    @Test
    fun `Can we inject a function with a provided complex object`() {
        val variables = HashMap<String, Any>()
        variables["complexObject"] = ComplexObject("foo", 0)
        variables["foo"] = "foo"
        variables["bar"] = 0

        assertTrue(injectFunction(DependencyInjectorTest::testComplexObject, variables))
    }

    @Test
    fun `Will injecting a function with a non provided complex object fail`() {
        val variables = HashMap<String, Any>()
        variables["foo"] = "foo"
        variables["bar"] = 0

        try {
            injectFunction(DependencyInjectorTest::testComplexObject, variables)
            assert(false)
        } catch (exception: Exception) {
            assert(true)
        }
    }

    @Test
    fun `Can we inject a non-provided basic object to be generated`() {
        assertTrue(injectFunction(DependencyInjectorTest::testBasicObject, HashMap()))
    }

    fun returnTrue(): Boolean {
        return true
    }

    fun returnFalse(): Boolean {
        return false
    }

    fun returnX(x: Boolean): Boolean {
        return x
    }

    fun testComplexObject(complexObject: ComplexObject, foo: String, bar: Int): Boolean {
        return complexObject.foo == foo && complexObject.bar == bar
    }

    fun testBasicObject(basicObject: BasicObject): Boolean {
        return true
    }

    data class ComplexObject(val foo: String, val bar: Int)
    class BasicObject()
}