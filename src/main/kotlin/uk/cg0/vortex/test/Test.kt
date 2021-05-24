package uk.cg0.vortex.test

class Test {
    companion object {
        fun test() {
            val result = TestTable().select("id", "name", "email_address").where("name", "Connor").orWhere("name", "James").where("status", "active").get()
            println(result.first()["name"])

            val data = HashMap<String, String>()
            data["name"] = "Connor"
            data["email_address"] = "thecg1997@gmail.com"
            data["status"] = "active"
            data["pos1"] = "10"
            data["pos2"] = "1001"
            val insert = TestTable().insert(data)
        }
    }
}