package uk.cg0.vortex.database.exceptions

import uk.cg0.vortex.database.token.SqlToken
import java.lang.Exception

class DatabaseTokenPositionMismatchException (token: SqlToken): Exception("Database token position mismatch, token $token")