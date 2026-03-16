package com.ruoyi.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.auth.annotation.RequiresType;
import com.ruoyi.common.entity.Children;
import com.ruoyi.common.entity.result.R;
import com.ruoyi.common.enums.UserTypes;
import com.ruoyi.user.dto.ChildrenDTO;
import com.ruoyi.user.service.ChildrenService;

/**
 * 孩子信息管理控制器
 * 仅普通会员（VIP）可访问，教练无权操作
 */
@RestController
@RequestMapping("/user/children")
@RequiresType(UserTypes.VIP)
public class ChildrenController {

    @Autowired
    private ChildrenService childrenService;

    /**
     * 查询当前用户的孩子列表
     */
    @GetMapping("/list")
    public R<?> list() {
        List<Children> children = childrenService.listByCurrentUser();
        return R.ok(children);
    }

    /**
     * 查询孩子详情
     */
    @GetMapping("/{childId}")
    public R<?> getInfo(@PathVariable Long childId) {
        Children child = childrenService.getById(childId);
        return R.ok(child);
    }

    /**
     * 新增孩子信息
     */
    @PostMapping
    public R<?> add(@RequestBody ChildrenDTO dto) {
        childrenService.add(dto);
        return R.ok("新增孩子信息成功");
    }

    /**
     * 修改孩子信息
     */
    @PutMapping
    public R<?> update(@RequestBody ChildrenDTO dto) {
        childrenService.update(dto);
        return R.ok("修改孩子信息成功");
    }

    /**
     * 删除孩子信息
     */
    @DeleteMapping("/{childId}")
    public R<?> delete(@PathVariable Long childId) {
        childrenService.delete(childId);
        return R.ok("删除孩子信息成功");
    }
}
