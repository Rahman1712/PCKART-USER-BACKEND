package com.ar.pckart.order.service;

import lombok.RequiredArgsConstructor;

//@Service
@RequiredArgsConstructor
public class OrderPaymentService {

/*
	private final WebClient webClient;
	
	@Value("${payment.service.api.url.create}")
	private String PAYMENT_SERVICE_URL_CREATE;
	
	@Value("${payment.service.api.url.save}")
	private String PAYMENT_SERVICE_URL_SAVE;
	
	public TransactionDetails createTransaction(Double amount, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION); //"Bearer TOKEN"
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request to Payments Service : Before No Token");
			return null;
		}
		
		TransactionDetails transactionDetails = webClient.get()
				.uri(PAYMENT_SERVICE_URL_CREATE + amount)
				.header(HttpHeaders.AUTHORIZATION, authHeader)
				.retrieve()
				.onStatus(HttpStatusCode::isError, res-> res.createError())
				.bodyToMono(TransactionDetails.class)
				.block();
		
		return transactionDetails;
	}

	public Payment savePayment(Payment payment) {
		return null;
	}
	
	*/
}
