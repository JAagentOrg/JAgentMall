package shop.jagentmall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.jagentmall.api.CommonResult;
import shop.jagentmall.domain.CartProduct;
import shop.jagentmall.domain.CartPromotionItem;
import shop.jagentmall.model.OmsCartItem;
import shop.jagentmall.service.OmsCartItemService;
import shop.jagentmall.service.UmsMemberService;

import java.util.List;

/**
 * @Title: OmsCartItemController
 * @Author: [tianyou]
 * @Date: 2025/2/16
 * @Description: 购物车管理
 */
@Controller
@Tag(name = "OmsCartItemController", description = "购物车管理")
@RequestMapping("/cart")
public class OmsCartItemController {
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private OmsCartItemService cartItemService;

    @Operation(summary = "添加商品到购物车")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult add(@RequestBody OmsCartItem cartItem) {
        int count = cartItemService.add(cartItem);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "获取某个会员的购物车列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OmsCartItem>> list() {
        List<OmsCartItem> cartItemList = cartItemService.list(memberService.getCurrentMember().getId());
        return CommonResult.success(cartItemList);
    }

    @Operation(summary = "获取某个会员的购物车列表,包括促销信息(支持获取部分购物车商品的促销信息)")
    @RequestMapping(value = "/list/promotion", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<CartPromotionItem>> listPromotion(@RequestParam(required = false) List<Long> cartIds) {
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(memberService.getCurrentMember().getId(), cartIds);
        return CommonResult.success(cartPromotionItemList);
    }

    @Operation(summary = "修改购物车中某个商品的数量")
    @RequestMapping(value = "/update/quantity", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult updateQuantity(@RequestParam Long id,
                                       @RequestParam Integer quantity) {
        int count = cartItemService.updateQuantity(id, memberService.getCurrentMember().getId(), quantity);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "获取购物车中某个商品的规格,用于重选规格")
    @RequestMapping(value = "/getProduct/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CartProduct> getCartProduct(@PathVariable Long productId) {
        CartProduct cartProduct = cartItemService.getCartProduct(productId);
        return CommonResult.success(cartProduct);
    }

    @Operation(summary = "修改购物车中商品的规格")
    @RequestMapping(value = "/update/attr", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateAttr(@RequestBody OmsCartItem cartItem) {
        int count = cartItemService.updateAttr(cartItem);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "删除购物车中的某个商品")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        int count = cartItemService.delete(memberService.getCurrentMember().getId(), ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "清空购物车")
    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult clear() {
        int count = cartItemService.clear(memberService.getCurrentMember().getId());
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

}
