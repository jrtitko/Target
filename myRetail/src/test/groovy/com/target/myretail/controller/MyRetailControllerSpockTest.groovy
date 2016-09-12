package com.target.myretail.controller

import com.target.myretail.MyRetailApplication
import com.target.myretail.model.Price
import com.target.myretail.repositories.PriceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.mock.http.MockHttpOutputMessage
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.hamcrest.Matchers.*
import static org.junit.Assert.assertNotNull
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
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

    def mockMvc

    Price origPrice;

    @SuppressWarnings("rawtypes")
    HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        mappingJackson2HttpMessageConverter = converters.find {
            it instanceof MappingJackson2HttpMessageConverter
        }

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(myRetailController).build()

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

    def "GET product"() {
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

    def "GET product where the product does not exist at the remote site"() {
        when:
        def response = mockMvc.perform(get("/myRetail/products/99999999"))

        then:
        response
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath('$.id', is(99999999)))
            .andExpect(jsonPath('$').value(not(hasKey('name'))))
            .andExpect(jsonPath('$').value(not(hasKey('current_price'))))
    }

    def "GET product where the price does not exist in our databaes"() {
        expect:
        mockMvc.perform(get("/myRetail/products/16752456"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath('$.id', is(16752456)))
                .andExpect(jsonPath('$.name', is('Lego&reg; Super Heroes The Tumbler 76023')))
                .andExpect(jsonPath('$').value(not(hasKey('current_price'))))
    }

    def "POST product"() {
        given:
        def productId = 13860428
        Price newPrice = new Price(13860428, 99.99, "USD")

        when:
        def response = mockMvc.perform(post("/myRetail/products/" + productId)
                .content(json(newPrice))
                .contentType(MediaType.APPLICATION_JSON_UTF8))

        then:
        response.andExpect(status().isOk());

        when:
        Price afterPrice = priceRepository.findOne(productId);

        then:
        afterPrice != null
        afterPrice.getValue() == 99.99
    }

    def "POST product with mismatched price id"() {
        given:
        def productId = 13860428
        Price beforePrice = priceRepository.findOne(productId)
        Price newPrice = new Price(99999999, 99.99, "USD");

        when:
        def response = mockMvc.perform(post("/myRetail/products/" + productId)
                .content(json(newPrice))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
        then:
        def e = thrown(Exception)
        e.getCause().getMessage().contains("A price update")

        when:
        Price afterPrice = priceRepository.findOne(productId)

        then:
        afterPrice != null
        beforePrice.getValue() == afterPrice.getValue()

    }

    @SuppressWarnings("unchecked")
    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
