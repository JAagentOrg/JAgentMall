package shop.jagentmall.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import shop.jagentmall.api.CommonResult;
import shop.jagentmall.service.UmsMemberService;

import java.util.HashMap;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/09/21:07
 * @Description: 会员登录注册管理
 */
@Controller
@Tag(name = "UmsMemberController", description = "会员登录注册管理")
@RequestMapping("/sso")
public class UmsMemberController {

    @Autowired
    private UmsMemberService memberService;

    //token请求头
    @Value("${sa-token.token-prefix}")
    private  String tokenHeader;

    @Operation(summary = "会员注册")
    @PostMapping( "/login")
    public CommonResult login(@RequestParam String username,
                              @RequestParam String password) {
        SaTokenInfo saTokenInfo  = memberService.login(username, password);
        if (saTokenInfo == null) {
            return CommonResult.validateFailed("用户名或密码错误");

        }
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", saTokenInfo.getTokenValue());
        tokenMap.put("tokenHeader", tokenHeader);
        return CommonResult.success(tokenMap);
    }


}
