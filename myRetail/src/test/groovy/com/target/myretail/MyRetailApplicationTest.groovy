package com.target.myretail

import com.mongodb.MongoClient
import spock.lang.Specification

/**
 * Created by jrtitko1 on 9/16/16.
 */
class MyRetailApplicationTest extends Specification {

    def myRetailApplication = new MyRetailApplication()

    def "mongo setup"() {
        given:
            def mongo = new MongoClient("localhost", 27017)
            def db = mongo.getDatabase("myRetail")

        when:
            myRetailApplication.init()

        then:
            def table = db.getCollection("price")
            table.count() == 5
    }
}
