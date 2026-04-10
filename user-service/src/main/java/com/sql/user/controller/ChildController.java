package com.sql.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.po.Child;
import com.sql.common.entity.result.R;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.user.dto.ChildrenCreateDTO;
import com.sql.user.dto.ChildrenUpdateDTO;
import com.sql.user.service.ChildService;

/**
 * 孩子信息管理控制器
 * 仅普通会员（VIP）可访问，教练无权操作
 */
@RestController
@RequestMapping("/user/children")
@LoginRequired
@RequiresType(UserTypes.VIP)
public class ChildController {

    @Autowired
    private ChildService childrenService;

    /**
     * 查询当前用户的孩子列表
     */
    @GetMapping("/list")
    public R<?> list() {
        List<Child> children = childrenService.listByCurrentUser();
        return R.ok(children);
    }

    /**
     * 查询孩子详情
     */
    @GetMapping("/{childId}")
    public R<?> getInfo(@PathVariable Long childId) {
        Child child = childrenService.getById(childId);
        return R.ok(child);
    }

    /**
     * 新增孩子信息
     */
    @Log(title = "孩子信息", businessType = BusinessType.INSERT)
    @PostMapping
    public R<?> add(@Validated @RequestBody ChildrenCreateDTO dto) {
        childrenService.add(dto);
        return R.ok("新增孩子信息成功");
    }

    /**
     * 修改孩子信息
     */
    @Log(title = "孩子信息", businessType = BusinessType.UPDATE)
    @PutMapping("/{childId}")
    public R<?> update(@PathVariable Long childId, @Validated @RequestBody ChildrenUpdateDTO dto) {
        childrenService.update(childId, dto);
        return R.ok("修改孩子信息成功");
    }

    /**
     * 上传/更换孩子照片
     */
    @Log(title = "孩子照片", businessType = BusinessType.UPDATE)
    @PostMapping("/{childId}/photo")
    public R<?> updatePhoto(@PathVariable Long childId,
            @RequestPart("childPhoto") MultipartFile childPhoto) {
        childrenService.updatePhoto(childId, childPhoto);
        return R.ok("上传孩子照片成功");
    }

    /**
     * 删除孩子信息
     */
    @Log(title = "孩子信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{childId}")
    public R<?> delete(@PathVariable Long childId) {
        childrenService.delete(childId);
        return R.ok("删除孩子信息成功");
    }
}
