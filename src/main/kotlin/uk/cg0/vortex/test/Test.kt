package uk.cg0.vortex.test

class Test {
    companion object {
        fun test() {
            val result = TestTable().select("id", "name", "email_address").where("name", "Connor").orWhere("name", "James").where("status", "active").get()
            println(result)
        }
    }
}