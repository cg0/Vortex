package uk.cg0.vortex.database.exceptions

import uk.cg0.vortex.database.token.SqlToken
import java.lang.Exception

class UnprocessableDatabaseTokenException (token: SqlToken): Exception("Unprocessable token: $token")