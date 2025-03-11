package shop.jagentmall.service.impl;


import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import shop.jagentmall.api.CommonResult;
import shop.jagentmall.fegin.AiPortalFeignClient;
import shop.jagentmall.model.OmsCartItem;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/05
 * @Description: portal ai 工具
 */
@Service
public class AiPortalServiceImpl {

	private static final Logger logger = LoggerFactory.getLogger(AiPortalServiceImpl.class);

    @Autowired
	private AiPortalFeignClient aiPortalFeignClient;

	@Autowired
	private StringRedisTemplate redisTemplate;

	/**
	 * 查询用户订单，先提取昵称
	 * @param nickName
	 */
	public record OrderListRequest(String nickName) {
	}

	/**
	 * 自动下单，先提取商品昵称，检索购物车在的商品名，在自动下单
	 * @param productName  商品名称
	 */
	public record shoppingCartOrderRequest(String productName) {
	}



	@Bean
	@Description("自动下单")
	public Function<shoppingCartOrderRequest, String> postShoppingCartOrderRequest() {
		return  request -> {
			//todo  根据用户提出的商品昵称检索购物车商品
			CommonResult<List<OmsCartItem>> listCommonResult = aiPortalFeignClient.aiCartListName(request.productName);
			List<OmsCartItem> cartData = listCommonResult.getData();
			// 获取 data 部分
			List<OmsCartItem> cartItems = listCommonResult.getData();
			if (cartItems == null || cartItems.isEmpty()) {
				System.out.println("购物车为空");
			}
			List<Long> ids = extractIdsFromCartItems(cartData);
			CommonResult commonResult = aiPortalFeignClient.aiGenerateOrder(ids);
			//todo 调用购物车生成订单方法 AigenerateOrder
			String resultJson = JSONUtil.toJsonStr(cartData) + ", " + JSONUtil.toJsonStr(commonResult);
			return resultJson;
		};
	}

	/**
	 * 从购物车商品列表中提取 ID 列表
	 * @param cartItems 购物车商品列表
	 * @return ID 列表
	 */
	public List<Long> extractIdsFromCartItems(List<OmsCartItem> cartItems) {
		return cartItems.stream()
				.map(OmsCartItem::getId)
				.collect(Collectors.toList());
	}

	@Bean(name = "orderList")
	@Description("查询订单信息")
	public Function<OrderListRequest, String> getOrderList() {

		return  request -> {
			//todo  Sa-token 从 redis 中获取会员信息对比昵称

			return String.valueOf(aiPortalFeignClient.orderList(-1,1,5));
		};
	}

}
