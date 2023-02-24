package net.rhseung.reimagined.utils

class Bunch<T, U> constructor(
	val basicType: T,
	val element: U
) {
	override fun toString(): String {
		return "Bunch(basicType=$basicType, element=$element)"
	}
	
	operator fun component1(): T {
		return basicType
	}
	
	operator fun component2(): U {
		return element
	}
	
	companion object {
		infix fun <T, U> U.belongTo(other: T): Bunch<T, U> {
			return Bunch(other, this)
		}
		
		infix fun <T, U> T.to(other: U): Bunch<T, U> {
			return Bunch(this, other)
		}
	}
}