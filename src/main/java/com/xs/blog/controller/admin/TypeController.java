package com.xs.blog.controller.admin;

import com.xs.blog.pojo.Type;
import com.xs.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/admin/types")
public class TypeController {

    @Autowired
    private TypeService typeService;

    @GetMapping("")
    public String list(@PageableDefault(size = 5, sort = {"id"}, direction = Sort.Direction.DESC)
                                   Pageable pageable, Model model) {
        model.addAttribute("page", typeService.listType(pageable));

        return "admin/types";
    }

    @GetMapping("/input")
    public String input(Model model) {
        model.addAttribute("type", new Type());
        return "admin/types-input";
    }

    @GetMapping("/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        model.addAttribute("type", typeService.getType(id));
        return "admin/types-input";
    }

    @PostMapping("")
    public String post(@Valid Type type, BindingResult result, RedirectAttributes attributes){

        Type type1 = typeService.getTypeByName(type.getName());
        if (type1 != null) {
            result.rejectValue("name", "nameError", "不能添加重复的分类");
        }

        if (result.hasErrors()) {
            return "admin/types-input";
        }

        Type t = typeService.saveType(type);

        if (t == null) {
            //新增失败
            attributes.addFlashAttribute("message", "新增失败");
        }else {
            attributes.addFlashAttribute("message", "新增成功");
        }
        return "redirect:/admin/types";
    }

    @PostMapping("/{id}")
    public String editPost(@Valid Type type,@PathVariable Long id, BindingResult result, RedirectAttributes attributes) {
        Type type1 = typeService.getTypeByName(type.getName());
        if (type1 != null) {
            result.rejectValue("name", "nameError", "不能添加重复的分类");
        }
        if (result.hasErrors()) {
            return "admin/types-input";
        }

        Type t = typeService.updateType(id,type);

        if (t == null) {
            attributes.addFlashAttribute("message", "更新失败");
        } else {
            attributes.addFlashAttribute("message", "更新成功");
        }
        return "redirect:/admin/types";
    }

    @GetMapping("/{id}/delete")
    public String deleteType(@PathVariable Long id,RedirectAttributes attributes) {
        typeService.deleteType(id);
        attributes.addFlashAttribute("message", "删除成功");
        return "redirect:/admin/types";
    }
}
