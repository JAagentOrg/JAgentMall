package shop.jagentmall.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import shop.jagentmall.api.CommonResult;
import shop.jagentmall.domain.UmsAdminLoginParam;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/09/16:52
 * @Description: 后台用户服务远程调用Service
 */
@FeignClient("jagenmall-admin")
public interface UmsAdminService {

    @PostMapping("/admin/login")
    CommonResult login(@RequestBody UmsAdminLoginParam umsAdminLoginParam);

}
