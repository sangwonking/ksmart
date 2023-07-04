package ksmart.mybatis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import ksmart.mybatis.dto.Goods;
import ksmart.mybatis.dto.Member;
import ksmart.mybatis.service.GoodsService;
import ksmart.mybatis.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/goods")
@AllArgsConstructor
@Slf4j
public class GoodsController {
	
	private final GoodsService goodsService;
	private final MemberService memberService;
	
	@GetMapping("/sellerList")
	public String getSellerList(Model model, HttpSession session) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		String memberId = (String) session.getAttribute("SID");
		
		if(memberId != null) {
			int memberLevel = (int) session.getAttribute("SLEVEL");
			if(memberLevel == 2) paramMap.put("sellerId", memberId);
		}
		
		paramMap.put("memberLevel", 2);
		
		List<Member> sellerList = goodsService.getSellerList(paramMap);
		
		model.addAttribute("title", "판매자별 상품목록 조회");
		model.addAttribute("sellerList", sellerList);
		
		return "goods/sellerList";
	}
	
	@PostMapping("/removeGoods")
	public String removeGoods( @RequestParam(value="goodsCode") String goodsCode,
							   @RequestParam(value="memberPw") String memberPw,
							   RedirectAttributes reAttr) {
		
		Goods goodsInfo = goodsService.getGoodsByCode(goodsCode);
		String sellerId = goodsInfo.getGoodsSellerId();
		
		// 회원 여부 검증
		Map<String, Object> isValidMap = memberService.isValidMember(sellerId, memberPw);
		boolean isValid = (boolean) isValidMap.get("isValid");
		
		// 비밀번호 일치 상품 삭제
		if(isValid) {
			goodsService.removeGoods(goodsCode);
			return "redirect:/goods/goodsList";
		}
		
		reAttr.addAttribute("goodsCode", goodsCode);
		reAttr.addAttribute("msg", "회원의 정보가 일치하지 않습니다.");
		
		return "redirect:/goods/removeGoods";
	}
	
	@GetMapping("/removeGoods")
	public String removeGoods(@RequestParam(value="goodsCode") String goodsCode,
							  @RequestParam(value="msg", required = false) String msg,
							  Model model) {
		
		Goods goodsInfo = goodsService.getGoodsByCode(goodsCode);
		String goodsName = goodsInfo.getGoodsName();
		
		model.addAttribute("title", "상품삭제");
		model.addAttribute("goodsCode", goodsCode);
		model.addAttribute("goodsName", goodsName);
		if(msg != null) model.addAttribute("msg", msg);
		
		return "goods/removeGoods";
	}
	
	@PostMapping("/modifyGoods")
	public String modifyGoods(Goods goods) {
		log.info("modifyGoods goods: {}", goods);
		goodsService.modifyGoods(goods);
		return "redirect:/goods/goodsList";
	}
	
	@GetMapping("/modifyGoods")
	public String modifyGoods(@RequestParam(value="goodsCode") String goodsCode,
							  Model model) {
		
		List<Member> sellerInfoList = goodsService.getSellerInfoList();
		Goods goodsInfo = goodsService.getGoodsByCode(goodsCode);
		
		model.addAttribute("title", "상품수정");
		model.addAttribute("sellerInfoList", sellerInfoList);
		model.addAttribute("goodsInfo", goodsInfo);
		
		return "goods/modifyGoods";
	}
	
	@PostMapping("/addGoods")
	public String addGoods(Goods goods) {
		log.info("goods: {}", goods);
		
		goodsService.addGoods(goods);
		
		return "redirect:/goods/goodsList";
	}

	@GetMapping("/addGoods")
	public String addGoods(Model model, HttpSession session) {
		String memberId = (String) session.getAttribute("SID");
		
		if(memberId != null) {
			int memberLevel = (int) session.getAttribute("SLEVEL");
			if(memberLevel == 1) {
				List<Member> sellerInfoList = goodsService.getSellerInfoList();
				model.addAttribute("sellerInfoList", sellerInfoList);
			}
		}
		
		model.addAttribute("title", "상품등록");
		
		return "goods/addGoods";
	}
	
	@GetMapping("/goodsList")
	public String getGoodsList(Model model,
							   @RequestParam(value="searchKey", required = false) String searchKey,
							   @RequestParam(value="searchValue", required = false) String searchValue) {
		List<Goods> goodsList = goodsService.getGoodsList(searchKey, searchValue);
		
		model.addAttribute("title", "상품목록");
		model.addAttribute("goodsList", goodsList);
		
		return "goods/goodsList";
	}
	
	
	
}
