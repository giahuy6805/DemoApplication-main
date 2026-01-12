package com.example.demo.controller;

import com.example.demo.entity.Order; // Nhớ import cái này
import com.example.demo.entity.User;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.MailService;
import com.example.demo.service.ShoppingCartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.entity.Product;
import com.example.demo.entity.OrderItem;
import com.example.demo.model.CartItem;

import java.time.LocalDateTime; // Nhớ import cái này để dùng được .now()
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private OrderItemRepository orderItemRepository; // Tiêm thêm repository này vào đầu class

    @Autowired
    private ProductRepository productRepository; // Để lấy đối tượng Product thực từ DB

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private ShoppingCartService cartService;

    // 1. Hiển thị trang giỏ hàng
    @GetMapping("/view")
    public String view(Model model) {
        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("cartCount", cartService.getCount());
        model.addAttribute("totalAmount", cartService.getAmount());
        return "cart";
    }

    // 2. Thêm sản phẩm vào giỏ
    @GetMapping("/add/{id}")
    public String add(@PathVariable("id") Integer id) {
        cartService.add(id);
        return "redirect:/cart/view";
    }

    // 3. Xóa một sản phẩm khỏi giỏ
    @GetMapping("/remove/{id}")
    public String remove(@PathVariable("id") Integer id) {
        cartService.remove(id);
        return "redirect:/cart/view";
    }

    // 4. Cập nhật số lượng
    @GetMapping("/update/{id}")
    public String update(@PathVariable("id") Integer id, @RequestParam("qty") Integer qty) {
        if (qty > 0) {
            cartService.update(id, qty);
        } else {
            cartService.remove(id);
        }
        return "redirect:/cart/view";
    }

    // 5. Làm trống giỏ hàng
    @GetMapping("/clear")
    public String clear() {
        cartService.clear();
        return "redirect:/cart/view";
    }

    // 6. Vào trang điền thông tin Thanh toán
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        if (cartService.getCount() == 0) {
            return "redirect:/cart/view";
        }
        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("cartCount", cartService.getCount());
        model.addAttribute("totalAmount", cartService.getAmount());
        return "checkout";
    }

    // 7. Xử lý nút "XÁC NHẬN ĐẶT HÀNG"
    @PostMapping("/order")
    public String processOrder(
            @RequestParam("receiverName") String receiverName,
            @RequestParam("receiverPhone") String receiverPhone,
            @RequestParam("address") String address,
            HttpSession session, Model model) {

        // 1. Kiểm tra đăng nhập
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        // 2. Kiểm tra giỏ hàng trống
        if (cartService.getCount() == 0) return "redirect:/cart/view";

        // 3. Lưu thông tin chung vào bảng Order
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setTotalAmount(cartService.getAmount());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDING");

        // Lưu và lấy lại đối tượng đã có ID để dùng cho OrderItem
        Order savedOrder = orderRepository.save(order);

        // 4. Duyệt giỏ hàng để lưu chi tiết và TRỪ TỒN KHO
        for (CartItem item : cartService.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder); // Gắn vào đơn hàng vừa tạo

            // Tìm sản phẩm thực tế từ Database
            Product product = productRepository.findById(item.getProductId()).orElse(null);

            if (product != null) {
                // A. Lưu chi tiết đơn hàng (OrderItem)
                orderItem.setProduct(product);
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(product.getPrice()); // Chốt giá tại thời điểm mua
                orderItemRepository.save(orderItem);

                // B. XỬ LÝ TRỪ TỒN KHO
                int currentStock = product.getQuantity();
                int orderQuantity = item.getQuantity();

                // Trừ số lượng và đảm bảo không âm (nếu cần)
                product.setQuantity(Math.max(0, currentStock - orderQuantity));

                // Lưu sản phẩm đã cập nhật số lượng mới vào DB
                productRepository.save(product);
            }
        }

        // 5. Gửi email xác nhận cho khách hàng
        mailService.sendOrderEmail(user.getEmail(), "TH" + savedOrder.getId(), receiverName, savedOrder.getTotalAmount());

        // 6. Dọn sạch giỏ hàng sau khi đặt thành công
        cartService.clear();

        // 7. Gửi mã đơn hàng ra trang thông báo thành công
        model.addAttribute("orderId", "TH" + savedOrder.getId());
        return "order-success";
    }

    @GetMapping("/my-orders")
    public String viewMyOrders(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
        model.addAttribute("orders", orders);

        // Sửa chỗ này: bỏ chữ user/ đi nếu file nằm ngay trong templates
        return "my-orders";
    }



}