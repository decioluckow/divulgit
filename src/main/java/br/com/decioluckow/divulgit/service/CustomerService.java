package br.com.decioluckow.divulgit.service;

import br.com.decioluckow.divulgit.model.Customer;

public interface CustomerService {

    public Customer getCustomerById(Integer customerId);
}