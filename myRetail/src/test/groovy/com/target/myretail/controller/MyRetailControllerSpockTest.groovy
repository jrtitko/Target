package com.target.myretail.controller

import com.target.myretail.MyRetailApplication
import com.target.myretail.model.Price
import com.target.myretail.repositories.PriceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * Created by jrtitko1 on 9/11/16.
 */

@SpringApplicationConfiguration(classes = MyRetailApplication.class)
@WebIntegrationTest
class MyRetailControllerSpockTest extends Specification {

    @Autowired
    PriceRepository priceRepository;

    @Autowired
    MyRetailController myRetailController;

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
        given:
        def mockMvc = MockMvcBuilders.standaloneSetup(myRetailController).build()

        when:
        def response = mockMvc.perform(get("/myRetail/products/13860428"))

        then:
        response
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath('$.id', is(13860428)))
            .andExpect(jsonPath('$.name', is('The Big Lebowski (Blu-ray)')))
            .andExpect(jsonPath('$.current_price', notNullValue()))
            .andExpect(jsonPath('$.current_price.value', is(13.49d)))
            .andExpect(jsonPath('$.current_price.currency_code', is('USD')))
    }
}
