package br.com.decioluckow.divulgit.repository;

import br.com.decioluckow.divulgit.model.Customer;

public interface CustomerRepository {
    public Customer getCustomerById(Integer customerId);
}