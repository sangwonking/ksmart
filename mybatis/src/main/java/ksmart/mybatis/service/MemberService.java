package ksmart.mybatis.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import ksmart.mybatis.dto.Member;
import ksmart.mybatis.dto.MemberLevel;
import ksmart.mybatis.mapper.GoodsMapper;
import ksmart.mybatis.mapper.MemberMapper;

/**
 * @Transactional: 트랜잭션 처리 A(원자성)C(일관성)I(고립성)D(영속성)
 * SQLException dao 관련된 작업 rollback
 * 정상적으로 수행이 되었다면? commit
 * @author ASUS
 *
 */
@Service
@Transactional
public class MemberService {
	
	private static final Logger log = LoggerFactory.getLogger(MemberService.class);
	
	private final MemberMapper memberMapper;
	private final GoodsMapper goodsMapper;

	// 생성자 메소드 의존성 주입방식
	public MemberService(MemberMapper memberMapper, GoodsMapper goodsMapper) {
		this.memberMapper = memberMapper;
		this.goodsMapper = goodsMapper;
	}
	
	@PostConstruct
	public void memberServiceInit() {
		System.out.println("memberService 객체 생성");
	}
	
	
	public Map<String, Object> getLoginHistory(int currentPage) {
		// 보여질 행의 갯수
		int rowPerPage = 5;
		
		// 페이지 계산(시작될 행의 인덱스)
		int startIndex = (currentPage-1) * rowPerPage;
		
		// 마지막 페이지 계산
		// 1. 보여질 테이블의 전체 행의 갯수
		double rowsCnt = memberMapper.getLoginCnt();
		// 2. 마지막 페이지
		int lastPage = (int) Math.ceil(rowsCnt/rowPerPage);
		
		int startPageNum = 1;
		int endPageNum = (lastPage < 10) ? lastPage : 10;
		
		// 동적페이지 구성 (7page 부터)
		if(lastPage > 10 && currentPage > 6) {
			startPageNum = currentPage - 5;
			endPageNum = currentPage + 4;
			if(endPageNum >= lastPage) {
				startPageNum = lastPage - 9;
				endPageNum = lastPage; 
			}
		}
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startIndex",		startIndex);
		paramMap.put("rowPerPage",		rowPerPage);
		
		
		// 화면에 보여질 로그인 이력 데이터
		List<Map<String, Object>> loginHistoryList = memberMapper.getLoginHistoryList(paramMap); 
		log.info("로그인이력: {}", loginHistoryList);
		
		//controller에 전달될 data
		paramMap.clear(); // map 객체안의 data 초기화		
		paramMap.put("lastPage", lastPage);
		paramMap.put("loginHistoryList", loginHistoryList);
		paramMap.put("startPageNum",	startPageNum);
		paramMap.put("endPageNum",		endPageNum);
		
		return paramMap;
		
	}
	
	/**
	 * 회원탈퇴(회원등급별 탈퇴 처리)
	 * @param memberInfo
	 */
	public void removeMember(Member memberInfo) {
		if(memberInfo != null) {
			String memberId = memberInfo.getMemberId();
			int memberLevel = memberInfo.getMemberLevel();
			
			switch (memberLevel) {
				case 2 -> {
					goodsMapper.removeOrderByGCode(memberId);
					goodsMapper.removeGoodsById(memberId);
				}
				case 3 -> {
					memberMapper.removeOrderById(memberId);
				}
			}
			memberMapper.removeLoginHistoryById(memberId);
			memberMapper.removeMemberById(memberId);
		}
	}
	
	/**
	 * 회원여부 검증
	 * @param memberId, memberPw
	 * @return Map<String,Object>  검증성공 : (검증여부, 검증이 완료된 회원객체), 검증실패(검증여부) 
	 */
	public Map<String, Object> isValidMember(String memberId, String memberPw) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean isValid = false;
		
		// 회원 검증 
		Member member = memberMapper.getMemberInfoById(memberId);
		if(member != null) {
			String checkPw = member.getMemberPw();
			if(checkPw.equals(memberPw)) {
				isValid = true;
				resultMap.put("memberInfo", member);
			}
		}
		
		resultMap.put("isValid", isValid);
		
		return resultMap;
	}
	
	/**
	 *  회원 정보 수정
	 * @param Member
	 * @return excuteUpdate() 메소드 리턴 값: (수정처리 완료:1, 미완료:0)
	 */
	public int modifyMember(Member member) {
		int result = memberMapper.modifyMember(member);
		return result;
	}
	
	/**
	 * 회원별 상세조회
	 * @param memberId(회원아이디)
	 * @return Member (회원정보)
	 */
	public Member getMemberInfoById(String memberId) {
		Member memberInfo = memberMapper.getMemberInfoById(memberId);
		return memberInfo;
	}
	
	/**
	 * 회원가입
	 * @param member
	 */
	public void addMember(Member member) {
		memberMapper.addMember(member);
	}
	
	/**
	 * 회원등급 조회
	 * @return
	 */
	public List<MemberLevel> getMemberLevelList(){
		
		List<MemberLevel> memberLevelList = memberMapper.getMemberLevelList();
			
		return memberLevelList;
	}
	
	/**
	 * 회원 목록 조회
	 * @return
	 */
	public List<Member> getMemberList(String searchKey, String searchValue){
		Map<String, Object> paramMap = null;
		
		if(searchValue != null) {
			switch (searchKey) {
				case "memberId" -> {
					searchKey = "m_id";
				}
				case "memberName" -> {
					searchKey = "m_name";
				}
				case "memberEmail" -> {
					searchKey = "m_email";
				}
			
			}
			paramMap = new HashMap<String, Object>();
			paramMap.put("searchKey", searchKey);
			paramMap.put("searchValue", searchValue);
		}
		
		List<Member> memberList = memberMapper.getMemberList(paramMap);
		
		String[] memberLevelArr = {"관리자", "판매자", "구매자", "테스트"};
		log.info("memberList : {}", memberList);
		if(memberList != null) {			
			memberList.forEach(member -> {
				int memberLevel = member.getMemberLevel();
				String memberLevelName = memberLevelArr[memberLevel-1];
				member.setMemberLevelName(memberLevelName);
			});
		}
		return memberList;
	}

	
}











