package com.target.myretail.controller

import com.target.myretail.MyRetailApplication
import com.target.myretail.model.Price
import com.target.myretail.repositories.PriceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import spock.lang.Unroll

import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Created by jrtitko1 on 9/11/16.
 */

@SpringApplicationConfiguration(classes = MyRetailApplication.class)
@WebIntegrationTest
class MyRetailControllerSpockTest extends Specification {

    @Autowired
    PriceRepository priceRepository;

//    def myRetailService = new MyRetailService()

//    def myRetailController = new MyRetailController(myRetailService: myRetailService)
    def myRetailController = new MyRetailController()

    def mockMvc = MockMvcBuilders.standaloneSetup(myRetailController).build()

    Price origPrice;

    def setup() {
        origPrice = priceRepository.findOne(13860428);
        priceRepository.save(new Price(13860428,  13.49, 'USD'));
    }

    def cleanup() {
        if (origPrice != null) {
            priceRepository.save(origPrice);
        } else {
            priceRepository.delete(13860428);
        }
    }

    def "get product"() {
        expect:
        def response = mockMvc.perform(get("/myRetail/products/13860428"))

//        then:
//        1 * myRetailService.getProduct(13860428)

        response
            .andExpect(status().isOk())
//            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath('$.id', is(13860428)))
            .andExpect(jsonPath('$.name', is('The Big Lebowski (Blu-ray)')))
            .andExpect(jsonPath('$.current_price', notNullValue()))
            .andExpect(jsonPath('$.current_price.value', is(13.49)))
            .andExpect(jsonPath('$.current_price.currency_code', is('USD')))
    }

    @Unroll
    def "minimum of #a and #b is #c"() {
        expect:
        Math.min(a, b) == c

        where:
        a | b || c
        3 | 7 || 3
        5 | 4 || 4
        9 | 9 || 9
    }

}
