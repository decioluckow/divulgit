package br.com.decioluckow.divulgit.service.impl;

import br.com.decioluckow.divulgit.model.Customer;
import br.com.decioluckow.divulgit.repository.CustomerRepository;
import br.com.decioluckow.divulgit.service.CustomerService;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Customer getCustomerById(Integer customerId) {
        logger.info("Inside CustomerServiceImpl........");
        Customer customer = customerRepository.getCustomerById(customerId);
        return customer;
    }

}