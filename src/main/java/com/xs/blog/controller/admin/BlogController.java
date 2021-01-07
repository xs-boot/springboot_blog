package com.xs.blog.controller.admin;

import com.xs.blog.pojo.Blog;
import com.xs.blog.pojo.Type;
import com.xs.blog.pojo.User;
import com.xs.blog.service.BlogService;
import com.xs.blog.service.TagService;
import com.xs.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/blogs")
public class BlogController {

    private final String INPUT = "admin/blogs-input";
    private final String LIST = "admin/blogs";
    private final String REDIRECT_LIST = "redirect:/admin/blogs";

    @Autowired
    private BlogService blogService;
    @Autowired
    TypeService typeService;
    @Autowired
    private TagService tagService;

    @GetMapping("")
    public String blogs(@PageableDefault(size = 2, sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                        Blog blog, Model model) {
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        model.addAttribute("types", typeService.listType());
        return LIST;
    }

    @PostMapping("/search")
    public String search(@PageableDefault(size = 2, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         @RequestParam Long typeId, Blog blog, Model model) {
        Type type = new Type();
        type.setId(typeId);
        blog.setType(type);

        model.addAttribute("page", blogService.listBlog(pageable, blog));
        return "admin/blogs :: blogList";//部分更新
    }

    @GetMapping("/input")
    public String input(Model model) {
        model.addAttribute("blog", new Blog());
        setTypeAndTag(model);
        return INPUT;
    }

    private void setTypeAndTag(Model model) {
        model.addAttribute("types", typeService.listType());
        model.addAttribute("tags", tagService.listTag());
    }

    @PostMapping("")
    public String addBlog(Blog blog, HttpSession session, RedirectAttributes attributes) {
        blog.setUser((User) session.getAttribute("user"));
        blog.setType(typeService.getType(blog.getType().getId()));
        blog.setTags(tagService.listTag(blog.getTagIds()));

        Blog blog1;
        if (blog.getId() == null) {
            blog1 =  blogService.saveBlog(blog);
        } else {
            blog1 = blogService.updateBlog(blog.getId(), blog);
        }
        if (blog1 == null) {
            //新增失败
            attributes.addFlashAttribute("message", "操作失败");
        }else {
            attributes.addFlashAttribute("message", "操作成功");
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        setTypeAndTag(model);
        Blog blog = blogService.getBlog(id);
        blog.init();
        model.addAttribute("blog",blog);
        return INPUT;
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message");
        return REDIRECT_LIST;
    }
}
