package com.ar.pckart.product.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.ar.pckart.order.model.OrderProduct;
import com.ar.pckart.product.config.JwtProductsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final WebClient webClient;
	private final JwtProductsService jwtProductsService;
	
	@Value("${product.service.api.url.get-quantity-byid}")
	private String PRODUCT_SERVICE_URL_GET_QUANTIY_BYID;
	
	@Value("${product.service.api.url.change-products-quantity}")
	private String PRODUCT_SERVICE_URL_UPDATE_PRODUCTS_QUANTIY;
	
	public Integer getProductById(Long id) {
		String productQuantity = webClient.get()
				.uri(PRODUCT_SERVICE_URL_GET_QUANTIY_BYID + id)
				.retrieve()
				.onStatus(HttpStatusCode::isError, response-> response.createError())
				.bodyToMono(String.class)
				.block();
		return Integer.parseInt(productQuantity);
	}
	
	public String updateProductsQuantiy(List<OrderProduct> orderProducts, String type) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder
				.fromUriString(PRODUCT_SERVICE_URL_UPDATE_PRODUCTS_QUANTIY)
				.queryParam("type", type); // ADD, DELETE
		
		String uriPathWithQueryParams = uriBuilder.toUriString();
		
		return  webClient.put()
				.uri(uriPathWithQueryParams)
				.body(BodyInserters.fromValue(orderProducts))
				.header(HttpHeaders.AUTHORIZATION,
						"Bearer "+jwtProductsService.generateToken(getAuthenticationUsername()))
				.header("Username", getAuthenticationUsername())
				.retrieve()
				.onStatus(HttpStatusCode::isError, response-> response.createError())
				.bodyToMono(String.class)
				.block();
	}
	
	public static String getAuthenticationUsername() {
		Authentication authentication = SecurityContextHolder
				.getContext().getAuthentication();
		return authentication.getName();
	}
}
