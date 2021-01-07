package com.xs.blog.service.impl;

import com.xs.blog.NotFindException;
import com.xs.blog.dao.BlogRepository;
import com.xs.blog.pojo.Blog;
import com.xs.blog.pojo.Type;
import com.xs.blog.service.BlogService;
import com.xs.blog.util.MarkdownUtils;
import com.xs.blog.util.MyBeanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogRepository blogRepository;

    @Override
    public Blog getBlog(Long id) {
        return blogRepository.findById(id).get();
    }

    @Transactional
    @Override
    public Blog getAndConvert(Long id) {
        Blog blog = blogRepository.findById(id).get();
//        blog.setViews(blog.getViews() + 1);
        blogRepository.updateViews(id);
        if (blog.getId() == null) {
            throw new NotFindException("博客不存在");
        }
        Blog b = new Blog();
        BeanUtils.copyProperties(blog, b);
        b.setViews(b.getViews() + 1);
        b.setContent(MarkdownUtils.markdownToHtmlExtensions(b.getContent()));
        return b;
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable, Blog blog) {

        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (blog.getTitle() != null && !"".equals(blog.getTitle())) {
                    predicates.add(cb.like(root.<String>get("title"), "%" + blog.getTitle() + "%"));
                }

                if (blog.getType() != null && blog.getType().getId() != null) {
                    predicates.add(cb.equal(root.<Type>get("type").get("id"), blog.getType().getId()));
                }

                if (blog.isRecommend()) {
                    predicates.add(cb.equal(root.<Boolean>get("recommend"), blog.isRecommend()));
                }
                cq.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        },pageable);
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Override
    public Page<Blog> listBlog(String query, Pageable pageable) {
        return blogRepository.findByQuery(query, pageable);
    }

    @Override
    public Page<Blog> listBlog(Long tagID, Pageable pageable) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Join join = root.join("tags");
                return cb.equal(join.get("id"), tagID);
            }
        },pageable);
    }

    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updateTime");
        Pageable pageable = PageRequest.of(0, size, sort);
        return blogRepository.findTop(pageable);
    }

    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years = blogRepository.findGroupYear();
        TreeMap<String,List<Blog>> map = new TreeMap<>((o1, o2)->Integer.parseInt(o2)-Integer.parseInt(o1));

        for (String year : years) {
            map.put(year, blogRepository.findByYear(year));
        }
        return map;
    }

    @Override
    public Long countBlog() {
        return blogRepository.count();
    }

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {

        Date now = new Date();

        if (blog.getId() == null) {
            //新增
            blog.setCreateTime(now);
            blog.setUpdateTime(now);
            blog.setViews(0);
        } else {
            //更新
            blog.setUpdateTime(now);
        }
        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Optional<Blog> byId = blogRepository.findById(id);
        Blog b = byId.get();
        if (b.getId() == null) {
            throw new NotFindException("该博客不存在");
        }
        BeanUtils.copyProperties(blog,b, MyBeanUtils.getNullPropertyNames(blog));
        b.setUpdateTime(new Date());
        return blogRepository.save(b);
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }
}
