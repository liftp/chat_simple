package com.hch.chat_simple.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.hch.chat_simple.auth.NoAuth;
import com.hch.chat_simple.pojo.dto.AddUserForm;
import com.hch.chat_simple.pojo.dto.TokenInfoDTO;
import com.hch.chat_simple.pojo.dto.UserLoginDTO;
import com.hch.chat_simple.pojo.po.UserPO;
import com.hch.chat_simple.pojo.query.UserQuery;
import com.hch.chat_simple.pojo.vo.UserVO;
import com.hch.chat_simple.service.IUserService;
import com.hch.chat_simple.util.BeanConvert;
import com.hch.chat_simple.util.ContextUtil;
import com.hch.chat_simple.util.Payload;
import com.hch.chat_simple.util.StatusCodeEnum;
import com.hch.chat_simple.util.TokenUtil;

// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/user")
@Tag(name = "用户操作")
@AllArgsConstructor
public class UserOpController {

    private final IUserService iUserService;

    
    @PostMapping("/login")
    @Operation(summary = "登录")
    @NoAuth(description = "登录")
    public Payload<String> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        // if (userLoginDTO)
        UserPO user = iUserService.getUserByName(userLoginDTO.getUsername());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (user == null) {
            return Payload.of("error", StatusCodeEnum.USER_NOT_FOUND);
        } else if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            return Payload.of("error", StatusCodeEnum.PWD_ERROR);
        }

        TokenInfoDTO tokeInfo = TokenInfoDTO.builder()
            .username(user.getUsername())
            .userId(user.getId())
            .realName(user.getName())
            .build();

        String token = JSON.toJSONString(tokeInfo);
        
        return Payload.of(TokenUtil.createToken(token), StatusCodeEnum.SUCCESS);
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String pwd = passwordEncoder.encode("123456");
        System.out.println(pwd);
    }

    @PostMapping("/userInfo")
    @Operation(summary = "用户信息获取")
    public Payload<UserVO> userInfo() {
        UserPO po = iUserService.getById(ContextUtil.getUserId());
        UserVO vo = BeanConvert.convertSingle(po, UserVO.class);
        return Payload.success(vo);
    }

    @PostMapping("test")
    @Operation(description = "测试权限")
    public Payload<String> getHasAuth() {
        System.out.println(ContextUtil.getUserId());
        return Payload.success("");
    }


    @PostMapping("saveUser")
    @Operation(description = "测试添加用户")
    public Payload<String> saveUser() {
        UserPO u = new UserPO();
        u.setName("sss");
        u.setCreatedAt(LocalDateTime.now());
        iUserService.save(u);
        return Payload.success("");
    }

    @PostMapping("/searchUser")
    @Operation(description = "搜索用户")
    public Payload<List<UserVO>> searchUser(@Valid @RequestBody UserQuery query) {
        return Payload.success(iUserService.searchUserByName(query));
    }

    @NoAuth
    @PostMapping("/insertUser")
    @Operation(description = "添加用户")
    public Payload<Boolean> insertUser(@Valid @RequestBody AddUserForm form) {
        return Payload.success(iUserService.insertUser(form));
    }

}
