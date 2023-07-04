package ksmart.mybatis.dto;

import lombok.Data;

@Data
public class Goods {
	private String goodsCode;
	private String goodsName;
	private int goodsPrice;
	private String goodsSellerId;
	private String goodsRegDate;
	
	// 판매자의 정보 (1:1)
	private Member sellerInfo;
}
