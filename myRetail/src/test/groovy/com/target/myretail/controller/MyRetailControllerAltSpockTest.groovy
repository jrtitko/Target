package com.target.myretail.controller

import com.target.myretail.MyRetailApplication
import com.target.myretail.model.Price
import com.target.myretail.model.Product
import com.target.myretail.repositories.PriceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.TestRestTemplate
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

/**
 * Created by jrtitko1 on 9/11/16.
 */

@SpringApplicationConfiguration(classes = MyRetailApplication.class)
@WebIntegrationTest
class MyRetailControllerAltSpockTest extends Specification {

    @Autowired
    PriceRepository priceRepository;

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
        RestTemplate restTemplate = new TestRestTemplate()
        def request = RequestEntity.get(new URI("http://localhost:8080/myRetail/products/13860428")).build()

        when:
        def response = restTemplate.exchange(request, Product)

        then:
        System.out.println("***** response head " + response.getHeaders())
        System.out.println("***** response body " + response.getBody())
        response.statusCode == org.springframework.http.HttpStatus.OK
        response.getHeaders().get("Content-Type").contains(MediaType.APPLICATION_JSON_UTF8_VALUE)
        Product product = response.getBody()
        product.getId() == 13860428
        product.getName() == 'The Big Lebowski (Blu-ray)'
        product.getCurrentPrice() != null
        product.getCurrentPrice().getValue() == 13.49
        product.getCurrentPrice().getCurrencyCode() == 'USD'
    }
}
