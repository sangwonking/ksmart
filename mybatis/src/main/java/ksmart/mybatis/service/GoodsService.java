package ksmart.mybatis.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ksmart.mybatis.dto.Goods;
import ksmart.mybatis.dto.Member;
import ksmart.mybatis.mapper.GoodsMapper;
import ksmart.mybatis.mapper.MemberMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class GoodsService {

	private final GoodsMapper goodsMapper;
	private final MemberMapper memberMapper;
	
	/**
	 * 판매자별 상품목록 조회
	 * @return List<Member>
	 */
	public List<Member> getSellerList(Map<String, Object> paramMap){
		List<Member> sellerList = goodsMapper.getSellerList(paramMap);
		log.info("sellerList : {}", sellerList);
		
		return sellerList;
	}
	
	public void addGoods(Goods goods) {
		// goods객체에 현재 상품코드가 없다
		log.info("insert 전 goods : {}", goods);
		goodsMapper.addGoods(goods);
		// goods객체에 현재 상품코드가 있다.
		log.info("insert 후 goods : {}", goods);
	}
	
	/**
	 * 판매자정보 조회
	 * @return List<Member>
	 */
	public List<Member> getSellerInfoList(){
		List<Member> sellerInfoList = memberMapper.getMemberListByLevel(2);
		return sellerInfoList;
	}
	
	/**
	 * 상품목록조회
	 * @return List<Goods>
	 */
	public List<Goods> getGoodsList(String searchKey, String searchValue){
		Map<String, Object> paramMap = null;
		if(searchValue != null) {
			switch(searchKey) {
				case "goodsCode" -> {
					searchKey = "g.g_code";
				}
				case "goodsName" -> {
					searchKey = "g.g_name";
				}
				case "sellerId" -> {
					searchKey = "g.g_seller_id";
				}
				case "sellerName" -> {
					searchKey = "m.m_name";
				}
				case "sellerEmail" -> {
					searchKey = "m.m_email";
				}
			}
			paramMap = new HashMap<String, Object>();
			paramMap.put("searchKey", searchKey);
			paramMap.put("searchValue", searchValue);
		}
		List<Goods> goodsList = goodsMapper.getGoodsList(paramMap);
		log.info("goodsList : {}", goodsList);
		return goodsList;
	}

	/**
	 * 특정 상품 조회
	 * @param goodsCode
	 * @return
	 */
	public Goods getGoodsByCode(String goodsCode) {
		
		Goods goodsInfo = goodsMapper.getGoodsByCode(goodsCode);

		return goodsInfo;
	}
	
	/**
	 * 특정 상품 수정
	 * @param goods
	 */
	public void modifyGoods(Goods goods) {
		goodsMapper.modifyGoods(goods);
	}
	
	/**
	 * 특정 상품 삭제
	 * @param goodsCode
	 */
	public void removeGoods(String goodsCode) {
		goodsMapper.removeOrderByGoodsCode(goodsCode);
		goodsMapper.removeGoodsByGoodsCode(goodsCode);
	}
}









