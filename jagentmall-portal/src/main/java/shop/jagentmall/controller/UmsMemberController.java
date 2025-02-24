package shop.jagentmall.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.jagentmall.api.CommonResult;
import shop.jagentmall.model.UmsMember;
import shop.jagentmall.service.UmsMemberService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    @Operation(summary = "会员登录")
    @PostMapping( "/login")
    @ResponseBody
    public CommonResult login(@RequestParam String username,
                              @RequestParam String password) {
        SaTokenInfo saTokenInfo  = memberService.login(username, password);
        if (saTokenInfo == null) {
            return CommonResult.validateFailed("用户名或密码错误");

        }
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", saTokenInfo.getTokenValue());
        tokenMap.put("tokenHead", tokenHeader + " ");
        return CommonResult.success(tokenMap);
    }

    @Operation(summary = "会员注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult register(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String telephone,
                                 @RequestParam String authCode) {
        memberService.register(username, password, telephone, authCode);
        return CommonResult.success(null,"注册成功");
    }


    @Operation(summary = "登出功能")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult logout() {
        memberService.logout();
        return CommonResult.success(null);
    }

    @Operation(summary = "获取会员信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult info() {
        UmsMember member = memberService.getCurrentMember();
        return CommonResult.success(member);
    }
    @Operation(summary = "获取验证码")
    @RequestMapping(value = "/getAuthCode", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getAuthCode(@RequestParam String telephone) {
        String authCode = memberService.generateAuthCode(telephone);
        return CommonResult.success(authCode,"获取验证码成功");
    }

    @Operation(summary = "修改密码")
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updatePassword(@RequestParam String telephone,
                                       @RequestParam String password,
                                       @RequestParam String authCode) {
        memberService.updatePassword(telephone,password,authCode);
        return CommonResult.success(null,"密码修改成功");
    }

    @Operation(summary = "更新用户信息")
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updatePassword(@RequestParam(required = false) String nickname,
                                       @RequestParam(required = false) String icon,
                                       @RequestParam(required = false) Integer gender,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthday,
                                       @RequestParam(required = false) String city,
                                       @RequestParam(required = false) String job,
                                       @RequestParam(required = false) String personalizedSignature) {
        boolean f = memberService.updateUserInfo(nickname,icon,gender,birthday,city,job,personalizedSignature);
        if(f){
            return CommonResult.success(null,"信息更新成功");
        }
        else{
            return CommonResult.failed("信息更新失败");
        }
    }
    @Operation(summary = "删除用户(注销用户)")
    @RequestMapping(value = "/delUser", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delUser(@RequestParam String telephone,
                                @RequestParam String password,
                                @RequestParam String authCode) {
        memberService.delUser(telephone,password,authCode);
        return CommonResult.success(null,"删除用户成功");
    }



}
