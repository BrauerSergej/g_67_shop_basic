package app.service;

import app.domain.Customer;
import app.domain.Product;
import app.exceptions.CustomerNotFoundException;
import app.exceptions.CustomerSaveException;
import app.exceptions.CustomerUpdateException;
import app.repository.CustomerRepository;

import java.util.List;

public class CustomerService {

    private final CustomerRepository repository = new CustomerRepository();
    private final ProductService productService = new ProductService();


    //Сохранить покупателя в базе данных (при сохранении покупатель автоматически считается активным).
    public Customer save(Customer customer) {
        if (customer == null) {
            throw new CustomerSaveException("Имя покупателя не может быть null");
        }

        String name = customer.getName();
        if (name == null || name.trim().isEmpty()) {
            throw new CustomerSaveException("Имя покупателя не должно быть пустым");
        }
        customer.setActive(true);
        return repository.save(customer);
    }
    //Вернуть всех покупателей из базы данных (активных).
    public List<Customer> getAllActiveCustomers() {
        return repository.findAll()
                .stream()
                .filter(Customer::isActive)
                .toList();
    }

    //Вернуть одного покупателя из базы данных по его идентификатору (если он активен).
    public Customer getActiveCustomerById(Long id){
        Customer customer = repository.findById(id);

        if (customer == null || !customer.isActive()) {
            throw new CustomerNotFoundException(id);
        }

        return customer;
    }

    //Изменить одного покупателя в базе данных по его идентификатору.
    public void update(Long id, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new CustomerUpdateException("Имя покупателя не может быть пустым");
        }
        repository.update(id, newName);
    }

    //Удалить покупателя из базы данных по его идентификатору.
    public void deleteById(Long id){
        Customer customer = getActiveCustomerById(id);
        customer.setActive(false);
    }

    //Удалить покупателя из базы данных по его имени.
    public void deleteByName(String name) {
        getAllActiveCustomers()
                .stream()
                .filter(x -> x.getName().equals(name))
                .forEach(x -> x.setActive(false));
    }

    //Восстановить удалённого покупателя в базе данных по его идентификатору.
    public void restoreById(Long id) {
        Customer customer = repository.findById(id);

        if (customer == null) {
            throw new CustomerNotFoundException(id);
        }

        customer.setActive(true);
    }

    //Вернуть общее количество покупателей в базе данных (активных).
    public int getActiveCustomersNumber() {
        return getAllActiveCustomers().size();
    }

    //Вернуть стоимость корзины покупателя по его идентификатору (если он активен).
    public double getCustomersCartTotalCost(Long customerId) {
        return getActiveCustomerById(customerId)
                .getCart()
                .stream()
                .filter(Product::isActive)
                .mapToDouble(Product::getPrice)
                .sum();


    }

    //Вернуть среднюю стоимость продукта в корзине покупателя по его идентификатору (если он активен)
    public double getCustomersCartAveragePrice(Long customerId) {
        return getActiveCustomerById(customerId)
                .getCart()
                .stream()
                .filter(Product::isActive)
                .mapToDouble(Product::getPrice)
                .average()
                .orElse(0.0);
    }

    //Добавить товар в корзину покупателя по их идентификаторам (если оба активны)
    public void addProductToCustomersCart(Long customerId, Long productId) {
        Customer customer = getActiveCustomerById(customerId);
        Product product = productService.getActiveProductById(productId);
        customer.getCart().add(product);
    }

    //Удалить товар из корзины покупателя по их идентификаторам
    public void removeProductFromCustomersCart(Long customerId, Long productId) {
        // Подход 1. Удаление всех продуктов одного наименования их корзины.
//        Customer customer = getActiveCustomerById(customerId);
//        customer.getCart().removeIf(x -> x.getId().equals(productId));

        // Подход 2. Удаление только одного продукта нужного наименования.
        Customer customer = getActiveCustomerById(customerId);
        Product product = productService.getActiveProductById(productId);
        customer.getCart().remove(product);
    }

    //Полностью очистить корзину покупателя по его идентификатору (если он активен)
    public void clearCustomersCart(Long customerId) {
        Customer customer = getActiveCustomerById(customerId);
        customer.getCart().clear();
    }

}

