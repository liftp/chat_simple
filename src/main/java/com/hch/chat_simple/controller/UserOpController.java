package com.hch.chat_simple.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.hch.chat_simple.auth.NoAuth;
import com.hch.chat_simple.pojo.dto.AddUserForm;
import com.hch.chat_simple.pojo.dto.TokenInfoDTO;
import com.hch.chat_simple.pojo.dto.TokenPairDTO;
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

import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/user")
@Tag(name = "用户操作")
@AllArgsConstructor
public class UserOpController {

    private final IUserService iUserService;

    /**
     * 登录接口 - 返回双Token
     * accessToken: 短期访问令牌(30分钟)，存储在Redis中
     * refreshToken: 长期刷新令牌(7天)，JWT存放在浏览器端
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    @NoAuth(description = "登录")
    public Payload<TokenPairDTO> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        UserPO user = iUserService.getUserByName(userLoginDTO.getUsername());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (user == null) {
            return Payload.of(null, StatusCodeEnum.USER_NOT_FOUND);
        } else if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            return Payload.of(null, StatusCodeEnum.PWD_ERROR);
        }

        TokenInfoDTO tokenInfo = TokenInfoDTO.builder()
            .username(user.getUsername())
            .userId(user.getId())
            .realName(user.getName())
            .build();

        // 创建AccessToken（存储在Redis中，30分钟有效）
        String accessToken = TokenUtil.createAccessToken();
        TokenUtil.storeAccessToken(accessToken, tokenInfo);

        // 创建RefreshToken（JWT，7天有效，返回给浏览器存储）
        String tokenInfoJson = JSON.toJSONString(tokenInfo);
        String refreshToken = TokenUtil.createRefreshToken(tokenInfoJson);

        TokenPairDTO tokenPair = TokenPairDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return Payload.of(tokenPair, StatusCodeEnum.SUCCESS);
    }

    /**
     * 手动刷新AccessToken接口
     * 当客户端检测到AccessToken过期时，可主动调用此接口刷新
     * @param refreshToken RefreshToken
     * @return 新的TokenPair
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新AccessToken")
    @NoAuth(description = "刷新Token")
    public Payload<TokenPairDTO> refreshToken(@RequestHeader("refreshToken") String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            return Payload.of(null, StatusCodeEnum.TOKEN_LACK);
        }

        TokenInfoDTO tokenInfo = TokenUtil.parseRefreshTokenInfo(refreshToken);
        if (tokenInfo == null) {
            // RefreshToken无效或已过期
            return Payload.of(null, StatusCodeEnum.REFRESH_TOKEN_EXPIRE);
        }

        // 创建新的AccessToken
        String newAccessToken = TokenUtil.createAccessToken();
        TokenInfoDTO newTokenInfo = TokenInfoDTO.builder()
                .username(tokenInfo.getUsername())
                .userId(tokenInfo.getUserId())
                .realName(tokenInfo.getRealName())
                .build();
        TokenUtil.storeAccessToken(newAccessToken, newTokenInfo);

        TokenPairDTO tokenPair = TokenPairDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // RefreshToken不变，继续使用
                .build();

        return Payload.of(tokenPair, StatusCodeEnum.SUCCESS);
    }

    /**
     * 登出接口 - 删除Redis中的AccessToken
     */
    @PostMapping("/logout")
    @Operation(summary = "登出")
    public Payload<String> logout() {
        Long userId = ContextUtil.getUserId();
        if (userId != null) {
            TokenUtil.removeAccessTokenByUserId(userId);
        }
        return Payload.success("登出成功");
    }

    @PostMapping("/userInfo")
    @Operation(summary = "用户信息获取")
    public Payload<UserVO> userInfo() {
        UserPO po = iUserService.getById(ContextUtil.getUserId());
        UserVO vo = BeanConvert.convertSingle(po, UserVO.class);
        return Payload.success(vo);
    }

    @PostMapping("/updateUserInfo")
    @Operation(summary = "修改用户信息(姓名+头像)")
    public Payload<UserVO> updateUserInfo(@RequestBody UpdateUserForm form) {
        Long userId = ContextUtil.getUserId();
        iUserService.updateUserInfo(userId, form.getName(), form.getAvatar());
        UserPO po = iUserService.getById(userId);
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

    @Data
    public static class UpdateUserForm {
        private String name;
        private String avatar;
    }

}
