package ksmart.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import ksmart.mybatis.dto.Goods;
import ksmart.mybatis.dto.Member;

@Mapper
public interface GoodsMapper {
	// 판매자별 상품 목록 조회
	public List<Member> getSellerList(Map<String, Object> paramMap);
	
	// 상품코드별 상품삭제
	public int removeGoodsByGoodsCode(String goodsCode);
	
	// 상품코드별 주문이력삭제
	public int removeOrderByGoodsCode(String goodsCode);
	
	// 상품 수정
	public int modifyGoods(Goods goods);
	
	// 상품조회
	public Goods getGoodsByCode(String goodsCode);
	
	// 상품등록
	public int addGoods(Goods goods); 
	
	// 상품목록조회
	public List<Goods> getGoodsList(Map<String, Object> paramMap);

	// 판매자별 상품삭제
	public int removeGoodsById(String sellerId);
	
	// 판매자가 등록한 상품코드별 주문이력삭제
	public int removeOrderByGCode(String sellerId);

	

	

	
}
