package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.nio.file.*;
import java.io.IOException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;


    // --- HÀM LƯU SẢN PHẨM (ĐÃ THÊM VALIDATION) ---
    @PostMapping("/admin/products/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product,
                              BindingResult result, // QUAN TRỌNG: Phải nằm ngay sau @ModelAttribute
                              @RequestParam("imageFile") MultipartFile imageFile,
                              Model model, // Dùng để gửi lại danh mục nếu có lỗi
                              RedirectAttributes ra) throws IOException {

        // 1. Kiểm tra lỗi Validation (Tên trống, giá âm...)
        if (result.hasErrors()) {
            // Nếu có lỗi, phải lấy lại danh sách categories để hiển thị lại cái Select Box
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/product-form"; // Trả về lại trang form (không redirect) để hiện lỗi
        }

        boolean isNew = (product.getId() == null);

        // 2. Xử lý Upload Ảnh (Giữ nguyên logic của Huy)
        if (!imageFile.isEmpty()) {
            String fileName = imageFile.getOriginalFilename();
            String uploadDir = "src/main/resources/static/images/";
            Path path = Paths.get(uploadDir + fileName);
            Files.write(path, imageFile.getBytes());
            product.setImage(fileName);
        } else if (!isNew) {
            Product oldProduct = productService.getProductById(product.getId());
            product.setImage(oldProduct.getImage());
        }

        // 3. Xử lý logic ngày tạo và lượt xem (Giữ nguyên logic của Huy)
        if (isNew) {
            product.setCreatedAt(LocalDateTime.now());
            product.setView(0);
        } else {
            Product existingProduct = productService.getProductById(product.getId());
            product.setCreatedAt(existingProduct.getCreatedAt());
            if (product.getView() == null) product.setView(existingProduct.getView());
        }

        productService.saveProduct(product);

        // 4. Gửi thông báo thành công
        String message = isNew ? "Thêm sản phẩm mới thành công!" : "Cập nhật sản phẩm thành công!";
        ra.addFlashAttribute("message", message);
        ra.addFlashAttribute("messageType", "success");

        return "redirect:/admin/dashboard";
    }

    // --- QUẢN LÝ DANH SÁCH ---
    @GetMapping("/admin/products")
    public String listAllProducts(Model model) {
        model.addAttribute("title", "Tất cả sản phẩm");
        model.addAttribute("products", productService.getAllProducts());
        return "admin/product-list";
    }

    @GetMapping("/admin/products/laptops")
    public String listLaptops(Model model) {
        List<Product> laptops = productService.getAllProducts().stream()
                .filter(p -> p.getCategory() != null && "Laptop".equals(p.getCategory().getName()))
                .toList();
        model.addAttribute("title", "Quản lý Laptop");
        model.addAttribute("products", laptops);
        return "admin/product-list";
    }

    @GetMapping("/admin/products/phones")
    public String listPhones(Model model) {
        List<Product> phones = productService.getAllProducts().stream()
                .filter(p -> p.getCategory() != null && "Điện thoại".equals(p.getCategory().getName()))
                .toList();
        model.addAttribute("title", "Quản lý Điện thoại");
        model.addAttribute("products", phones);
        return "admin/product-list";
    }

    // --- THÊM & SỬA (MỞ FORM) ---
    @GetMapping("/admin/products/add")
    public String showAddForm(Model model) {
        Product product = new Product();
        product.setView(0);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/product-form";
    }

    @GetMapping("/admin/products/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/product-form";
        }
        return "redirect:/admin/products";
    }

    // --- XÓA ---
    @GetMapping("/admin/products/delete/{id}")
    public String deleteProduct(@PathVariable Integer id, RedirectAttributes ra) {
        productService.deleteProduct(id);
        ra.addFlashAttribute("message", "Đã xóa sản phẩm thành công!");
        ra.addFlashAttribute("messageType", "danger");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String showDashboard(Model model,
                                @RequestParam(name = "keyword", required = false) String keyword,
                                @RequestParam(name = "page", defaultValue = "0") int page) {

        // 1. Phân trang sản phẩm (Giữ nguyên)
        Pageable pageable = PageRequest.of(page, 5, Sort.by("id").descending());
        Page<Product> productPage;
        if (keyword != null && !keyword.isEmpty()) {
            productPage = productService.searchProducts(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            productPage = productService.getAllProducts(pageable);
        }

        // 2. THỐNG KÊ (Sửa logic kiểm tra null tại đây)
        Double totalRevenue = orderRepository.getTotalRevenue();
        model.addAttribute("totalRevenue", (totalRevenue != null) ? totalRevenue : 0.0);

        model.addAttribute("totalOrders", orderRepository.count());

        // Đảm bảo hàm countPendingOrders() trong Repository không trả về null
        model.addAttribute("pendingOrders", orderRepository.countPendingOrders());
        model.addAttribute("totalUsers", userRepository.count());

        // Thống kê theo loại (Check null cẩn thận)
        Double laptopRev = orderRepository.getRevenueByCategory("Laptop");
        model.addAttribute("laptopRev", (laptopRev != null) ? laptopRev : 0.0);

        Double phoneRev = orderRepository.getRevenueByCategory("Điện thoại");
        model.addAttribute("phoneRev", (phoneRev != null) ? phoneRev : 0.0);

        // Mảng doanh thu 12 tháng
        double[] monthlyRevenue = new double[12];
        for (int i = 1; i <= 12; i++) {
            Double rev = orderRepository.getRevenueByMonth(i);
            monthlyRevenue[i-1] = (rev != null) ? rev : 0.0;
        }
        model.addAttribute("monthlyRevenue", monthlyRevenue);

        // Gửi danh sách sản phẩm
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        return "admin/admin-dashboard";
    }

    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        // Bây giờ dòng này sẽ hết đỏ
        model.addAttribute("users", userService.findAll());
        return "admin/user-list";
    }

    @GetMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Integer id) {
        // Dòng này cũng sẽ hết đỏ
        userService.delete(id);
        return "redirect:/admin/users?deleted";
    }

    @PostMapping("/admin/users/update-role")
    public String updateUserRole(@RequestParam("id") Integer id, @RequestParam("role") String role) {
        // Thêm .orElse(null) ở cuối để lấy đúng đối tượng User
        User user = userService.findById(id);

        // Nếu dòng trên vẫn đỏ, hãy thử:
        // User user = (User) userService.findById(id); (Ép kiểu thủ công)

        if (user != null) {
            user.setRole(role);
            userService.save(user);
        }
        return "redirect:/admin/users?updated";
    }

}