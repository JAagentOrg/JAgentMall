package shop.jagentmall.dto;

import lombok.*;

import java.util.List;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/08/21:10
 * @Description: 权限框架中使用的用户信息封装类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String clientId;
    private List<String> permissionList;
}
