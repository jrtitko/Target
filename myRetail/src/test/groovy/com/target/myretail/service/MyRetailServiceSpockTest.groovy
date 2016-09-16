package com.target.myretail.service

import com.target.myretail.model.Price
import com.target.myretail.model.Product
import com.target.myretail.model.productapi.Item
import com.target.myretail.model.productapi.OnlineDescription
import com.target.myretail.model.productapi.ProductCompositeResponse
import com.target.myretail.model.productapi.ProductResponse
import com.target.myretail.repositories.PriceRepository
import mockit.Expectations
import mockit.Mock
import mockit.MockUp
import mockit.Mocked
import mockit.integration.junit4.JMockit
import org.apache.log4j.Logger
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import spock.lang.*
/**
 * Created by jrtitko1 on 9/11/16.
 */
class MyRetailServiceSpockTest extends Specification {

    def productURL = 'https://dummyURL'

    def mockPriceRepository = Mock(PriceRepository)

    def mockLogger = Mock(Logger)

    def mockRestTemplate = Mock(RestTemplate)

    def myRetailService = new MyRetailService(
            productURL: productURL,
            priceRepository: mockPriceRepository,
            log: mockLogger,
            restTemplate: mockRestTemplate
    )

    def "build product"() {
        given:
        def productId = 13860428
        def price = new Price(13860428, 19.99, "USD")
        def productResponse = buildProductResponse()

        when:
        Product product = myRetailService.getProduct(productId)

        then:
        1 * mockRestTemplate.getForObject(_, _) >> productResponse
        1 * mockPriceRepository.findOne(productId) >> price
        1 * mockLogger.info(_ as String)

        product != null
        product.getId() == Integer.valueOf(productId)
        product.getName() == 'Hewy Lewis & The News Greatest Hits'
        product.getCurrentPrice() != null
        product.getCurrentPrice().getId() == Integer.valueOf(productId)
        product.getCurrentPrice().getValue() == 19.99
        product.getCurrentPrice().getCurrencyCode() == 'USD'
    }

    def "build product where the product is not found"() {
        given:
        def productId = 13860428
        def productResponse = buildProductResponseForInvalidProduct()

        when:
        Product product = myRetailService.getProduct(productId)

        then:
        1 * mockRestTemplate.getForObject(_, _) >> productResponse
        0 * mockPriceRepository.findOne(_ as Integer)
        1 * mockLogger.info(productResponse.toString())
        1 * mockLogger.warn("ProductID: 13860428 => Item Online Description is unavailable")

        product != null
        product.getId() == Integer.valueOf(productId)
        product.getName() == null
        product.getCurrentPrice() == null
    }

    def "build product where the price is not found"() {
        given:
        def productId = 13860428
        def productResponse = buildProductResponse()
        def price = null

        when:
        Product product = myRetailService.getProduct(productId)

        then:
        1 * mockRestTemplate.getForObject(_, _) >> productResponse
        1 * mockPriceRepository.findOne(_ as Integer) >> price
        1 * mockLogger.info(productResponse.toString())
        1 * mockLogger.warn("ProductID: 13860428 => Pricing is unavailable")

        product != null
        product.getId() == Integer.valueOf(productId)
        product.getName() == 'Hewy Lewis & The News Greatest Hits'
        product.getCurrentPrice() == null
    }

    private ProductResponse buildProductResponse() {
        OnlineDescription onlineDescription = new OnlineDescription();
        onlineDescription.setValue("Hewy Lewis & The News Greatest Hits");
        Item item = new Item();
        item.setOnlineDescription(onlineDescription);
        List<Item> items = Arrays.asList(item);
        ProductCompositeResponse productCompositeResponse = new ProductCompositeResponse();
        productCompositeResponse.setItems(items);
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductCompositeResponse(productCompositeResponse);
        return productResponse;
    }

    private ProductResponse buildProductResponseForInvalidProduct() {
        Item item = new Item();
        item.setOnlineDescription(null);
        List<Item> items = Arrays.asList(item);
        ProductCompositeResponse productCompositeResponse = new ProductCompositeResponse();
        productCompositeResponse.setItems(items);
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductCompositeResponse(productCompositeResponse);
        return productResponse;
    }

}
