package br.com.decioluckow.divulgit.controller;


import br.com.decioluckow.divulgit.restcaller.GitLabRestCaller;
import br.com.decioluckow.divulgit.model.Customer;
import br.com.decioluckow.divulgit.service.CustomerService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private GitLabRestCaller restCaller;

    @GetMapping("customer/{customerId}")
    public Customer getCustomer(@PathVariable Integer customerId) {
        log.info("Inside CustomerController........");

        return customerService.getCustomerById(customerId);
    }
}