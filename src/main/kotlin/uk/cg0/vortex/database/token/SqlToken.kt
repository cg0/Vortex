package uk.cg0.vortex.database.token

enum class SqlToken {
    SELECT,
    INSERT,
    WHERE,
    WHERE_EXTENDED,
    WHERE_NOT,
    WHERE_NOT_EXTENDED,
    OR_WHERE,
    OR_WHERE_EXTENDED,
    OR_WHERE_NOT,
    OR_WHERE_NOT_EXTENDED,
    LIMIT,
    OFFSET,
    ORDER_BY,
    ORDER_BY_EXTENDED,
    NONE
}