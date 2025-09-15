Чистая архитектура (Clean Architecture):
Чистая архитектура — это способ организации кода, при котором каждая часть системы выполняет строго свою задачу. Это помогает делать код удобным для сопровождения, тестирования и масштабирования.

Пример слоёв в чистой архитектуре:
1️⃣ Ядро (домен, предметная область) – содержит описание сущностей, с которыми работает наше приложение.
2️⃣ Репозиторий – отвечает за работу с базой данных.
3️⃣ Сервис – содержит бизнес-логику приложения (решает задачи заказчика).
4️⃣ Контроллер – принимает запросы от пользователя и вызывает сервис.

🔹 Пример: интернет-магазин (добавление товара в корзину)
Допустим, у нас есть система интернет-магазина, где пользователи могут добавлять товары в корзину.

1️⃣ Ядро (домен)
Этот слой содержит основные объекты и их поведение. Он не знает, где хранятся данные и как они обрабатываются.

public class Product {
private Long id;
private String name;
private double price;

    public Product(Long id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}

public class Cart {
private List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public List<Product> getProducts() {
        return products;
    }
}

➡️ Здесь нет кода базы данных, сервисов или API — только чистые бизнес-объекты.

2️⃣ Репозиторий – работа с базой данных
Этот слой отвечает только за хранение и получение данных.

import java.util.HashMap;
import java.util.Map;

public class CartRepository {
private final Map<Long, Cart> carts = new HashMap<>();

    public Cart getCart(Long userId) {
        return carts.get(userId);
    }

    public void saveCart(Long userId, Cart cart) {
        carts.put(userId, cart);
    }
}

➡️ Здесь нет бизнес-логики, только сохранение и получение данных.

3️⃣ Сервис
Этот слой обрабатывает данные и определяет, что делать.

public class CartService {
private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public void addProductToCart(Long userId, Product product) {
        Cart cart = cartRepository.getCart(userId);
        cart.addProduct(product);
        cartRepository.saveCart(userId, cart);
    }

    public List<Product> getCartProducts(Long userId) {
        return cartRepository.getCart(userId).getProducts();
    }
}

➡️ Здесь нет базы данных и API — только работа с бизнес-логикой.

4️⃣ Контроллер – принимает запросы от пользователя
Этот слой принимает HTTP-запросы и вызывает сервис.

import org.springframework.web.bind.annotation.*;

import java.util.List;

public class CartController {
private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    
    public void addProduct(Long userId, Product product) {
        cartService.addProductToCart(userId, product);
    }
    
    public List<Product> getCart(Long userId) {
        return cartService.getCartProducts(userId);
    }
}

➡️ Здесь нет логики корзины или базы данных — только обработка запросов.

🔹 Итог: зачем нужна чистая архитектура?
✅ Код легко изменять – например, можно заменить базу данных, не трогая бизнес-логику.
✅ Простая поддержка – каждый слой делает только свою работу.
✅ Лёгкость тестирования – можно отдельно тестировать бизнес-логику без базы данных и API.
✅ Гибкость – можно добавить новую функциональность, не меняя всю систему.

🔹 Вопрос: А зачем столько слоёв?
Представьте, что вы строите дом. Если проводка замурована в стены, то при поломке придётся ломать стену. А если все коммуникации отделены, то вы легко поменяете что-то одно.

Точно так же чистая архитектура отделяет важные части кода, чтобы они не зависели друг от друга.