package shop.jagentmall.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.jagentmall.api.CommonResult;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/09/16:56
 * @Description: 前台会员服务远程调用Service
 */
@FeignClient("jagentmall-portal")
public interface UmsMemberService {
    @PostMapping("/sso/login")
    CommonResult login(@RequestParam("username") String username, @RequestParam("password") String password);
}
