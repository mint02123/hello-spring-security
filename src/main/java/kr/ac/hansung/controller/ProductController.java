package kr.ac.hansung.controller;

import kr.ac.hansung.dto.ProductDto;
import kr.ac.hansung.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import kr.ac.hansung.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        // page/size로 페이지 요청 객체 생성, id 순 정렬
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id"));
        // 빈 검색어("")는 null로 정규화 → URL에 keyword 안 붙음
        String normalizedKeyword = (keyword != null && !keyword.isBlank()) ? keyword : null;

        Page<Product> productPage;
        if (normalizedKeyword != null) {
            productPage = productService.searchProducts(normalizedKeyword, pageRequest);
        } else {
            productPage = productService.getProducts(pageRequest);
        }

        model.addAttribute("productPage", productPage);
        model.addAttribute("keyword", normalizedKeyword);
        return "products/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "products/detail";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("product", new ProductDto());
        return "products/add";
    }

    @PostMapping
    public String save(@ModelAttribute ProductDto dto) {
        productService.save(dto);
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/products";
    }
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "products/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute ProductDto dto) {
        productService.update(id, dto);
        return "redirect:/products";
    }
}
