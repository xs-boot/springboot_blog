package com.xs.blog.service.impl;


import com.xs.blog.NotFindException;
import com.xs.blog.dao.TagRepository;
import com.xs.blog.pojo.Tag;
import com.xs.blog.service.TagService;
import javassist.NotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by limi on 2017/10/16.
 */
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Transactional
    @Override
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Transactional
    @Override
    public Tag getTag(Long id) {
        Optional<Tag> byId = tagRepository.findById(id);
        return byId.get();
    }

    @Override
    public Tag getTagByName(String name) {
        return tagRepository.findByName(name);
    }

    @Transactional
    @Override
    public Page<Tag> listTag(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Override
    public List<Tag> listTag() {
        return tagRepository.findAll();
    }

    @Override
    public List<Tag> listTagTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "blogs.size");
        Pageable pageable = PageRequest.of(0, size, sort);
        return tagRepository.findTop(pageable);
    }


    @Override
    public List<Tag> listTag(String ids) { //1,2,3
        return tagRepository.findAllById(convertToList(ids));
    }

    private List<Long> convertToList(String ids) {
        List<Long> list = new ArrayList<>();

        if (!StringUtils.isEmpty(ids)) {
            String[] array = ids.split(",");
            for (int i = 0; i < array.length; i++) {
                list.add(Long.valueOf(array[i]));
            }
        }
        return list;
    }



    @Transactional
    @Override
    public Tag updateTag(Long id, Tag tag) {
        Optional<Tag> byId = tagRepository.findById(id);
        Tag t = byId.get();

        if (t.getId() == null) {
            throw new NotFindException("不存在该标签");
        }
        BeanUtils.copyProperties(tag,t);
        return tagRepository.save(t);
    }



    @Transactional
    @Override
    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }
}
