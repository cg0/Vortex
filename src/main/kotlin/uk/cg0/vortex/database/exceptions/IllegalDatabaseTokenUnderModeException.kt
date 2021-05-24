package uk.cg0.vortex.database.exceptions

import uk.cg0.vortex.database.QueryBuilder
import uk.cg0.vortex.database.token.SqlToken
import java.lang.Exception

class IllegalDatabaseTokenUnderModeException (token: SqlToken, mode: QueryBuilder.DatabaseMode):
    Exception("Token $token illegal under mode $mode")