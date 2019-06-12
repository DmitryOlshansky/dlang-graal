package org.dlang.dmd.root

import junit.framework.TestCase


data class int(val v: Int) : RootObject() {
    override fun toChars() = BytePtr("$v".toByteArray(), 0)
}

class TestDArray : TestCase() {

    fun testInsert() {
        val arr = darrayOf<int>(int(1), int(2), int(3))
        // in front
        arr.insert(0, int(0))
        assertEquals(arr, darrayOf<int>(int(0), int(1), int(2), int(3)))
        // at end
        arr.insert(arr.length, int(5))
        assertEquals(arr, darrayOf<int>(int(0), int(1), int(2), int(3), int(5)))
        // middle
        arr.insert(4, int(4))
        assertEquals(arr, darrayOf<int>(int(0), int(1), int(2), int(3), int(4), int(5)))
    }

    fun testRemove() {
        val arr = darrayOf<int>(int(1), int(2), int(3))
        arr.remove(0)
        assertEquals(arr, darrayOf<int>(int(2), int(3)))
        arr.remove(arr.length-1)
        assertEquals(arr, darrayOf<int>(int(2)))
        arr.remove(0)
        assertEquals(arr, darray<int>(0))

        val arr2 = darrayOf<int>(int(1), int(2), int(3), int(4))
        arr2.remove(1)
        assertEquals(arr2, darrayOf<int>(int(1), int(3), int(4)))
    }

    fun testPushShift() {
        val arr = darray<int>(0)
        arr.push(int(1))
        assertEquals(arr, darrayOf<int>(int(1)))
        arr.push(int(2))
        assertEquals(arr, darrayOf<int>(int(1), int(2)))
        arr.shift(int(0))
        assertEquals(arr, darrayOf<int>(int(0), int(1), int(2)))
    }

    fun testPushSlice() {
        val arr1 = darrayOf<int>(int(1), int(2))
        val arr2 = darrayOf<int>(int(3), int(4), int(5))
        arr1.pushSlice(arr2.slice())
        assertEquals(arr1, darrayOf<int>(int(1), int(2),int(3), int(4), int(5)))
    }

    fun testPop() {
        val arr =  darrayOf<int>(int(1), int(2))
        assertEquals(arr.pop(), int(2))
        assertEquals(arr.pop(), int(1))
        arr.push(int(3))
        assertEquals(arr.pop(), int(3))
    }

    fun testZero() {
        val arr =  darrayOf<int>(int(1), int(2))
        arr.zero()
        assertEquals(arr, darrayOf<int>(null, null))
        assertEquals(arr.toString(), "[null, null]")
    }

    fun testSlice() {
        val arr = darrayOf<int>(int(1), int(2), int(3))
        val el = darrayOf<int>(int(2))
        assertEquals(arr.slice(1, 2), el.slice())
    }

    fun testSplit() {
        val array = darray<int>(0)
        split(array, 0, 0)
        assertEquals(darray<int>(), array)
        array.push(int(1)).push(int(3))
        split(array, 1, 1)
        array[1] = int(2)
        assertEquals(darrayOf<int>(int(1), int(2), int(3)), array)
        split(array, 2, 3)
        array[2] = int(8)
        array[3] = int(20)
        array[4] = int(4)
        assertEquals(darrayOf<int>(int(1), int(2), int(8), int(20), int(4), int(3)), array)
        split(array, 0, 0)
        assertEquals(darrayOf<int>(int(1), int(2), int(8), int(20), int(4), int(3)), array)
        split(array, 0, 1)
        array[0] = int(123)
        assertEquals(darrayOf<int>(int(123), int(1), int(2), int(8), int(20), int(4), int(3)), array)
        split(array, 0, 3)
        array[0] = int(123)
        array[1] = int(421)
        array[2] = int(910)
        assert(darrayOf<int>(int(123), int(421), int(910), int(123), int(1), int(2), int(8), int(20), int(4), int(3)).slice() == peekSlice(array))
    }
}