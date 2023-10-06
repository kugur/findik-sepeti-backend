package com.kolip.findiksepeti.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService{
    Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public boolean pay(Payment payment) {
        logger.info("Dummy payment implementation, always return true");
        return true;
    }
}
