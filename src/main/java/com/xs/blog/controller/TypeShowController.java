package com.xs.blog.controller;

import com.xs.blog.pojo.Blog;
import com.xs.blog.pojo.Type;
import com.xs.blog.service.BlogService;
import com.xs.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping
public class TypeShowController {

    @Autowired
    private TypeService typeService;
    @Autowired
    private BlogService blogService;
    @GetMapping("/types/{id}")
    public String types(@PageableDefault(size = 5, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        @PathVariable Long id, Model model) {
        //种类一般不会超过1000，用1000模拟所有种类数
        List<Type> types = typeService.listTypeTop(1000);
        if (id == -1) {
            id = types.get(0).getId();
        }

        Blog blog = new Blog();
        Type type = new Type();
        type.setId(id);
        blog.setType(type);
        model.addAttribute("types", types);
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        model.addAttribute("activeTypeId", id);
        return "types";
    }
}
