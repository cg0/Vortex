package uk.cg0.vortex.database.attribute

interface DatabaseAttribute {
    fun getAttribute(): String
    fun getType(): DatabaseAttributeType
}